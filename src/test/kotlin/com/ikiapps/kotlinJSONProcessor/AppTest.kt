package com.ikiapps.kotlinJSONProcessor

import com.beust.klaxon.*
import java.io.File
import java.io.StringReader
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.test.*

/**
 * These tests demonstrate some ways, and some ways not to, to parse JSON with Klaxon.
 */
class AppTest
{
    val pathname = "src/test/resources/uber-daily.json"

    data class Daily
    (
        @Json(name = "1. open")
        val open: String,
        @Json(name = "2. high")
        val high: String,
        @Json(name = "3. low")
        val low: String,
        @Json(name = "4. close")
        val close: String,
        @Json(name = "5. adjusted close")
        val adjustedClose: String,
        @Json(name = "6. volume")
        val volume: String,
        @Json(name = "7. dividend amount")
        val dividendAmount: String,
        @Json(name = "8. split coefficient")
        val splitCoefficient: String
    )

    @Test fun testReadFile()
    {
        assertTrue(File(pathname).isFile(), "Not a file at: " + pathname)
    }

    @Test fun testFirstAttempt0()
    {
        val content = File(pathname).readText()
        assertNotNull(content, "Missing file content for " + pathname + ".")
    }

    // Gives:
    //     KlaxonException: Unexpected character at position 1: 'r' (ASCII: 114)'
    @Test fun testFirstAttempt1()
    {
        try {
            Klaxon().parse<String>(File(pathname).toString())
        } catch (e: Exception) {
            assertTrue(e is KlaxonException, "Bad exception.")
            return
        }
        fail("Failed to get an exception.")
    }

    @Test fun testFirstAttempt2()
    {
        try {
            Klaxon().parse<File>(StringReader(File(pathname).toString()))
        } catch (e: Exception) {
            assertTrue(e is KlaxonException, "Bad exception.")
            return
        }
        fail("Failed to get an exception.")
    }

    @Test fun testFirstAttempt3()
    {
        val data3 = Klaxon().parse<String>(File(pathname).readText())
        assertEquals(data3?.length, 0, "Unexpected data.")
    }

    // Get the keys
    @Test fun testFirstAttempt4()
    {
        val data4 = Klaxon().parseJsonObject(StringReader(File(pathname).readText()))
        assertEquals(data4.size, 2, "Mismatched keys.")
    }

    @Test fun testParseDaily1()
    {
        val cnt = 0
        val dataKey = "Time Series (Daily)"
        val klx = Klaxon()
        val f = File(pathname).readText()
        val parsed = klx.parseJsonObject(StringReader(f))
        val timeSeries = parsed.filter {
            it.key == dataKey
        }.map {
            it.value as JsonObject
        }
        timeSeries.first().values.forEach {
            cnt.inc()
            val day = klx.parseFromJsonObject<Daily>(it as JsonObject)
        }
        assertEquals(cnt, 0, "Expected fall-through.")
    }

    suspend fun parseDaily(): Int
    {
        val dataKey = "Time Series (Daily)"
        val klx = Klaxon()
        val f = File(pathname).readText()
        val parsed = klx.parseJsonObject(StringReader(f))
        val timeSeries = parsed.filter {
            it.key == dataKey
        }.map {
            it.value as JsonObject
        }
        val days = ArrayList<Daily?>()
        timeSeries.first().values.forEach {
            days.add(klx.parseFromJsonObject<Daily>(it as JsonObject))
        }
        return days.size
    }

    fun waitForCount() = runBlocking {
        val count = async {
            parseDaily()
        }
        count.await()
    }

    @Test fun testAsyncDailyCount()
    {
        val expected = 13
        val result = waitForCount()
        assertEquals(result, expected, "Bad count of " + result + ", expected " + expected + ".")
    }

    @Test fun testParseDaily2()
    {
        val expected = 13
        val klx = Klaxon()
        val dataKey = "Time Series (Daily)"
        val days = klx.parseJsonObject(StringReader(File(pathname).readText()))
            .filter { it.key == dataKey }
            .map { it.value as JsonObject }.first().values
            .map { klx.parseFromJsonObject<Daily>(it as JsonObject)?.close }
        assertEquals(days.size, expected, "Bad count of " + days.size + ", expected " + expected + ".")
    }

    @Test fun testParseDaily3()
    {
        val expected = 13
        val klx = Klaxon()
        val dataKey = "Time Series (Daily)"
        val days = klx.parseJsonObject(StringReader(File(pathname).readText()))
            .filter { it.key == dataKey }
            .map { it.value as JsonObject }.first()
            .map { Pair(it.key, klx.parseFromJsonObject<Daily>(it.value as JsonObject)?.close) }
        assertEquals(days.size, expected, "Bad count of " + days.size + ", expected " + expected + ".")
    }
}
