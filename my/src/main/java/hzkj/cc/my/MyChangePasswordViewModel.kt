package hzkj.cc.my

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-03-06-15:08
 */
class MyChangePasswordViewModel : BaseViewModel() {
    companion object {
        var CHANGEPASSWORD = 1
    }

    var datas = MutableLiveData<Int>()
    var resoposity = MyResoposity()

    fun updatePassword(newPassword: String) {
        launchUI({
            datas.value = resoposity.updatePassword(
                Gson().fromJson(
                    MMKV.defaultMMKV().decodeString("user"),
                    AppUser::class.java
                ), newPassword
            ).data
        }, CHANGEPASSWORD)
    }
}