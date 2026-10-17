package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "video_projects")
data class VideoProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val originalPrompt: String,
    val empatheticEnglishScript: String,
    val visualAestheticId: String,
    val cameraMotionId: String,
    val aspectRatioId: String,
    val voicePresetId: String,
    val durationSeconds: Int,
    val seed: Int,
    val mp4FilePath: String?,
    val keyframeBase64: String?,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY createdAtTimestamp DESC")
    fun getAllProjects(): Flow<List<VideoProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProjectEntity): Long

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)

    @Query("DELETE FROM video_projects")
    suspend fun clearAllProjects()
}

@Database(entities = [VideoProjectEntity::class], version = 1, exportSchema = false)
abstract class StudioDatabase : RoomDatabase() {
    abstract fun videoProjectDao(): VideoProjectDao

    companion object {
        @Volatile
        private var INSTANCE: StudioDatabase? = null

        fun getDatabase(context: Context): StudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudioDatabase::class.java,
                    "cinemotion_studio_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class StudioRepository(private val dao: VideoProjectDao) {
    val allProjects: Flow<List<VideoProjectEntity>> = dao.getAllProjects()

    suspend fun saveProject(project: VideoProjectEntity): Long = dao.insertProject(project)

    suspend fun deleteProject(id: Int) = dao.deleteProjectById(id)

    suspend fun clearHistory() = dao.clearAllProjects()
}
