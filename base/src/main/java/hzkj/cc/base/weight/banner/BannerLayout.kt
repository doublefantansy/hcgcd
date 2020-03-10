package hzkj.cc.base.weight.banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import hzkj.cc.base.R
import hzkj.cc.base.ViewUtil
import hzkj.cc.base.base.BaseActivity

/**

 * @Author chencheng
 * @Date 2019-12-06-10:46
 */
class BannerLayout(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet)
{
    var banner: Banner
    var bannerTips: BannerTips
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ViewUtil.dipToPx(context, 200).toInt())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int)
    {
        super.onLayout(changed, left, top, right, bottom)
    }

    init
    {
        //        System.out.println(measuredHeight)
        //        var banner = Banner(context).apply {
        //            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT,
        //                                                    MATCH_PARENT)
        //        }
        //        bannerTips = BannerTips(context).apply {}
        //        addView(banner)
        //        addView(bannerTips,
        //                FrameLayout.LayoutParams(MATCH_PARENT,
        //                                         MATCH_PARENT))
        var view = LayoutInflater.from(context)
            .inflate(R.layout.base_s, this, true)
        banner = view.findViewById(R.id.banner)
        bannerTips = view.findViewById(R.id.bannerTips)
        banner.position1.observe((context as BaseActivity<*>), Observer {
            bannerTips.position = it - 1
            bannerTips.invalidate()
        })
    }

    fun setUrls(urls: MutableList<String>)
    {
        //        this.urls = urls
        banner.setUrl1s(urls)
        bannerTips.count = urls.size
        bannerTips.invalidate()
    }
}