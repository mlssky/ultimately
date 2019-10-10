package tv.yixia.kotlinapp

/**
 * Created by mengliwei on 2019-07-09.
 */
interface ILogger {
    fun d(tag: String, msg: String, tr: Throwable): Void;
    fun i(): Void;
    fun w(): Void;
    fun v(): Void;
    fun log(level:Int,tag: String, msg: String, tr: Throwable): Void;
}