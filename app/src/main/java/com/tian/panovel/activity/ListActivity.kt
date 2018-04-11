package com.tian.panovel.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.i7play.supertian.adapter.AutoCommonAdapter
import com.i7play.supertian.base.BaseActivity
import com.i7play.supertian.ext.addTo
import com.i7play.supertian.ext.jumpToAndFinish
import com.i7play.supertian.ext.toast
import com.i7play.supertian.manager.ActivityManager
import com.i7play.supertian.network.BaseObserver
import com.i7play.supertian.utils.LogHelper
import com.maning.library.zxing.utils.ZXingUtils
import com.tian.panovel.R
import com.tian.panovel.beans.BookDetail
import com.tian.panovel.beans.BookList
import com.tian.panovel.network.APIService
import com.tian.panovel.network.HttpMethods
import com.zhy.adapter.recyclerview.base.ViewHolder
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.item_book_zhang.view.*
import kotlinx.android.synthetic.main.share_layout.view.*

/**
 * 目录
 */
class ListActivity : BaseActivity() {
    val datas = arrayListOf<BookDetail.Zhang>()
    override fun getLayoutId(): Int {
        return R.layout.activity_list
    }

    private var url: String = ""
    override fun initData() {
        url = intent.getStringExtra("bookid")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initList(list)

        list.adapter = object : AutoCommonAdapter<BookDetail.Zhang>(this, R.layout.item_book_zhang, datas) {
            override fun convert(holder: ViewHolder?, t: BookDetail.Zhang?, position: Int) {
                if (holder == null || t == null) return

                holder.convertView.tvTitle.text = t.name
                holder.convertView.setOnClickListener {
                    if (t.url == "#bottom") {
                        //(list.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(list.adapter.itemCount, 0)
                    } else {
                        ActivityManager.finishActivity(ReaderActivity::class.java)
                        jumpToAndFinish(ReaderActivity::class.java, "url", t.url)
                    }
                }
            }
        }

        loadData()
    }

    fun loadData() {
        val ob = object : BaseObserver<BookList>(this) {
            override fun _onNext(t: BookList) {
                LogHelper.LogE(t)
                supportActionBar?.title = t.title
                datas.addAll(t.items)
                list.adapter.notifyDataSetChanged()
            }

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                d.addTo(compositeDisposable)
                show(getString(R.string.loading))
            }
        }

        HttpMethods.instance.boolist(ob, url)
    }

    override fun initListener() {
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.action_share -> {
                val url = "${APIService.BASEURL}${url}all.html"
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
