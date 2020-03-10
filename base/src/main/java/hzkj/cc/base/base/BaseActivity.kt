package hzkj.cc.base.base

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV
import hzkj.cc.base.FinishBroadCast
import hzkj.cc.base.NotifyDialog
import hzkj.cc.base.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(),
    CoroutineScope by MainScope() {
    abstract fun getLayoutId(): Int
    abstract fun initView()
    abstract fun initData()
    lateinit var viewModel: VM
    var finishBroadCast = FinishBroadCast()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutId())
        viewModel = ViewModelProviders.of(this)
            .get(((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>))
        ARouter.getInstance()
            .inject(this)
        lifecycle.addObserver(viewModel)
        statusBarTransparent()
        initData()
        initView()
        startObserve()
        startCommonObserve()
        registerReceiver(finishBroadCast, IntentFilter().apply {
            addAction("finish")
        })
    }

    abstract fun startObserve()
    abstract var statusBarColor: Int
    abstract var isStatusBarDark: Boolean
    abstract var isImmerse: Boolean
    private fun startCommonObserve() {
        viewModel.run {
            viewModel.error.observe(this@BaseActivity, Observer { updateError(it) })
            viewModel.tokenTimeOut.observe(this@BaseActivity, Observer { updateTokenTimeOut(it) })
        }
    }

    private fun statusBarTransparent() {
        val window = window
        var option = 0
        if (isStatusBarDark) {
            option = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        // 前者将状态栏覆盖在全屏上,后者稳定布局。当StatusBar和NavigationBar动态显示和隐藏时，系统为fitSystemWindow=true的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动
        if (isImmerse) {
            option =
                option or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
        }
        window.decorView.systemUiVisibility = option
        //        window.statusBarColor = Color.TRANSPARENT
    }

    open fun updateTokenTimeOut(it: Throwable?) {
        NotifyDialog.Builder(this)
            .canCancel(false)
            .canOutsideCancel(false)
            .image(R.drawable.base_token_timeout_dialog_error_icon)
            .dismissListenner {
                ARouter.getInstance()
                    .build("/login1/login")
                    .navigation(this@BaseActivity, object : NavigationCallback {
                        override fun onLost(postcard: Postcard?) {
                        }

                        override fun onFound(postcard: Postcard?) {
                        }

                        override fun onInterrupt(postcard: Postcard?) {
                        }

                        override fun onArrival(postcard: Postcard?) {
                            MMKV.defaultMMKV()
                                .clearAll()
                            sendBroadcast(Intent("finish"))
                        }
                    })
            }
            .text("登陆认证过期,请重新登陆")
            .build()
            .show()
    }

    open fun updateError(it: Int?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
        unregisterReceiver(finishBroadCast)
        cancel()
    }
}