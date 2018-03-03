package io.github.sununiq

import io.github.sununiq.config.Config
import io.github.sununiq.pipeline.Pipeline
import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import io.github.sununiq.response.Result
import io.github.sununiq.response.addRequest
import io.github.sununiq.response.css
import io.github.sununiq.spider.BaseSpider
import io.github.sununiq.util.getFilePath
import io.github.sununiq.util.println
import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import org.junit.Test
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

class MeizituSpiderTest {

    @Test
    fun testJiandanImg() {
        val spider = MeizituSpider("JiandanMeizitu")
//        Engine.add(spider, Config.default()).start()
        val s = "2cc0JvUeKRxikDlW58U1Wjgo2+Fhd5pkUyD0fpF7mDWVWt+iClzKWFLLV3ASctzx8qN9N2amknEYrcvu4ZpGoxn6/4Z2atoawqtCzJD4cax2NY1hSwdG+Q"
        spider.jiandanLoadImg(s).println()
    }
}

class MeizituSpider(name: String) : BaseSpider<List<String>?>(name) {
    private val log = LoggerFactory.getLogger(MeizituSpider::class.java)

    init {
        this.addStartUrls(
                "http://jandan.net/ooxx/page-58#comments"
        )
    }

    override fun onStart(config: Config) {
        /**
         * 添加获取对象的处理方法
         */
        this.addPipeline(object : Pipeline<List<String>?> {
            override fun process(item: List<String>?, request: Request<List<String>?>) {
                runBlocking {
                    item?.let {
                        it.forEach {
                            log.debug("content is: {}", it)
                            downloadImg(it, "")
                        }
                    }
                }
            }
        })
    }

    override fun parse(response: Response<List<String>?>): Result<List<String>?> {
        val elements = response.css("#comments ol.commentlist div.text a")

        val titles = elements?.map {
            "http:" + it.attr("href")
        }

        val result = Result(titles)

        // 获取下一页 URL
        val nextEl = response.css("#nav_prev")
        if (null != nextEl && nextEl.size > 0) {
            val nextPageUrl = nextEl[0].attr("href")
            val nextReq = this.generateRequest(nextPageUrl)
            result.addRequest(nextReq)
        }
        return result
    }

    private suspend fun downloadImg(url: String, location: String) {
        val dir = getFilePath(location).toAbsolutePath().normalize().toString()
        val fileType = url.takeLastWhile { it == '.' }.toString()
        val fileName = dir + File.separator + System.currentTimeMillis() + "." + fileType
        try {
            org.apache.http.client.fluent.Request.Get(url).execute().saveContent(File(fileName))
        } catch (e: Throwable) {
            log.error("Failed to save $url", e)
        }
    }

    fun jiandanLoadImg(m: String, r: String = "hT3tnqweJ77hyVF7FDOpaD81ktyKFh8X", d: Int = 0): String {
        val rr = DigestUtils.md5Hex(r)
        val o = DigestUtils.md5Hex(rr.take(16))
        val n = DigestUtils.md5Hex(rr.takeLast(rr.length - 16))

        val l = m.take(4)
        val c = o + DigestUtils.md5Hex(o + l)
        val mm = m.takeLast(m.length - 4)
        val k = String(Base64.decodeBase64(mm + "=".repeat(4 - mm.length % 4)),StandardCharsets.UTF_8)

        val h = IntArray(256) { i: Int -> i }
        val b = IntArray(256) { i: Int -> c[i % c.length].toInt() }
        var f = 0
        (0..255).forEach { i ->
            f = (f + h[i] + b[i]) % 256
            run { val tmp = h[i]; h[i] = h[f]; h[f] = tmp }
        }
        var t = ""
        var p = 0
        f = 0
        (0..(k.length - 1)).forEach { i ->
            p = (p + 1) % 256
            f = (f + h[p]) % 256
            run { val tmp = h[p]; h[p] = h[f]; h[f] = tmp }
            t += (k[i].toInt() xor h[(h[p] + h[f]) % 256]).toChar()
        }
        return t.takeLast(t.length - 26)
    }
}

