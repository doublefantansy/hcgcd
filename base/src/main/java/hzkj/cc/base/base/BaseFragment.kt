package hzkj.cc.base.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV
import hzkj.cc.base.NotifyDialog
import hzkj.cc.base.R
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : BaseViewModel> : Fragment() {
    abstract fun getLayoutId(): Int
    abstract fun initView()
    abstract fun initData()
    abstract fun onShow()
    abstract fun onHide()
//    var hasAdd: Boolean = false
    lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(getLayoutId(), container, false)
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            onShow()
//            hasAdd = true
        } else {
            onHide()
        }
    }

    open fun updateTokenTimeOut(it: Throwable?) {
        NotifyDialog.Builder(activity!!)
            .canCancel(false)
            .canOutsideCancel(false)
            .image(R.drawable.base_token_timeout_dialog_error_icon)
            .dismissListenner {
                ARouter.getInstance()
                    .build("/login1/login")
                    .navigation(activity, object : NavigationCallback {
                        override fun onLost(postcard: Postcard?) {
                        }

                        override fun onFound(postcard: Postcard?) {
                        }

                        override fun onInterrupt(postcard: Postcard?) {
                        }

                        override fun onArrival(postcard: Postcard?) {
                            MMKV.defaultMMKV()
                                .clearAll()
                            activity!!.sendBroadcast(Intent("finish"))
                        }
                    })
            }
            .text("登陆认证过期,请重新登陆")
            .build()
            .show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this)
            .get(((this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VM>))
        lifecycle.addObserver(viewModel)

        initData()
        initView()
        startObserve()
        startCommonObserve()
    }

    abstract fun startObserve()

    private fun startCommonObserve() {
        viewModel.run {
            viewModel.tokenTimeOut.observe(this@BaseFragment, Observer { updateTokenTimeOut(it) })

            viewModel.error.observe(this@BaseFragment, Observer {
                updateError(it)
            })
//            viewModel.finally.observe(this@BaseFragment, Observer {
//                updateFinally(it)
//            })
        }
    }

    open fun updateFinally(it: Int?) {
        Log.d("cc_lsy", "complete+${it.toString()}")
    }

    open fun updateError(it: Int?) {
        Log.d("cc_lsy", it.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }
}