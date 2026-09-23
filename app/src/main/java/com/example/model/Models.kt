package com.example.model

enum class OlympiadCategory(val displayName: String, val classes: String, val ageGroup: String) {
    PRIMARY("Primary", "Class 3 - 5", "Under 11"),
    JUNIOR("Junior", "Class 6 - 8", "Under 14"),
    SECONDARY("Secondary", "Class 9 - 10 / SSC", "Under 16"),
    SPECIAL("Special", "Class 11 - 12", "Preparatory")
}

enum class UserRole {
    STUDENT,
    MANAGER,
    ADMIN
}

data class UserProfile(
    val username: String, // e.g. "600032"
    val name: String,
    val email: String,
    val mobile: String,
    val role: UserRole,
    val permissions: List<String> = emptyList(),
    val instituteName: String = "",
    val classGrade: String = "",
    val category: OlympiadCategory = OlympiadCategory.JUNIOR,
    val division: String = "Dhaka",
    val district: String = "Dhaka",
    val upazila: String = "Dhaka Metropolitan",
    val gender: String = "Male",
    val dateOfBirth: String = "2010-01-01",
    val loginCount: Int = 1,
    val confirmed: Boolean = true
)

enum class QuestionType {
    MCQ,
    NUMERIC
}

data class QuizQuestion(
    val id: Int,
    val code: String = "", // e.g. "Primary-Q1", "Junior-Q2"
    val subject: String,
    val category: OlympiadCategory,
    val questionBangla: String = "",
    val questionEnglish: String = "",
    val question: String = "", // legacy compatibility
    val questionType: QuestionType = QuestionType.MCQ,
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val correctNumericAnswer: String = "",
    val answerLabel: String = "", // e.g. "Answer: 30", "Answer: A"
    val diagramType: String = "NONE", // "SPEED_TIME_GRAPH", "LEVER_BALANCE", "FOOD_CHAIN", "FORCE_COORDINATE_GRAPH", "HYDRAULIC_SYRINGE", "TOROIDAL_PLANET"
    val explanation: String = "",
    val marks: Int = 2
)

data class PastPaper(
    val id: String,
    val year: Int,
    val stage: String,
    val category: OlympiadCategory,
    val totalMarks: Int,
    val timeMinutes: Int,
    val questionCount: Int,
    val downloadStatus: String = "Available"
)

data class Announcement(
    val id: String,
    val title: String,
    val date: String,
    val tag: String,
    val description: String,
    val isImportant: Boolean = false
)

data class ScienceTopic(
    val id: String,
    val subject: String,
    val title: String,
    val category: OlympiadCategory,
    val summary: String,
    val formulas: List<String> = emptyList(),
    val tips: List<String> = emptyList()
)

data class StudentResult(
    val rollNumber: String,
    val name: String,
    val category: OlympiadCategory,
    val district: String,
    val school: String,
    val marks: Double,
    val nationalRank: Int,
    val medal: String,
    val selectedForCamp: Boolean
)
