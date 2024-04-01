package com.mgws.adownkyi.utils

import androidx.annotation.IntDef
import kotlin.reflect.KClass

object AppMessagePublisher {

    @IntDef(INFO, WARN, ERROR, ALL)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Level

    const val INFO = 0b1
    const val WARN = 0b10
    const val ERROR = 0b100
    const val ALL = INFO or WARN or ERROR

    data class Message(
        @Level val level: Int,
        val value: String,
    )

    private val eventEmitter = HashMap<KClass<*>, HashMap<KClass<*>, (Message) -> Unit>>()

    /**
     * @param clazz 对这个类发布的消息感兴趣
     */
    fun Any.unSubscribe(clazz: KClass<*>) {
        val messageMap = eventEmitter[clazz] ?: return
        messageMap.remove(this@unSubscribe::class)
    }

    /**
     * @param clazz 对这个类发布的消息感兴趣
     * @param level  消息级别
     * @param listener 消息处理
     */
    fun Any.subscribe(
        clazz: KClass<*>,
        @Level level: Int,
        listener: (String) -> Unit,
    ) {
        if (eventEmitter[clazz] == null) {
            eventEmitter[clazz] = hashMapOf(
                this::class to { if (it.level and level != 0) listener(it.value) }
            )
        }
    }

    fun Any.publish(message: Message) {
        val messageMap = eventEmitter[this@publish::class] ?: return
        messageMap.values.forEach { it(message) }
    }

}

fun infoMsg(value: String) = AppMessagePublisher.Message(AppMessagePublisher.INFO, value)
fun warnMsg(value: String) = AppMessagePublisher.Message(AppMessagePublisher.WARN, value)
fun errorMsg(value: String) = AppMessagePublisher.Message(AppMessagePublisher.ERROR, value)
fun msg(value: String) = AppMessagePublisher.Message(AppMessagePublisher.ALL, value)