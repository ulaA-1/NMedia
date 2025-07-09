package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostDaoImpl

class AppDb private constructor(db: SQLiteDatabase) {
    val postDao: PostDao = PostDaoImpl(db)

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): AppDb {
            val helper = object : SQLiteOpenHelper(context, "app.db", null, 1) {
                override fun onCreate(db: SQLiteDatabase) {
                    db.execSQL("""
    CREATE TABLE posts (
        id INTEGER PRIMARY KEY,
        author TEXT NOT NULL,
        published TEXT NOT NULL,
        content TEXT NOT NULL,
        likedByMe BOOLEAN NOT NULL DEFAULT 0,
        likes INTEGER NOT NULL DEFAULT 0,
        shares INTEGER NOT NULL DEFAULT 0,
        views INTEGER NOT NULL DEFAULT 0,
        video TEXT
    );
""".trimIndent())

                }

                override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    // No migration for now
                }
            }
            return AppDb(helper.writableDatabase)
        }
    }
}