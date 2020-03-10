package hzkj.cc.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hzkj.cc.base.base.CommonAdapter
import hzkj.cc.base.base.Convert
import hzkj.cc.base.weight.CcTextView

object NotifyDialog
{
    var LIST = 100
    var NOTIFY = 1000
    var EDIT = 11
    var SINGLE_SELECT = 1111
    var MULTI_SELECT = 1111111

    class Builder(context: Context)
    {
        var hint = ""
        var type = 0
        var selectImageView: ImageView? = null
        var selectPosition = -1
        var selectPositions = mutableListOf<Int>()
        var state = NOTIFY
        var dialog = Dialog(context)
        var context: Context? = null
        var text: String? = null
        var title: String? = null
        var image: Int = 0
        var canCancel = true
        var canOutsideCancel = true
        var strs: MutableList<String>? = null
        var listItemListenner: ((Int) -> Unit)? = null
        var dismissListenner: (() -> Unit)? = null
        var view: View? = null
        fun inflate(layout: Int): View
        {
            var view: View? = null
            //            withContext(Dispatchers.IO, {
            view = LayoutInflater.from(context)
                .inflate(layout, null)
            //            })
            return view!!
        }

        init
        {
            this.context = context
        }

        fun build(): Builder
        {
            var image: ImageView? = null
            var text: TextView? = null
            var submit: TextView? = null
            var recyclerView: RecyclerView? = null
            //            GlobalScope.launch(Dispatchers.Main) {
            when (state)
            {
                EDIT ->
                {
                    view = inflate(R.layout.base_edit_dialog_layout)
                    var title: CcTextView = view!!.findViewById(R.id.title)
                    title.text = this.title
                    //                    var include = view!!.findViewById<RecyclerView>(R.id.recyclerview)
                }
                NOTIFY ->
                {
                    view = inflate(R.layout.base_notify_dialog_layout)
                    image = view!!.findViewById(R.id.image)
                    text = view!!.findViewById(R.id.text)
                    submit = view!!.findViewById(R.id.submit)
                    text?.text = this@Builder.text
                    if (this@Builder.image != 0)
                    {
                        image?.setImageDrawable(context?.resources?.getDrawable(this@Builder.image, null))
                    }
                    else
                    {
                        image?.visibility = View.GONE
                    }
                    submit?.setOnClickListener {
                        dialog.dismiss()
                        dismissListenner?.invoke()
                    }
                }
                LIST ->
                {
                    view = inflate(R.layout.base_list_dialog_layout)
                    var recyclerView = view!!.findViewById<RecyclerView>(R.id.recyclerview)
                    var title: CcTextView = view!!.findViewById(R.id.title)
                    var cancel = view!!.findViewById<CcTextView>(R.id.cancel)
                    var submit = view!!.findViewById<CcTextView>(R.id.submit)
                    cancel.setOnClickListener { dialog.dismiss() }
                    submit.setOnClickListener {
                        dialog.dismiss()
                    }
                    title.text = this.title
                    recyclerView!!.layoutManager = LinearLayoutManager(context).apply {
                        orientation = RecyclerView.VERTICAL
                    }
                    var adapter = CommonAdapter<String>(context!!, R.layout.base_list_item, strs!!)
                    recyclerView?.adapter = adapter
                    adapter.listener = object : Convert<String>
                    {
                        override fun convert(holder: CommonAdapter.BaseHolder, position: Int, data: String)
                        {
                            holder.itemView.measure(0, 0)
                            System.out.println(holder.itemView.measuredHeight)
                            with(holder) {
                                if (type == SINGLE_SELECT)
                                {
                                    getView<ImageView>(R.id.check).setImageDrawable(context!!.resources.getDrawable(if (selectPosition == position) R.drawable.base_dialog_selected else R.drawable.base_dialog_unselected, null))
                                }
                                else
                                {
                                    getView<ImageView>(R.id.check).setImageDrawable(context!!.resources.getDrawable(if (selectPositions.contains(position)) R.drawable.base_dialog_selected else R.drawable.base_dialog_unselected, null))
                                }
                                getView<CcTextView>(R.id.text).run {
                                    this.text = data
                                    setOnClickListener {
                                        if (type == SINGLE_SELECT)
                                        {
                                            if (selectPosition != position)
                                            {
                                                selectImageView?.setImageDrawable(resources.getDrawable(R.drawable.base_dialog_unselected))
                                                getView<ImageView>(R.id.check).setImageDrawable(resources.getDrawable(R.drawable.base_dialog_selected))
                                                selectPosition = position
                                                selectImageView = getView<ImageView>(R.id.check)
                                            }
                                        }
                                        else
                                        {
                                            if (selectPositions.contains(position))
                                            {
                                                getView<ImageView>(R.id.check).setImageDrawable(resources.getDrawable(R.drawable.base_dialog_unselected))
                                                selectPositions.remove(position)
                                            }
                                            else
                                            {
                                                getView<ImageView>(R.id.check).setImageDrawable(resources.getDrawable(R.drawable.base_dialog_selected))
                                                selectPositions.add(position)
                                            }
                                        }
                                        System.out.println(selectPositions)
                                        //                                        dialog.dismiss()
                                        //                                        listItemListenner?.invoke(position)
                                    }
                                }
                            }
                        }
                    }
                }
            }




            dialog.run {
                //                setOnDismissListener {
                //                    dialog.dismiss()
                //                }
                setCancelable(canCancel)
                setCanceledOnTouchOutside(canOutsideCancel)
                setContentView(view)
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                //                }
            }
            return this
        }

        //        fun state(state: Int): Builder {
        //            this.state = state
        //            return this
        //        }
        fun list(strs: MutableList<String>, type: Int = SINGLE_SELECT): Builder
        {
            this.type = type
            this.strs = strs
            state = LIST
            return this
        }

        fun title(strs: String): Builder
        {
            this.title = strs
            return this
        }

        fun show()
        {
            if (state == LIST)
            {
                view!!.post {
                    val lp = dialog.window!!.attributes
                    view?.measure(0, 0)
                    //                    dialog.window!!.attributes = lp.apply {
                    //                        width = view!!.measuredWidth
                    //                        height = view!!.measuredHeight
                    //                    }
                    if (view!!.measuredHeight > (ViewUtil.getScreenParams(context!!).heightPixels * 0.5).toInt())
                    {
                        dialog.window!!.attributes = lp.apply {
                            width = view!!.measuredWidth
                            height = (ViewUtil.getScreenParams(context!!).heightPixels * 0.5).toInt()
                        }
                    }
                }
            }
            dialog.show()
        }

        fun dismissListenner(dismissListenner: (() -> Unit)?): Builder
        {
            this.dismissListenner = dismissListenner
            return this
        }

        fun listItemListenner(listItemListenner: ((Int) -> Unit)?): Builder
        {
            this.listItemListenner = listItemListenner
            return this
        }

        fun image(image: Int): Builder
        {
            this.image = image
            return this
        }

        fun text(text: String): Builder
        {
            this.text = text
            return this
        }

        fun edit(hint: String, text: String): Builder
        {
            this.text = text
            this.hint = hint
            state = EDIT
            return this
        }

        fun canCancel(canCancel: Boolean): Builder
        {
            this.canCancel = canCancel
            return this
        }

        fun canOutsideCancel(canOutsideCancel: Boolean): Builder
        {
            this.canOutsideCancel = canOutsideCancel
            return this
        }
    }
}