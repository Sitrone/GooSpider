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

class DoubanSpiderTest {

    @Test
    fun test() {
        val spider = DoubanSpider("douban")
        Engine.add(spider, Config.default()).start()
    }
}

/**
 *
 */
class DoubanSpider(name: String) : BaseSpider<List<String>?>(name) {
    private val log = LoggerFactory.getLogger(DoubanSpider::class.java)

    init {
        this.addStartUrls(
                "https://movie.douban.com/tag/爱情",
                "https://movie.douban.com/tag/喜剧",
                "https://movie.douban.com/tag/动画",
                "https://movie.douban.com/tag/动作",
                "https://movie.douban.com/tag/史诗",
                "https://movie.douban.com/tag/犯罪"
        )
    }

    override fun onStart(config: Config) {

        /**
         * 添加获取对象的处理方法
         */
        this.addPipeline(object : Pipeline<List<String>?> {
            override fun process(item: List<String>?, request: Request<List<String>?>) {
                item?.let {
                    log.debug("save to file: {}", it)
                }
            }
        })
    }

    override fun parse(response: Response<List<String>?>): Result<List<String>?>{
        val elements = response.css("#content table .pl2 a")

        val titles = elements?.map{ it.text() }

        val result = Result(titles)

        // 获取下一页 URL
        val nextEl = response.css("#content > div > div.article > div.paginator > span.next > a")
        if (null != nextEl && nextEl.size > 0) {
            val nextPageUrl = nextEl[0].attr("href")
            val nextReq = this.makeRequest(nextPageUrl)
            result.addRequest(nextReq)
        }
        return result
    }

}

