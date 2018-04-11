package com.tian.panovel.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.Html
import android.view.Menu
import android.view.MenuItem
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
import com.tian.panovel.beans.BookMark
import com.tian.panovel.beans.Chapter
import com.tian.panovel.network.APIService
import com.tian.panovel.network.HttpMethods
import com.umeng.analytics.MobclickAgent
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_reader.*
import kotlinx.android.synthetic.main.share_layout.view.*

class ReaderActivity : BaseActivity() {
    lateinit var url: String
    override fun getLayoutId(): Int {
        return R.layout.activity_reader
    }

    var REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>"
    var P_SCRIPT = "<p[^>]*?>[\\s\\S]*?<\\/p>"
    override fun initData() {
        url = intent.getStringExtra("url")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadData()
    }

    lateinit var chapter: Chapter
    private fun loadData() {
        val ob = object : BaseObserver<Chapter>(this) {
            override fun _onNext(t: Chapter) {
                chapter = t
                LogHelper.LogE(t)
                supportActionBar?.title = t.title
                tvContent.text = Html.fromHtml(t.content.replace(REGEX_SCRIPT.toRegex(), "").replace(P_SCRIPT.toRegex(), ""))

                val bookId = url.substringBeforeLast("/") + "/"
                val bookMark = App.Instance.db.load(BookMark::class.java, bookId)
                if (bookMark != null){
                    bookMark.lastReadName = t.title
                    bookMark.lastReadUrl = url
                    App.Instance.db.storeOrUpdate(bookMark)
                }
            }

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                d.addTo(compositeDisposable)
                show("正在加载...")
            }
        }

        HttpMethods.instance.chapter(ob, url)
    }

    override fun initListener() {
        menu.setClosedOnTouchOutside(true)
        menu_top.setOnClickListener {
            menu.close(true)
            scrollView.smoothScrollTo(0, 0)
        }

        menu_refresh.setOnClickListener {
            menu.close(true)
            loadData()
        }
        //顶部
        menu_top.setOnClickListener {
            menu.close(true)
            scrollView.smoothScrollTo(0, 0)
        }
        //下一章
        menu_down.setOnClickListener {
            menu.close(true)
            scrollView.smoothScrollTo(0, 0)
            if (chapter.nextUrl == "./"){
                finish()
                return@setOnClickListener
            }
            url = url.replaceAfterLast('/', chapter.nextUrl)
            loadData()
        }

        //上一章
        menu_up.setOnClickListener {
            menu.close(true)
            scrollView.smoothScrollTo(0, 0)
            if (chapter.preUrl == "./"){
                finish()
                return@setOnClickListener
            }
            url = url.replaceAfterLast('/', chapter.preUrl)
            loadData()
        }

        //打开目录
        menu_list.setOnClickListener {
            menu.close(true)
            ActivityManager.finishActivity(ListActivity::class.java)
            jumpTo(ListActivity::class.java, "bookid", chapter.listUrl)
        }

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
                val url = "${APIService.BASEURL}$url"
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

    public override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    public override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}
