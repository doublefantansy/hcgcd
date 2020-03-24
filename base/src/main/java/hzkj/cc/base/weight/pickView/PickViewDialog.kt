package hzkj.cc.base.weight.pickView

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.weight.CcTextView

/**

 * @Author chencheng
 * @Date 2020-02-27-09:54
 */
class PickViewDialog(context: Context, theme: Int = R.style.myDialogStyle) :
    Dialog(context, theme) {

    private var pickLayout: PickerView
    private var cancel: CcTextView
    private var submit: CcTextView
    private lateinit var chooseListener: (Int, String) -> Unit
    private lateinit var array: ArrayList<String>

    init {

        var view = layoutInflater.inflate(R.layout.custom_picker, null)
        pickLayout = view.findViewById(R.id.pickLayout)
        cancel = view.findViewById(R.id.cancel)
        submit = view.findViewById(R.id.choose)
        cancel.setOnClickListener { this.dismiss() }
        submit.setOnClickListener {
            chooseListener.invoke(
                (pickLayout.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition(),
                array[(pickLayout.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()]
            )
            this.dismiss()
        }
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(view)
    }

    override fun show() {
        super.show()

        val lp: WindowManager.LayoutParams = getWindow().getAttributes()
        window.setGravity(Gravity.BOTTOM)
        lp.width = ViewUtil.dipToPx(context, 360).toInt() //设置宽度
        lp.height = ViewUtil.dipToPx(context, 250).toInt() //设置宽度
        window.setWindowAnimations(R.style.myStyle)
        window.attributes = lp
    }

    fun setChooseListener(listener: (p: Int, str: String) -> Unit): PickViewDialog {
        this.chooseListener = listener
        return this
    }

    fun setArray(array: ArrayList<String>, index: Int): PickViewDialog {
        this.array = array
        pickLayout.setData(array, index)
        return this
    }
}