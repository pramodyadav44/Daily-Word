package com.pramod.dailyword.db.local.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.room.*
import com.pramod.dailyword.db.model.WordOfTheDay

@Dao
interface WordOfTheDayDao {

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord WHERE date=:date")
    fun getJust(date: String): LiveData<WordOfTheDay?>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord WHERE WordOfTheDay.word=:word")
    fun getWord(word: String): LiveData<WordOfTheDay>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord ORDER BY dateTimeInMillis DESC LIMIT 1 OFFSET 0")
    fun getJustTopOne(): LiveData<WordOfTheDay?>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord ORDER BY dateTimeInMillis DESC LIMIT :count OFFSET 1")
    fun getFewExceptTopOne(count: Int): LiveData<List<WordOfTheDay>?>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord ORDER BY dateTimeInMillis DESC LIMIT :count")
    fun getFewFromTop(count: Int): LiveData<List<WordOfTheDay>?>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord ORDER BY dateTimeInMillis DESC")
    fun pagingSourceWords(): PagingSource<Int, WordOfTheDay>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord ORDER BY dateTimeInMillis DESC")
    fun dataSourceWords(): DataSource.Factory<Int, WordOfTheDay>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord WHERE date!=:date")
    fun getAllExcept(date: String): LiveData<List<WordOfTheDay>?>

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord WHERE date!=:date LIMIT :count")
    fun getFewExcept(date: String, count: Int): LiveData<List<WordOfTheDay>?>

    //nonLiveFunction
    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord WHERE date=:date")
    suspend fun getJustNonLive(date: String): WordOfTheDay?

    @Query("SELECT * FROM WordOfTheDay LEFT JOIN Bookmark ON WordOfTheDay.word==Bookmark.bookmarkedWord ORDER BY dateTimeInMillis DESC LIMIT 1 OFFSET 0")
    suspend fun getJustTopOneNonLive(): WordOfTheDay?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(word: List<WordOfTheDay>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(wordOfTheDay: WordOfTheDay): Long

    @Update
    suspend fun update(wordOfTheDay: WordOfTheDay): Int

    @Query("DELETE FROM WordOfTheDay WHERE word=:word")
    suspend fun delete(word: String): Int

    @Query("DELETE FROM WordOfTheDay")
    suspend fun deleteAll(): Int

}