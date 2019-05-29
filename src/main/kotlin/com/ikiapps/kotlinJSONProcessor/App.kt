package com.ikiapps.kotlinJSONProcessor

import com.beust.klaxon.Json
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.File
import java.io.StringReader

class App {
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

    fun printDaily()
    {
        val klx = Klaxon()
        val dataKey = "Time Series (Daily)"
        klx.parseJsonObject(StringReader(File(pathname).readText()))
            .filter { it.key == dataKey }
            .map { it.value as JsonObject }.first()
            .map {
                val open = Pair(it.key, klx.parseFromJsonObject<Daily>(it.value as JsonObject)?.open)
                val close = Pair(it.key, klx.parseFromJsonObject<Daily>(it.value as JsonObject)?.close)
                println(open.first + "," + open.second + "," + close.second)
            }
    }
}

fun main(args: Array<String>) {
    App().printDaily()
}
