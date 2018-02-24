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

class AlbumSpiderTest {

    @Test
    fun testDoubanAlbumTitle() {
        val spider = AlbumSpider("Douban Album")
        Engine.add(spider, Config.default()).start()
    }
}

private class AlbumSpider(name: String) : BaseSpider<List<String>?>(name) {
    private val log = LoggerFactory.getLogger(AlbumSpider::class.java)

    init {
        this.addStartUrls(
                "https://www.douban.com/photos/album/105181925/",
                "https://www.douban.com/photos/album/127493069/"
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

        val elements = response.css("#content div.article div.photo_wrap a.photolst_photo")

        val picPages = elements?.map { it.attr("href") }

        val result = Result(getPicUrl(picPages))

        // 获取下一页 URL
        val nextEl = response.css("#content > div > div.article > div.paginator > span.next > a")
        if (null != nextEl && nextEl.size > 0) {
            val nextPageUrl = nextEl[0].attr("href")
            val nextReq = this.generateRequest(nextPageUrl)
            result.addRequest(nextReq)
        }
        return result
    }

    private fun getPicUrl(titleUrls: List<String>?): List<String>? {
        val elements = LinkedList<String>()
        titleUrls?.let {
            it.forEach {
                val pageResp = downloadPage(it)
                val pageUrl = pageResp.css("#content div.photo-edit a")
                pageUrl?.attr("href")?.let { url -> elements.add(url) }
            }
        }

        return elements
    }
}
