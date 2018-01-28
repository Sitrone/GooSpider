package io.github.sununiq

import io.github.sununiq.config.Config
import io.github.sununiq.pipeline.Pipeline
import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import io.github.sununiq.response.Result
import io.github.sununiq.response.addRequest
import io.github.sununiq.response.css
import io.github.sununiq.spider.BaseSpider
import org.junit.Test
import org.slf4j.LoggerFactory

class V2exSpiderTest {

    @Test
    fun testV2exTitle() {
        val spider = V2exSpider("v2ex")
        Engine.add(spider, Config.default()).start()
    }
}

private class V2exSpider(name: String) : BaseSpider<List<String>?>(name) {
    private val log = LoggerFactory.getLogger(V2exSpider::class.java)

    init {
        this.addStartUrls(
                "https://www.v2ex.com/?tab=tech",
                "https://www.v2ex.com/?tab=play",
                "https://www.v2ex.com/?tab=creative"
        )
    }

    override fun onStart(config: Config) {

        /**
         * 添加获取对象的处理方法
         */
        this.addPipeline(object : Pipeline<List<String>?> {
            override fun process(item: List<String>?, request: Request<List<String>?>) {
                item?.let {
                    it.forEach {
                        log.debug("content is: {}", it)
                    }
                }
            }
        })
    }

    override fun parse(response: Response<List<String>?>): Result<List<String>?> {

        val elements = response.css("#Main span.item_title a")

        val titles = elements?.map { it.text() }

        val result = Result(titles)

        // 获取下一页 URL
        val nextEl = response.css("#content > div > div.article > div.paginator > span.next > a")
        if (null != nextEl && nextEl.size > 0) {
            val nextPageUrl = nextEl[0].attr("href")
            val nextReq = this.generateRequest(nextPageUrl)
            result.addRequest(nextReq)
        }
        return result
    }
}
