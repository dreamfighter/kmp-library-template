package id.dreamfighter.multiplatform.utils

@JsModule("currency-formatter")
external object CurrencyFormatter {
    @JsName("format")
    fun format(value: Double, options: JsAny): String
}

fun options(): JsAny = js("({code:'IDR', precision: 2})")

actual fun Double.toCurrency(code: String): String {
    return CurrencyFormatter.format(this, options())
}