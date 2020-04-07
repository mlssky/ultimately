package com.xcleans.apm.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by mengliwei on 2020/4/1.
 */
@SinceKotlin("1.0")
interface IDatabase {

    @SinceKotlin("1.0")
    fun name(): String

    @SinceKotlin("1.0")
    fun version(): Int

    /**
     * executor create database table
     * @param db SQLiteDatabase?
     */
    @SinceKotlin("1.0")
    fun onCreate(db: SQLiteDatabase?)

    /**
     *
     * @param db SQLiteDatabase?
     * @param oldVersion Int
     * @param newVersion Int
     */
    @SinceKotlin("1.0")
    fun onUpgrade(
        db: SQLiteDatabase?, oldVersion: Int, newVersion: Int
    )

    /**
     *
     * @param db SQLiteDatabase?
     * @param oldVersion Int
     * @param newVersion Int
     */
    @SinceKotlin("1.0")
    fun onDowngrade(
        db: SQLiteDatabase?, oldVersion: Int, newVersion: Int
    )

    /**
     * init table
     * @param table ITable
     */
    @SinceKotlin("1.0")
    fun addTable(table: ITable)

    @SinceKotlin("1.0")
    fun getTables(): MutableList<ITable>

    fun initSQLiteOpenHelper(sqLiteOpenHelperAbs: SQLiteOpenHelper)
}