package com.tian.panovel.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.i7play.supertian.adapter.AutoCommonAdapter
import com.i7play.supertian.base.BaseActivity
import com.i7play.supertian.ext.addTo
import com.i7play.supertian.ext.jumpTo
import com.i7play.supertian.ext.toast
import com.i7play.supertian.manager.ActivityManager
import com.i7play.supertian.network.BaseObserver
import com.i7play.supertian.utils.LogHelper
import com.maning.library.zxing.utils.ZXingUtils
import com.tian.panovel.R
import com.tian.panovel.beans.BklistItem
import com.tian.panovel.beans.SearchResp
import com.tian.panovel.network.APIService
import com.tian.panovel.network.HttpMethods
import com.zhy.adapter.recyclerview.base.ViewHolder
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.item_search.view.*
import kotlinx.android.synthetic.main.share_layout.view.*

/**
 * 搜索结果显示
 */
class SearchActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    private var words: String = ""
    private var pageNum = 1
    private var totalPage = 1
    private var datas = arrayListOf<SearchResp.Item>()
    override fun initData() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "搜索结果"
        words = intent.getStringExtra("words")

        initList(list)

        val adapter = LRecyclerViewAdapter(object : AutoCommonAdapter<SearchResp.Item>(this, R.layout.item_search, datas) {
            override fun convert(holder: ViewHolder?, t: SearchResp.Item?, position: Int) {
                if (holder == null || t == null) {
                    toast("未搜索到记录")
                    return
                }

                holder.convertView.tvName.text = t.title
                holder.convertView.tvDetail.text = t.infos[0]
                holder.convertView.tvAuthor.text = t.infos[1]
                holder.convertView.setOnClickListener {
                    val b = BklistItem.Item()
                    b.title = t.title
                    b.author = t.infos[0]
                    b.url = t.bookId
                    ActivityManager.finishActivity(BookDetailActivity::class.java)
                    jumpTo(BookDetailActivity::class.java, b)
                }
            }
        })

        list.adapter = adapter
        load(true, false)
    }

    fun load(isFirst: Boolean, isRefresh: Boolean) {
        val ob = object : BaseObserver<SearchResp>(this) {
            override fun _onNext(t: SearchResp) {
                if (t.books == null || t.books.size == 0) return

                LogHelper.LogE(t)
                if (totalPage == 1) {
                    totalPage = t.pageInfo.split("/")[1].toInt()
                }
                if (isRefresh) {
                    datas.clear()
                }
                datas.addAll(t.books)
                list.adapter.notifyDataSetChanged()
                list.refreshComplete(datas.size)
            }

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                d.addTo(compositeDisposable)
                if (isFirst)
                    show(getString(R.string.loading))
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                if (isRefresh) {
                    list.refreshComplete(datas.size)
                }
            }
        }

        HttpMethods.instance.search(ob, words, pageNum)
    }

    override fun initListener() {
        list.setOnRefreshListener {
            pageNum = 1
            load(false, true)
        }

        list.setOnLoadMoreListener {
            if (pageNum == totalPage) {
                list.setNoMore(true)
                return@setOnLoadMoreListener
            }

            pageNum++
            LogHelper.LogE(pageNum)
            load(false, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.action_share -> {
                val url = "${APIService.BASEURL}/SearchBook.php?q=$words&page=$pageNum"
                val view = layoutInflater.inflate(R.layout.share_layout, null)
                view.ivCode.setImageBitmap(ZXingUtils.createQRImage(url, 600))
                view.tvUrl.text = url
                view.ivCode.setOnClickListener {
                    copy(url)
                }
                AlertDialog.Builder(this).setTitle("分享").setPositiveButton("确定", null).setView(view).create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return true
    }

    private fun copy(url: String) {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.primaryClip = ClipData.newPlainText("", url)
        toast("复制成功")
    }
}
