package hzkj.cc.base.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CommonAdapter<T> constructor(
        context: Context, layout: Int, datas: MutableList<T>
) : RecyclerView.Adapter<CommonAdapter.BaseHolder>() {
    var layout: Int = 0
    var datas: MutableList<T>
    var context: Context
    var listener: Convert<T>? = null
    var recyclerListener: ((BaseHolder) -> Unit)? = null

    init {
        this.layout = layout
        this.datas = datas
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        //        System.out.println("create")
        return BaseHolder(LayoutInflater.from(context).inflate(layout, parent, false))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        listener?.convert(holder, position, datas[position])
    }

    override fun onViewRecycled(holder: BaseHolder) {
        recyclerListener?.invoke(holder)
        super.onViewRecycled(holder)
    }

    open class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun <V : View> getView(id: Int): V {
            return itemView.findViewById(id)
        }
    }
}