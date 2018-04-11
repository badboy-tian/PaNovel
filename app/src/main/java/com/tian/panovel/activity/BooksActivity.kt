package com.tian.panovel.activity

import android.view.MenuItem
import com.bumptech.glide.Glide
import com.i7play.supertian.adapter.AutoCommonAdapter
import com.i7play.supertian.base.BaseActivity
import com.i7play.supertian.ext.jumpToAndFinish
import com.i7play.supertian.ext.toast
import com.i7play.supertian.manager.ActivityManager
import com.i7play.supertian.network.BaseObserver
import com.i7play.supertian.utils.LogHelper
import com.tian.panovel.App
import com.tian.panovel.R
import com.tian.panovel.beans.BookDetail
import com.tian.panovel.beans.BookMark
import com.tian.panovel.network.HttpMethods
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.activity_books.*
import kotlinx.android.synthetic.main.item_bookmark.view.*

/**
 * 书架
 */
class BooksActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_books
    }

    override fun initData() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "书架"

        val datas = App.Instance.db.loadAll(BookMark::class.java)
        initList(list)

        val adapter = object : AutoCommonAdapter<BookMark>(this, R.layout.item_bookmark, datas) {
            override fun convert(holder: ViewHolder?, t: BookMark?, position: Int) {
                if (holder == null || t == null) return
                Glide.with(this@BooksActivity).load(t.item.img).into(holder.convertView.ivImg)

                holder.convertView.tvNew.text = t.newReadName
                holder.convertView.tvName.text = t.item.title
                holder.convertView.tvAuthor.text = t.item.author
                holder.convertView.tvLast.text = t.lastReadName
                holder.convertView.subroot.setOnClickListener {
                    ActivityManager.finishActivity(BookDetailActivity::class.java)
                    jumpToAndFinish(BookDetailActivity::class.java, t.item)
                }

                holder.convertView.tvLast.setOnClickListener {
                    ActivityManager.finishActivity(BookDetailActivity::class.java)
                    jumpToAndFinish(ReaderActivity::class.java, "url", t.lastReadUrl)
                }

                holder.convertView.tvDel.setOnClickListener {
                    holder.convertView.root.quickClose()
                    App.Instance.db.delete(t)
                    datas.remove(t)
                    list.adapter.notifyDataSetChanged()
                    toast("删除成功!")
                }

                holder.convertView.tvNew.setOnClickListener {
                    ActivityManager.finishActivity(BookDetailActivity::class.java)
                    jumpToAndFinish(ReaderActivity::class.java, "url", t.newReadUrl)
                }

                if (!updateCompleted)
                    handleItem(t, position)
            }
        }

        list.adapter = adapter
    }

    var updateCompleted = false
    //更新图片, 以及最新章节
    private fun handleItem(bookMark: BookMark, position: Int) {
        val ob = object : BaseObserver<BookDetail>(this) {
            override fun _onNext(t: BookDetail) {
                LogHelper.LogE(t)
                bookMark.item.img = t.book.img
                bookMark.newReadName = t.book.newpostText
                bookMark.newReadUrl = t.book.newpostUrl
                App.Instance.db.storeOrUpdate(bookMark)

                LogHelper.LogE("$position ${list.adapter.itemCount - 1}")
                if (position == list.adapter.itemCount - 1) {
                    updateCompleted = true
                    list.adapter.notifyDataSetChanged()
                }
            }
        }

        HttpMethods.instance.bookDetail(ob, bookMark.id)
    }

    override fun initListener() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
