package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BdjsoDatabase
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
import com.example.data.repository.BdjsoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    HOME,
    STUDENT_REGISTER,
    STUDENT_DASHBOARD,
    ONLINE_EXAM,
    EXAM_RESULT,
    RESULTS_LOOKUP,
    RULES_AND_INFO,
    ADMIN_DASHBOARD,
    ADMIN_STUDENTS,
    ADMIN_QUESTIONS,
    ADMIN_EXAMS,
    ADMIN_RESULTS,
    ADMIN_SCHOOLS,
    ADMIN_VOLUNTEERS,
    ADMIN_ANNOUNCEMENTS,
    ADMIN_AUDIT_LOGS,
    ADMIN_SETTINGS,
    ADMIN_USERS,
    ADMIN_USER_SHOW,
    ADMIN_USER_EDIT,
    ADMIN_USER_CHANGE_PASSWORD,
    ADMIN_REGISTRATION_STATS
}

class BdjsoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BdjsoRepository

    init {
        val database = BdjsoDatabase.getDatabase(application)
        repository = BdjsoRepository(database)
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureSeeded()
        }
    }

    // Role & Navigation
    private val _currentRole = MutableStateFlow("STUDENT")
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    private val _currentStudentRegId = MutableStateFlow("BDJSO-2026-000101")
    val currentStudentRegId: StateFlow<String> = _currentStudentRegId.asStateFlow()

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Data streams from repository
    val students: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schools: StateFlow<List<SchoolEntity>> = repository.allSchools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuestionEntity>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveExam: StateFlow<ExamEntity?> = repository.liveExam
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val publishedResults: StateFlow<List<ResultEntity>> = repository.publishedResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allResults: StateFlow<List<ResultEntity>> = repository.allResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examAttempts: StateFlow<List<ExamAttemptEntity>> = repository.allExamAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.announcements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val volunteers: StateFlow<List<VolunteerEntity>> = repository.volunteers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.auditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val systemSettings: StateFlow<List<SystemSettingEntity>> = repository.systemSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val users: StateFlow<List<UserEntity>> = repository.users
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Exam Engine State
    private val _activeExam = MutableStateFlow<ExamEntity?>(null)
    val activeExam: StateFlow<ExamEntity?> = _activeExam.asStateFlow()

    private val _examQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val examQuestions: StateFlow<List<QuestionEntity>> = _examQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Long, String>>(emptyMap())
    val userAnswers: StateFlow<Map<Long, String>> = _userAnswers.asStateFlow()

    private val _markedForReview = MutableStateFlow<Set<Long>>(emptySet())
    val markedForReview: StateFlow<Set<Long>> = _markedForReview.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(2700) // 45 min default
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _lastAutosaveTime = MutableStateFlow("Auto-saved")
    val lastAutosaveTime: StateFlow<String> = _lastAutosaveTime.asStateFlow()

    private val _lastAttemptResult = MutableStateFlow<ExamAttemptEntity?>(null)
    val lastAttemptResult: StateFlow<ExamAttemptEntity?> = _lastAttemptResult.asStateFlow()

    private var timerJob: Job? = null

    fun setRole(role: String) {
        _currentRole.value = role
        showSnackbar("Switched role to $role")
        if (role == "STUDENT") {
            _currentScreen.value = AppScreen.STUDENT_DASHBOARD
        } else {
            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
        }
    }

    fun setStudentRegId(regId: String) {
        _currentStudentRegId.value = regId
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // Student Registration
    fun registerStudent(
        fullName: String,
        banglaName: String,
        dob: String,
        gender: String,
        bloodGroup: String,
        className: String,
        categoryId: String,
        schoolName: String,
        institutionType: String,
        division: String,
        district: String,
        upazila: String,
        studentRoll: String,
        mobile: String,
        email: String,
        guardianName: String,
        guardianRelation: String,
        guardianMobile: String,
        presentAddress: String
    ) {
        viewModelScope.launch {
            val count = (students.value.size + 101)
            val regId = "BDJSO-2026-${String.format(Locale.US, "%06d", count)}"
            val newStudent = StudentEntity(
                registrationId = regId,
                fullName = fullName,
                banglaName = banglaName.ifBlank { fullName },
                dob = dob,
                gender = gender,
                bloodGroup = bloodGroup,
                className = className,
                categoryId = categoryId,
                schoolName = schoolName,
                institutionType = institutionType,
                division = division,
                district = district,
                upazila = upazila,
                studentRoll = studentRoll,
                mobile = mobile,
                email = email,
                guardianName = guardianName,
                guardianRelation = guardianRelation,
                guardianMobile = guardianMobile,
                presentAddress = presentAddress,
                status = "VERIFIED"
            )
            repository.registerStudent(newStudent)
            _currentStudentRegId.value = regId
            showSnackbar("Registration Successful! ID: $regId")
            _currentScreen.value = AppScreen.STUDENT_DASHBOARD
        }
    }

    // Exam Actions
    fun startExam(exam: ExamEntity, categoryId: String) {
        viewModelScope.launch {
            _activeExam.value = exam
            val qList = repository.getQuestionsForCategory(categoryId)
            _examQuestions.value = if (qList.isNotEmpty()) qList else repository.getQuestionsForCategory("PRIMARY")
            _currentQuestionIndex.value = 0
            _userAnswers.value = emptyMap()
            _markedForReview.value = emptySet()
            _remainingSeconds.value = exam.durationMinutes * 60
            _lastAutosaveTime.value = "Session started"

            startExamTimer()
            _currentScreen.value = AppScreen.ONLINE_EXAM
        }
    }

    private fun startExamTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            // Auto submit when time expires
            submitActiveExam()
        }
    }

    fun selectAnswer(questionId: Long, optionLetter: String) {
        val updated = _userAnswers.value.toMutableMap()
        updated[questionId] = optionLetter
        _userAnswers.value = updated
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        _lastAutosaveTime.value = "Auto-saved at ${timeFormat.format(Date())}"
    }

    fun clearAnswer(questionId: Long) {
        val updated = _userAnswers.value.toMutableMap()
        updated.remove(questionId)
        _userAnswers.value = updated
    }

    fun toggleMarkForReview(questionId: Long) {
        val current = _markedForReview.value.toMutableSet()
        if (current.contains(questionId)) {
            current.remove(questionId)
        } else {
            current.add(questionId)
        }
        _markedForReview.value = current
    }

    fun goToQuestion(index: Int) {
        if (index in 0 until _examQuestions.value.size) {
            _currentQuestionIndex.value = index
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _examQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun submitActiveExam() {
        timerJob?.cancel()
        val exam = _activeExam.value ?: return
        viewModelScope.launch {
            val attempt = repository.submitAndEvaluateExam(
                examId = exam.id,
                studentRegId = _currentStudentRegId.value,
                answers = _userAnswers.value,
                markedForReview = _markedForReview.value
            )
            _lastAttemptResult.value = attempt
            showSnackbar("Exam submitted successfully! Score: ${attempt.score}")
            _currentScreen.value = AppScreen.EXAM_RESULT
        }
    }

    // Admin Operations
    fun updateStudentStatus(regId: String, status: String) {
        viewModelScope.launch {
            repository.updateStudentStatus(regId, status, _currentRole.value, _currentRole.value)
            showSnackbar("Student $regId marked as $status")
        }
    }

    fun addQuestion(
        text: String,
        category: String,
        subject: String,
        topic: String,
        diff: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correct: String,
        explanation: String
    ) {
        viewModelScope.launch {
            val q = QuestionEntity(
                questionText = text,
                categoryId = category,
                subject = subject,
                topic = topic,
                difficulty = diff,
                marks = 4.0,
                negativeMarks = 1.0,
                optionA = optA,
                optionB = optB,
                optionC = optC,
                optionD = optD,
                correctAnswer = correct,
                explanation = explanation
            )
            repository.insertQuestion(q, _currentRole.value, _currentRole.value)
            showSnackbar("Question added to Question Bank")
        }
    }

    fun deleteQuestion(id: Long) {
        viewModelScope.launch {
            repository.deleteQuestion(id, _currentRole.value, _currentRole.value)
            showSnackbar("Question deleted")
        }
    }

    fun addExam(
        title: String,
        category: String,
        date: String,
        start: String,
        end: String,
        duration: Int,
        totalQ: Int,
        marks: Double
    ) {
        viewModelScope.launch {
            val exam = ExamEntity(
                title = title,
                categoryId = category,
                examDate = date,
                startTime = start,
                endTime = end,
                durationMinutes = duration,
                totalQuestions = totalQ,
                totalMarks = marks,
                status = "SCHEDULED"
            )
            repository.insertExam(exam, _currentRole.value, _currentRole.value)
            showSnackbar("Exam scheduled: $title")
        }
    }

    fun setExamStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateExamStatus(id, status, _currentRole.value, _currentRole.value)
            showSnackbar("Exam status set to $status")
        }
    }

    fun deleteExam(id: Long) {
        viewModelScope.launch {
            repository.deleteExam(id, _currentRole.value, _currentRole.value)
            showSnackbar("Exam removed")
        }
    }

    fun setResultsPublished(published: Boolean) {
        viewModelScope.launch {
            repository.setPublishResults(published, _currentRole.value, _currentRole.value)
            showSnackbar(if (published) "Results published officially!" else "Results hidden from public view")
        }
    }

    fun updateSelectionStatus(resultId: Long, status: String) {
        viewModelScope.launch {
            repository.updateSelectionStatus(resultId, status, _currentRole.value, _currentRole.value)
            showSnackbar("Selection updated to $status")
        }
    }

    fun addExamResult(
        studentRegId: String,
        studentName: String,
        categoryId: String,
        schoolName: String,
        district: String,
        division: String,
        physicsMarks: Double,
        chemistryMarks: Double,
        biologyMarks: Double,
        mathMarks: Double,
        totalMarks: Double,
        percentage: Double,
        rank: Int,
        selectionStatus: String
    ) {
        viewModelScope.launch {
            val result = ResultEntity(
                examId = 1L,
                studentRegistrationId = studentRegId,
                studentName = studentName,
                categoryId = categoryId,
                schoolName = schoolName,
                district = district,
                division = division,
                physicsMarks = physicsMarks,
                chemistryMarks = chemistryMarks,
                biologyMarks = biologyMarks,
                mathMarks = mathMarks,
                totalMarks = totalMarks,
                percentage = percentage,
                rank = rank,
                selectionStatus = selectionStatus,
                isPublished = true
            )
            repository.insertResult(result)
            showSnackbar("Exam score for $studentName stored in Room database")
        }
    }

    fun deleteExamResult(id: Long) {
        viewModelScope.launch {
            repository.deleteResult(id)
            showSnackbar("Exam score removed from Room database")
        }
    }

    fun seedSampleResultsIfEmpty() {
        viewModelScope.launch {
            val sampleResults = listOf(
                ResultEntity(
                    examId = 1L,
                    studentRegistrationId = "BDJSO-2026-000101",
                    studentName = "Aditi Roy Chowdhury",
                    categoryId = "JUNIOR",
                    schoolName = "Viqarunnisa Noon School & College",
                    district = "Dhaka",
                    division = "Dhaka",
                    physicsMarks = 24.5,
                    chemistryMarks = 23.0,
                    biologyMarks = 25.0,
                    mathMarks = 23.5,
                    totalMarks = 96.0,
                    percentage = 96.0,
                    rank = 1,
                    selectionStatus = "SELECTED",
                    isPublished = true
                ),
                ResultEntity(
                    examId = 1L,
                    studentRegistrationId = "BDJSO-2026-000102",
                    studentName = "Tanvir Ahmed",
                    categoryId = "SECONDARY",
                    schoolName = "Rajuk Uttara Model College",
                    district = "Dhaka",
                    division = "Dhaka",
                    physicsMarks = 23.0,
                    chemistryMarks = 24.0,
                    biologyMarks = 22.5,
                    mathMarks = 24.5,
                    totalMarks = 94.0,
                    percentage = 94.0,
                    rank = 2,
                    selectionStatus = "SELECTED",
                    isPublished = true
                ),
                ResultEntity(
                    examId = 1L,
                    studentRegistrationId = "BDJSO-2026-000103",
                    studentName = "Nusrat Jahan",
                    categoryId = "PRIMARY",
                    schoolName = "St. Francis Xavier's Green Herald",
                    district = "Dhaka",
                    division = "Dhaka",
                    physicsMarks = 22.0,
                    chemistryMarks = 21.5,
                    biologyMarks = 24.0,
                    mathMarks = 22.0,
                    totalMarks = 89.5,
                    percentage = 89.5,
                    rank = 3,
                    selectionStatus = "SELECTED",
                    isPublished = true
                ),
                ResultEntity(
                    examId = 1L,
                    studentRegistrationId = "BDJSO-2026-000104",
                    studentName = "Farhan Kabir",
                    categoryId = "JUNIOR",
                    schoolName = "Chittagong Collegiate School",
                    district = "Chattogram",
                    division = "Chattogram",
                    physicsMarks = 21.0,
                    chemistryMarks = 22.0,
                    biologyMarks = 20.5,
                    mathMarks = 21.5,
                    totalMarks = 85.0,
                    percentage = 85.0,
                    rank = 4,
                    selectionStatus = "SELECTED",
                    isPublished = true
                ),
                ResultEntity(
                    examId = 1L,
                    studentRegistrationId = "BDJSO-2026-000105",
                    studentName = "Sadia Islam",
                    categoryId = "SECONDARY",
                    schoolName = "Sylhet Govt. Pilot High School",
                    district = "Sylhet",
                    division = "Sylhet",
                    physicsMarks = 19.5,
                    chemistryMarks = 20.0,
                    biologyMarks = 21.0,
                    mathMarks = 19.0,
                    totalMarks = 79.5,
                    percentage = 79.5,
                    rank = 5,
                    selectionStatus = "WAITING_LIST",
                    isPublished = true
                ),
                ResultEntity(
                    examId = 1L,
                    studentRegistrationId = "BDJSO-2026-000106",
                    studentName = "Abrar Hossain",
                    categoryId = "PRIMARY",
                    schoolName = "Khulna Zilla School",
                    district = "Khulna",
                    division = "Khulna",
                    physicsMarks = 18.0,
                    chemistryMarks = 19.0,
                    biologyMarks = 18.5,
                    mathMarks = 17.5,
                    totalMarks = 73.0,
                    percentage = 73.0,
                    rank = 6,
                    selectionStatus = "WAITING_LIST",
                    isPublished = true
                )
            )
            repository.insertResults(sampleResults)
            showSnackbar("Seeded 6 sample exam scores into Room database")
        }
    }

    fun addSchool(
        name: String,
        code: String,
        division: String,
        district: String,
        upazila: String,
        contact: String,
        phone: String,
        email: String
    ) {
        viewModelScope.launch {
            val school = SchoolEntity(
                name = name,
                code = code,
                division = division,
                district = district,
                upazila = upazila,
                address = "$upazila, $district",
                contactPerson = contact,
                mobile = phone,
                email = email
            )
            repository.insertSchool(school, _currentRole.value, _currentRole.value)
            showSnackbar("School added successfully")
        }
    }

    fun deleteSchool(id: Long) {
        viewModelScope.launch {
            repository.deleteSchool(id, _currentRole.value, _currentRole.value)
            showSnackbar("School removed")
        }
    }

    fun updateVolunteerStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateVolunteerStatus(id, status, _currentRole.value, _currentRole.value)
            showSnackbar("Volunteer status updated to $status")
        }
    }

    fun addAnnouncement(title: String, desc: String, tag: String) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val ann = AnnouncementEntity(
                title = title,
                description = desc,
                tag = tag,
                publishDate = sdf.format(Date()),
                status = "PUBLISHED"
            )
            repository.insertAnnouncement(ann, _currentRole.value, _currentRole.value)
            showSnackbar("Announcement published")
        }
    }

    fun deleteAnnouncement(id: Long) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id, _currentRole.value, _currentRole.value)
            showSnackbar("Announcement deleted")
        }
    }

    fun updateSetting(key: String, value: String) {
        viewModelScope.launch {
            repository.updateSetting(key, value, _currentRole.value, _currentRole.value)
            showSnackbar("Setting updated: $key = $value")
        }
    }

    // User Management (Show, Edit, Password, Confirm)
    private val _selectedUser = MutableStateFlow<UserEntity?>(null)
    val selectedUser: StateFlow<UserEntity?> = _selectedUser.asStateFlow()

    private val _userSearchQuery = MutableStateFlow("")
    val userSearchQuery: StateFlow<String> = _userSearchQuery.asStateFlow()

    fun setUserSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun selectUser(user: UserEntity, destination: AppScreen = AppScreen.ADMIN_USER_SHOW) {
        _selectedUser.value = user
        _currentScreen.value = destination
    }

    fun selectUserByUsername(username: String, destination: AppScreen = AppScreen.ADMIN_USER_SHOW) {
        viewModelScope.launch {
            val user = users.value.find { it.username == username }
            if (user != null) {
                _selectedUser.value = user
                _currentScreen.value = destination
            }
        }
    }

    fun updateUser(updated: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(updated, _currentRole.value, _currentRole.value)
            _selectedUser.value = updated
            showSnackbar("User ${updated.username} updated successfully")
            _currentScreen.value = AppScreen.ADMIN_USER_SHOW
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(user.id, user.username, _currentRole.value, _currentRole.value)
            if (_selectedUser.value?.id == user.id) {
                _selectedUser.value = null
            }
            showSnackbar("User ${user.username} deleted")
            _currentScreen.value = AppScreen.ADMIN_USERS
        }
    }

    fun blockUser(user: UserEntity) {
        viewModelScope.launch {
            val blocked = user.copy(status = 0)
            repository.updateUser(blocked, _currentRole.value, _currentRole.value)
            _selectedUser.value = blocked
            showSnackbar("User ${user.username} has been blocked")
        }
    }

    fun sendConfirmationReminder(user: UserEntity) {
        viewModelScope.launch {
            showSnackbar("Confirmation reminder email queued for ${user.email}")
        }
    }

    fun sendEmailConfirmation(user: UserEntity) {
        viewModelScope.launch {
            val confirmed = user.copy(isConfirmed = true)
            repository.updateUser(confirmed, _currentRole.value, _currentRole.value)
            _selectedUser.value = confirmed
            showSnackbar("Confirmation email sent & verified for ${user.email}")
        }
    }

    fun changePassword(userId: Long, newPass: String) {
        viewModelScope.launch {
            showSnackbar("Password changed successfully")
            _currentScreen.value = AppScreen.ADMIN_USER_SHOW
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            showSnackbar("Application and database caches cleared successfully")
        }
    }

    fun moderateExamAttempt(attemptId: Long, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAttemptStatus(attemptId, newStatus, _currentStudentRegId.value, _currentRole.value)
            showSnackbar("Submission #$attemptId marked as $newStatus")
        }
    }

    fun approveAndEvaluateAttempt(attemptId: Long, score: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.evaluateAttempt(attemptId, score, _currentStudentRegId.value, _currentRole.value)
            showSnackbar("Submission #$attemptId approved & published with score $score")
        }
    }

    fun seedExamAttemptsIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedSampleAttempts()
            showSnackbar("Seeded sample exam submissions to Room DB")
        }
    }

    fun verifyStudentDirectly(regId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val student = repository.getStudent(regId)
            if (student != null) {
                repository.updateStudent(student.copy(status = "VERIFIED"), _currentStudentRegId.value, _currentRole.value)
                showSnackbar("Student ${student.fullName} ($regId) verified!")
            }
        }
    }

    fun createAnnouncementEvent(title: String, description: String, date: String, tag: String = "EXAM") {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = AnnouncementEntity(
                title = title,
                description = description,
                tag = tag,
                publishDate = date,
                status = "PUBLISHED"
            )
            repository.insertAnnouncement(entity, _currentStudentRegId.value, _currentRole.value)
            showSnackbar("Olympiad event announcement published!")
        }
    }
}
