package hzkj.cc.base.weight.tabLayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import hzkj.cc.base.R

/**

 * @Author chencheng
 * @Date 2019-12-03-15:15
 */
class MyTabLayout(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet)
{
    var targetP = 0
    var indicator: MyTabIndicator? = null
    var viewPager: ViewPager? = null
    fun setAdapter(fragmentManager: FragmentManager, tabs: MutableList<Tab>)
    {
        viewPager?.setAdapter(object : FragmentPagerAdapter(fragmentManager)
                              {
                                  override fun getItem(i: Int): Fragment
                                  {
                                      return tabs[i].fragment
                                  }

                                  override fun getCount(): Int
                                  {
                                      return tabs.size
                                  }
                              })
        indicator?.tabs = tabs.map { it.name }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int)
    {
        super.onLayout(changed, l, t, r, b)
    }
    init
    {
        orientation = VERTICAL
        var view = LayoutInflater.from(context)
            .inflate(R.layout.base_tab_layout, null)
        viewPager = view.findViewById(R.id.viewPager)
        indicator = view.findViewById(R.id.indicator)
        addView(view)

        indicator?.viewPagerCallBack = {
            targetP = it
            indicator?.isClick = true
            System.out.println("1 ${indicator?.isClick}")

            viewPager?.setCurrentItem(it, true)
        }
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
                                           {
                                               override fun onPageScrollStateChanged(state: Int)
                                               {
                                                   if (state == 0 && indicator?.isClick!!)
                                                   {
                                                       indicator?.isClick = false
                                                       indicator?.move()
                                                       //                                                       indicator?.move()
                                                   }
                                                   System.out.println("${state}|2 ${indicator?.isClick}")
                                               }

                                               override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
                                               {
                                                   //                                                   if (position == targetP)
                                                   //                                                   {
                                                   //                                                       indicator?.isClick = false
                                                   //                                                   }
                                                   //                                                   System.out.println(position)
                                                   //滑动事件不触发onDraw里的scrollto
                                                   indicator?.isScrolling = false
                                                   // 滑动viewpager时重置upx
                                                   indicator?.lastScrollX = indicator?.scrollX!!.toFloat()
                                                   if (positionOffset == 0f)
                                                   {
                                                       indicator?.selectIndex = position
                                                   }
                                                   indicator?.distance = positionOffset + position
                                                   indicator?.invalidate()
                                               }

                                               override fun onPageSelected(position: Int)
                                               {
                                               }
                                           })
    }
}