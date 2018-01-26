package io.github.sununiq.scheduler

import io.github.sununiq.request.Request
import io.github.sununiq.response.Response
import java.util.concurrent.LinkedBlockingQueue

/**
 * 调度器
 */
class Scheduler<T> {
    private val pended: HashSet<String> = HashSet()
    private val pending: LinkedBlockingQueue<Request<T>> = LinkedBlockingQueue()
    private val result: LinkedBlockingQueue<Response<T>> = LinkedBlockingQueue()

    fun addRequest(request: Request<T>) {
        if (!pended.contains(request.url)) {
            pending.put(request)
            pended.add(request.url)
        }
    }

    fun addResponse(response: Response<T>) = result.put(response)

    fun hasRequest() = pending.size > 0

    fun hasResponse() = result.size > 0

    fun nextRequest(): Request<T> = pending.take()

    fun nextResponse(): Response<T> = result.take()

    fun addRequests(requests: List<Request<T>>) = requests.forEach { addRequest(it) }

    fun clear() = pending.clear()
}