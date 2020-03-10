package hzkj.cc.my

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import com.tencent.bugly.beta.Beta
import com.tencent.mmkv.MMKV
import hzkj.cc.base.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_my_center.*

/**

 * @Author chencheng
 * @Date 2020-02-28-10:08
 */
@Route(path = "/my/my_center")
class MyCenterFragment : BaseFragment<MyCenterViewModel>() {
    var appUser: AppUser? = null
    override fun getLayoutId(): Int {
        return R.layout.fragment_my_center
    }

    override fun onShow() {

    }

    override fun initView() {
        name.text = appUser!!.userName
        mobile.text = appUser!!.mobilePhone
        quit.setOnClickListener {
            MMKV.defaultMMKV().clearAll()
            ARouter.getInstance().build("/login1/login").navigation()
            activity!!.finish()
        }
        changePassword.setOnClickListener {
            ARouter.getInstance().build("/my/change_password").navigation()
        }
        checkUpdate.setOnClickListener {
            Beta.checkUpgrade()
        }
    }

    override fun initData() {
        appUser = Gson().fromJson(MMKV.defaultMMKV().decodeString("user"), AppUser::class.java)
    }


    override fun startObserve() {
    }

    override fun onHide() {

    }
}