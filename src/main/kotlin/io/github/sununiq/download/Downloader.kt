package io.github.sununiq.download

import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import io.github.sununiq.scheduler.Scheduler
import org.apache.http.HttpHeaders
import org.slf4j.LoggerFactory

/**
 * downloader
 */
class Downloader<T>(private val scheduler: Scheduler<T>, val request: Request<T>) : Runnable {
    private val log = LoggerFactory.getLogger(Downloader::class.java)

    override fun run() {
        log.debug("[{}] begin request.", request.url)

        val body = org.apache.http.client.fluent.Request
                .Get(request.url)
                .addHeader(HttpHeaders.CONTENT_TYPE, request.contentType)
                .execute()
                .returnContent()
                .asString(request.charset)
        val response = Response<T>(request, body)
        scheduler.addResponse(response)

        log.debug("Finished request.")
    }
}