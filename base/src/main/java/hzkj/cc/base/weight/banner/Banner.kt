package hzkj.cc.base.weight.banner

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import hzkj.cc.base.ViewUtil

/**

 * @Author chencheng
 * @Date 2019-12-05-14:35
 */
class Banner(context: Context, attributeSet: AttributeSet) : ViewPager(context, attributeSet)
{
    lateinit var runnable: Runnable
    var isCounting = true
    var h = ViewUtil.dipToPx(context, 200)
        .toInt()
    var handler1: Handler = object : Handler()
    {
        override fun handleMessage(msg: Message)
        {
            isCounting = false
            setCurrentItem(position1.value!! + 1, true)
            super.handleMessage(msg)
        }
    }
    var state = -1111
    var position1 = MutableLiveData<Int>().apply {
        value = 0
    }
    var urls: MutableList<String>? = null
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h)
    }

    init
    {
        overScrollMode = View.OVER_SCROLL_NEVER
        runnable = object : Runnable
        {
            override fun run()
            {
                if (state == 0 || state == -1111)
                {
                    val msg: Message = handler1.obtainMessage()
                    handler1.sendMessage(msg)
                }
            }
        }
        handler1.postDelayed(runnable, 0)




        addOnPageChangeListener(object : OnPageChangeListener
                                {
                                    override fun onPageScrollStateChanged(state: Int)
                                    {
                                        this@Banner.state = state
                                        if (state == ViewPager.SCROLL_STATE_IDLE)
                                        {
                                            handler1.removeCallbacks(runnable)
                                            handler1.postDelayed(runnable, 3000)
                                            if (position1.value == 0)
                                            {
                                                setCurrentItem(urls!!.size, false)
                                            }
                                            else if (position1.value == urls!!.size + 1)
                                            {
                                                setCurrentItem(1, false)
                                            }
                                        }
                                        //                                        System.out.println(state)
                                    }

                                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
                                    {
                                        position1.value = position
                                    }

                                    override fun onPageSelected(position: Int)
                                    {
                                    }
                                })
    }

    //    override fun onDrawForeground(canvas: Canvas?)
    //    {
    //        super.onDrawForeground(canvas)
    //        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), 100f, circlePaint);
    //
    //    }
    fun setUrl1s(urls: MutableList<String>)
    {
        this.urls = urls
        adapter = object : PagerAdapter()
        {
            override fun isViewFromObject(view: View, o: Any): Boolean
            {
                return view == o
            }

            override fun getCount(): Int
            {
                return urls!!.size + 2
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any
            {
                var image = ImageView(context).apply {
                    scaleType = ImageView.ScaleType.FIT_XY
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h)
                    //                    setImageDrawable(context.getDrawable(R.drawable.base_dialog_selected))
                }
                var view = LinearLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

                    addView(image)
                }
                //                                System.out.println(urls!![position])
                Glide.with(view.context)
                    .load(if (position == 0) urls!![urls!!.size - 1]
                          else if (position == getCount() - 1) urls!![0]
                    else urls!![position - 1])
                    .listener(object : RequestListener<String, GlideDrawable>
                              {
                                  override fun onException(e: Exception?, model: String?, target: com.bumptech.glide.request.target.Target<GlideDrawable>?, isFirstResource: Boolean): Boolean
                                  {
                                      //                                      System.out.println(e)
                                      return false
                                  }

                                  override fun onResourceReady(resource: GlideDrawable?, model: String?, target: com.bumptech.glide.request.target.Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean
                                  {
                                      //                                      System.out.println(resource)
                                      invalidate()

                                      return false
                                  }
                              })
                    .into(image)
                container.addView(view)
                return view
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any)
            {
                container.removeView(`object` as View)
            }
        }
    }
}