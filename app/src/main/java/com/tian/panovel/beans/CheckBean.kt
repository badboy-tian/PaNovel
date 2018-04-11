package com.tian.panovel.beans

import com.google.gson.annotations.SerializedName

/**
 * Created by Administrator on 2017/8/22.
 *升级用
 */

class CheckBean {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("version")
    var version: String? = null
    @SerializedName("changelog")
    var changelog: String? = null
    @SerializedName("updated_at")
    var updatedAt: Int = 0
    @SerializedName("versionShort")
    var versionShort: String? = null
    @SerializedName("build")
    var build: String? = null
    @SerializedName("installUrl")
    var installUrl: String? = null
    @SerializedName("install_url")
    var install_Url: String? = null
    @SerializedName("direct_install_url")
    var directInstallUrl: String? = null
    @SerializedName("update_url")
    var updateUrl: String? = null
    @SerializedName("binary")
    var binary: BinaryEntity? = null

    class BinaryEntity {
        @SerializedName("fsize")
        var fsize: Int = 0
    }
}
