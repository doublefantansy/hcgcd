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
 * @Date 2020-03-05-14:18
 */
class MediaBackViewModel:BaseViewModel() {
    companion object {
        var CAMERAS = 11
    }
    var isSuccess = MutableLiveData<UserInfo>()

    var cameraDatas = MutableLiveData<MutableList<Camera>>()
    private var resposity = MediaBackResposity()
    fun getCamarasBySennorCode(sennorCode: String) {
        launchUI({ cameraDatas.value = resposity.getCamarasBySennorCode(sennorCode).data },
            CAMERAS
        )
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
                System.out.println(e.message)
            }

            isSuccess.value = info

        }
    }
}