package hzkj.cc.login

import androidx.lifecycle.MutableLiveData
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-02-27-14:06
 */
class LoginViewModel : BaseViewModel() {
    companion object {
        var LOGIN = 1
    }

    var datas: MutableLiveData<AppUser> = MutableLiveData()
    private var loginResposity = LoginResposity()
    fun login(phone: String, passWord: String, jPush: String) {
        launchUI({
            val result = loginResposity.login(phone, passWord, jPush)
            datas.value = result.data
        }, LOGIN)
    }
}