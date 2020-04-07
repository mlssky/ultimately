package com.xcleans.apm.db

import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by mengliwei on 2020/4/1.
 */
class SQLiteOpenHelperAbs constructor(
    context: Context? = null, database: IDatabase
) : SQLiteOpenHelper(context, database.name(), null, database.version(), null) {

    private var mDB: IDatabase = database

    private var mOpenCounter: AtomicInteger = AtomicInteger()

    init {
        mDB.initSQLiteOpenHelper(this)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        mDB.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        mDB.onUpgrade(db, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        mDB.onDowngrade(db, oldVersion, newVersion)
    }


    override fun getWritableDatabase(): SQLiteDatabase {
        mOpenCounter.incrementAndGet()
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        mOpenCounter.incrementAndGet()
        return super.getReadableDatabase()
    }

    /**
     * 打开使用完成立即关闭
     */
    override fun close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            super.close()
        }
    }

}