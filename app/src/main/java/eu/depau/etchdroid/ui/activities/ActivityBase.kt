package eu.depau.etchdroid.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import eu.depau.etchdroid.R
import eu.depau.etchdroid.ui.misc.DoNotShowAgainDialogFragment
import eu.depau.etchdroid.ui.misc.NightModeHelper
import eu.depau.etchdroid.utils.ktexts.toast
import me.jfenn.attribouter.Attribouter


abstract class ActivityBase : AppCompatActivity() {
    private var nightModeHelper: NightModeHelper? = null

    companion object {
        const val DISMISSED_DIALOGS_PREFS = "dismissed_dialogs"
        const val READ_REQUEST_CODE = 42
        const val READ_EXTERNAL_STORAGE_PERMISSION = 29
    }

    var shouldShowAndroidPieAlertDialog: Boolean
        get() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
                return false
            val settings = getSharedPreferences(DISMISSED_DIALOGS_PREFS, 0)
            return !settings.getBoolean("Android_Pie_alert", false)
        }
        set(value) {
            val settings = getSharedPreferences(DISMISSED_DIALOGS_PREFS, 0)
            val editor = settings.edit()
            editor.putBoolean("Android_Pie_alert", !value)
            editor.apply()
        }

    internal val isNightMode: Boolean
        get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_YES != 0

    fun showAndroidPieAlertDialog(callback: () -> Unit) {
        val dialogFragment = DoNotShowAgainDialogFragment(isNightMode)
        dialogFragment.title = getString(R.string.android_pie_bug)
        dialogFragment.message = getString(R.string.android_pie_bug_dialog_text)
        dialogFragment.positiveButton = getString(R.string.i_understand)
        dialogFragment.listener = object : DoNotShowAgainDialogFragment.DialogListener {
            override fun onDialogNegative(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {}
            override fun onDialogPositive(dialog: DoNotShowAgainDialogFragment, showAgain: Boolean) {
                shouldShowAndroidPieAlertDialog = showAgain
                callback()
            }
        }
        dialogFragment.show(supportFragmentManager, "DMGBetaAlertDialogFragment")
    }

    fun checkAndRequestStorageReadPerm(): Boolean {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                toast(getString(R.string.storage_permission_required))
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_STORAGE_PERMISSION)
            }
        } else {
            // Permission granted
            return true
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            nightModeHelper = NightModeHelper(this, R.style.AppTheme)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_attributions -> {
                Attribouter.from(this)
                        .withAboutShown(true)
                        .withAboutIcon(R.mipmap.ic_launcher)
                        .withAboutVersionName(BuildConfig.VERSION_NAME)
                        .withAboutVersionCode(BuildConfig.VERSION_CODE)
                        .withAboutDescription(getString(R.string.app_description))
                        .withAboutSpecial1(getString(R.string.app_license))
                        .withAboutSpecial1Icon(R.drawable.ic_gpl)
                        .withAboutSpecial1IconColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .withAboutSpecial1IconSize(24)
                        .withAboutSpecial1IconPadding(8)
                        .withAboutSpecial1IconBackground(R.drawable.ic_gpl_bg)
                        .withAboutSpecial1IconBackgroundSize(32)
                        .withAboutSpecial1IconBackgroundPadding(4)
                        .withAboutSpecial1IconBackgroundTint(ContextCompat.getColor(this, R.color.colorPrimary))
                        .withAboutSpecial1IconBackgroundRipple(true)
                        .withAboutSpecial1IconBackgroundRippleColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                        .withAboutSpecial1IconBackgroundRippleAlpha(0.2f)
                        .withAboutSpecial1IconBackgroundRippleDuration(250)
                        .withAboutSpecial1IconBackgroundRippleDelay(0)
                        .withAboutSpecial1IconBackgroundRippleInterpolator("accelerate_decelerate")
                        .withAboutSpecial1IconBackgroundRippleInfinite(false)
                        .withAboutSpecial1IconBackgroundRippleRepeatCount(0)
                        .withAboutSpecial1IconBackgroundRippleRepeatMode("restart")
                        .withAboutSpecial1IconBackgroundRippleAutoCancel(true)
                        .withAboutSpecial1IconBackgroundRippleRadius(0)
                        .withAboutSpecial1IconBackgroundRippleHotspot(0.5f, 0.5f)
                        .withAboutSpecial1IconBackgroundRippleHotspotBounds(0, 0, 0, 0)
                        .withAboutSpecial1IconBackgroundRippleFadeDuration(75)
                        .withAboutSpecial1IconBackgroundRippleScale(1.0f)
                        .withAboutSpecial1IconBackgroundRippleType("foreground")
                        .withAboutSpecial1IconBackgroundRippleMask(null)
                        .withAboutSpecial1IconBackgroundRippleForceSoftware(false)
                        .
                        

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_attributions -> {
                Attribouter.from(this)
                        .withAboutShown(true)
                        .withAboutIcon(R.mipmap.ic_launcher)
                        .withAboutVersionName(BuildConfig.VERSION_NAME)
                        .withAboutVersionCode(BuildConfig.VERSION_CODE)
                        .withAboutDescription(getString(R.string.app_description))
                        .withAboutSpecial1(getString(R.string.app_license))
                        .withAboutSpecial1Icon(R.drawable.ic_gpl)
                        .withAboutSpecial1IconColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .withAboutSpecial1IconSize(24)
                        .withAboutSpecial1IconPadding(8)
                        .withAboutSpecial1IconBackground(R.drawable.ic_gpl_bg)
                        .withAboutSpecial1IconBackgroundSize(32)
                        .withAboutSpecial1IconBackgroundPadding(4)
                        .withAboutSpecial1IconBackgroundTint(ContextCompat.getColor(this, R.color.colorPrimary))
                        .withAboutSpecial1IconBackgroundRipple(true)
                        .withAboutSpecial1IconBackgroundRippleColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                        .withAboutSpecial1IconBackgroundRippleAlpha(0.2f)
                        .withAboutSpecial1IconBackgroundRippleDuration(250)
                        .withAboutSpecial1IconBackgroundRippleDelay(0)
                        .withAboutSpecial1IconBackgroundRippleInterpolator("accelerate_decelerate")
                        .withAboutSpecial1IconBackgroundRippleInfinite(false)
                        .withAboutSpecial1IconBackgroundRippleRepeatCount(0)
                        .withAboutSpecial1IconBackgroundRippleRepeatMode("restart")
                        .withAboutSpecial1IconBackgroundRippleAutoCancel(true)
                        .withAboutSpecial1IconBackgroundRippleRadius(0)
                        .withAboutSpecial1IconBackgroundRippleHotspot(0.5f, 0.5f)
                        .withAboutSpecial1IconBackgroundRippleHotspotBounds(0, 0, 0, 0)
                        .withAboutSpecial1IconBackgroundRippleFadeDuration(75)
                        .withAboutSpecial1IconBackgroundRippleScale(1.0f)
                        .withAboutSpecial1IconBackgroundRippleType("foreground")
                        .withAboutSpecial1IconBackgroundRippleMask(null)
                        .withAboutSpecial1IconBackgroundRippleForceSoftware(false)
                        .

withAboutSpecial1IconBackgroundRipplePersistent(false)
                        .withAboutSpecial1IconBackgroundRippleSelectable(false)
                        .withAboutSpecial1IconBackgroundRippleStateListAnimator(null)
                        .withAboutSpecial1IconBackgroundRippleUseLevel(false)
                        .withAboutSpecial1IconBackgroundRippleVisible(true)
                        .withAboutSpecial1IconBackgroundRippleEnabled(true)
                        .withAboutSpecial1IconBackgroundRippleClickable(true)
                        .withAboutSpecial1IconBackgroundRippleFocusable(true)
                        .withAboutSpecial1IconBackgroundRippleHovered(false)
                        .withAboutSpecial1IconBackgroundRipplePressed(false)
                        .withAboutSpecial1IconBackgroundRippleSelected(false)
                        .withAboutSpecial1IconBackgroundRippleActivated(false)
                        .withAboutSpecial1IconBackgroundRippleChecked(false)
                        .withAboutSpecial1IconBackgroundRippleIndeterminate(false)
                        .withAboutSpecial1IconBackgroundRippleDragged(false)
                        .withAboutSpecial1IconBackgroundRippleCanReceiveAccessibilityFocused(false)
                        .withAboutSpecial1IconBackgroundRippleAccessibilityFocused(false)
                        .withAboutSpecial1IconBackgroundRippleAccessibilityHeading(false)
                        .withAboutSpecial1IconBackgroundRippleAccessibilityPaneTitle(false)
                        .withAboutSpecial1IconBackgroundRippleLongClickable(false)
                        .withAboutSpecial1IconBackgroundRippleContextClickable(false)
                        .withAboutSpecial1IconBackgroundRippleShowingHint(false)
                        .withAboutSpecial1IconBackgroundRippleImportantForAccessibility("auto")
                        .withAboutSpecial1IconBackgroundRippleAccessibilityLiveRegion("none")
                        .withAboutSpecial1IconBackgroundRippleAccessibilityTraversalAfter(null)
                        .withAboutSpecial1IconBackgroundRippleAccessibilityTraversalBefore(null)
                        .withAboutSpecial1IconBackgroundRippleLabelFor(null)
                        .withAboutSpecial1IconBackgroundRippleLabeledBy(null)
                        .withAboutSpecial1IconBackgroundRippleNextFocusDownId(null)
                        .withAboutSpecial1IconBackgroundRippleNextFocusForwardId(null)
                        .withAboutSpecial1IconBackgroundRippleNextFocusLeftId(null)
                        .withAboutSpecial1IconBackgroundRippleNextFocusRightId(null)
                        .withAboutSpecial1IconBackgroundRippleNextFocusUpId(null)
                        .withAboutSpecial1IconBackgroundRippleTooltipText(null)
                        .withAboutSpecial1IconBackgroundRippleAlpha(1.0f)
                        .withAboutSpecial1Icon


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Hide night mode menu on Android 10 as it causes weird issues
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            menu.findItem(R.id.action_nightmode).isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                Attribouter
                        .from(this)
                        .withFile(R.xml.about)
                        .show()
                return true
            }
            R.id.action_donate -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://etchdroid.depau.eu/donate/"))
                startActivity(intent)
                return true
            }
            R.id.action_reset_warnings -> {
                getSharedPreferences(DISMISSED_DIALOGS_PREFS, 0)
                        .edit().clear().apply()
                toast(getString(R.string.warnings_reset))
                return true
            }
            R.id.action_nightmode -> {
                nightModeHelper?.toggle()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
