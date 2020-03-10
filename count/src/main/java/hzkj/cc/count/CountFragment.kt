package hzkj.cc.count

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import hzkj.cc.base.TimeUtil
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_count.*
import java.util.*

/**

 * @Author chencheng
 * @Date 2020-02-28-11:07
 */
@Route(path = "/count/fragment")
class CountFragment : BaseFragment<CountViewModel>() {

    var areaName: String? = null
    var areaCode: String? = null

    private var areaBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            areaName = intent!!.extras.getString("areaName")
            areaCode = intent!!.extras.getString("areaCode")
            textview_area.text = areaName
            textview_total_ship.text = ""
            textview_in_ship.text = ""
            textview_out_ship.text = ""
            viewModel.getCountDatas(
                startTime.text.toString(), "${endTime.text} 23:59:59", areaCode!!
            )
        }
    }
    private var timeBroadCast = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            startTime.text = intent!!.extras.getString("startTime")
            endTime.text = intent!!.extras.getString("endTime")
            textview_area.text = areaName
            textview_total_ship.text = ""
            textview_in_ship.text = ""
            textview_out_ship.text = ""
            viewModel.getCountDatas(
                startTime.text.toString(), "${endTime.text} 23:59:59", areaCode!!
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(areaBroadCast)
        activity!!.unregisterReceiver(timeBroadCast)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_count
    }

    override fun initView() {
        startTime.text = TimeUtil.getFirstDayInMonth(Calendar.getInstance(), TimeUtil.YMD)
        endTime.text = TimeUtil.dateToFormatString(Calendar.getInstance().time, TimeUtil.YMD)
    }

    override fun initData() {
        activity!!.registerReceiver(areaBroadCast, IntentFilter().apply { addAction("count") })
        activity!!.registerReceiver(timeBroadCast, IntentFilter().apply { addAction("countTime") })
    }

    override fun updateError(it: Int?) {
        ViewUtil.toast(
            activity as Context, when (it) {
                CountViewModel.COUNT -> "获取统计信息失败"
                else -> ""
            }
        )
        super.updateError(it)
    }

    override fun startObserve() {
        viewModel.countDatas.observe(this, Observer {
            activity?.sendBroadcast(Intent().apply {
                action = "main"
                putExtra("areaCode", areaCode)
                putExtra("from", "count")
            })
            textview_total_ship.text = it[0].percentAge.toString()
            textview_in_ship.text = it[1].percentAge.toString()
            textview_out_ship.text = it[2].percentAge.toString()
        })
    }

    override fun onShow() {

    }

    override fun onHide() {

    }
}