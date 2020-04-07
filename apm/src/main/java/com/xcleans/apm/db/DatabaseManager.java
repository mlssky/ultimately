//package com.xcleans.apm.db;
//
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * 针对多线程访问同一个数据库
// * <p>
// * Tips:
// * 1.
// * android.database.sqlite.SQLiteDatabaseLockedException: database is locked (code 5)
// * To use database with multiple threads we need to make sure we are using one database connection.
// * 2.
// * java.lang.IllegalStateException: attempt to re-open an already-closed object: SQLiteDatabase
// * 如果多个线程同时打开关闭
// */
//public class DatabaseManager {
//
//    private AtomicInteger mOpenCounter = new AtomicInteger();
//
//    //指令重排
//    private static volatile DatabaseManager  instance;
//    private static          SQLiteOpenHelper mDatabaseHelper;
//    private                 SQLiteDatabase   mDatabase;
//
//    /**
//     * @param helper
//     */
//    public static synchronized void initializeInstance(SQLiteOpenHelper helper) {
//        if (instance == null) {
//            instance = new DatabaseManager();
//            mDatabaseHelper = helper;
//        }
//    }
//
//    public static synchronized DatabaseManager getInstance() {
//        if (instance == null) {
//            throw new IllegalStateException(DatabaseManager.class.getSimpleName() + " is not initialized, call initializeInstance(..) method first.");
//        }
//        return instance;
//    }
//
//    public synchronized SQLiteDatabase openDatabase() {
//        if (mOpenCounter.incrementAndGet() == 1) {
//            // Opening new database
//            mDatabase = mDatabaseHelper.getWritableDatabase();
//        }
//        return mDatabase;
//    }
//
//    public synchronized void closeDatabase() {
//        if (mOpenCounter.decrementAndGet() == 0) {
//            // Closing database
//            mDatabase.close();
//        }
//    }
//}