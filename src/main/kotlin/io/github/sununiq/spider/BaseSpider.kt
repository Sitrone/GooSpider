package io.github.sununiq.spider

import io.github.sununiq.config.Config
import io.github.sununiq.config.EventManager
import io.github.sununiq.config.EventType
import io.github.sununiq.pipeline.Pipeline
import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import io.github.sununiq.response.Result
import java.util.function.Consumer
import kotlin.properties.Delegates

/**
 *
 */
abstract class BaseSpider<T> constructor(val name: String) {
    var config: Config by Delegates.notNull()
    val startUrls = ArrayList<String>()
    val requests = ArrayList<Request<T>>()
    val pipelines = ArrayList<Pipeline<T>>()

    init {
        EventManager.registerEvent(EventType.GLOBAL) { config -> onStart(config) }
    }

    fun addStartUrls(vararg urls: String) = urls.map { startUrls.add(it) }

    /**
     * execute before scraw
     */
    abstract fun onStart(config: Config)

    /**
     * 添加 Pipeline 处理
     */
    protected fun addPipeline(pipeline: Pipeline<T>): BaseSpider<T> {
        this.pipelines.add(pipeline)
        return this
    }

    /**
     * 解析生成请求
     */
    fun makeRequest(url: String): Request<T> = makeRequest(url) { response -> parse(response) }

    /**
     * 解析生成请求
     */
    fun makeRequest(url: String, parser: (response: Response<T>) -> Result<T>): Request<T> {
        return Request<T>(this, url, parser)
    }

    /**
     * parse dom
     */
    abstract fun parse(response: Response<T>): Result<T>

    protected fun resetRequest(requestConsumer: (Request<T>) -> Unit) {
        this.resetRequest(this.requests, requestConsumer)
    }

    private fun resetRequest(requests: List<Request<T>>, requestConsumer: (Request<T>) -> Unit) {
        requests.forEach(requestConsumer)
    }

}