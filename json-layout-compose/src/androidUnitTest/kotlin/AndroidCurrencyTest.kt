import id.dreamfighter.multiplatform.utils.toCurrency
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidCurrencyTest {

    @Test
    fun `test double to curreny`() {
        assertEquals("IDR1,000.00", 1000.0.toCurrency("IDR"))
    }
}