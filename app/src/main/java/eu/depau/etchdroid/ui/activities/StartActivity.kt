package eu.depau.etchdroid.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.codekidlabs.storagechooser.StorageChooser
import com.github.mjdev.libaums.usb.UsbCommunicationFactory
import eu.depau.etchdroid.R
import eu.depau.etchdroid.StateKeeper
import eu.depau.etchdroid.ui.misc.DoNotShowAgainDialogFragment
import eu.depau.etchdroid.utils.enums.FlashMethod
import eu.depau.etchdroid.utils.ktexts.toast
import kotlinx.android.synthetic.main.activity_start.*
import me.jahnen.libaums.libusbcommunication.LibusbCommunicationCreator
import java.io.File



class StartActivity : ActivityBase() {
    private var delayedButtonClicked: Boolean = false

    companion object {
        const val TAG = "StartActivity"
    }

    var shouldShowDMGAlertDialog: Boolean
        get() {
            val settings = getSharedPreferences(DISMISSED_DIALOGS_PREFS, 0)
            return !settings.getBoolean("DMG_beta_alert", false)
        }
        set(value) {
            val settings = getSharedPreferences(DISMISSED_DIALOGS_PREFS, 0)
            val editor = settings.edit()
            editor.putBoolean("DMG_beta_alert", !value)
            editor.apply()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        btn_image_raw.setOnClickListener(this::onButtonClicked)
        btn_image_dmg.setOnClickListener(this::onButtonClicked)
        btn_broken_usb.setOnClickListener(this::onButtonClicked)

        if (!StateKeeper.libusbRegistered) {
            UsbCommunicationFactory.apply {
                registerCommunication(LibusbCommunicationCreator())
                underlyingUsbCommunication = UsbCommunicationFactory.UnderlyingUsbCommunication.OTHER
            }
            StateKeeper.libusbRegistered = true
        }
    }

    private fun onButtonClicked(view: View) = onButtonClicked(view, true)

    private fun openBrokenUsbPage() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://etchdroid.depau.eu/broken_usb/"))
        startActivity(intent)
    }

    private fun onButtonClicked(view: View?, showDMGDialog: Boolean = true, showAndroidPieDialog: Boolean = true) {
        if (view != null) {
            if (view.id == R.id.btn_broken_usb) {
                openBrokenUsbPage()
                return
            }

            StateKeeper.flashMethod = when (view.id) {
                R.id.btn_image_raw -> FlashMethod.FLASH_API
                R.id.btn_image_dmg -> FlashMethod.FLASH_DMG_API
                else               -> null
            }

//        if (showAndroidPieDialog && shouldShowAndroidPieAlertDialog) {
//            showAndroidPieAlertDialog { onButtonClicked(view, showDMGDialog, false) }
//            return
//        }

            if (showDMGDialog && shouldShowDMGAlertDialog && StateKeeper.flashMethod == FlashMethod.FLASH_DMG_API) {
                showDMGBetaAlertDialog { onButtonClicked(view, false, showAndroidPieDialog) }
                return
            }

            showFilePicker()
        }
    }

    private fun showDMGBetaAlertDialog(callback: () -> Unit) {
        val dialogFragment = DoNotShowAgainDialogFragment(isNightMode)
        dialogFragment.title = getString(R.string.here_be_dragons)
        dialogFragment.message = getString(R.string.dmg_alert_dialog_text)
        dialogFragment.positiveButton = getString(R.string.i_understand)
        dialogFragment.listener = object : DoNotShowAgainDialogFragment.DialogListener {
            override fun onDialogNegative(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {}
            override fun onDialogPositive(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {
                shouldShowDMGAlertDialog = showAgain
                callback()
            }
        }
        dialogFragment.show(supportFragmentManager, "DMGBetaAlertDialogFragment")
    }

    private fun showAndroidPieAlertDialog(callback: () -> Unit) {
        val dialogFragment = DoNotShowAgainDialogFragment(isNightMode)
        dialogFragment.title = getString(R.string.android_pie)
        dialogFragment.message = getString(R.string.android_pie_alert_dialog_text)
        dialogFragment.positiveButton = getString(R.string.i_understand)
        dialogFragment.listener = object : DoNotShowAgainDialogFragment.DialogListener {
            override fun onDialogNegative(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {}
            override fun onDialogPositive(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {
                shouldShowAndroidPieAlertDialog = showAgain
                callback()
            }
        }
        dialogFragment.show(supportFragmentManager, "AndroidPieAlertDialogFragment")
    }

    private fun showFilePicker() {
        val chooser = StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(fragmentManager)
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build()

        chooser.setOnSelectListener {
            val file = File(it)
            if (file.exists() && file.canRead()) {
                StateKeeper.imageFile = file
                startActivity(Intent(this, FlashActivity::class.java))
            } else {
                toast(R.string.file_not_readable, Toast.LENGTH_LONG)
            }
        }

        chooser.show()
    }



    private fun showFilePicker() {
        when (StateKeeper.flashMethod) {
            FlashMethod.FLASH_API -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                startActivityForResult(intent, READ_REQUEST_CODE)
            }
            FlashMethod.FLASH_DMG_API -> {
                if (checkAndRequestStorageReadPerm()) {
                    val sdcard = Environment.getExternalStorageDirectory().absolutePath

                    val chooser = StorageChooser.Builder()
                            .withActivity(this)
                            .withFragmentManager(fragmentManager)
                            .withMemoryBar(true)
                            .allowCustomPath(true)
                            .setType(StorageChooser.FILE_PICKER)
                            .customFilter(arrayListOf("dmg"))
                            .build()
                    chooser.show()
                    chooser.setOnSelectListener {
                        StateKeeper.imageFile = Uri.fromFile(File(it))
                        nextStep()
                    }
                } else {
                    delayedButtonClicked = true
                }
            }
            FlashMethod.FLASH_UNETBOOTIN -> {
            }
            FlashMethod.FLASH_WOEUSB -> {
            }
            null -> {
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (delayedButtonClicked) {
                        onButtonClicked(null, showDMGDialog = false, showAndroidPieDialog = false)
                        toast(getString(R.string.now_press_again), Toast.LENGTH_SHORT)
                    }
                    return
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            var uri: Uri? = null
            if (data != null) {
                StateKeeper.imageFile = data.data

                nextStep()
            }
        }
    }

    private fun nextStep() {
        val intent = Intent(this, FlashActivity::class.java)
        startActivity(intent)
    }

    private fun checkAndRequestStorageReadPerm(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_EXTERNAL_STORAGE_PERMISSION)
            return false
        }
        return true
    }

    private fun showAndroidPieAlertDialog(callback: () -> Unit) {
        val dialogFragment = DoNotShowAgainDialogFragment(isNightMode)
        dialogFragment.title = getString(R.string.android_pie)
        dialogFragment.message = getString(R.string.android_pie_alert_dialog_text)
        dialogFragment.positiveButton = getString(R.string.i_understand)
        dialogFragment.listener = object : DoNotShowAgainDialogFragment.DialogListener {
            override fun onDialogNegative(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {}
            override fun onDialogPositive(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {
                shouldShowAndroidPieAlertDialog = showAgain
                callback()
            }
        }
        dialogFragment.show(supportFragmentManager, "AndroidPieAlertDialogFragment")
    }

    private fun openBrokenUsbPage() {
        val intent = Intent(this, BrokenUsbActivity::class.java)
        startActivity(intent)
    }

    private fun openAboutPage() {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun openSettingsPage() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun openHelpPage() {
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    private fun openDonatePage() {
        val intent = Intent(this, DonateActivity::class.java)
        startActivity(intent)
    }

    private fun openChangelogPage() {
        val intent = Intent(this, ChangelogActivity::class.java)
        startActivity(intent)
    }

    private fun openLicensePage() {
        val intent = Intent(this, LicenseActivity::class.java)
        startActivity(intent)
    }

    private fun openPrivacyPolicyPage() {
        val intent = Intent(this, PrivacyPolicyActivity::class.java)
        startActivity(intent)
    }


    private fun nextStep() {
        val intent = Intent(this, UsbDrivePickerActivity::class.java)
        startActivity(intent)
    }
}
