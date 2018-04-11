package com.tian.panovel.activity

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.i7play.supertian.ext.jumpToAndFinish
import com.i7play.supertian.ext.toast
import com.tian.panovel.R
import com.umeng.analytics.MobclickAgent
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import com.zhy.autolayout.AutoLayoutActivity

class SplashActivity : AutoLayoutActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash)

        AndPermission.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).requestCode(10).callback(object : PermissionListener {
            override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                when (requestCode) {
                    10 -> {
                        Handler().postDelayed({
                            jumpToAndFinish(MainActivity::class.java)
                        }, 1000)
                    }
                }
            }

            override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                when (requestCode) {
                    10 -> {
                        toast("权限申请失败")
                        finish()
                    }
                }
            }

        }).start()
    }

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}
