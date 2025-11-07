package id.dreamfighter.multiplatform.utils

import id.dreamfighter.multiplatform.swift.Utils
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun Double.toCurrency(code: String): String {
    return Utils.formatNumberWithCode(code,this)
}