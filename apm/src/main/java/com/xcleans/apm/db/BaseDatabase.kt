package com.xcleans.apm.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by mengliwei on 2020/4/1.
 */
abstract class BaseDatabase : IDatabase {

    private var mTables = arrayListOf<ITable>()
    private var mSQLiteOpenHelper: SQLiteOpenHelper? = null

    override fun onCreate(db: SQLiteDatabase?) {
        mTables.forEach {
            db?.execSQL(it.createSql())
        }
    }

    override fun addTable(table: ITable) {
        mTables.takeIf { !it.contains(table) }?.run {
            this.add(table)
        }
    }

    override fun getTables(): MutableList<ITable> = mTables

    override fun initSQLiteOpenHelper(sqLiteOpenHelperAbs: SQLiteOpenHelper) {
        mSQLiteOpenHelper = sqLiteOpenHelperAbs
    }

    fun ss() {
        val db = mSQLiteOpenHelper?.writableDatabase
    }


}