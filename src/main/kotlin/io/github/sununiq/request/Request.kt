package io.github.sununiq.request

import io.github.sununiq.response.Response
import io.github.sununiq.response.Result
import io.github.sununiq.spider.BaseSpider
import org.apache.http.HttpHeaders
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * 请求
 */
data class Request<T>(val spider: BaseSpider<T>, val url: String, val parser: (response: Response<T>) -> Result<T>) {
    var method: String = "get"

    val headers = HashMap<String, String>().apply {
        put(HttpHeaders.USER_AGENT, spider.config.userAgent)
        put(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
    }

    var charset: Charset = StandardCharsets.UTF_8
}


typealias Parser<T> = (response: Response<T>) -> Result<T>