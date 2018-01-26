package io.github.sununiq

import io.github.sununiq.config.Config
import io.github.sununiq.config.EventManager
import io.github.sununiq.config.EventType
import io.github.sununiq.download.Downloader
import io.github.sununiq.scheduler.Scheduler
import io.github.sununiq.spider.BaseSpider
import io.github.sununiq.util.NamedThreadFactory
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 *
 */
class Engine<E> {
    val spiders: ArrayList<BaseSpider<E>> = ArrayList()
    var config: Config by Delegates.notNull()

    fun start() = SpiderEngine(this.spiders, this.config).start()

    companion object {
        fun <T> add(spider: BaseSpider<T>): Engine<T> {
            return add(spider, Config.default())
        }

        fun <T> add(spider: BaseSpider<T>, config: Config): Engine<T> {
            val engine = Engine<T>()
            engine.spiders.add(spider)
            engine.config = config
            return engine
        }
    }
}

/**
 *
 */
class SpiderEngine<T>(
        private val spiders: List<BaseSpider<T>>,
        private val config: Config,
        private var isRunning: Boolean = false,
        private val scheduler: Scheduler<T> = Scheduler()
) {
    private val log = LoggerFactory.getLogger(SpiderEngine::class.java)

    private val executorService = Executors.newFixedThreadPool(config.threadNum, NamedThreadFactory("task"))

    fun start() {
        EventManager.runEvent(EventType.GLOBAL, this.config)

        this.isRunning = true

        this.spiders.forEach { spider ->
            val localConfig = config.copy()
            spider.config = localConfig

            log.info("BaseSpider [{}] started ...", spider.name)
            log.info("BaseSpider [{}] config [{}]", spider.name, localConfig)

            val requests = spider.startUrls.map {
                spider.makeRequest(it)
            }

            spider.requests.addAll(requests)
            this.scheduler.addRequests(requests)

            EventManager.runEvent(EventType.SPECIFIC, localConfig)
        }

        // 后台生产
        val downloadTread = Thread {
            while (isRunning) {
                if (!scheduler.hasRequest()) {
                    TimeUnit.MILLISECONDS.sleep(100)
                    continue
                }
                val request = scheduler.nextRequest()
                executorService.execute(Downloader(scheduler, request))
                TimeUnit.MILLISECONDS.sleep(request.spider.config.delay.toLong())
            }
        }

        downloadTread.isDaemon = true
        downloadTread.name = "download-thread"
        downloadTread.start()

        this.complete()
    }

    /**
     * start background task
     */
    private fun complete() {
        while (isRunning) {
            if (!scheduler.hasResponse()) {
                TimeUnit.MILLISECONDS.sleep(100)
                continue
            }

            val response = scheduler.nextResponse()
            val parser = response.request.parser
            val result = parser.invoke(response)

            if (!result.requests.isEmpty()) {
                scheduler.addRequests(result.requests)
            }

            val item = result.item
            val pipelines = response.request.spider.pipelines
            pipelines.forEach { it.process(item, response.request) }
        }
    }


    fun stop() {
        isRunning = false
        scheduler.clear()
        log.info("stop spider.")
    }
}