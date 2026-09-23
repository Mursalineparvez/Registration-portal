package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val name: String,
    val role: String, // STUDENT, SUPER_ADMIN, ADMIN, REGISTRATION_ADMIN, EXAM_ADMIN, QUESTION_ADMIN, RESULT_ADMIN, VOLUNTEER_COORDINATOR, VIEWER, Manager
    val studentRegistrationId: String? = null,
    val email: String,
    val mobile: String,
    val gender: String = "Male",
    val dateOfBirth: String = "2017-07-17 00:00:00",
    val address: String = "",
    val bio: String = "",
    val lastIp: String = "103.166.170.13",
    val loginCount: Int = 1,
    val lastLogin: String = "2026-08-23 09:00:45",
    val status: Int = 1,
    val isConfirmed: Boolean = false,
    val permissions: String = "view_backend,view_users,edit_users,view_questions,view_stats",
    val createdAt: String = "2023-07-17 05:02:09",
    val updatedAt: String = "2026-08-23 09:00:45",
    val deletedAt: String = "",
    val instituteName: String = "",
    val className: String = "",
    val categoryName: String = "",
    val division: String = "",
    val district: String = "",
    val upazila: String = ""
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val registrationId: String, // e.g. "BDJSO-2026-000101"
    val fullName: String,
    val banglaName: String,
    val dob: String, // YYYY-MM-DD
    val gender: String, // Male, Female, Other
    val bloodGroup: String, // A+, B+, O+, AB+, A-, B-, O-, AB-
    val photoUrl: String = "",
    val className: String, // Class 3 to Class 10
    val categoryId: String, // PRIMARY, JUNIOR, SECONDARY, SPECIAL
    val schoolName: String,
    val institutionType: String, // Bangla Medium, English Medium, English Version, Madrasah
    val division: String,
    val district: String,
    val upazila: String,
    val studentRoll: String,
    val mobile: String,
    val email: String,
    val guardianName: String,
    val guardianRelation: String,
    val guardianMobile: String,
    val presentAddress: String,
    val permanentAddress: String = "",
    val status: String = "VERIFIED", // PENDING, VERIFIED, REJECTED, CANCELLED
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String, // PRIMARY, JUNIOR, SECONDARY, SPECIAL
    val name: String,
    val classRange: String, // "Class 3 - 5"
    val minClass: Int,
    val maxClass: Int,
    val durationMinutes: Int,
    val totalMarks: Double,
    val questionCount: Int,
    val description: String,
    val isRegistrationOpen: Boolean = true
)

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String,
    val division: String,
    val district: String,
    val upazila: String,
    val address: String,
    val contactPerson: String,
    val mobile: String,
    val email: String,
    val studentCount: Int = 0
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionText: String,
    val categoryId: String, // PRIMARY, JUNIOR, SECONDARY, SPECIAL
    val subject: String, // Physics, Chemistry, Biology, Mathematics, General Science
    val topic: String,
    val difficulty: String, // EASY, MEDIUM, HARD
    val marks: Double = 4.0,
    val negativeMarks: Double = 1.0,
    val questionType: String = "MCQ", // MCQ, MULTIPLE_SELECT, TRUE_FALSE, SHORT_ANSWER
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // "A", "B", "C", "D"
    val explanation: String = "",
    val questionCode: String = "", // e.g. "Primary-Q1", "Junior-Q2"
    val diagramType: String = "" // "DISTANCE_TIME", "LEVER", "LIFT_WORK", "MANGO_DROP", "ENERGY_PYRAMID", "MAGNET", "SYRINGES", "FUSE_CIRCUIT", "GRID_WORK", "TOROIDAL"
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val categoryId: String,
    val examDate: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val totalMarks: Double,
    val negativeMarkingRate: Double = 1.0,
    val status: String = "SCHEDULED" // DRAFT, SCHEDULED, LIVE, ENDED, RESULT_PROCESSING, PUBLISHED
)

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val studentRegistrationId: String,
    val startTime: Long,
    val submitTime: Long = 0L,
    val answersJson: String = "{}", // Map of questionId -> selectedOption
    val markedForReviewJson: String = "[]",
    val score: Double = 0.0,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val unattemptedCount: Int = 0,
    val status: String = "IN_PROGRESS" // IN_PROGRESS, SUBMITTED, EVALUATED
)

@Entity(tableName = "results")
data class ResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: Long,
    val studentRegistrationId: String,
    val studentName: String,
    val categoryId: String,
    val schoolName: String,
    val district: String,
    val division: String,
    val physicsMarks: Double = 0.0,
    val chemistryMarks: Double = 0.0,
    val biologyMarks: Double = 0.0,
    val mathMarks: Double = 0.0,
    val totalMarks: Double,
    val percentage: Double,
    val rank: Int,
    val selectionStatus: String = "SELECTED", // SELECTED, WAITING_LIST, NOT_SELECTED
    val isPublished: Boolean = true
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val tag: String, // GENERAL, EXAM, REGISTRATION, RESULT
    val publishDate: String,
    val status: String = "PUBLISHED" // DRAFT, PUBLISHED, ARCHIVED
)

@Entity(tableName = "volunteers")
data class VolunteerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val university: String,
    val department: String,
    val mobile: String,
    val email: String,
    val district: String,
    val team: String, // Technical, Academic, Communication, HR, Logistics, Question, Exam, Registration
    val role: String,
    val status: String = "APPROVED" // PENDING, APPROVED, REJECTED, INACTIVE
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String, // VERIFIED_STUDENT, PUBLISHED_RESULT, CREATED_EXAM, DELETED_QUESTION, etc.
    val userId: String,
    val role: String,
    val targetId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_settings")
data class SystemSettingEntity(
    @PrimaryKey val settingKey: String,
    val settingValue: String
)
