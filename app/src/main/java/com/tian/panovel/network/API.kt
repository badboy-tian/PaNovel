package com.tian.panovel.network

import com.tian.panovel.beans.*
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface API {
    @GET("/bqgclass/{typeId}/{pageId}.html")
    fun booklist(@Path("typeId") typeId: Int, @Path("pageId") pageId: Int): Observable<BklistItem>

    @GET("{bookurl}")
    fun bookDetail(@Path("bookurl") bookurl: String) : Observable<BookDetail>

    @GET("{bookurl}")
    fun chapter(@Path("bookurl") bookurl: String) : Observable<Chapter>

    @GET("{bookurl}/all.html")
    fun bookList(@Path("bookurl") bookurl: String) : Observable<BookList>

    @GET("/SearchBook.php")
    fun search(@Query("q") words: String, @Query("page") page: Int) : Observable<SearchResp>

    @GET("/bqgph/{rank}_{pageId}.html")
    fun rank(@Path("rank") rank: String, @Path("pageId") pageId: Int) : Observable<BklistItem>
    ///SearchBook.php?q=
}