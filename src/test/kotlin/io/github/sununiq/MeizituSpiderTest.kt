package io.github.sununiq

import io.github.sununiq.config.Config
import io.github.sununiq.config.UserAgent
import io.github.sununiq.pipeline.Pipeline
import io.github.sununiq.request.Request
import io.github.sununiq.response.*
import io.github.sununiq.spider.BaseSpider
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Paths
import java.time.Instant

class MeizituSpiderTest {

    @Test
    fun testMeizitu() {
        val spider = MeizituSpider("meizitu")
        Engine.add(spider, Config.default()).start()
    }
}

/**
 *
 */
class MeizituSpider(name: String) : BaseSpider<List<String>?>(name) {
    private val log = LoggerFactory.getLogger(MeizituSpider::class.java)

    private val storageDir = Paths.get(Thread.currentThread().contextClassLoader.getResource("").toURI())
            .toAbsolutePath().normalize().toString()

    init {
        this.addStartUrls(
                "http://www.meizitu.com/a/pure.html",
                "http://www.meizitu.com/a/cute.html",
                "http://www.meizitu.com/a/sexy.html",
                "http://www.meizitu.com/a/fuli.html",
                "http://www.meizitu.com/a/legs.html"
        )
    }


    override fun onStart(config: Config) {
        /**
         * 添加获取对象的处理方法
         */
        this.addPipeline(object : Pipeline<List<String>?> {
            override fun process(item: List<String>?, request: Request<List<String>?>) {
                item?.forEach { imgUrl ->
                    log.info("start to download: {}", imgUrl)
                    org.apache.http.client.fluent.Request.Get(imgUrl)
                            .addHeader("Referer", request.url)
                            .addHeader("User-Agent", UserAgent.getAgent())
                            .execute()
                            .saveContent(File(storageDir, Instant.now().toEpochMilli().toString() + ".jpg"))
                }

                log.info("[{}] finished download.", request.url)
            }
        })

        this.requests.forEach { this.resetRequest(it) }
    }


    override fun parse(response: Response<List<String>?>): Result<List<String>?> {
        val elements = response.css("#maincontent > div.inWrap > ul > li:nth-child(1) > div > div > a")
        log.info("elements size: {}", elements?.size)

        val hrefs = elements?.run {
            map {
                it.attr("href")
            }
        }

        val requests = hrefs
                ?.run {
                    map { href ->
                        makeRequest(href, picParser)
                    }.map {
                        resetRequest(it)
                    }
                }

        val result = Result(hrefs)
        result.addRequests(requests as MutableList<Request<List<String>?>>)

        // 获取下一页 URL
        val nextEl = response.css("#wp_page_numbers > ul > li > a")
                ?.firstOrNull {
                    element -> "下一页" == element.text()
                }

        if (nextEl != null) {
            val nextPageUrl = "http://www.meizitu.com/a/" + nextEl.attr("href")
            val nextReq = this.makeRequest(nextPageUrl, picParser)
            result.addRequest(this.resetRequest(nextReq))
        }
        return result
    }

    private val picParser: (response: Response<List<String>?>) -> Result<List<String>?> = {
        val elements1 = it.css("#picture > p > img")
        val src = elements1?.map { element ->
            element.attr("src")
        }
        Result<List<String>?>(src)
    }

    private fun resetRequest(request: Request<List<String>?>): Request<List<String>?> {
        request.contentType = "text/html; charset=gb2312"
        request.charset = Charset.forName("gb2312")
        return request
    }

}

