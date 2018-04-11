package com.tian.panovel.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.i7play.supertian.base.BaseLazyFragment
import com.tian.panovel.R
import kotlinx.android.synthetic.main.fragment_rank.*

class RankFragment : BaseLazyFragment() {
    override fun fetchData() {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_rank
    }

    private lateinit var fragments: ArrayList<RankSubFragment>
    override fun initView() {
        val titles = arrayListOf("周榜", "月榜", "总榜")
        fragments = arrayListOf(RankSubFragment.newInstance("week"),
                RankSubFragment.newInstance("month"),
                RankSubFragment.newInstance("all"))

        viewpager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return fragments[position]
            }

            override fun getCount(): Int {
                return fragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }
        }

        tablayout.setupWithViewPager(viewpager)
    }

    fun getUrl(): String{
        return (fragments[viewpager.currentItem]).getUrl()
    }

    fun refresh(){
        (fragments[viewpager.currentItem]).fetchData()
    }
}
