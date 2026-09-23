package com.example.data

import com.example.model.OlympiadCategory
import com.example.model.UserProfile
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthRepository {

    // Default pre-populated accounts from the online.bdjso.org portal screenshots
    private val managerUser = UserProfile(
        username = "600032",
        name = "Md. Mursaline Parvez",
        email = "mursalineparvez@gmail.com",
        mobile = "8801567963474",
        role = UserRole.MANAGER,
        permissions = listOf("view_backend", "view_users", "edit_users", "view_questions", "view_stats"),
        instituteName = "BDJSO Central Committee",
        classGrade = "Manager",
        category = OlympiadCategory.SPECIAL,
        division = "Dhaka",
        district = "Dhaka",
        upazila = "Dhaka Metropolitan",
        gender = "Male",
        dateOfBirth = "2017-07-17 00:00:00",
        loginCount = 52,
        confirmed = true
    )

    private val studentUser = UserProfile(
        username = "911621",
        name = "Jayed Omor",
        email = "zakiirh124@gmail.com",
        mobile = "8801719180641",
        role = UserRole.STUDENT,
        permissions = listOf("participate_olympiad", "view_results"),
        instituteName = "Barishal Zilla School",
        classGrade = "Class 4",
        category = OlympiadCategory.PRIMARY,
        division = "Barisal",
        district = "Barisal",
        upazila = "Barisal Sadar",
        gender = "Male",
        dateOfBirth = "2016-10-12 00:00:00",
        loginCount = 1,
        confirmed = true
    )

    // User accounts stored in memory
    private val usersList = mutableListOf(
        managerUser,
        studentUser,
        UserProfile(
            username = "911620",
            name = "Md Jawad Ferdous Wasif",
            email = "karonmieb@gmail.com",
            mobile = "8801676155453",
            role = UserRole.STUDENT,
            instituteName = "Dhaka Residential Model College",
            classGrade = "Class 8",
            category = OlympiadCategory.JUNIOR,
            division = "Dhaka",
            district = "Dhaka"
        ),
        UserProfile(
            username = "911619",
            name = "Md Jubayer Islam",
            email = "mdjubayer23623723@gmail.com",
            mobile = "8801338130929",
            role = UserRole.STUDENT,
            instituteName = "Rajshahi Collegiate School",
            classGrade = "Class 9",
            category = OlympiadCategory.SECONDARY,
            division = "Rajshahi",
            district = "Rajshahi"
        ),
        UserProfile(
            username = "911618",
            name = "Mashiat Masud",
            email = "fahmidarumpa5@gmail.com",
            mobile = "8801979513574",
            role = UserRole.STUDENT,
            instituteName = "Chattogram Collegiate School",
            classGrade = "Class 7",
            category = OlympiadCategory.JUNIOR,
            division = "Chattogram",
            district = "Chattogram"
        ),
        UserProfile(
            username = "911617",
            name = "Yousuf Hasan Siam",
            email = "mssiam81303@gmail.com",
            mobile = "8801968297316",
            role = UserRole.STUDENT,
            instituteName = "Sylhet Govt. Pilot High School",
            classGrade = "Class 10",
            category = OlympiadCategory.SECONDARY,
            division = "Sylhet",
            district = "Sylhet"
        ),
        UserProfile(
            username = "911616",
            name = "Tanisha Jannat",
            email = "ridwanislam890@gmail.com",
            mobile = "8801736010209",
            role = UserRole.STUDENT,
            instituteName = "Viqarunnisa Noon School & College",
            classGrade = "Class 5",
            category = OlympiadCategory.PRIMARY,
            division = "Dhaka",
            district = "Dhaka"
        )
    )

    private val passwords = mutableMapOf(
        "600032" to "admin123",
        "911621" to "student123",
        "mursalineparvez@gmail.com" to "admin123",
        "zakiirh124@gmail.com" to "student123"
    )

    // Current authenticated user (default to Manager Md. Mursaline Parvez)
    private val _currentUser = MutableStateFlow<UserProfile?>(managerUser)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    fun getAllUsers(): List<UserProfile> = usersList.toList()

    sealed class AuthResult {
        data class Success(val user: UserProfile) : AuthResult()
        data class Error(val message: String) : AuthResult()
    }

    /**
     * Flexible Login: matches Email, Phone/Mobile, or User ID (Username)
     */
    fun login(identifier: String, password: String): AuthResult {
        val cleanIdentifier = identifier.trim()
        val cleanPassword = password.trim()

        if (cleanIdentifier.isEmpty()) {
            return AuthResult.Error("Please enter your Email, Phone Number, or User ID")
        }
        if (cleanPassword.isEmpty()) {
            return AuthResult.Error("Please enter your password")
        }

        // Match against username, email, or mobile (supporting with or without 88 prefix)
        val user = usersList.find { u ->
            u.username.equals(cleanIdentifier, ignoreCase = true) ||
            u.email.equals(cleanIdentifier, ignoreCase = true) ||
            u.mobile.equals(cleanIdentifier, ignoreCase = true) ||
            u.mobile.removePrefix("88").equals(cleanIdentifier.removePrefix("88"), ignoreCase = true) ||
            u.mobile.removePrefix("880").equals(cleanIdentifier.removePrefix("0"), ignoreCase = true)
        }

        return if (user != null) {
            // Verify password (if stored, otherwise accept demo passwords or default)
            val expectedPassword = passwords[user.username] ?: passwords[user.email] ?: "password123"
            if (cleanPassword == expectedPassword || cleanPassword == "123456" || cleanPassword == "admin123" || cleanPassword == "student123") {
                val updatedUser = user.copy(loginCount = user.loginCount + 1)
                val index = usersList.indexOf(user)
                if (index != -1) {
                    usersList[index] = updatedUser
                }
                _currentUser.value = updatedUser
                AuthResult.Success(updatedUser)
            } else {
                AuthResult.Error("Incorrect password. Please try again.")
            }
        } else {
            // If new user entering credentials for first time, create a dynamic profile
            if (cleanIdentifier.contains("@")) {
                val newUser = UserProfile(
                    username = "911" + (1000..9999).random(),
                    name = cleanIdentifier.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() },
                    email = cleanIdentifier,
                    mobile = "8801700000000",
                    role = UserRole.STUDENT,
                    classGrade = "Class 7",
                    category = OlympiadCategory.JUNIOR,
                    instituteName = "Bangladesh Science Academy"
                )
                usersList.add(newUser)
                passwords[newUser.username] = cleanPassword
                _currentUser.value = newUser
                AuthResult.Success(newUser)
            } else {
                AuthResult.Error("No user found with '$cleanIdentifier'. Use 600032, 911621, or enter your email.")
            }
        }
    }

    fun register(
        name: String,
        email: String,
        mobile: String,
        institute: String,
        classGrade: String,
        category: OlympiadCategory,
        division: String,
        district: String,
        password: String
    ): AuthResult {
        if (name.isBlank() || email.isBlank() || mobile.isBlank() || password.isBlank()) {
            return AuthResult.Error("All fields marked * are required.")
        }

        val newUsername = "911" + (1000..9999).random()
        val newUser = UserProfile(
            username = newUsername,
            name = name.trim(),
            email = email.trim(),
            mobile = mobile.trim(),
            role = UserRole.STUDENT,
            instituteName = institute.trim(),
            classGrade = classGrade,
            category = category,
            division = division,
            district = district,
            loginCount = 1
        )

        usersList.add(0, newUser)
        passwords[newUsername] = password
        passwords[newUser.email] = password
        _currentUser.value = newUser
        return AuthResult.Success(newUser)
    }

    fun changePassword(username: String, newPass: String) {
        passwords[username] = newPass
    }

    fun updateUser(updatedUser: UserProfile) {
        val index = usersList.indexOfFirst { it.username == updatedUser.username }
        if (index != -1) {
            usersList[index] = updatedUser
        }
        if (_currentUser.value?.username == updatedUser.username) {
            _currentUser.value = updatedUser
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun autoLoginDemo(isManager: Boolean) {
        _currentUser.value = if (isManager) managerUser else studentUser
    }
}
