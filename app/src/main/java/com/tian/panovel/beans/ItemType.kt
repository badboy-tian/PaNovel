package com.tian.panovel.beans

enum class ItemType {
    XUANHUAN(1),
    WUXIA(2),
    DUSHI(3),
    LISHI(4),
    KEHUAN(5),
    WANGYOU(6),
    NVSHENG(7),
    PAIHANG(8);

    var typeNum = 1
    constructor(typeNum: Int) {
        this.typeNum = typeNum
    }



}