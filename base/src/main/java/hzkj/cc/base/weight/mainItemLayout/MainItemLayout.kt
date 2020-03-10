package hzkj.cc.base.weight.mainItemLayout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.weight.CcTextView

class MainItemLayout(
		context: Context,
		attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {
	lateinit var mainItems: MutableList<MainItem>
	var clickListenner: MainItemClickListenner? = null
	var totalHeight: Int = 0
	var totalWidth: Int = 0
	var paint = Paint().also {
		it.strokeWidth = ViewUtil.dipToPx(context, 2)
		it.color = resources.getColor(R.color.base_gray)
	}
	var countEachRow = 3

	init {
		setOnTouchListener(object : OnTouchListener {
			override fun onTouch(v: View?, event: MotionEvent?): Boolean {
				var x = ((event?.x)!! / (measuredWidth / countEachRow)).toInt()
				var y = countEachRow * ((event?.y)!! / (measuredWidth / countEachRow)).toInt()
				if (x + y < mainItems.size) {
					clickListenner?.callBack(mainItems.get(x + y)?.path)
				}

				return false
			}
		})
		setWillNotDraw(false)
	}

	fun init(mainItems: MutableList<MainItem>) {
		this.mainItems = mainItems
		for (mainItem in mainItems) {
			addView(ImageView(context).also {
				it.layoutParams =
						ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
				it.setImageDrawable(resources.getDrawable(mainItem.icon))
//				it.setBackgroundColor(Color.BLACK)
			})
			addView(CcTextView(context).also {
				it.layoutParams =
						ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
				it.text = mainItem.text
			})
		}
		requestLayout()
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		for (i in 0..if (mainItems.size > 2) countEachRow else mainItems.size) {
			canvas?.drawLine(((measuredWidth / countEachRow) * (i % countEachRow)).toFloat(), 0f, ((measuredWidth / countEachRow) * ((i % countEachRow) + 1)).toFloat(), 0f, paint)
//			canvas?.drawLine(((measuredWidth / countEachRow) * (i % countEachRow)).toFloat(), 0f, ((measuredWidth / countEachRow) * ((i % countEachRow) + 1)).toFloat(), 0f, textPaint)
//			canvas?.drawLine(((measuredWidth / countEachRow) * (i % countEachRow)).toFloat(), 0f, ((measuredWidth / countEachRow) * ((i % countEachRow) + 1)).toFloat(), 0f, textPaint)
		}
		for (i in 0..childCount / 2 / countEachRow) {
			canvas?.drawLine(0f, ((measuredWidth / countEachRow) * i).toFloat(), 0f, ((measuredWidth / countEachRow) * (i + 1)).toFloat(), paint)
//			canvas?.drawLine(0f, 0f, 0f, 0f, textPaint)
		}
		for (i in 0..childCount / 2 - 1) {
			canvas?.drawLine(((measuredWidth / countEachRow) * (i % countEachRow)).toFloat(), ((measuredWidth / countEachRow) * ((i / countEachRow) + 1)).toFloat(), ((measuredWidth / countEachRow) * ((i % countEachRow) + 1)).toFloat(), ((measuredWidth / countEachRow) * ((i / countEachRow) + 1)).toFloat(), paint)
//			if (i % countEachRow < 2) {
			canvas?.drawLine(((measuredWidth / countEachRow) * ((i % countEachRow) + 1)).toFloat(), ((measuredWidth / countEachRow) * ((i / countEachRow))).toFloat(), ((measuredWidth / countEachRow) * ((i % countEachRow) + 1)).toFloat(), ((measuredWidth / countEachRow) * ((i / countEachRow) + 1)).toFloat(), paint)
//			}
		}
	}

	override fun onMeasure(
			widthMeasureSpec: Int,
			heightMeasureSpec: Int
	) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        if (height > 0) {
		if (mainItems.size % countEachRow == 0) {
			totalHeight = ((((mainItems.size / countEachRow) * (measuredWidth / countEachRow)).toInt()))
		} else {
			totalHeight = (((((mainItems.size / countEachRow) + 1) * (measuredWidth / countEachRow)).toInt()))
		}
		setMeasuredDimension(measuredWidth, (totalHeight + ViewUtil.dipToPx(context, 1)).toInt())

		totalWidth = measuredWidth
		measureChildren(widthMeasureSpec, heightMeasureSpec)
	}

	override fun onLayout(
			changed: Boolean,
			left: Int,
			top: Int,
			right: Int,
			bottom: Int
	) {
		for (i in 0..childCount - 1) {
			if (i % 2 == 0) {
				getChildAt(i).layout(
						((totalWidth / countEachRow) * ((i / 2) % countEachRow) + (totalWidth / countEachRow) / 2 - getChildAt(
								i
						).measuredWidth / 2),
						((((((totalWidth / countEachRow) * (((i / 2) / countEachRow) + 1)) - (totalWidth / countEachRow / 2) - getChildAt(i).measuredHeight * 2 / 3)).toInt()).toInt())
						,
						((totalWidth / countEachRow) * ((i / 2) % countEachRow) + (totalWidth / countEachRow) / 2 + getChildAt(
								i
						).measuredWidth / 2).toInt(),
						((((((totalWidth / countEachRow) * (((i / 2) / countEachRow) + 1)) - (totalWidth / countEachRow / 2) + getChildAt(i).measuredHeight / 3)).toInt()).toInt()
								))
			} else {
				getChildAt(i).layout(
						(totalWidth / countEachRow) * ((i / 2) % countEachRow) + (totalWidth / countEachRow) / 2 - getChildAt(
								i
						).measuredWidth / 2,
						(((((totalWidth / countEachRow) * (((i / 2) / countEachRow) + 1)) - (totalWidth / countEachRow / 2) + getChildAt(i - 1).measuredHeight / 3) + ViewUtil.dipToPx(context, 15)).toInt())
						,
						(totalWidth / countEachRow) * ((i / 2) % countEachRow) + (totalWidth / countEachRow) / 2 + getChildAt(
								i
						).measuredWidth / 2,
						(((((totalWidth / countEachRow) * (((i / 2) / countEachRow) + 1)) - (totalWidth / countEachRow / 2) + getChildAt(i - 1).measuredHeight / 3) + ViewUtil.dipToPx(context, 15) + getChildAt(i).measuredHeight).toInt()
								))
			}
		}
	}
}

