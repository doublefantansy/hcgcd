package cc.hc.media

import android.content.Context
import android.telephony.TelephonyManager
import androidx.lifecycle.MutableLiveData
import com.android.business.adapter.DataAdapterInterface
import com.android.business.entity.UserInfo
import com.android.business.exception.BusinessException
import hzkj.cc.base.base.BaseViewModel

/**

 * @Author chencheng
 * @Date 2020-03-02-14:55
 */
class MediaViewModel : BaseViewModel() {

    companion object {
        var CAMERAS = 1
    }

    var isSuccess = MutableLiveData<UserInfo>()

    var cameraDatas = MutableLiveData<MutableList<Camera>>()
    var resposity = MediaResposity()
    fun getCameras(areaCode: String) {
        launchUI({ cameraDatas.value = resposity.getCameras(areaCode).data }, CAMERAS)
    }

    fun login(
            context: Context,
            dataAdapterInterface: DataAdapterInterface?,
            cameraUser: String?,
            cameraPassword: String?
    ) {
        launch {
            val tm =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val clientMac = tm.deviceId
            var info: UserInfo? = null
            try {
                info = dataAdapterInterface!!.login(cameraUser, cameraPassword, "1", clientMac, 2)
            } catch (e: BusinessException) {
                println(e.message)
            }

            isSuccess.value = info

        }
    }
}