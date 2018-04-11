package com.tian.panovel.fragment


import android.os.Bundle
import android.widget.Button
import com.bumptech.glide.Glide
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.i7play.supertian.adapter.AutoCommonAdapter
import com.i7play.supertian.base.BaseLazyFragment
import com.i7play.supertian.ext.addTo
import com.i7play.supertian.network.BaseObserver
import com.kennyc.view.MultiStateView
import com.tian.panovel.R
import com.tian.panovel.activity.BookDetailActivity
import com.tian.panovel.beans.BklistItem
import com.tian.panovel.beans.ItemType
import com.tian.panovel.network.APIService
import com.tian.panovel.network.HttpMethods
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_novel.view.*

class HomeFragment : BaseLazyFragment() {
    var currentPageNum = 1
    val data = arrayListOf<BklistItem.Item>()
    override fun fetchData() {
        loadData(true, false)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_home
    }

    private lateinit var type: ItemType
    override fun initView() {
        type = arguments!!.getSerializable("type") as ItemType
        initList(list)
        val adapter = LRecyclerViewAdapter(object : AutoCommonAdapter<BklistItem.Item>(activity, R.layout.item_novel, data) {
            override fun convert(holder: com.zhy.adapter.recyclerview.base.ViewHolder?, t: BklistItem.Item?, position: Int) {
                if (holder == null || t == null) return

                //LogHelper.LogE(t)
                Glide.with(this@HomeFragment).load(t.img).into(holder.convertView.ivImg)
                holder.convertView.tvName.text = t.title
                holder.convertView.tvDetail.text = t.review
                holder.convertView.tvAuthor.text = t.author

                holder.convertView.setOnClickListener {
                    jumpTo(BookDetailActivity::class.java, t)
                }
            }
        })

        list.adapter = adapter
    }

    override fun initListener() {
        super.initListener()
        list.setOnRefreshListener {
            currentPageNum = 1
            loadData(false, true)
        }

        list.setOnLoadMoreListener {
            currentPageNum++
            loadData(false, false)
        }

        state.getView(MultiStateView.VIEW_STATE_ERROR)?.let {
            it.findViewById<Button>(R.id.refreshBtn).setOnClickListener {
                loadData(true, true)
            }
        }
    }

    companion object {
        fun newInstance(type: ItemType): HomeFragment {
            val homeFragment = HomeFragment()

            val bundle = Bundle()
            bundle.putSerializable("type", type)
            homeFragment.arguments = bundle

            return homeFragment
        }
    }

    private fun loadData(isFirst: Boolean, isRefresh: Boolean) {
        HttpMethods.instance.booklist(object : BaseObserver<BklistItem>(activity) {
            override fun _onNext(t: BklistItem) {
                if (isRefresh) {
                    data.clear()
                }
                data.addAll(t.items)
                list.adapter.notifyDataSetChanged()
                list.refreshComplete(t.items.size)
                if (data.size == 0) {
                    state.viewState = MultiStateView.VIEW_STATE_EMPTY
                } else {
                    state.viewState = MultiStateView.VIEW_STATE_CONTENT
                }
            }

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                d.addTo(compositeDisposable)
                if (isFirst) {
                    state.viewState = MultiStateView.VIEW_STATE_LOADING
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                if (isFirst) {
                    state.viewState = MultiStateView.VIEW_STATE_ERROR
                } else {
                    list.refreshComplete(data.size)
                    list.setNoMore(false)
                }
            }
        }, type, currentPageNum)
    }

    fun getUrl(): String {
        return "${APIService.BASEURL}/bqgclass/${type.typeNum}/$currentPageNum.html"
    }
}
