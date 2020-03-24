package hzkj.cc.base.weight.pickView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import hzkj.cc.base.R
import hzkj.cc.base.weight.CcTextView

/**

 * @Author chencheng
 * @Date 2020-02-27-08:48
 */
class PickLayout(context: Context, attributeSet: AttributeSet?) :
    LinearLayout(context, attributeSet) {

    var pickerView: PickerView
    private var cancelButton: CcTextView
    private var chooseButton: CcTextView
    lateinit var cancelListener: () -> Unit
    lateinit var chooseListener: (Int) -> Unit

    init {
        var view = LayoutInflater.from(context)
            .inflate(R.layout.base_pick_layout, null)
        pickerView = view.findViewById(R.id.pickview)
        cancelButton = view.findViewById(R.id.cancel)
        chooseButton = view.findViewById(R.id.choose)
        cancelButton.setOnClickListener { cancelListener.invoke() }
        chooseButton.setOnClickListener {
            chooseListener.invoke((pickerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
        }
        addView(view)

    }

    fun setArray(array: ArrayList<String>, index: Int) {
        pickerView.setData(array, index)
    }
}