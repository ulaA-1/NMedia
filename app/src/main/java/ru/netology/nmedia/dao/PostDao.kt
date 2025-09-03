package ru.netology.nmedia.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE isVisible = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun maxId(): Long?

    @Query("SELECT COUNT(*) FROM PostEntity WHERE isVisible = 0")
    fun countHidden(): Flow<Int>

    @Query("UPDATE PostEntity SET isVisible = 1 WHERE isVisible = 0")
    suspend fun showAllHidden()

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity?

    @Update
    suspend fun updatePost(post: PostEntity)

}
