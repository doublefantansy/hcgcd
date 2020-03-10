package hzkj.cc.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import hzkj.cc.base.NotifyDialog
import hzkj.cc.base.TimeUtil
import hzkj.cc.base.base.BaseActivity
import hzkj.cc.base.weight.pickView.PickViewDialog
import hzkj.cc.base.weight.pickView.TimePicker
import hzkj.cc.mybottomnavigation.BottomChild
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**

 * @Author chencheng
 * @Date 2020-02-27-16:22
 */
@Route(path = "/main/main")
class MainActivity(
    override var statusBarColor: Int = R.color.base_blue,
    override var isStatusBarDark: Boolean = false,
    override var isImmerse: Boolean = false
) : BaseActivity<MainViewModel>() {

    lateinit var defalutStartTime: String
    lateinit var defalutEndTime: String
    var areas = mutableListOf<Area>()
    private lateinit var bottom_navigation_array: MutableList<BottomChild>

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    var mediaSelectAreaDialogIndex = 0
    var mapSelectAreaDialogIndex = 0
    var countSelectAreaDialogIndex = 0
    var alarmSelectAreaDialogIndex = 0

    private var mainBroadCast = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            areas.map { it.areaCode }
                .indexOf(intent?.getStringExtra("areaCode"))
                .apply {
                    when (intent?.getStringExtra("from")) {
                        "media" -> {
                            mediaSelectAreaDialogIndex = this
                        }
                        "map" -> {
                            mapSelectAreaDialogIndex = this
                        }
                        "alarm" -> {
                            alarmSelectAreaDialogIndex = this
                        }
                        "count" -> {
                            countSelectAreaDialogIndex = this
                        }
                    }
                }
        }
    }
    private var changePasswordBroadCast = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            finish()
        }
    }

    private fun showAreaDialog(index: Int, action: String) {
        PickViewDialog(this@MainActivity).setArray(areas.map {
            it.areaName
        } as ArrayList<String>, index)
            .setChooseListener { p: Int, str: String ->
                sendBroadcast(Intent().apply {
                    putExtra("areaName", areas[p].areaName)
                    putExtra("areaCode", areas[p].areaCode)
                    this.action = action
                })
            }
            .show()
    }

    override fun initView() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        bottom_navigation_array = mutableListOf(
            BottomChild(
                "告警",
                (ARouter.getInstance().build("/alarm/main").navigation()) as Fragment,
                getDrawable(R.drawable.alarm_icon_unselected),
                getDrawable(R.drawable.alarm_icon_selected)
            ), BottomChild(
                "地图",
                (ARouter.getInstance().build("/map/main").navigation()) as Fragment,
                getDrawable(R.drawable.map_icon_unselected),
                getDrawable(R.drawable.map_icon_selected)
            ), BottomChild(
                "统计",
                (ARouter.getInstance().build("/count/fragment").navigation()) as Fragment,
                getDrawable(R.drawable.count_icon_unselected),
                getDrawable(R.drawable.count_icon_selected)
            ),
            BottomChild(
                "实时视频",
                (ARouter.getInstance().build("/media/main").navigation()) as Fragment,
                getDrawable(R.drawable.media_icon_unselected),
                getDrawable(R.drawable.media_icon_selected)
            ),
            BottomChild(
                "个人中心",
                (ARouter.getInstance().build("/my/my_center").navigation()) as Fragment,
                getDrawable(R.drawable.my_center_icon_unselected),
                getDrawable(R.drawable.my_center_icon_selected)
            )
        )

        bottomNavigation.initBottomChildren(
            supportFragmentManager,
            bottom_navigation_array,
            0
        )
        bottomNavigation.setOnClickBottomChildListener { bottomChild, position ->
            when (position) {

                0 -> {
                    indulgeTitle.rightImageView.visibility = View.VISIBLE
                    indulgeTitle.textView.text = "告警信息"
                    indulgeTitle.leftImageView.visibility = View.VISIBLE
                    indulgeTitle.visibility = View.VISIBLE
                    indulgeTitle.leftImageClickListenner = {
                        showAreaDialog(alarmSelectAreaDialogIndex, "alarm")
                    }
                    indulgeTitle.rightImageClickListenner = {
                        drawer.openDrawer(Gravity.RIGHT)
                    }
                    indulgeTitle.setRightDrawable(getDrawable(R.drawable.more))
                    indulgeTitle.setLeftDrawable(getDrawable(R.drawable.area))
                }
                1 -> {
                    indulgeTitle.textView.text = "地图"
                    indulgeTitle.visibility = View.VISIBLE
                    indulgeTitle.rightImageView.visibility = View.VISIBLE
                    indulgeTitle.leftImageView.visibility = View.GONE
                    indulgeTitle.rightImageClickListenner = {
                        showAreaDialog(mapSelectAreaDialogIndex, "map")
                    }
                    indulgeTitle.setRightDrawable(getDrawable(R.drawable.area))
                }
                2 -> {
                    indulgeTitle.visibility = View.VISIBLE
                    indulgeTitle.textView.text = "统计信息"
                    indulgeTitle.rightImageView.visibility = View.VISIBLE
                    indulgeTitle.leftImageView.visibility = View.VISIBLE
                    indulgeTitle.rightImageClickListenner = {
                        drawer.openDrawer(Gravity.RIGHT)
                    }
                    indulgeTitle.leftImageClickListenner = {
                        showAreaDialog(countSelectAreaDialogIndex, "count")

                    }
                    indulgeTitle.setRightDrawable(getDrawable(R.drawable.more))
                    indulgeTitle.setLeftDrawable(getDrawable(R.drawable.area))
                }
                3 -> {
                    indulgeTitle.visibility = View.VISIBLE
                    indulgeTitle.setRightDrawable(getDrawable(R.drawable.area))
                    indulgeTitle.rightImageView.visibility = View.VISIBLE
                    indulgeTitle.rightImageClickListenner = {
                        showAreaDialog(mediaSelectAreaDialogIndex, "media")
                    }
                    indulgeTitle.textView.text = "实时视频"
                    indulgeTitle.leftImageView.visibility = View.GONE
                }
                4 -> {
                    indulgeTitle.visibility = View.VISIBLE
                    indulgeTitle.rightImageView.visibility = View.GONE
                    indulgeTitle.textView.text = "个人中心"
                    indulgeTitle.leftImageView.visibility = View.GONE
                }
            }
        }
        defalutStartTime =
            TimeUtil.getFirstDayInMonth(Calendar.getInstance(), TimeUtil.YMD)!!.also {
                startTimeShow.text = it
            }
        defalutEndTime =
            TimeUtil.dateToFormatString(Calendar.getInstance().time, TimeUtil.YMD)!!.also {
                endTimeShow.text = it
            }
        startTimeChoose.setOnClickListener {
            TimePicker(this).setDate(
                TimeUtil.formatStringToDate(startTimeShow.text.toString(), TimeUtil.YMD)!!
            )
                .setChooseListenner { y, m, d ->
                    startTimeShow.text = TimeUtil.formatTimeStr("$y-$m-$d")
                }
                .show()
        }
        endTimeChoose.setOnClickListener {
            TimePicker(this).setDate(
                TimeUtil.formatStringToDate(endTimeShow.text.toString(), TimeUtil.YMD)!!
            )
                .setChooseListenner { y, m, d ->
                    endTimeShow.text = TimeUtil.formatTimeStr("$y-$m-$d")
                }
                .show()
        }
        reset.setOnClickListener {
            startTimeShow.text = defalutStartTime
            endTimeShow.text = defalutEndTime
        }
        submit.setOnClickListener {
            sendBroadcast(Intent().apply {
                putExtra("startTime", startTimeShow.text.toString())
                putExtra("endTime", endTimeShow.text.toString())
                action = when (bottomNavigation.currentIndex) {
                    2 -> "countTime"
                    0 -> "alarmTime"
                    else -> ""
                }
            })
            drawer.closeDrawer(Gravity.RIGHT)
        }
        indulgeTitle.leftImageClickListenner = {
            PickViewDialog(this@MainActivity).setArray(areas.map {
                it.areaName
            } as ArrayList<String>, alarmSelectAreaDialogIndex)
                .setChooseListener { p: Int, str: String ->
                    sendBroadcast(Intent().apply {
                        putExtra("areaName", areas[p].areaName)
                        putExtra("areaCode", areas[p].areaCode)
                        action = "alarm"
                    })
                }
                .show()
        }
        indulgeTitle.rightImageClickListenner = {
            drawer.openDrawer(Gravity.RIGHT)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mainBroadCast)
        unregisterReceiver(changePasswordBroadCast)

    }

    override fun updateError(it: Int?) {
        super.updateError(it)
//        ViewUtil.toast(this, "获取采区信息失败")
        NotifyDialog.Builder(this)
            .canCancel(false)
            .canOutsideCancel(false)
            .image(hzkj.cc.base.R.drawable.base_token_timeout_dialog_error_icon)
            .dismissListenner {
                viewModel.getAreas()
            }
            .text("获取采区信息失败,点击重新获取")
            .build()
            .show()
    }

    override fun initData() {
        viewModel.getAreas()
        registerReceiver(mainBroadCast, IntentFilter().apply { addAction("main") })
        registerReceiver(
            changePasswordBroadCast,
            IntentFilter().apply { addAction("changePassword") })
    }

    override fun startObserve() {
        viewModel.areaDatas.observe(this, Observer {
            areas.run {
                clear()
                areas.addAll(it)
                sendBroadcast(Intent().apply {
                    putExtra("areaName", areas[0].areaName)
                    putExtra("areaCode", areas[0].areaCode)
                    action = "alarm"
                })
                sendBroadcast(Intent().apply {
                    putExtra("areaName", areas[0].areaName)
                    putExtra("areaCode", areas[0].areaCode)
                    action = "count"
                })
                sendBroadcast(Intent().apply {
                    putExtra("areaName", areas[0].areaName)
                    putExtra("areaCode", areas[0].areaCode)
                    action = "map"
                })
                sendBroadcast(Intent().apply {
                    putExtra("areaName", areas[0].areaName)
                    putExtra("areaCode", areas[0].areaCode)
                    action = "media"
                })
            }
        })
    }
}