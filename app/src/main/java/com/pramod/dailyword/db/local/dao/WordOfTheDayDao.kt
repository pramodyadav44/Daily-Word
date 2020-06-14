package com.pramod.dailyword.db.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.pramod.dailyword.db.model.WordOfTheDay

@Dao
interface WordOfTheDayDao {

    @Query("SELECT * FROM WordOfTheDay WHERE date=:date")
    fun getJust(date: String): LiveData<WordOfTheDay?>

    @Query("SELECT * FROM WordOfTheDay ORDER BY dateTimeInMillis DESC LIMIT 1 OFFSET 0")
    fun getJustTopOne(): LiveData<WordOfTheDay?>

    @Query("SELECT * FROM WordOfTheDay ORDER BY dateTimeInMillis DESC LIMIT :count OFFSET 1")
    fun getFewExceptTopOne(count: Int): LiveData<List<WordOfTheDay>?>

    @Query("SELECT * FROM WordOfTheDay ORDER BY dateTimeInMillis DESC")
    fun getAll(): DataSource.Factory<Int, WordOfTheDay>

    @Query("SELECT * FROM WordOfTheDay WHERE date!=:date")
    fun getAllExcept(date: String): LiveData<List<WordOfTheDay>?>

    @Query("SELECT * FROM WordOfTheDay WHERE date!=:date LIMIT :count")
    fun getFewExcept(date: String, count: Int): LiveData<List<WordOfTheDay>?>

    //nonLiveFunction
    @Query("SELECT * FROM WordOfTheDay WHERE date=:date")
    suspend fun getJustNonLive(date: String): WordOfTheDay?

    @Query("SELECT * FROM WordOfTheDay ORDER BY dateTimeInMillis DESC LIMIT 1 OFFSET 0")
    suspend fun getJustTopOneNonLive(): WordOfTheDay?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(word: List<WordOfTheDay>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(wordOfTheDay: WordOfTheDay): Long

    @Update
    suspend fun update(wordOfTheDay: WordOfTheDay): Int

    @Query("DELETE FROM WordOfTheDay WHERE word=:word COLLATE NOCASE")
    suspend fun delete(word: String)

    @Query("DELETE FROM WordOfTheDay")
    suspend fun deleteAll()

}