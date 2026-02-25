package com.autotranslate.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TranslationDao {
    @Insert
    suspend fun insert(entity: TranslationHistoryEntity)

    @Query("SELECT * FROM translation_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TranslationHistoryEntity>>

    @Query("SELECT * FROM translation_history WHERE originalText LIKE '%' || :query || '%' OR translatedText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun search(query: String): Flow<List<TranslationHistoryEntity>>

    @Query("DELETE FROM translation_history")
    suspend fun deleteAll()
}
