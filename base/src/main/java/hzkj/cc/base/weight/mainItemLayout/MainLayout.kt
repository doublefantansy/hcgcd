package hzkj.cc.base.weight.mainItemLayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView

class MainLayout(
		context: Context,
		attributeSet: AttributeSet
) : NestedScrollView(context, attributeSet) {
	var layout = LinearLayout(context)
	var layout1 = MainItemLayout(context)
	var clickListenner: MainItemClickListenner? = null

	init {
		layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		layout1.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
		layout.addView(layout1)
		addView(layout)
	}

	fun init(mainItems: MutableList<MainItem>) {
		layout1.init(mainItems)
	}

	fun setListenner(clickListenner: MainItemClickListenner?) {
		layout1.clickListenner = clickListenner
	}
}