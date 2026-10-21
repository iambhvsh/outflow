package `in`.iambhvsh.outflow.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ParserTest {

    @Test
    fun parseUserSample1_sentToClassicMultiSales() {
        val sms = "Sent Rs.130.00 from XX1522 to CLASSICMULTISALESamp on 21-Oct-26. UPI ref no. 666000773159. Not you? Tap https://kotk.in/KOTAKD/IHAeZG to report -Kotak"
        val parsed = Parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals(TransactionType.OUTFLOW, parsed.type)
        assertEquals(130.00, parsed.amount, 0.001)
        assertEquals("CLASSICMULTISALESamp", parsed.title)
        assertEquals(Category.SHOPPING, parsed.category)
        assertEquals("666000773159", parsed.refNo)
    }

    @Test
    fun parseUserSample2_receivedFromAniket() {
        val sms = "Received Rs.45.00 from Aniket Pundlik Ratho in your Kotak811 a/c XX1522 on 21-Oct-26. UPI ref no. 568403700601. View balance: https://kotk.in/KOTAKD/IHCwpa -Kotak"
        val parsed = Parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals(TransactionType.INFLOW, parsed.type)
        assertEquals(45.00, parsed.amount, 0.001)
        assertEquals("Aniket Pundlik Ratho", parsed.title)
        assertEquals(Category.OTHER, parsed.category)
        assertEquals("568403700601", parsed.refNo)
    }

    @Test
    fun parseUserSample3_sentToLolgePetroleum() {
        val sms = "Sent Rs.112.86 from XX1522 to Lolge petroleum on 21-Oct-26. UPI ref no. 666009755482. Not you? Tap https://kotk.in/KOTAKD/IGwLMe to report -Kotak"
        val parsed = Parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals(TransactionType.OUTFLOW, parsed.type)
        assertEquals(112.86, parsed.amount, 0.001)
        assertEquals("Lolge petroleum", parsed.title)
        assertEquals(Category.TRANSPORT, parsed.category)
        assertEquals("666009755482", parsed.refNo)
    }

    @Test
    fun parseUserSample4_receivedFromMrVaishnav() {
        val sms = "Received Rs.10000.00 from Mr VAISHNAV PRAVIN P in your Kotak811 a/c XX1522 on 11-Oct-26. UPI ref no. 330324369966. View balance: https://kotk.in/KOTAKD/H3mcRy -Kotak"
        val parsed = Parser.parse(sms)

        assertNotNull(parsed)
        parsed!!
        assertEquals(TransactionType.INFLOW, parsed.type)
        assertEquals(10000.00, parsed.amount, 0.001)
        assertEquals("VAISHNAV PRAVIN P", parsed.title)
        assertEquals(Category.OTHER, parsed.category)
        assertEquals("330324369966", parsed.refNo)
    }

    @Test
    fun parseExclusions_otpShouldReturnNull() {
        val otpSms = "123456 is your Kotak net banking OTP. Do not share with anyone."
        val parsed = Parser.parse(otpSms)
        assertNull(parsed)
    }

    @Test
    fun parseExclusions_genericPromoShouldReturnNull() {
        val sms = "Get pre-approved personal loan of up to Rs 5 Lakhs instantly. Click here."
        val parsed = Parser.parse(sms)
        assertNull(parsed)
    }
}
