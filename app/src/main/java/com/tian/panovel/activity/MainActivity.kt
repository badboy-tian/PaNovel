package com.tian.panovel.activity

import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import com.i7play.supertian.base.BaseActivity
import com.i7play.supertian.ext.jumpTo
import com.i7play.supertian.ext.showTDialog
import com.i7play.supertian.ext.toast
import com.i7play.supertian.manager.ActivityManager
import com.i7play.supertian.utils.CommonUtils
import com.i7play.supertian.utils.LogHelper
import com.i7play.supertian.utils.SharedPrefsUtils
import com.jkt.tdialog.TDialog
import com.maning.library.zxing.utils.ZXingUtils
import com.tian.panovel.App
import com.tian.panovel.R
import com.tian.panovel.beans.CheckBean
import com.tian.panovel.beans.ItemType
import com.tian.panovel.fragment.HomeFragment
import com.tian.panovel.fragment.RankFragment
import com.tian.panovel.fragment.RankSubFragment
import com.umeng.analytics.MobclickAgent
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import com.zhy.http.okhttp.callback.StringCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.share_layout.view.*
import okhttp3.Call
import okhttp3.Request
import java.io.File

class MainActivity : BaseActivity() {
    val titles = arrayListOf("玄幻", "仙侠", "都市", "历史", "网游", "科幻", "女生", "排行")
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        setSupportActionBar(toolbar)
        initTabLayout()
        checkUpdate()

        val agree = SharedPrefsUtils.getBooleanPreference(this, "agree", false)
        if (agree) return
        showTDialog("免责声明", "本软件所有数据均来自https://biquguan.com/, 本软件不保存任何内容, 仅供学习之用, 谢谢", arrayOf("不同意", "同意"), object : TDialog.onItemClickListener {
            override fun onItemClick(`object`: Any?, position: Int) {
                when (position) {
                    0 -> {
                        finish()
                    }
                    1 -> {
                        SharedPrefsUtils.setBooleanPreference(this@MainActivity, "agree", true)
                    }
                }
            }
        })

        /*AlertDialog.Builder(this@MainActivity).setTitle("免责声明")
                .setMessage("本软件所有数据均来自https://biquguan.com/, 本软件不保存任何内容, 仅供学习之用, 谢谢")
                .setPositiveButton("同意", null).setNegativeButton("不同意", null).show()*/
    }

    override fun initListener() {
        menu_refresh.setOnClickListener {
            menu.close(true)
            val fragment = fragments[viewpager.currentItem]
            if (fragment is HomeFragment) {
                fragment.fetchData()
            }else if (fragment is RankFragment){
                fragment.refresh()
            }
        }

        menu_books.setOnClickListener {
            menu.close(true)
            jumpTo(BooksActivity::class.java)
        }


    }


    private fun initTabLayout() {
        viewpager.adapter = MainAdapter(supportFragmentManager)
        viewpager.offscreenPageLimit = fragments.size - 1
        tablayout.setupWithViewPager(viewpager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(menuItem) as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.queryHint = "输入书名/作者"
        //searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                p0?.let {
                    ActivityManager.finishActivity(SearchActivity::class.java)
                    jumpTo(SearchActivity::class.java, "words", it)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.action_share -> {
                val fragment = fragments[viewpager.currentItem]
                var url = ""
                if (fragment is HomeFragment) {
                    url = fragment.getUrl()

                }else if (fragment is RankFragment){
                    url = fragment.getUrl()
                }
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

    private fun copy(url: String) {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.primaryClip = ClipData.newPlainText("", url)
        toast("复制成功")
    }

    val fragments = ArrayList<Fragment>()

    private inner class MainAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm), ViewPager.OnPageChangeListener {
        init {
            fragments.add(HomeFragment.newInstance(ItemType.XUANHUAN))
            fragments.add(HomeFragment.newInstance(ItemType.WUXIA))
            fragments.add(HomeFragment.newInstance(ItemType.DUSHI))
            fragments.add(HomeFragment.newInstance(ItemType.LISHI))
            fragments.add(HomeFragment.newInstance(ItemType.WANGYOU))
            fragments.add(HomeFragment.newInstance(ItemType.KEHUAN))
            fragments.add(HomeFragment.newInstance(ItemType.NVSHENG))
            fragments.add(RankFragment())
        }


        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }


        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {

        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    var preTime: Long = 0
    override fun onBackPressed() {
        if (viewpager.currentItem != 0) {
            viewpager.currentItem = 0
        } else {
            if (System.currentTimeMillis() - preTime <= 1000) {
                ActivityManager.finishAllActivity()
            } else {
                toast("再点一次退出软件")
                preTime = System.currentTimeMillis()
            }
        }
    }


    private fun checkUpdate() {
        val url = "http://api.fir.im/apps/latest/5acd68f5548b7a2552b18a2a"
        OkHttpUtils
                .get()
                .url(url)
                .addParams("api_token", "4e4b1fc369c45a4b95a31ccda7fdf6ce")
                .build()
                .execute(object : StringCallback() {

                    override fun onError(call: Call, e: Exception, id: Int) {
                        LogHelper.LogE(e.toString() + "")
                    }

                    override fun onResponse(response: String, id: Int) {
                        if (TextUtils.isEmpty(response)) {
                            return
                        }

                        try {
                            val gson = Gson()
                            val bean = gson.fromJson<CheckBean>(response, CheckBean::class.java!!)
                            if (CommonUtils.getVerCode(`this`) < Integer.valueOf(bean.version)) {
                                showSimpleDialog(bean.changelog!!, bean.install_Url!!)
                            }
                        } catch (e: Exception) {

                        }

                    }
                })
    }

    private fun showSimpleDialog(changelog: String, install_url: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("正在下载...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.max = 100
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)

        showTDialog("有新版本了", changelog, arrayOf(getString(R.string.btn_cancel), getString(R.string.btn_sure)), TDialog.onItemClickListener { `object`, position ->
            when (position) {
                1 -> {
                    OkHttpUtils
                            .get()
                            .url(install_url)
                            .build()
                            .execute(object : FileCallBack(Environment.getExternalStorageDirectory().absolutePath, "zhua_update_android.apk") {
                                override fun onError(call: Call, e: Exception, id: Int) {
                                    e.printStackTrace()
                                    progressDialog.dismiss()
                                }


                                override fun onResponse(response: File, id: Int) {
                                    progressDialog.dismiss()
                                    CommonUtils.installAPK(`this`, response)
                                }


                                override fun inProgress(progress: Float, total: Long, id: Int) {
                                    //LogHelper.LogE(progress + "");
                                    progressDialog.progress = (progress * 100).toInt()
                                    super.inProgress(progress, total, id)
                                }


                                override fun onBefore(request: Request, id: Int) {
                                    super.onBefore(request, id)
                                    progressDialog.show()
                                }
                            })
                }
            }
        })
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
