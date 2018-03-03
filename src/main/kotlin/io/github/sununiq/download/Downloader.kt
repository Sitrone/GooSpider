package io.github.sununiq.download

import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import io.github.sununiq.response.SimpleResponse
import io.github.sununiq.scheduler.Scheduler
import io.github.sununiq.util.toHeaders
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * downloader
 */
class Downloader<T>(private val scheduler: Scheduler<T>, val request: Request<T>) : Runnable {
    private val log = LoggerFactory.getLogger(Downloader::class.java)

    override fun run() {
        log.debug("[{}] begin request.", request.url)

        try {
            val body = org.apache.http.client.fluent.Request
                    .Get(request.url)
                    .addHeader(HttpHeaders.USER_AGENT, request.headers[HttpHeaders.USER_AGENT])
                    .addHeader(HttpHeaders.CONTENT_TYPE, request.headers[HttpHeaders.CONTENT_TYPE])
                    .execute()
                    .returnContent()
                    .asString(request.charset)
            val response = Response<T>(request, body)
            scheduler.addResponse(response)
        } catch (e: IOException) {
            log.error("Failed to download ${request.url}", e)
        }


        log.debug("Finished request.")
    }
}

fun downloadPage(url: String,
                 contentType: String = "text/html; charset=UTF-8",
                 charset: Charset = StandardCharsets.UTF_8,
                 userAgent:String = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)"
) = SimpleResponse(org.apache.http.client.fluent.Request
        .Get(url)
        .addHeader(HttpHeaders.USER_AGENT, userAgent)
        .addHeader(HttpHeaders.CONTENT_TYPE, contentType)
        .execute()
        .returnContent()
        .asString(charset)
)