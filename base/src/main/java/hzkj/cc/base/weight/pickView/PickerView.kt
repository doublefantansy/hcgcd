package hzkj.cc.base.weight.pickView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil

/**
 *
 * Created by lilifeng on 2019/8/30
 *
 * Copyright github.com/limxing
 *
 */
class PickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {

    private var numberCell: Int
    private val paint = Paint()
    private var textSize: Float
    private var lineHeight: Float
    private var lineColor: Int
    private var textColor: Int
    private var sizeWidth: Int = 0
    private var sizeHeight: Int = 0
    private var cellHeight: Int = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PickerView)
        textSize = typedArray.getDimension(R.styleable.PickerView_pv_text_size, 17f)
        textColor = typedArray.getColor(R.styleable.PickerView_pv_text_color, Color.parseColor("#333333"))
        lineHeight = typedArray.getDimension(R.styleable.PickerView_pv_line_height, 3f)
        lineColor = typedArray.getColor(R.styleable.PickerView_pv_line_color, Color.GRAY)
        numberCell = typedArray.getInt(R.styleable.PickerView_pv_number, 5)
        typedArray.recycle()
        layoutManager = object : LinearLayoutManager(context) {
            override fun scrollVerticallyBy(dy: Int, recycler: Recycler?, state: State?): Int {
                updateChildren()
                return super.scrollVerticallyBy(dy, recycler, state)
            }
        }

        adapter = ThisAdapter(this, ViewUtil.px2sp(context, textSize), textColor)
        LinearSnapHelper().attachToRecyclerView(this)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    private fun updateChildren() {
        for (i in 0 until childCount) {
            refreshItemTranslate(getChildAt(i))
        }
    }

    private fun refreshItemTranslate(childAt: View?) {
        val cellH = sizeHeight / numberCell / 1f
        childAt?.let {
            var scale: Float = (it.top + cellH) / sizeHeight + 0.4f
            if (scale > 1f) scale -= 2f
            if (scale < 0f) scale = -scale
            it.scaleX = scale
//            it.alpha = scale
            it.scaleY = scale
        }
    }


    fun setData(array: ArrayList<String>, index: Int) {
        (adapter as ThisAdapter).setData(array)
        layoutManager?.scrollToPosition(index)
        post {
            updateChildren()
        }
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        paint.strokeWidth = lineHeight
        paint.color = lineColor
        c?.drawLine(0f, cellHeight * 2f, sizeWidth.toFloat(), cellHeight * 2f, paint)
        c?.drawLine(0f, sizeHeight - cellHeight * 2f, sizeWidth.toFloat(), sizeHeight - cellHeight * 2f, paint)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        sizeWidth = MeasureSpec.getSize(widthSpec)
        sizeHeight = MeasureSpec.getSize(heightSpec)
        cellHeight = sizeHeight / numberCell
    }

    private class ThisAdapter(val pickerView: PickerView, val textSize: Int, val textColor: Int) :
        Adapter<ViewHolder>() {

        var array = ArrayList<String>()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context)
            tv.layoutParams =
                RecyclerView.LayoutParams(pickerView.sizeWidth, pickerView.sizeHeight / pickerView.numberCell)
            tv.gravity = Gravity.CENTER
            tv.textSize = textSize.toFloat()
            tv.setTextColor(textColor)
            tv.paint.isFakeBoldText = true
            return object : ViewHolder(tv) {}
        }

        override fun getItemCount(): Int {
            if (array.isEmpty()) return 0
            return array.size + pickerView.numberCell - 1
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                position < pickerView.numberCell / 2 -> 0
                position > array.size + pickerView.numberCell / 2 - 1 -> 0
                else -> 1
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (getItemViewType(position) == 1) (holder.itemView as TextView).text = array[position - 2]
            else {
            }
        }

        fun setData(array: ArrayList<String>) {
            this.array = array
            notifyDataSetChanged()
        }
    }
}