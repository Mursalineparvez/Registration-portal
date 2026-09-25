package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE studentRegistrationId = :regId OR registrationNumber = :regId LIMIT 1")
    suspend fun getUserByRegistrationId(regId: String): UserEntity?

    @Query("SELECT * FROM users WHERE studentRegistrationId = :regId OR registrationNumber = :regId LIMIT 1")
    fun getUserByRegistrationIdFlow(regId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY id DESC")
    fun getRegisteredStudents(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE registrationStatus = :status ORDER BY id DESC")
    fun getUsersByRegistrationStatus(status: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY registeredAt DESC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE registrationId = :regId LIMIT 1")
    fun getStudentByRegIdFlow(regId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE registrationId = :regId LIMIT 1")
    suspend fun getStudentByRegId(regId: String): StudentEntity?

    @Query("SELECT * FROM students WHERE categoryId = :categoryId ORDER BY registeredAt DESC")
    fun getStudentsByCategory(categoryId: String): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students WHERE status = 'VERIFIED'")
    fun getVerifiedCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM students WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET status = :status WHERE registrationId = :regId")
    suspend fun updateStudentStatus(regId: String, status: String)

    @Query("DELETE FROM students WHERE registrationId = :regId")
    suspend fun deleteStudent(regId: String)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)
}

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    @Query("SELECT * FROM schools WHERE division = :division ORDER BY name ASC")
    fun getSchoolsByDivision(division: String): Flow<List<SchoolEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: SchoolEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<SchoolEntity>)

    @Update
    suspend fun updateSchool(school: SchoolEntity)

    @Query("DELETE FROM schools WHERE id = :id")
    suspend fun deleteSchool(id: Long)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId ORDER BY id ASC")
    fun getQuestionsByCategory(categoryId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId ORDER BY id ASC")
    suspend fun getQuestionsListByCategory(categoryId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE categoryId = :categoryId AND subject = :subject ORDER BY id ASC")
    fun getQuestionsByCategoryAndSubject(categoryId: String, subject: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Long): QuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: Long)
}

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY id DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE categoryId = :categoryId ORDER BY id DESC")
    fun getExamsByCategory(categoryId: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: Long): ExamEntity?

    @Query("SELECT * FROM exams WHERE status = 'LIVE' LIMIT 1")
    fun getLiveExamFlow(): Flow<ExamEntity?>

    // Student Progress Tracking queries
    @Query("SELECT * FROM exams WHERE studentRegistrationId = :studentRegistrationId ORDER BY id DESC")
    fun getExamsForStudent(studentRegistrationId: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE studentRegistrationId = :studentRegistrationId AND isCompleted = 0 ORDER BY id DESC")
    fun getExamsInProgressForStudent(studentRegistrationId: String): Flow<List<ExamEntity>>

    @Query("SELECT * FROM exams WHERE studentRegistrationId = :studentRegistrationId AND isCompleted = 1 ORDER BY id DESC")
    fun getCompletedExamsForStudent(studentRegistrationId: String): Flow<List<ExamEntity>>

    @Query("UPDATE exams SET progressPercentage = :progress, questionsAnswered = :answered, currentScore = :score, isCompleted = :completed, timeSpentSeconds = :timeSpent, lastAttemptDate = :lastAttemptDate WHERE id = :examId")
    suspend fun updateStudentExamProgress(examId: Long, progress: Int, answered: Int, score: Double, completed: Boolean, timeSpent: Long, lastAttemptDate: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamEntity>)

    @Update
    suspend fun updateExam(exam: ExamEntity)

    @Query("UPDATE exams SET status = :status WHERE id = :id")
    suspend fun updateExamStatus(id: Long, status: String)

    @Query("DELETE FROM exams WHERE id = :id")
    suspend fun deleteExam(id: Long)
}

@Dao
interface ExamAttemptDao {
    @Query("SELECT * FROM exam_attempts WHERE studentRegistrationId = :regId AND examId = :examId LIMIT 1")
    suspend fun getAttempt(examId: Long, regId: String): ExamAttemptEntity?

    @Query("SELECT * FROM exam_attempts WHERE studentRegistrationId = :regId ORDER BY id DESC")
    fun getAttemptsForStudent(regId: String): Flow<List<ExamAttemptEntity>>

    @Query("SELECT * FROM exam_attempts ORDER BY submitTime DESC")
    fun getAllAttempts(): Flow<List<ExamAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExamAttemptEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempts(attempts: List<ExamAttemptEntity>)

    @Update
    suspend fun updateAttempt(attempt: ExamAttemptEntity)

    @Query("UPDATE exam_attempts SET status = :status WHERE id = :id")
    suspend fun updateAttemptStatus(id: Long, status: String)

    @Query("UPDATE exam_attempts SET score = :score, status = :status WHERE id = :id")
    suspend fun updateAttemptScoreAndStatus(id: Long, score: Double, status: String)

    @Query("DELETE FROM exam_attempts WHERE id = :id")
    suspend fun deleteAttempt(id: Long)
}

@Dao
interface ResultDao {
    @Query("SELECT * FROM results WHERE isPublished = 1 ORDER BY rank ASC")
    fun getPublishedResults(): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results ORDER BY rank ASC")
    fun getAllResults(): Flow<List<ResultEntity>>

    @Query("SELECT * FROM results WHERE studentRegistrationId = :regId LIMIT 1")
    fun getResultByStudent(regId: String): Flow<ResultEntity?>

    @Query("SELECT * FROM results WHERE categoryId = :categoryId ORDER BY rank ASC")
    fun getResultsByCategory(categoryId: String): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<ResultEntity>)

    @Query("UPDATE results SET isPublished = :published")
    suspend fun setPublishStatus(published: Boolean)

    @Query("UPDATE results SET selectionStatus = :status WHERE id = :resultId")
    suspend fun updateSelectionStatus(resultId: Long, status: String)

    @Query("DELETE FROM results WHERE id = :id")
    suspend fun deleteResult(id: Long)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE status = 'PUBLISHED' ORDER BY id DESC")
    fun getPublishedAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    @Update
    suspend fun updateAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)
}

@Dao
interface VolunteerDao {
    @Query("SELECT * FROM volunteers ORDER BY id DESC")
    fun getAllVolunteers(): Flow<List<VolunteerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteer(volunteer: VolunteerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolunteers(volunteers: List<VolunteerEntity>)

    @Update
    suspend fun updateVolunteer(volunteer: VolunteerEntity)

    @Query("UPDATE volunteers SET status = :status WHERE id = :id")
    suspend fun updateVolunteerStatus(id: Long, status: String)

    @Query("DELETE FROM volunteers WHERE id = :id")
    suspend fun deleteVolunteer(id: Long)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<AuditLogEntity>)
}

@Dao
interface SystemSettingDao {
    @Query("SELECT * FROM system_settings")
    fun getAllSettings(): Flow<List<SystemSettingEntity>>

    @Query("SELECT settingValue FROM system_settings WHERE settingKey = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SystemSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: List<SystemSettingEntity>)
}
