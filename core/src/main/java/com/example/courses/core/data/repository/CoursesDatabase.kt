package com.example.courses.core.data.repository

import android.content.Context
import androidx.room.*

// 1. Таблица для хранения ID лайкнутых курсов
@Entity(tableName = "liked_courses")
data class LikedCourseEntity(
    @PrimaryKey val courseId: Int
)

// 2. Интерфейс для работы с базой данных
@Dao
interface LikedCoursesDao {
    @Query("SELECT EXISTS(SELECT 1 FROM liked_courses WHERE courseId = :id)")
    suspend fun isLiked(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLike(like: LikedCourseEntity)

    @Delete
    suspend fun deleteLike(like: LikedCourseEntity)
}

// 3. Главный класс Базы Данных
@Database(entities = [LikedCourseEntity::class], version = 1, exportSchema = false)
abstract class CoursesDatabase : RoomDatabase() {
    abstract fun likedCoursesDao(): LikedCoursesDao

    companion object {
        @Volatile
        private var INSTANCE: CoursesDatabase? = null

        fun getDatabase(context: Context): CoursesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CoursesDatabase::class.java,
                    "courses_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
