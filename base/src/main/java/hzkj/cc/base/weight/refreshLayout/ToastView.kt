package hzkj.cc.base.weight.refreshLayout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import hzkj.cc.base.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ToastView(context: Context) : LinearLayout(context) {
    private lateinit var view: View
    lateinit var textView: TextView
    var count = 0

    companion object {
        var HASREFRESH = "数据已刷新"
        var HASLOAD = "数据已加载"
        var LOADEMPTY = "没有数据了"
        var REFRESHEMPTY = "暂无数据"
        var NETERROR = "网络走神了"
    }

    init {
        MainScope().launch {
            initLayout()
            textView = view.findViewById(R.id.text)
            textView.setText(HASREFRESH)
            addView(view)
        }
    }

    private suspend fun initLayout() {
        withContext(Dispatchers.IO)
        {
            view = LayoutInflater.from(context).inflate(R.layout.base_toast_view, this@ToastView, false)
        }
    }
}