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

class ImportNewSpiderTest {

    @Test
    fun testImportNew() {
        val spider = ImportNewSpider("importnew")
        Engine.add(spider, Config.default()).start()
    }
}

private class ImportNewSpider(name: String) : BaseSpider<List<Article>?>(name) {
    private val log = LoggerFactory.getLogger(ImportNewSpider::class.java)

    init {
        this.addStartUrls(
                "http://www.importnew.com/all-posts/page/1"
        )
    }

    override fun onStart(config: Config) {

        /**
         * 添加获取对象的处理方法
         */
        this.addPipeline(object : Pipeline<List<Article>?> {
            override fun process(item: List<Article>?, request: Request<List<Article>?>) {
                item?.let {
                    it.forEach {
                        log.debug("content is: {}", it)
                    }
                }
            }
        })
    }

    override fun parse(response: Response<List<Article>?>): Result<List<Article>?> {

        val elements = response.css("#wrapper div.grid-8 div.post-thumb a")

        val titles = elements?.map {
            Article(it.attr("title"), it.attr("href"))
        }

        val result = Result(titles)

        // 获取下一页 URL
        val nextEl = response.css("#wrapper .navigation.margin-20 .next.page-numbers")
        if (null != nextEl && nextEl.size > 0) {
            val nextPageUrl = nextEl[0].attr("href")
            val nextReq = this.generateRequest(nextPageUrl)
            result.addRequest(nextReq)
        }
        return result
    }
}

data class Article(val title: String, val url: String)
