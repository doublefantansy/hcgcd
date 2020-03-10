package cc.hc.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.android.business.adapter.DataAdapterImpl
import com.android.business.adapter.DataAdapterInterface
import com.android.business.adapter.DeviceWithChannelList
import com.android.business.entity.ChannelInfo
import com.android.business.entity.ChannelInfo.ChannelStreamType
import com.android.business.entity.GroupInfo
import com.android.business.exception.BusinessException
import com.android.dahua.dhplaycomponent.IMediaPlayListener
import com.android.dahua.dhplaycomponent.PlayManagerProxy
import com.android.dahua.dhplaycomponent.camera.RTCamera.DPSRTCamera
import com.android.dahua.dhplaycomponent.camera.RTCamera.DPSRTCameraParam
import com.android.dahua.dhplaycomponent.camera.inner.Camera
import com.android.dahua.dhplaycomponent.camera.inner.RealInfo
import com.android.dahua.dhplaycomponent.common.PlayStatusType
import com.android.dahua.dhplaycomponent.common.PlayStatusType.*
import com.google.gson.Gson
import com.tencent.mmkv.MMKV
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_media.*

/**

 * @Author chencheng
 * @Date 2020-03-02-14:53
 */
@Route(path = "/media/main")
class MediaFragment : BaseFragment<MediaViewModel>() {
    companion object {

    }

    //    var userInfo: UserInfo? = null
    var areaCode: String? = null
    var areaName: String? = null

    private lateinit var camera: cc.hc.media.Camera
    var mPlayManager: PlayManagerProxy? = null
    private var mDeviceWithChannelList: DeviceWithChannelList? = null
    private var list: MutableList<GroupInfo>? = null
    private var channelInfos: MutableList<ChannelInfo> = mutableListOf()

    //    private lateinit var mReceiver: DeviceBroadcastReceiver
    private var dataAdapterInterface: DataAdapterInterface? = null

    private var mediaBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            areaCode = intent!!.getStringExtra("areaCode")
            areaName = intent!!.getStringExtra("areaName")
            viewModel.getCameras(areaCode!!)
        }
    }

    var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg?.what) {
                0 -> {
                    mPlayManager?.stopAll()
                    ViewUtil.toast(activity!!, "网络异常")
                }
                1 -> {
                    mPlayManager?.stopAll()
                    ViewUtil.toast(activity!!, "播放失败")
                }
            }

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_media
    }

    override fun initView() {
    }

    override fun initData() {
        dataAdapterInterface = DataAdapterImpl.getInstance()
        activity!!.registerReceiver(mediaBroadCast, IntentFilter().apply {
            addAction("media")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(mediaBroadCast)
        dataAdapterInterface?.logout()
        mPlayManager?.unitPlayManager()
        mPlayManager = null
        MMKV.defaultMMKV().remove("mediaUserInfo")
    }

    override fun updateError(it: Int?) {
        super.updateError(it)
        ViewUtil.toast(activity as Context, "获取摄像机信息失败")
    }

    override fun onShow() {
        println(MMKV.defaultMMKV().containsKey("mediaUserInfo"))
        if (mPlayManager == null) {
            areaCode?.run {
                viewModel.getCameras(this)
            }
        } else {
            replay()
        }
    }

    private val iMediaPlayListener: IMediaPlayListener = object : IMediaPlayListener() {
        override fun onPlayeStatusCallback(winIndex: Int, type: PlayStatusType, code: Int) {
            Log.d(
                "ccnb",
                "onPlayeStatusCallback:$type winIndex: $winIndex"
            )
            val msg = Message.obtain()
            msg.obj = winIndex
            if (type == eStreamPlayed) {
            } else if (type == ePlayFirstFrame) {
            } else if (type == eNetworkaAbort) {
                handler.sendEmptyMessage(0)
            } else if (type == ePlayFailed) {
                handler.sendEmptyMessage(1)

            }
        }
    }

    private fun getCamera(channelInfo: ChannelInfo): Camera {
        val dpsrtCameraParam = DPSRTCameraParam()
        dpsrtCameraParam.cameraID = channelInfo.chnSncode
        try {
            dpsrtCameraParam.dpHandle = dataAdapterInterface!!.dpsdkEntityHandle.toString()
        } catch (e: BusinessException) {
            e.printStackTrace()
        }
        val realInfo = RealInfo()
        var mStreamType = ChannelStreamType.getValue(channelInfo.streamType)
        if (mStreamType > 2) mStreamType = 2
        realInfo.streamType = mStreamType
        realInfo.mediaType = 3
        realInfo.trackID = "601"
        realInfo.startChannelIndex = 0
        realInfo.separateNum = "1"
        realInfo.isCheckPermission = true
        dpsrtCameraParam.realInfo = realInfo
        var c = DPSRTCamera(dpsrtCameraParam)
        return c
    }

    private fun replay() {
        mPlayManager?.playCurpage()
    }

    private fun stopAll() {
        mPlayManager?.stopAll()
    }

    override fun onResume() {
        super.onResume()
        replay()
    }

    override fun onPause() {
        super.onPause()
        stopAll()
    }

    override fun startObserve() {
        viewModel.cameraDatas.observe(this, Observer {

            if (it.size > 0) {
                textview_areaName.text = areaName
                camera = it[0]
                if (MMKV.defaultMMKV().containsKey("mediaUserInfo")) {
                    init()
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
                        activity as Context,
                        dataAdapterInterface,
                        camera.cameraUser,
                        camera.cameraPwd
                    )
                }
//                dataAdapterInterface?.logout()
//                MMKV.defaultMMKV().remove("mediaUserInfo")

            } else {
                ViewUtil.toast(activity as Context, "该地区没有摄像机")
            }
        })
        viewModel.isSuccess.observe(this, Observer {
            if (it != null) {
                MMKV.defaultMMKV().putString("mediaUserInfo", Gson().toJson(it))
                activity?.sendBroadcast(Intent().apply {
                    action = "main"
                    putExtra("areaCode", areaCode)
                    putExtra("from", "media")
                })
//                ViewUtil.toast(activity as Context, "登陆成功")
                init()
            } else {
                ViewUtil.toast(activity as Context, "登陆失败")
            }
        })
    }

    private fun init() {
        channelInfos.clear()
        list = dataAdapterInterface?.queryGroup(null)
        mDeviceWithChannelList =
            dataAdapterInterface!!.getDeviceListByChannelList(list!![0].channelList)
        for (deviceWithChannelListBean in mDeviceWithChannelList!!.devWithChannelList) {
            if (deviceWithChannelListBean.deviceInfo.snCode.equals(camera.channelNumber)) {
                for (channelInfo in deviceWithChannelListBean.channelList) {
                    channelInfos!!.add(channelInfo)
                }
            }
        }
        mPlayManager = PlayManagerProxy()
        mPlayManager?.init(activity as Context, 4, 1, play_window)
        mPlayManager?.setOnMediaPlayListener(iMediaPlayListener)
        val metric = DisplayMetrics()
        activity!!.windowManager.defaultDisplay
            .getMetrics(metric)
        val mScreenWidth = metric.widthPixels // 屏幕宽度（像素）
        var mScreenHeight = metric.heightPixels // 屏幕高度（像素）
        mScreenHeight = mScreenWidth * 3 / 4
        val lp = play_window.layoutParams as LinearLayout.LayoutParams
        lp.width = mScreenWidth
        lp.height = mScreenHeight
        play_window.layoutParams = lp
        play_window.forceLayout(mScreenWidth, mScreenHeight)
        mPlayManager?.addCamera(0, getCamera(channelInfos!!.get(0)))
        mPlayManager?.playCurpage()
    }

    override fun onHide() {
        mPlayManager?.stopAll()
    }
}