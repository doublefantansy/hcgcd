package hzkj.cc.my

import android.content.Intent
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV
import hzkj.cc.base.NotifyDialog
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseActivity
import kotlinx.android.synthetic.main.activity_change_password.*

/**

 * @Author chencheng
 * @Date 2020-03-06-15:08
 */
@Route(path = "/my/change_password")
class MyChangePasswordActivity(
        override var statusBarColor: Int = R.color.base_blue,
        override var isStatusBarDark: Boolean = false,
        override var isImmerse: Boolean = false
) : BaseActivity<MyChangePasswordViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun initView() {
        textview_change.setOnClickListener {
            (edittext_new_password.text.isBlank() || edittext_old_password.text.isBlank() || edittext_new_password_confirm.text.isBlank()).also {
                if (it) {
                    ViewUtil.toast(this, "不能为空")
                } else {
                    (edittext_new_password.text.toString() == edittext_new_password_confirm.text.toString()).also { isEqual ->
                        if (isEqual) {
                            viewModel.updatePassword(edittext_new_password.text.toString())
                        } else {
                            ViewUtil.toast(this, "两次新密码不相同")
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        viewModel.datas.observe(this, Observer {
            if (it == 1) {
                MMKV.defaultMMKV().clearAll()
                NotifyDialog.Builder(this)
                        .canCancel(false)
                        .canOutsideCancel(false)
                        .image(R.drawable.dialog_change_password_success_icon)
                        .dismissListenner {
                            ARouter.getInstance().build("/login1/login")
                                    .navigation(this, object : NavigationCallback {
                                        override fun onLost(postcard: Postcard?) {

                                        }

                                        override fun onFound(postcard: Postcard?) {
                                        }

                                        override fun onInterrupt(postcard: Postcard?) {
                                        }

                                        override fun onArrival(postcard: Postcard?) {
                                            finish()
                                            sendBroadcast(Intent().apply {
                                                action = "changePassword"
                                            })
                                        }

                                    })
                        }
                        .text("修改密码成功,请重新登陆")
                        .build()
                        .show()


            }

        })
    }

}