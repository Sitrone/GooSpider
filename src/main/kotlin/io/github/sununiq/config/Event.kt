package io.github.sununiq.config

import java.util.*

enum class EventType {
    GLOBAL,

    SPECIFIC
}

object EventManager {
    private val events = HashMap<EventType, MutableList<(Config) -> Unit>>()

    /**
     * 注册事件
     */
    fun registerEvent(eventType: EventType, action: (Config) -> Unit) {
        var actionList = events[eventType]
        if (actionList == null)
            actionList = ArrayList<(Config) -> Unit>()

        actionList.add(action)
        events[eventType] = actionList
    }

    /**
     * 执行事件
     */
    fun runEvent(eventType: EventType, config: Config) {
        Optional.ofNullable(events[eventType])
                .ifPresent {
                    it.forEach {
                        it.invoke(config)
                    }
                }
    }
}