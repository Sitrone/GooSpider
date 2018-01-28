package io.github.sununiq

import io.github.sununiq.config.Config
import io.github.sununiq.download.downloadPage
import io.github.sununiq.pipeline.Pipeline
import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import io.github.sununiq.response.Result
import io.github.sununiq.response.addRequest
import io.github.sununiq.response.css
import io.github.sununiq.spider.BaseSpider
import org.junit.Test
import org.slf4j.LoggerFactory
import java.util.*

class HaixiuzuSpiderTest {

    @Test
    fun test() {
        val spider = HaixiuSpider("qiushibaike")
        Engine.add(spider, Config.default()).start()
    }

    @Test
    fun testParseSinglePage() {
        val url = "https://www.douban.com/group/topic/112212732/"
        getPicUrl(url)?.forEach {
            println(it)
        }
    }

    private fun getPicUrl(titleUrl: String): List<String>? {
        val elements = LinkedList<String>()
        titleUrl.let {
            val pageResp = downloadPage(it)
            val pageUrls = pageResp.css("#content div.topic-doc img")

            pageUrls?.map { it.attr("src") }?.forEach {
                elements.add(it)
            }

        }

        return elements
    }
}

class HaixiuSpider(name: String) : BaseSpider<List<String>?>(name) {
    private val log = LoggerFactory.getLogger(DoubanSpider::class.java)

    init {
        this.addStartUrls(
                "https://www.douban.com/group/haixiuzu/discussion?start=0"
        )
    }

    override fun onStart(config: Config) {
        /**
         * 添加获取对象的处理方法
         */
        this.addPipeline(object : Pipeline<List<String>?> {
            override fun process(item: List<String>?, request: Request<List<String>?>) {
                item?.let {
                    log.debug("content is: {}", it)
                }
            }
        })
    }

    override fun parse(response: Response<List<String>?>): Result<List<String>?> {
        val elements = response.css("#content td.title a")

        val titles = elements?.map { it.attr("href") }

        val picUrls = getPicUrl(titles)

        val result = Result(picUrls)

        // 获取下一页 URL
        val nextEl = response.css("#content > div > div.article > div.paginator > span.next > a")
        if (null != nextEl && nextEl.size > 0) {
            val nextPageUrl = nextEl[0].attr("href")
            val nextReq = this.makeRequest(nextPageUrl)
            result.addRequest(nextReq)
        }
        return result
    }

    private fun getPicUrl(titleUrls: List<String>?): List<String>? {
        val elements = LinkedList<String>()
        titleUrls?.let {
            it.forEach {
                val pageResp = downloadPage(it)
                val pageUrls = pageResp.css("#content div.topic-doc img")
                pageUrls?.map { it.attr("src") }?.forEach {
                    elements.add(it)
                }
            }
        }

        return elements
    }
}