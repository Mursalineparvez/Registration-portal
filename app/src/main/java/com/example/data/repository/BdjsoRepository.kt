package com.example.data.repository

import com.example.data.db.BdjsoDatabase
import com.example.data.db.DatabaseSeedData
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class BdjsoRepository(private val database: BdjsoDatabase) {

    // Flows
    val allStudents: Flow<List<StudentEntity>> = database.studentDao().getAllStudents()
    val allCategories: Flow<List<CategoryEntity>> = database.categoryDao().getAllCategories()
    val allSchools: Flow<List<SchoolEntity>> = database.schoolDao().getAllSchools()
    val allQuestions: Flow<List<QuestionEntity>> = database.questionDao().getAllQuestions()
    val allExams: Flow<List<ExamEntity>> = database.examDao().getAllExams()
    val allExamAttempts: Flow<List<ExamAttemptEntity>> = database.examAttemptDao().getAllAttempts()
    val liveExam: Flow<ExamEntity?> = database.examDao().getLiveExamFlow()
    val publishedResults: Flow<List<ResultEntity>> = database.resultDao().getPublishedResults()
    val allResults: Flow<List<ResultEntity>> = database.resultDao().getAllResults()
    val announcements: Flow<List<AnnouncementEntity>> = database.announcementDao().getPublishedAnnouncements()
    val allAnnouncements: Flow<List<AnnouncementEntity>> = database.announcementDao().getAllAnnouncements()
    val volunteers: Flow<List<VolunteerEntity>> = database.volunteerDao().getAllVolunteers()
    val auditLogs: Flow<List<AuditLogEntity>> = database.auditLogDao().getAllLogs()
    val systemSettings: Flow<List<SystemSettingEntity>> = database.systemSettingDao().getAllSettings()
    val users: Flow<List<UserEntity>> = database.userDao().getAllUsers()

    // User operations
    fun searchUsers(query: String): Flow<List<UserEntity>> = database.userDao().searchUsers(query)
    suspend fun getUserById(id: Long): UserEntity? = database.userDao().getUserById(id)
    suspend fun updateUser(user: UserEntity, adminUser: String, role: String) {
        database.userDao().updateUser(user)
        logAction("UPDATE_USER", adminUser, role, user.username, "Updated user details for ${user.name}")
    }
    suspend fun deleteUser(id: Long, username: String, adminUser: String, role: String) {
        database.userDao().deleteUser(id)
        logAction("DELETE_USER", adminUser, role, username, "Deleted user record")
    }
    suspend fun saveUser(user: UserEntity): Long = database.userDao().insertUser(user)

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val userCount = database.userDao().getUserByUsername("super_admin")
        if (userCount == null) {
            BdjsoDatabase.seedDatabase(database)
        } else {
            val sampleAttempt = database.examAttemptDao().getAttempt(1L, "BDJSO-2026-000101")
            if (sampleAttempt == null) {
                database.examAttemptDao().insertAttempts(DatabaseSeedData.examAttempts)
            }
        }
    }

    // Exam Attempt & Moderation operations
    suspend fun updateAttemptStatus(id: Long, status: String, adminUser: String, role: String) {
        database.examAttemptDao().updateAttemptStatus(id, status)
        logAction("MODERATE_ATTEMPT", adminUser, role, "ATTEMPT-$id", "Status set to $status")
    }

    suspend fun evaluateAttempt(id: Long, score: Double, adminUser: String, role: String) {
        database.examAttemptDao().updateAttemptScoreAndStatus(id, score, "EVALUATED")
        logAction("EVALUATE_ATTEMPT", adminUser, role, "ATTEMPT-$id", "Graded with score $score")
    }

    suspend fun insertExamAttempt(attempt: ExamAttemptEntity): Long {
        return database.examAttemptDao().insertAttempt(attempt)
    }

    suspend fun seedSampleAttempts() = withContext(Dispatchers.IO) {
        database.examAttemptDao().insertAttempts(DatabaseSeedData.examAttempts)
    }

    // Student operations
    fun getStudentFlow(regId: String): Flow<StudentEntity?> = database.studentDao().getStudentByRegIdFlow(regId)
    suspend fun getStudent(regId: String): StudentEntity? = database.studentDao().getStudentByRegId(regId)
    fun getStudentAttempts(regId: String): Flow<List<ExamAttemptEntity>> = database.examAttemptDao().getAttemptsForStudent(regId)
    fun getStudentResult(regId: String): Flow<ResultEntity?> = database.resultDao().getResultByStudent(regId)

    suspend fun registerStudent(student: StudentEntity): String {
        database.studentDao().insertStudent(student)
        logAction("REGISTER_STUDENT", "SYSTEM", "STUDENT", student.registrationId, "Registered ${student.fullName} for category ${student.categoryId}")
        return student.registrationId
    }

    suspend fun updateStudentStatus(regId: String, status: String, adminUser: String, role: String) {
        database.studentDao().updateStudentStatus(regId, status)
        logAction("UPDATE_STUDENT_STATUS", adminUser, role, regId, "Changed registration status to $status")
    }

    suspend fun updateStudent(student: StudentEntity, adminUser: String, role: String) {
        database.studentDao().updateStudent(student)
        logAction("UPDATE_STUDENT_DETAILS", adminUser, role, student.registrationId, "Updated personal/academic details")
    }

    // Exam & Attempt operations
    suspend fun getQuestionsForCategory(categoryId: String): List<QuestionEntity> {
        return database.questionDao().getQuestionsListByCategory(categoryId)
    }

    suspend fun getExamById(id: Long): ExamEntity? = database.examDao().getExamById(id)

    suspend fun getAttempt(examId: Long, regId: String): ExamAttemptEntity? {
        return database.examAttemptDao().getAttempt(examId, regId)
    }

    suspend fun saveExamAttempt(attempt: ExamAttemptEntity): Long {
        return database.examAttemptDao().insertAttempt(attempt)
    }

    suspend fun submitAndEvaluateExam(
        examId: Long,
        studentRegId: String,
        answers: Map<Long, String>,
        markedForReview: Set<Long>
    ): ExamAttemptEntity {
        val exam = database.examDao().getExamById(examId)
        val student = database.studentDao().getStudentByRegId(studentRegId)
        val questions = database.questionDao().getQuestionsListByCategory(student?.categoryId ?: "PRIMARY")

        var correctCount = 0
        var wrongCount = 0
        var unattemptedCount = 0
        var physicsScore = 0.0
        var chemistryScore = 0.0
        var biologyScore = 0.0
        var mathScore = 0.0

        val answersJsonObj = JSONObject()
        answers.forEach { (qId, ans) -> answersJsonObj.put(qId.toString(), ans) }

        val reviewJsonArray = org.json.JSONArray()
        markedForReview.forEach { reviewJsonArray.put(it) }

        questions.forEach { q ->
            val studentAns = answers[q.id]
            val marks = q.marks
            val neg = q.negativeMarks

            if (studentAns.isNullOrBlank()) {
                unattemptedCount++
            } else if (studentAns.equals(q.correctAnswer, ignoreCase = true)) {
                correctCount++
                when (q.subject) {
                    "Physics" -> physicsScore += marks
                    "Chemistry" -> chemistryScore += marks
                    "Biology" -> biologyScore += marks
                    "Mathematics" -> mathScore += marks
                    else -> physicsScore += marks
                }
            } else {
                wrongCount++
                when (q.subject) {
                    "Physics" -> physicsScore = maxOf(0.0, physicsScore - neg)
                    "Chemistry" -> chemistryScore = maxOf(0.0, chemistryScore - neg)
                    "Biology" -> biologyScore = maxOf(0.0, biologyScore - neg)
                    "Mathematics" -> mathScore = maxOf(0.0, mathScore - neg)
                    else -> physicsScore = maxOf(0.0, physicsScore - neg)
                }
            }
        }

        val totalScore = physicsScore + chemistryScore + biologyScore + mathScore
        val maxTotal = if (questions.isNotEmpty()) questions.sumOf { it.marks } else 100.0
        val percentage = if (maxTotal > 0) (totalScore / maxTotal) * 100.0 else 0.0

        val attempt = ExamAttemptEntity(
            examId = examId,
            studentRegistrationId = studentRegId,
            startTime = System.currentTimeMillis() - 1800000,
            submitTime = System.currentTimeMillis(),
            answersJson = answersJsonObj.toString(),
            markedForReviewJson = reviewJsonArray.toString(),
            score = totalScore,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unattemptedCount = unattemptedCount,
            status = "EVALUATED"
        )
        database.examAttemptDao().insertAttempt(attempt)

        // Generate Result entry
        val selectionStatus = if (percentage >= 80.0) "SELECTED" else if (percentage >= 60.0) "WAITING_LIST" else "NOT_SELECTED"
        val result = ResultEntity(
            examId = examId,
            studentRegistrationId = studentRegId,
            studentName = student?.fullName ?: "Student",
            categoryId = student?.categoryId ?: "PRIMARY",
            schoolName = student?.schoolName ?: "BDJSO School",
            district = student?.district ?: "Dhaka",
            division = student?.division ?: "Dhaka",
            physicsMarks = physicsScore,
            chemistryMarks = chemistryScore,
            biologyMarks = biologyScore,
            mathMarks = mathScore,
            totalMarks = totalScore,
            percentage = percentage,
            rank = if (percentage >= 90.0) 1 else if (percentage >= 80.0) 2 else 3,
            selectionStatus = selectionStatus,
            isPublished = true
        )
        database.resultDao().insertResult(result)

        logAction("SUBMIT_EXAM", studentRegId, "STUDENT", "EXAM-$examId", "Scored $totalScore / $maxTotal ($percentage%)")
        return attempt
    }

    // Question Bank operations
    suspend fun insertQuestion(question: QuestionEntity, user: String, role: String): Long {
        val id = database.questionDao().insertQuestion(question)
        logAction("CREATE_QUESTION", user, role, "Q-$id", "Added ${question.subject} question (${question.difficulty})")
        return id
    }

    suspend fun updateQuestion(question: QuestionEntity, user: String, role: String) {
        database.questionDao().updateQuestion(question)
        logAction("UPDATE_QUESTION", user, role, "Q-${question.id}", "Updated question details")
    }

    suspend fun deleteQuestion(id: Long, user: String, role: String) {
        database.questionDao().deleteQuestion(id)
        logAction("DELETE_QUESTION", user, role, "Q-$id", "Removed question from question bank")
    }

    // Exam Management
    suspend fun insertExam(exam: ExamEntity, user: String, role: String): Long {
        val id = database.examDao().insertExam(exam)
        logAction("CREATE_EXAM", user, role, "EXAM-$id", "Created exam: ${exam.title}")
        return id
    }

    suspend fun updateExamStatus(id: Long, status: String, user: String, role: String) {
        database.examDao().updateExamStatus(id, status)
        logAction("UPDATE_EXAM_STATUS", user, role, "EXAM-$id", "Exam status set to $status")
    }

    suspend fun deleteExam(id: Long, user: String, role: String) {
        database.examDao().deleteExam(id)
        logAction("DELETE_EXAM", user, role, "EXAM-$id", "Deleted exam")
    }

    // Results & Selection
    suspend fun setPublishResults(published: Boolean, user: String, role: String) {
        database.resultDao().setPublishStatus(published)
        logAction("PUBLISH_RESULTS", user, role, "ALL", "Results publication set to $published")
    }

    suspend fun updateSelectionStatus(resultId: Long, status: String, user: String, role: String) {
        database.resultDao().updateSelectionStatus(resultId, status)
        logAction("UPDATE_SELECTION_STATUS", user, role, "RESULT-$resultId", "Selection updated to $status")
    }

    suspend fun insertResult(result: ResultEntity): Long {
        val id = database.resultDao().insertResult(result)
        logAction("INSERT_RESULT", "ADMIN", "ADMIN", "RESULT-$id", "Inserted score for ${result.studentName}")
        return id
    }

    suspend fun insertResults(results: List<ResultEntity>) {
        database.resultDao().insertResults(results)
    }

    suspend fun deleteResult(id: Long) {
        database.resultDao().deleteResult(id)
        logAction("DELETE_RESULT", "ADMIN", "ADMIN", "RESULT-$id", "Deleted score entry")
    }

    // School operations
    suspend fun insertSchool(school: SchoolEntity, user: String, role: String): Long {
        val id = database.schoolDao().insertSchool(school)
        logAction("ADD_SCHOOL", user, role, "SCHOOL-$id", "Added school ${school.name}")
        return id
    }

    suspend fun deleteSchool(id: Long, user: String, role: String) {
        database.schoolDao().deleteSchool(id)
        logAction("DELETE_SCHOOL", user, role, "SCHOOL-$id", "Deleted school")
    }

    // Volunteer operations
    suspend fun updateVolunteerStatus(id: Long, status: String, user: String, role: String) {
        database.volunteerDao().updateVolunteerStatus(id, status)
        logAction("UPDATE_VOLUNTEER", user, role, "VOL-$id", "Status changed to $status")
    }

    // Announcement operations
    suspend fun insertAnnouncement(announcement: AnnouncementEntity, user: String, role: String): Long {
        val id = database.announcementDao().insertAnnouncement(announcement)
        logAction("CREATE_ANNOUNCEMENT", user, role, "ANN-$id", "Published: ${announcement.title}")
        return id
    }

    suspend fun deleteAnnouncement(id: Long, user: String, role: String) {
        database.announcementDao().deleteAnnouncement(id)
        logAction("DELETE_ANNOUNCEMENT", user, role, "ANN-$id", "Deleted announcement")
    }

    // Settings
    suspend fun updateSetting(key: String, value: String, user: String, role: String) {
        database.systemSettingDao().insertSetting(SystemSettingEntity(key, value))
        logAction("UPDATE_SETTING", user, role, key, "Updated setting $key = $value")
    }

    // Audit Logging
    suspend fun logAction(action: String, userId: String, role: String, targetId: String, details: String) {
        database.auditLogDao().insertLog(
            AuditLogEntity(
                action = action,
                userId = userId,
                role = role,
                targetId = targetId,
                details = details,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
