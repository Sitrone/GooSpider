package io.github.sununiq.response

import io.github.sununiq.request.Request

/**
 * 结果集
 * item : 结果
 * requests : 结果页面解析出来的新的请求
 */
data class Result<T>(
        val item: T,
        val requests: ArrayList<Request<T>> = ArrayList<Request<T>>()
)

fun <T> Result<T>.addRequest(request: Request<T>) = this.requests.add(request)

fun <T> Result<T>.addRequests(requests: MutableList<Request<T>>) = requests.forEach { this.addRequest(it) }