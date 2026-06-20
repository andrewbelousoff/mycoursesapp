package com.example.courses.feature.data.repository

import android.content.Context
import com.example.courses.core.domain.model.Course
import com.example.courses.core.domain.repository.CoursesRepository
import com.example.courses.core.data.repository.CoursesDatabase
import com.example.courses.core.data.repository.LikedCourseEntity
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import java.io.InputStreamReader

class CoursesRepositoryImpl(
    private val context: Context,
    private val gson: Gson = Gson()
) : CoursesRepository {

    private val _coursesState = MutableStateFlow<List<Course>>(emptyList())
    private var isInitialized = false
    
    // Подключаем DAO нашей скомпилированной базы данных Room
    private val likedCoursesDao = CoursesDatabase.getDatabase(context).likedCoursesDao()

    override fun getCourses(): Flow<List<Course>> {
        if (!isInitialized) {
            loadCoursesFromAssets()
        }
        return _coursesState.asStateFlow()
    }

    override suspend fun toggleLike(courseId: Int) {
        // 1. Проверяем в базе данных Room, стоял ли уже лайк
        val isAlreadyLiked = likedCoursesDao.isLiked(courseId)
        
        // 2. Атомарно сохраняем или удаляем ID из базы Room
        if (isAlreadyLiked) {
            likedCoursesDao.deleteLike(LikedCourseEntity(courseId))
        } else {
            likedCoursesDao.insertLike(LikedCourseEntity(courseId))
        }

        // 3. Синхронизируем состояние с экраном (сравниваем чистые Int-идентификаторы)
        _coursesState.update { currentList ->
            currentList.map {
                if (it.id == courseId) it.copy(isLiked = !it.isLiked) else it
            }
        }
    }

    private fun loadCoursesFromAssets() {
        try {
            context.assets.open("courses.json").use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val fileText = reader.readText()
                    val jsonArray = JsonParser.parseString(fileText).asJsonObject
                        .getAsJsonArray("courses")
                    val domainCourses = mutableListOf<Course>()
                    
                    for (element in jsonArray) {
                        val obj = element.asJsonObject
                        
                        // Извлекаем числовой ID из JSON
                        val idInt = obj.get("id").asString.toIntOrNull() ?: 0
                        
                        // Адаптируем цену: превращаем число из JSON в String для модели Course
                        val priceStr = obj.get("price").asInt.toString()

                        val title = obj.get("title").asString
                        val description = obj.get("description").asString
                        val rating = obj.get("rating").asFloat
                        val startDate = if (obj.has("startDate")) obj.get("startDate").asString else (if (obj.has("start_date")) obj.get("start_date").asString else "")

                        // Спрашиваем у Room, был ли этот курс лайкнут ранее (используем runBlocking для инициализации)
                        val isLikedInDb = runBlocking { likedCoursesDao.isLiked(idInt) }

                        // Собираем оригинальный объект Course строго по вашей структуре
                        domainCourses.add(
                            Course(
                                id = idInt,
                                title = title,
                                description = description,
                                price = priceStr,
                                rating = rating,
                                startDate = startDate,
                                isLiked = isLikedInDb
                            )
                        )
                    }
                    _coursesState.value = domainCourses
                    isInitialized = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
