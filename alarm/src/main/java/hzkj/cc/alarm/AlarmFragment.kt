package hzkj.cc.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import hzkj.cc.base.ImgUtil
import hzkj.cc.base.TimeUtil
import hzkj.cc.base.base.BaseFragment
import hzkj.cc.base.base.CommonAdapter
import hzkj.cc.base.base.Convert
import hzkj.cc.base.weight.CcTextView
import hzkj.cc.base.weight.refreshLayout.LoadingListenner
import hzkj.cc.base.weight.refreshLayout.RefreshListener
import hzkj.cc.base.weight.refreshLayout.ToastView
import kotlinx.android.synthetic.main.fragment_alram.*
import java.util.*

/**

 * @Author chencheng
 * @Date 2020-03-04-13:46
 */
@Route(path = "/alarm/main")
class AlarmFragment : BaseFragment<AlarmViewModel>() {

    private var alarmTimeBroadReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            startTime = intent!!.extras.getString("startTime")
            endTime = intent!!.extras.getString("endTime")
            recyclerview.startRefresh()
            pageNum = 1
            viewModel.getAlarmDatas(
                areaCode!!, startTime!!, "$endTime 23:59:59"
                , pageNum
            )
        }
    }
    private var imgSrc: String? = null
    var pageNum = 1
    var id: String? = null
    var startTime: String? = null

    var endTime: String? = null
    private var areaName: String? = null
    private var areaCode: String? = null
    private var datas = mutableListOf<SensorAlarmInfo>()
    lateinit var adapter: CommonAdapter<SensorAlarmInfo>
    private var alarmBroadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
//            println(hasAdd)
            areaName = intent!!.extras.getString("areaName")
            areaCode = intent!!.extras.getString("areaCode")

//            if (hasAdd) {
            recyclerview.startRefresh()
            pageNum = 1
            viewModel.getAlarmDatas(
                areaCode!!, startTime!!, endTime!!
                , pageNum
            )
//            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_alram
    }

    override fun initView() {
        adapter = CommonAdapter(activity as Context, R.layout.recyclerview_alarm_item, datas)
        recyclerview.initAdapter(
            adapter
        )
        recyclerview.refreshListener = object : RefreshListener {
            override fun refresh() {
                pageNum = 1
                viewModel.getAlarmDatas(areaCode!!, startTime!!, endTime!!, pageNum)
            }
        }
        recyclerview.loadingListenner = object : LoadingListenner {
            override fun loadMore() {
                areaCode?.run {
                    viewModel.getAlarmDatas(this, startTime!!, endTime!!, ++pageNum)
                }

            }
        }
        adapter.recyclerListener = {
            //            it.getView<ImageView>(R.id.pic).setImageResource(R.drawable.load_image_placeholder)
        }
        adapter.listener =
            object : Convert<SensorAlarmInfo> {
                override fun convert(
                    holder: CommonAdapter.BaseHolder,
                    position: Int,
                    data: SensorAlarmInfo
                ) {

                    with(holder) {
                        itemView.setOnClickListener {
                            data.alarmDateSlot?.split("_")
                                .let {
                                    ARouter.getInstance()
                                        .build("/media/back")
                                        .withString("sensorCode", data.sensorCode)
                                        .withString("startTime", it?.get(0))
                                        .withString("endTime", it?.get(1))
                                        .navigation()
                                }

                        }
                        getView<CcTextView>(R.id.outShipNumber)
                            .text = "${data.out}艘"
                        getView<CcTextView>(R.id.inShipNumber)
                            .text = "${data.getInfo}艘"
                        getView<CcTextView>(R.id.date)
                            .text = TimeUtil.dateToFormatString(data.alarmDate)
//                        getView<ImageView>(R.id.pic).setImageResource(
//                            R.drawable.load_image_placeholder
//                        )
                        ImgUtil.createImgByBase64(
                            activity as Context, data.alarmImg!!, getView(R.id.pic)
                        )
//                        getView<ImageView>(R.id.pic).setTag(R.id.item_tag, position)
//                        viewModel.searchAlarmImgById(position, data.id, getView(R.id.pic))
                    }
                }
            }
    }

    override fun initData() {
        activity?.registerReceiver(
            alarmTimeBroadReceiver, IntentFilter().apply { addAction("alarmTime") })
        startTime = TimeUtil.getFirstDayInMonth(Calendar.getInstance())!!
        endTime = TimeUtil.dateToFormatString(Calendar.getInstance().time, TimeUtil.YMDHMS)!!
        activity?.registerReceiver(
            alarmBroadCastReceiver,
            IntentFilter().apply { addAction("alarm") })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(alarmBroadCastReceiver)
        activity?.unregisterReceiver(alarmTimeBroadReceiver)
    }

    override fun onShow() {

    }

    override fun updateError(it: Any) {
        super.updateError(it)
        when (it) {
            is Int -> {
                recyclerview.toast(
                    ToastView.NETERROR,
                    resources.getColor(hzkj.cc.base.R.color.base_red)
                )
                if (pageNum == 1) {
                    recyclerview.closeRefresh()
                    recyclerview.showNetErrorState()
                } else {
                    recyclerview.closeLoad()
                }
            }
            is ImageView -> {
                it.setImageResource(R.drawable.load_image_fail)
            }
        }
    }

    override fun onHide() {
    }

    override fun startObserve() {
        viewModel.alarmDatas.observe(this, Observer {
            textview_areaName.text = areaName
            activity?.sendBroadcast(Intent().apply {
                action = "main"
                putExtra("areaCode", areaCode)
                putExtra("from", "alarm")
            })
            if (pageNum == 1) {
                datas.clear()
                datas.addAll(it)
                recyclerview.refreshComplete()
            } else {
                datas.addAll(it)
                recyclerview.loadComplete(
                    if (it.size == 0) ToastView.LOADEMPTY else ToastView.HASLOAD, it.size
                )
            }
        })
        viewModel.imageView.observe(this, Observer {
            if (((it.first.getTag(R.id.item_tag)) as Int).toString() == it.second) {
                ImgUtil.createImgByBase64(
                    activity as Context, it.third!!, it.first
                )
            }
        })
    }
}