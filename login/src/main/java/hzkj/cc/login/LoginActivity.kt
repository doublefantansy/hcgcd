package hzkj.cc.login

import android.view.WindowManager
import androidx.lifecycle.Observer
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseActivity
import hzkj.cc.base.weight.LoadingDialog
import kotlinx.android.synthetic.main.activity_login.*

/**

 * @Author chencheng
 * @Date 2020-02-27-14:05
 */
@Route(path = "/login1/login")
class LoginActivity(
    override var statusBarColor: Int = R.color.base_blue,
    override var isStatusBarDark: Boolean = false,
    override var isImmerse: Boolean = true
) :
    BaseActivity<LoginViewModel>() {
    private lateinit var loadingDialog: LoadingDialog
    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }


    override fun initView() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (MMKV.defaultMMKV().containsKey("mediaUserInfo")) {
            MMKV.defaultMMKV().remove("mediaUserInfo")
        }
        if (MMKV.defaultMMKV().containsKey("user")) {
            ARouter.getInstance()
                .build("/main/main")
                .navigation(this, object : NavigationCallback {
                    override fun onLost(postcard: Postcard?) {
                    }

                    override fun onFound(postcard: Postcard?) {
                    }

                    override fun onInterrupt(postcard: Postcard?) {
                    }

                    override fun onArrival(postcard: Postcard?) {
                        finish()
                    }
                })
            return
        }
        loadingDialog = LoadingDialog(this)
        textview_login.setOnClickListener {
            if (edittext_phone.text.toString().isEmpty() || edittext_password.text.toString().isEmpty()) {
                ViewUtil.toast(this, "用户名或密码不能为空")
            } else {
                loadingDialog.show()
                viewModel.login(
                    edittext_phone.text.toString(), edittext_password.text.toString(),
                    JPushInterface.getRegistrationID(this)
                )
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        viewModel.datas.observe(this, Observer {
            loadingDialog.dismiss()
            if (it == null) {
                ViewUtil.toast(this, "用户名或密码错误")
            } else {
                MMKV.defaultMMKV()
                    .putString("user", Gson().toJson(it))
                ARouter.getInstance()
                    .build("/main/main")
                    .navigation(this, object : NavigationCallback {
                        override fun onLost(postcard: Postcard?) {
                        }

                        override fun onFound(postcard: Postcard?) {
                        }

                        override fun onInterrupt(postcard: Postcard?) {
                        }

                        override fun onArrival(postcard: Postcard?) {
                            finish()
                        }
                    })
            }
        })
    }
}