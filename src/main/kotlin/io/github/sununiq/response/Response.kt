package io.github.sununiq.response

import io.github.sununiq.request.Request
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import us.codecraft.xsoup.XElements
import us.codecraft.xsoup.Xsoup

/**
 * 响应
 */
data class Response<T>(
        val request: Request<T>,
        val body: String
)

fun <T> Response<T>.css(css: String): Elements? = Jsoup.parse(this.body).select(css)


fun <T> Response<T>.xpath(xpath: String): XElements? = Xsoup.compile(xpath).evaluate(Jsoup.parse(this.body))

data class SimpleResponse(
        val body: String
)

fun SimpleResponse.css(css: String): Elements? = Jsoup.parse(this.body).select(css)


fun SimpleResponse.xpath(xpath: String): XElements? = Xsoup.compile(xpath).evaluate(Jsoup.parse(this.body))