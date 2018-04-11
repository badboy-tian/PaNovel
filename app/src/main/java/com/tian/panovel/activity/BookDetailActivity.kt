package com.tian.panovel.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.i7play.supertian.adapter.AutoCommonAdapter
import com.i7play.supertian.base.BaseActivity
import com.i7play.supertian.ext.addTo
import com.i7play.supertian.ext.jumpTo
import com.i7play.supertian.ext.toast
import com.i7play.supertian.manager.ActivityManager
import com.i7play.supertian.network.BaseObserver
import com.i7play.supertian.utils.LogHelper
import com.maning.library.zxing.utils.ZXingUtils
import com.tian.panovel.App
import com.tian.panovel.R
import com.tian.panovel.beans.BklistItem
import com.tian.panovel.beans.BookDetail
import com.tian.panovel.beans.BookMark
import com.tian.panovel.network.APIService
import com.tian.panovel.network.HttpMethods
import com.zhy.adapter.recyclerview.base.ViewHolder
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.item_book_zhang.view.*
import kotlinx.android.synthetic.main.share_layout.view.*

class BookDetailActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_book_detail
    }

    private lateinit var data: BklistItem.Item
    val items = arrayListOf<BookDetail.Zhang>()
    override fun initData() {
        data = intent.getSerializableExtra("item") as BklistItem.Item
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = data.title

        initList(list)
        list.adapter = object : AutoCommonAdapter<BookDetail.Zhang>(this, R.layout.item_book_zhang, items) {
            override fun convert(holder: ViewHolder?, t: BookDetail.Zhang?, position: Int) {
                if (holder == null || t == null) return

                holder.convertView.tvTitle.text = t.name
                holder.convertView.setOnClickListener {
                    ActivityManager.finishActivity(ReaderActivity::class.java)
                    jumpTo(ReaderActivity::class.java, "url", t.url)
                }
            }
        }

        loadData()
    }

    lateinit var bookDetail: BookDetail
    private lateinit var bookMark: BookMark
    private fun loadData() {
        val ob = object : BaseObserver<BookDetail>(this) {
            override fun _onNext(t: BookDetail) {
                LogHelper.LogE(t)
                bookDetail = t

                Glide.with(this@BookDetailActivity).load(t.book.img).into(tvImg)
                tvName.text = t.book.state[0]
                tvType.text = t.book.state[1]
                tvState.text = t.book.state[2]
                tvDate.text = t.book.state[3]
                tvNew.text = Html.fromHtml("最新：<a href=\"${APIService.BASEURL}${t.book.newpostUrl}\">${t.book.newpostText}</a>")

                tvUpdateTitle.text = "最新章节  ${t.book.state[3]}"
                tvDetail.text = Html.fromHtml(t.detail)
                items.addAll(t.items.asReversed())
                list.adapter.notifyDataSetChanged()

                bookMark = BookMark("", "", data)
            }

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                d.addTo(compositeDisposable)
                show(getString(R.string.loading))
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                finish()
            }
        }

        HttpMethods.instance.bookDetail(ob, data.url)
    }

    override fun initListener() {
        tvStart.setOnClickListener {
            ActivityManager.finishActivity(ListActivity::class.java)
            jumpTo(ListActivity::class.java, "bookid", data.url)
        }

        tvSave.setOnClickListener {
            App.Instance.db.storeOrUpdate(bookMark)
            toast("加入书架成功!")
        }

        tvNew.setOnClickListener {
            ActivityManager.finishActivity(ReaderActivity::class.java)
            jumpTo(ReaderActivity::class.java, "url", bookDetail.book.newpostUrl)
        }

        tvAll.setOnClickListener {
            ActivityManager.finishActivity(ListActivity::class.java)
            jumpTo(ListActivity::class.java, "bookid", data.url)
        }

        menu_refresh.setOnClickListener {
            menu.close(true)
            loadData()
        }
        //书架
        menu_books.setOnClickListener {
            menu.close(true)
            ActivityManager.finishActivity(BooksActivity::class.java)
            jumpTo(BooksActivity::class.java)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.action_share -> {
                val url = "${APIService.BASEURL}${data.url}"
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
