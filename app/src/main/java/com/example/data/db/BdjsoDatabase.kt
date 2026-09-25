package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AnnouncementDao
import com.example.data.dao.AuditLogDao
import com.example.data.dao.CategoryDao
import com.example.data.dao.ExamAttemptDao
import com.example.data.dao.ExamDao
import com.example.data.dao.QuestionDao
import com.example.data.dao.ResultDao
import com.example.data.dao.SchoolDao
import com.example.data.dao.StudentDao
import com.example.data.dao.SystemSettingDao
import com.example.data.dao.UserDao
import com.example.data.dao.VolunteerDao
import com.example.data.model.AnnouncementEntity
import com.example.data.model.AuditLogEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ExamEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.ResultEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.StudentEntity
import com.example.data.model.SystemSettingEntity
import com.example.data.model.UserEntity
import com.example.data.model.VolunteerEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.room.withTransaction

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        CategoryEntity::class,
        SchoolEntity::class,
        QuestionEntity::class,
        ExamEntity::class,
        ExamAttemptEntity::class,
        ResultEntity::class,
        AnnouncementEntity::class,
        VolunteerEntity::class,
        AuditLogEntity::class,
        SystemSettingEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BdjsoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun categoryDao(): CategoryDao
    abstract fun schoolDao(): SchoolDao
    abstract fun questionDao(): QuestionDao
    abstract fun examDao(): ExamDao
    abstract fun examAttemptDao(): ExamAttemptDao
    abstract fun resultDao(): ResultDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun volunteerDao(): VolunteerDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun systemSettingDao(): SystemSettingDao

    companion object {
        @Volatile
        private var INSTANCE: BdjsoDatabase? = null

        fun getDatabase(context: Context): BdjsoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BdjsoDatabase::class.java,
                    "bdjso_database.db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(database: BdjsoDatabase) {
            database.withTransaction {
                database.userDao().insertUsers(DatabaseSeedData.users)
                database.categoryDao().insertCategories(DatabaseSeedData.categories)
                database.schoolDao().insertSchools(DatabaseSeedData.schools)
                database.studentDao().insertStudents(DatabaseSeedData.students)
                database.questionDao().insertQuestions(DatabaseSeedData.questions)
                database.examDao().insertExams(DatabaseSeedData.exams)
                database.examAttemptDao().insertAttempts(DatabaseSeedData.examAttempts)
                database.resultDao().insertResults(DatabaseSeedData.results)
                database.announcementDao().insertAnnouncements(DatabaseSeedData.announcements)
                database.volunteerDao().insertVolunteers(DatabaseSeedData.volunteers)
                database.auditLogDao().insertLogs(DatabaseSeedData.auditLogs)
                database.systemSettingDao().insertSettings(DatabaseSeedData.systemSettings)
            }
        }
    }
}
