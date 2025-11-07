package id.dreamfighter.multiplatform.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

actual fun Double.toCurrency(code: String): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    if (numberFormat is DecimalFormat) {
        val df: DecimalFormat = numberFormat as DecimalFormat
        // use local/default decimal symbols with original currency symbol
        val dfs: DecimalFormatSymbols = DecimalFormat().decimalFormatSymbols
        dfs.currency = df.currency
        df.decimalFormatSymbols = dfs
    }
    return numberFormat.format(this)
}