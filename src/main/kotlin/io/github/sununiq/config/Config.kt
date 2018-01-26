package io.github.sununiq.config

import java.util.*

data class Config(
        var timeOut: Int = 20_000,
        val delay: Int = 50 + Random().nextInt(500),
        val userAgent: String = UserAgent.getAgent(),
        val threadNum: Int = 1,
        var queueSize: Int = 0
) {
    companion object {
        fun default() = Config()
    }
}

object UserAgent {
    private const val SAFARI_FOR_MAC = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50"
    private const val IE_9_FOR_WIN = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;"
    private const val IE_8_FOR_WIN = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)"
    private const val IE_7_FOR_WIN = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)"
    private const val FIREFOX_FOR_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1"
    private const val OPERA_FOR_MAC = "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11"
    private const val CHROME_FOR_MAC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11"
    private const val TENCENT_TT = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)"
    private const val THE_WORLD = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; The World)"
    private const val SOUGOU = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)"
    private const val QIHU_360 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)"

    /**
     * 移动端UA
     */
    private const val SAFARI_FOR_IPHONE = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5"

    fun getAgent(): String {
        val fields = UserAgent.javaClass.declaredFields
                .filter {
                    it.type.isAssignableFrom(String::class.java)
                }
        return fields[Random().nextInt(fields.size)].get(UserAgent).toString()
    }
}