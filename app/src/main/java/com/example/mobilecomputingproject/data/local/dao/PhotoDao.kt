package com.example.mobilecomputingproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobilecomputingproject.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE id = :id")
    fun getPhotoById(id: Int): Flow<PhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun clearAll()

    suspend fun clearAndInsert(photos: List<PhotoEntity>) {
        clearAll()
        insertAll(photos)
    }
}
