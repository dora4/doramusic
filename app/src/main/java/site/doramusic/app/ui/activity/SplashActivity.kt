package site.doramusic.app.ui.activity

import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lwh.jackknife.av.util.MusicUtils
import dora.arouter.openWithFinish
import dora.crash.DoraCrash
import dora.util.StatusBarUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import site.doramusic.app.R
import site.doramusic.app.annotation.RequirePermission
import site.doramusic.app.base.BaseSkinActivity
import site.doramusic.app.base.conf.ARoutePath
import site.doramusic.app.base.conf.AppConfig
import site.doramusic.app.databinding.ActivitySplashBinding
import site.doramusic.app.media.SimpleAudioPlayer
import site.doramusic.app.util.PreferencesManager
import site.doramusic.app.util.UserManager

/**
 * 启动页，无法使用AppCompatActivity主题，所有直接继承Activity。
 */
@Route(path = ARoutePath.ACTIVITY_SPLASH)
class SplashActivity : BaseSkinActivity<ActivitySplashBinding>() {

    private var audioPlayer: SimpleAudioPlayer? = null

    companion object {
        private const val SPLASH_TIME = 300
    }

    override fun onSetStatusBar() {
        super.onSetStatusBar()
        StatusBarUtils.setTransparencyStatusBar(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE).request { permissions, allGranted ->
            initAppFolder()
            DoraCrash.initCrash(
                this@SplashActivity,
                "DoraMusic/log"
            )
        }
    }

    private fun initAppFolder() {
        if (dora.util.IoUtils.checkMediaMounted()) {
            dora.util.IoUtils.createFolder(arrayOf(
                AppConfig.FOLDER_LOG, AppConfig.FOLDER_LRC,
                AppConfig.FOLDER_SONG, AppConfig.FOLDER_APK,
                AppConfig.FOLDER_CACHE, AppConfig.FOLDER_COVER,
                AppConfig.FOLDER_PATCH)
            )
        }
    }

    @RequirePermission(Permission.WRITE_EXTERNAL_STORAGE)
    private fun init() {
        UserManager.update(this)
        val prefsManager = PreferencesManager(this)
        splashLoading(prefsManager)
    }

    override fun initData(savedInstanceState: Bundle?) {
        XXPermissions.with(this).permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request { permissions, allGranted -> init() }
    }

    private fun splashLoading(prefsManager: PreferencesManager) {
        Handler().postDelayed({
            openWithFinish(ARoutePath.ACTIVITY_MAIN)
        }, SPLASH_TIME.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicUtils.clearCache()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(msg: String) {
    }
}
