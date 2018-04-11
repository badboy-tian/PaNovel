package com.tian.panovel.network

import com.tian.panovel.beans.*
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by Administrator on 2017/8/15.
 * http相关
 */

class HttpMethods//构造方法私有
private constructor() {
    init {
        APIService.init()
    }


    //在访问HttpMethods时创建单例
    private object SingletonHolder {
        val INSTANCE = HttpMethods()
    }

    private fun <T> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun booklist(subscriber: Observer<BklistItem>, type: ItemType, pageNum: Int) {
        APIService.mAPI_SERVICE!!.booklist(type.typeNum, pageNum).compose(applySchedulers()).subscribe(subscriber)
    }

    fun bookDetail(subscriber: Observer<BookDetail>, url: String) {
        APIService.mAPI_SERVICE!!.bookDetail(url).compose(applySchedulers()).subscribe(subscriber)
    }

    fun chapter(subscriber: Observer<Chapter>, url: String) {
        APIService.mAPI_SERVICE!!.chapter(url).compose(applySchedulers()).subscribe(subscriber)
    }

    fun boolist(subscriber: Observer<BookList>, url: String) {
        APIService.mAPI_SERVICE!!.bookList(url).compose(applySchedulers()).subscribe(subscriber)
    }

    fun search(subscriber: Observer<SearchResp>, words: String, pageNum: Int) {
        APIService.mAPI_SERVICE!!.search(words, pageNum).compose(applySchedulers()).subscribe(subscriber)
    }

    fun rank(subscriber: Observer<BklistItem>, type: String, pageNum: Int) {
        APIService.mAPI_SERVICE!!.rank(type, pageNum).compose(applySchedulers()).subscribe(subscriber)
    }

    companion object {
        private val DEFAULT_TIMEOUT = 5

        //获取单例
        val instance: HttpMethods
            get() = SingletonHolder.INSTANCE
    }

}
