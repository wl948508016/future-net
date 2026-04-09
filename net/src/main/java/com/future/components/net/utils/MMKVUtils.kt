package com.bdtd.bdcar.utils

import android.text.TextUtils
import com.tencent.mmkv.MMKV

/**
 *
 * @Description:
 * @Author:         future
 * @CreateDate:     2021/11/4 2:37 下午
 */
private fun getMMKV(): MMKV? {
    return MMKV.defaultMMKV()
}

private fun isNull(): Boolean {
    return TextUtils.isEmpty(MMKV.getRootDir())
}

fun pushBoolean(key: String, value: Boolean) {
    if (!isNull()) {
        getMMKV()?.apply {
            this.encode(key, value)
        }
    }
}

fun getBoolean(key: String): Boolean {
    return if (isNull()) {
        false
    } else {
        getMMKV().run {
            this?.decodeBool(key) ?: false
        }
    }
}

fun pushString(key: String, value: String) {
    if (!isNull()) {
        getMMKV()?.apply {
            this.encode(key, value)
        }
    }
}

fun getString(key: String, default:String =""): String {
    return if (isNull()) {
        default
    } else {
        getMMKV().run {
            this?.decodeString(key) ?: default
        }
    }
}

fun pushInt(key: String, value: Int) {
    if (!isNull()) {
        getMMKV()?.apply {
            this.encode(key, value)
        }
    }
}

fun getInt(key: String, default: Int =-1): Int {
    return if (isNull()) {
        default
    } else {
        getMMKV().run {
            this?.decodeInt(key) ?: default
        }
    }
}