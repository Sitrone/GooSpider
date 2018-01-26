package io.github.sununiq.pipeline

import io.github.sununiq.request.Request

/**
 * 处理响应获得的数据
 */
interface Pipeline<T> {

    /**
     * @param item 响应获取的数据
     * @param request 对应的请求
     */
    fun process(item: T, request: Request<T>)
}