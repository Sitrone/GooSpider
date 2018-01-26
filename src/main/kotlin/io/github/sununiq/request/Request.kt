package io.github.sununiq.request

import io.github.sununiq.response.Response
import io.github.sununiq.response.Result
import io.github.sununiq.spider.BaseSpider
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * 请求
 */
data class Request<T>(val spider: BaseSpider<T>, val url: String, val parser: (response: Response<T>) -> Result<T>) {
    var method: String = "get"

    val headers = HashMap<String, String>()

    var charset: Charset = StandardCharsets.UTF_8

    var contentType: String = "text/html; charset=UTF-8"
}
