package com.xcleans.apm.db

/**
 * Keep it Simple and Stupid
 */
interface ITable {

    @SinceKotlin("1.0")
    fun createSql(): String

    @SinceKotlin("1.0")
    fun tableName(): String
}