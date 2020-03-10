package hzkj.cc.base.weight.pickView

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.weight.CcTextView
import java.util.Calendar
import java.util.Date

/**

 * @Author chencheng
 * @Date 2020-02-29-14:42
 */
class TimePicker(context: Context, theme: Int = R.style.myDialogStyle) : Dialog(context, theme) {

    private var yearPickLayout: PickerView
    private var monthPickLayout: PickerView
    private var dayLayout: PickerView
    private var cancel: CcTextView
    private var choose: CcTextView
    private var yearList: ArrayList<String> = arrayListOf()
    private var monthList: ArrayList<String> = arrayListOf()
    private var dayList: ArrayList<String> = arrayListOf()
    var chooseListenner: ((y: String, m: String, d: String) -> Unit)? = null

    init {

        setCancelable(false)
        setCanceledOnTouchOutside(false)
        var view = layoutInflater.inflate(R.layout.time_picker, null)
        yearPickLayout = view.findViewById(R.id.yearPickLayout)
        monthPickLayout = view.findViewById(R.id.monthPickLayout)
        dayLayout = view.findViewById(R.id.dayPickLayout)
        for (i in 4 downTo 0) {
            yearList.add((Calendar.getInstance().get(Calendar.YEAR) - i).toString())
        }
        cancel = view.findViewById(R.id.cancel)
        choose = view.findViewById(R.id.choose)
        cancel.setOnClickListener { dismiss() }
        choose.setOnClickListener {
            dismiss()
            chooseListenner?.invoke(
                yearList[(yearPickLayout.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()],
                monthList[(monthPickLayout.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()],
                dayList[(dayLayout.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()]
            )
        }
        setContentView(view)
    }

    fun setDate(date: Date): TimePicker {

        var calendar = Calendar.getInstance().apply {
            time = date
        }

        for (i in 1..12) {
            monthList.add(i.toString())
        }
        for (i in 1..31) {
            dayList.add(i.toString())
        }
        dayLayout.setData(dayList, dayList.indexOf(calendar.get(Calendar.DAY_OF_MONTH).toString()))
        monthPickLayout.setData(
            monthList,
            monthList.indexOf((calendar.get(Calendar.MONTH) + 1).toString())
        )
        yearPickLayout.setData(yearList, yearList.indexOf(calendar.get(Calendar.YEAR).toString()))
        return this
    }

    fun setChooseListenner(chooseListenner: (y: String, m: String, d: String) -> Unit): TimePicker {
        this.chooseListenner = chooseListenner
        return this
    }

    override fun show() {
        super.show()
        val windowManager: WindowManager = getWindow().getWindowManager()
        val lp: LayoutParams = getWindow().getAttributes()
        window.setGravity(Gravity.BOTTOM)
        lp.width = ViewUtil.dipToPx(context, 360).toInt() //设置宽度
        lp.height = ViewUtil.dipToPx(context, 250).toInt() //设置宽度
        window.setWindowAnimations(R.style.myStyle)
        getWindow().setAttributes(lp)
    }
}