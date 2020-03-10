package cc.hc.media

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.business.adapter.DataAdapterImpl
import com.android.business.adapter.DataAdapterInterface
import com.android.business.entity.ChannelInfo
import com.android.business.entity.RecordInfo
import com.android.business.entity.RecordInfo.RecordResource
import com.android.business.exception.BusinessException
import com.android.dahua.dhplaycomponent.IMediaPlayListener
import com.android.dahua.dhplaycomponent.PlayManagerProxy
import com.android.dahua.dhplaycomponent.camera.PBCamera.DPSPBCamera
import com.android.dahua.dhplaycomponent.camera.PBCamera.DPSPBCameraParam
import com.android.dahua.dhplaycomponent.camera.inner.DPSRecordFile
import com.android.dahua.dhplaycomponent.camera.inner.PlayBackInfo
import com.android.dahua.dhplaycomponent.common.MutipleType
import com.android.dahua.dhplaycomponent.common.PlayStatusType
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import hzkj.cc.base.TimeUtil
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseActivity
import kotlinx.android.synthetic.main.media_back_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**

 * @Author chencheng
 * @Date 2020-03-05-14:18
 */
@Route(path = "/media/back")
class MediaBackActivity(

    override var statusBarColor: Int = R.color.base_blue,
    override var isStatusBarDark: Boolean = false,
    override var isImmerse: Boolean = false
) : BaseActivity<MediaBackViewModel>() {
    private var recordInfos: MutableList<RecordInfo>? = null
    var dpsRecordFiles = ArrayList<DPSRecordFile>()
    val KEY_Handler_Stream_Start_Request = 0
    val KEY_Handler_Stream_Played = 1
    val KEY_Handler_First_Frame = 2
    val KEY_Handler_Net_Error = 3
    val KEY_Handler_Play_Failed = 4
    val KEY_Handler_Play_End = 5
    val KEY_Handler_Bad_File = 6
    val KEY_Handler_Seek_Success = 7
    val KEY_Handler_Seek_Cross_Border = 8
    val KEY_Handler_Seek_failed = 9
    val KEY_Handler_Play_Unknow = 15
    private lateinit var dataAdapterInterface: DataAdapterInterface
    private lateinit var mPlayManager: PlayManagerProxy
    var startTime: Long = 0
    var endTime: Long = 0
    var speed = 1.0

    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    display_speed.text = "X${speed}"
                }
                1 -> {
                    mPlayManager?.stopAll()
                    ViewUtil.toast(this@MediaBackActivity, "播放完毕")
                }
                2 -> {
                    mPlayManager?.stopAll()
                    ViewUtil.toast(this@MediaBackActivity, "网络异常")
                }
                3 -> {
                    mPlayManager?.stopAll()
                    ViewUtil.toast(this@MediaBackActivity, "播放失败")
                }
            }
        }
    }

    lateinit var camera: cc.hc.media.Camera
    private lateinit var channelInfo: ChannelInfo
    private val recordResource = RecordResource.Platform
    override fun getLayoutId(): Int {
        return R.layout.media_back_activity
    }

    override fun updateError(it: Int?) {
        super.updateError(it)
        ViewUtil.toast(this, "获取摄像机信息失败")
    }

    override fun initView() {
        speed_up.setOnClickListener {
            changeMutiple((speed * 2).takeIf { it in 0.125..8.0 })
        }
        speed_down.setOnClickListener {
            changeMutiple((speed / 2).takeIf { it in 0.125..8.0 })
        }
    }

    private fun changeMutiple(multiple: Double?) {
        multiple?.let {
            mPlayManager?.setPlaySpeed(
                mPlayManager.selectedWindowIndex, when (multiple) {
                    0.125 -> MutipleType.MULTIPLE_1_8X
                    0.25 -> MutipleType.MULTIPLE_1_4X
                    0.5 -> MutipleType.MULTIPLE_1_2X
                    1.0 -> MutipleType.MULTIPLE_1X
                    2.0 -> MutipleType.MULTIPLE_2X
                    4.0 -> MutipleType.MULTIPLE_4X
                    8.0 -> MutipleType.MULTIPLE_8X
                    else -> 0.0f
                }

            )
            speed = multiple
            display_speed.text = "X${multiple}"
        }


    }

    private val iMediaPlayListener: IMediaPlayListener = object : IMediaPlayListener() {
        override fun onPlayeStatusCallback(
            winIndex: Int,
            type: PlayStatusType,
            code: Int
        ) {
            when (type) {
                PlayStatusType.eStreamStartRequest -> {
                    println(KEY_Handler_Stream_Start_Request)
                }
                PlayStatusType.eStreamPlayed -> {
                    handler.sendEmptyMessage(0)
                    println(KEY_Handler_Stream_Played)
                }
                PlayStatusType.ePlayFirstFrame -> {
                    println(KEY_Handler_First_Frame)
                }
                PlayStatusType.ePlayEnd -> {
                    handler.sendEmptyMessage(1)
                    println(KEY_Handler_Play_End)
                }
                PlayStatusType.eNetworkaAbort -> {
                    handler.sendEmptyMessage(2)
                    println(KEY_Handler_Net_Error)
                }
                PlayStatusType.ePlayFailed -> {
                    handler.sendEmptyMessage(3)
                    println(KEY_Handler_Play_Failed)
                }
                PlayStatusType.eBadFile -> {
                    println(KEY_Handler_Bad_File)
                }
                PlayStatusType.eStatusUnknow -> {
                    println(KEY_Handler_Play_Unknow)
                }
                PlayStatusType.eSeekSuccess -> {
                    println(KEY_Handler_Seek_Success)
                }
                PlayStatusType.eSeekFailed -> {
                    println(KEY_Handler_Seek_failed)
                }
                PlayStatusType.eSeekCrossBorder -> {
                    println(KEY_Handler_Seek_Cross_Border)

                }
            }
        }

        override fun onPlayTime(winIndex: Int, time: Long) {}
        override fun onPlayerTimeAndStamp(
            winIndex: Int,
            time: Long,
            timeStamp: Long
        ) {
            super.onPlayerTimeAndStamp(winIndex, time, timeStamp)
        }
    }

    override fun initData() {
        viewModel.getCamarasBySennorCode(intent.getStringExtra("sensorCode"))
        dataAdapterInterface = DataAdapterImpl.getInstance()

        mPlayManager = PlayManagerProxy()
        mPlayManager.init(this, 1, 1, play_window)
        mPlayManager.setOnMediaPlayListener(iMediaPlayListener)

        startTime =
            TimeUtil.formatStringToDate(
                intent.getStringExtra("startTime"),
                TimeUtil.YMDHMS
            )!!.time / 1000L
        endTime =
            TimeUtil.formatStringToDate(
                intent.getStringExtra("endTime"),
                TimeUtil.YMDHMS
            )!!.time / 1000L

    }

    private fun recordToDpsRecord() {
        dpsRecordFiles.clear()
        for (recordInfo in recordInfos!!) {
            val dpsRecordFile = DPSRecordFile()
            dpsRecordFile.ssId = recordInfo.ssId
            dpsRecordFile.fileHandler = recordInfo.fileHandle
            dpsRecordFile.diskId = if (recordInfo.diskId == null) "" else recordInfo.diskId
            dpsRecordFile.fileName = recordInfo.fileName
            dpsRecordFile.recordSource = 3
            dpsRecordFile.beginTime = recordInfo.startTime.toInt()
            dpsRecordFile.endTime = recordInfo.endTime.toInt()
            dpsRecordFiles.add(dpsRecordFile)
        }
    }

    private suspend fun queryRecord(beginTime: Long, endTime: Long) {
        withContext(Dispatchers.IO)
        {
            try {
                recordInfos = dataAdapterInterface.queryRecord(
                    channelInfo.chnSncode,
                    recordResource,
                    RecordInfo.RecordEventType.All,
                    beginTime, endTime,
                    RecordInfo.StreamType.All_Type
                )
            } catch (e: BusinessException) {
                Looper.prepare()
                ViewUtil.toast(this@MediaBackActivity, "该时间段没有视频")
                Looper.loop()
            }

        }

    }

    private fun startPlayBack() {
        if (recordResource == RecordResource.Platform && (dpsRecordFiles == null
                    || dpsRecordFiles.size == 0)
        ) {
            return
        }
        val dpspbCameraParam = DPSPBCameraParam()
        dpspbCameraParam.cameraID = channelInfo.chnSncode
        try {
            dpspbCameraParam.dpHandle = dataAdapterInterface.dpsdkEntityHandle.toString()
        } catch (e: BusinessException) {
            e.printStackTrace()
        }
        val info = PlayBackInfo()
        info.setIsBack(false)
        if (mPlayManager != null) {
            info.needBeginTime =
                mPlayManager.getCurrentProgress(mPlayManager.selectedWindowIndex).toInt()
        }
        if (recordResource == RecordResource.Device) {
//            info.isPlayBackByTime = true
//            info.beginTime = mRecordStartTime as Int
//            info.endTime = mRecordEndTime as Int
        } else {
            info.isPlayBackByTime = false
            info.recordFileList = dpsRecordFiles
        }
        dpspbCameraParam.playBackInfo = info
        val camera = DPSPBCamera(dpspbCameraParam)
        if (mPlayManager != null) {
            mPlayManager.addCamera(mPlayManager.selectedWindowIndex, camera)
            mPlayManager.playSingle(mPlayManager.selectedWindowIndex)
        }
    }

    private fun start() {
        launch {
            queryRecord(startTime, endTime)
            recordToDpsRecord()
            startPlayBack()
        }
    }

    private fun getChannel() {
        var list = dataAdapterInterface?.queryGroup(null)

        var mDeviceWithChannelList =
            dataAdapterInterface!!.getDeviceListByChannelList(list!![0].channelList)

        for (deviceWithChannelListBean in mDeviceWithChannelList!!.devWithChannelList) {
            if (deviceWithChannelListBean.deviceInfo.snCode.equals(camera.channelNumber)) {
                for (channelInfo in deviceWithChannelListBean.channelList) {
                    this.channelInfo = channelInfo
                }
            }
        }
        start()
    }

    override fun startObserve() {
        viewModel.cameraDatas.observe(this, Observer {
            if (it.size > 0) {
                camera = it[0]
                if (MMKV.defaultMMKV().containsKey("mediaUserInfo")) {
                    getChannel()
                } else {
                    try {
                        dataAdapterInterface!!.initServer(
                            camera.extraNetIp,
                            camera.extraNetPort!!.toInt()
                        )
                    } catch (e: BusinessException) {
                        e.printStackTrace()
                    }

                    viewModel.login(
                        this,
                        dataAdapterInterface,
                        camera.cameraUser,
                        camera.cameraPwd
                    )
                }
            } else {
                ViewUtil.toast(this, "该地区没有摄像机")
            }

        })
        viewModel.isSuccess.observe(this, Observer {
            if (it != null) {

//                ViewUtil.toast(this, "登陆成功")

                MMKV.defaultMMKV().putString("mediaUserInfo", Gson().toJson(it))
                getChannel()

            } else {
                ViewUtil.toast(this, "登陆失败")
            }
        })
    }
}