package tv.yixia.kotlinapp

import android.util.Log

/**
 * Created by mengliwei on 2019-07-09.
 */
class AndroidLogger : ILogger {

    override fun d(tag: String, msg: String, tr: Throwable): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        log(Log.DEBUG, tag, msg, tr);
    }

    override fun i(): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun w(): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun v(): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun log(level: Int, tag: String, msg: String, tr: Throwable): Void {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (level) {
            Log.VERBOSE -> Log.v(tag, msg, tr);
            Log.INFO -> Log.i(tag, msg, tr);
            Log.DEBUG -> Log.d(tag, msg, tr);
            Log.ERROR -> Log.e(tag, msg, tr);
            Log.WARN -> Log.e(tag, msg, tr);
            Log.ASSERT -> Log.wtf(tag, msg, tr);
        }
    }


}