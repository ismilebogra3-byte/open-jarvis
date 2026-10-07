package com.openjarvis.automation

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context

class AutomationScheduleConverter {

    @TypeConverter
    fun fromSchedule(schedule: AutomationManager.AutomationSchedule): String {
        return when (schedule) {
            is AutomationManager.AutomationSchedule.Daily ->
                "daily:${schedule.hour}:${schedule.minute}"

            is AutomationManager.AutomationSchedule.Weekly ->
                "weekly:${schedule.dayOfWeek}:${schedule.hour}:${schedule.minute}"

            is AutomationManager.AutomationSchedule.Interval ->
                "interval:${schedule.intervalMs}"

            is AutomationManager.AutomationSchedule.Once ->
                "once:${schedule.atMs}"
        }
    }

    @TypeConverter
    fun toSchedule(value: String): AutomationManager.AutomationSchedule {
        val parts = value.split(":")

        return when (parts.firstOrNull()?.lowercase()) {
            "daily" -> {
                AutomationManager.AutomationSchedule.Daily(
                    hour = parts.getOrNull(1)?.toIntOrNull() ?: 0,
                    minute = parts.getOrNull(2)?.toIntOrNull() ?: 0
                )
            }

            "weekly" -> {
                AutomationManager.AutomationSchedule.Weekly(
                    dayOfWeek = parts.getOrNull(1)?.toIntOrNull() ?: 1,
                    hour = parts.getOrNull(2)?.toIntOrNull() ?: 0,
                    minute = parts.getOrNull(3)?.toIntOrNull() ?: 0
                )
            }

            "interval" -> {
                AutomationManager.AutomationSchedule.Interval(
                    intervalMs = parts.getOrNull(1)?.toLongOrNull()
                        ?: (60 * 60 * 1000L)
                )
            }

            "once" -> {
                AutomationManager.AutomationSchedule.Once(
                    atMs = parts.getOrNull(1)?.toLongOrNull()
                        ?: System.currentTimeMillis()
                )
            }

            else -> {
                AutomationManager.AutomationSchedule.Interval(
                    intervalMs = 60 * 60 * 1000L
                )
            }
        }
    }
}

@Dao
interface AutomationDao {

    @Query("SELECT * FROM automations ORDER BY name")
    suspend fun getAll(): List<AutomationManager.Automation>

    @Query("SELECT * FROM automations WHERE id = :id")
    suspend fun getById(id: String): AutomationManager.Automation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(automation: AutomationManager.Automation)

    @Query("UPDATE automations SET name = :name, command = :command, schedule = :schedule, enabled = :enabled, lastRun = :lastRun, lastResult = :lastResult, runCount = :runCount WHERE id = :id")
    suspend fun updateFields(
        id: String,
        name: String,
        command: String,
        schedule: AutomationManager.AutomationSchedule,
        enabled: Boolean,
        lastRun: Long?,
        lastResult: String?,
        runCount: Int
    )

    @Delete
    suspend fun delete(automation: AutomationManager.Automation)

    @Query("DELETE FROM automations WHERE id = :id")
    suspend fun deleteById(id: String)

    @androidx.room.Update
    suspend fun update(automation: AutomationManager.Automation)
}

@Database(
    entities = [AutomationManager.Automation::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AutomationScheduleConverter::class)
abstract class AutomationDB : RoomDatabase() {

    abstract fun automationDao(): AutomationDao

    companion object {
        @Volatile
        private var INSTANCE: AutomationDB? = null

        fun getInstance(context: Context): AutomationDB {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AutomationDB::class.java,
                    "automation.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
