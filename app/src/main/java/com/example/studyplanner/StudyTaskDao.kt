package com.example.studyplanner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudyTaskDao {

    @Insert
    suspend fun insertTask(task: StudyTask): Long

    @Update
    suspend fun updateTask(task: StudyTask): Int

    @Delete
    suspend fun deleteTask(task: StudyTask): Int

    @Query("SELECT * FROM study_tasks ORDER BY dateTime ASC")
    suspend fun getAllTasks(): List<StudyTask>

    @Query("SELECT * FROM study_tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Int): StudyTask?
}