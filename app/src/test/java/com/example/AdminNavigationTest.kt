package com.example

import com.example.data.AuthRepository
import com.example.model.UserRole
import com.example.ui.viewmodel.AppScreen
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying Admin Panel access, Manager authentication, and AppScreen routing.
 */
class AdminNavigationTest {

    @Test
    fun testDefaultManagerAuthenticationForAdminPanel() {
        val currentUser = AuthRepository.currentUser.value
        assertNotNull("Current user should be pre-logged in as Manager", currentUser)
        assertEquals("User role must be MANAGER for admin panel access", UserRole.MANAGER, currentUser?.role)
        assertEquals("Username should match manager account", "600032", currentUser?.username)
    }

    @Test
    fun testAdminScreenEnumValues() {
        val adminScreens = listOf(
            AppScreen.ADMIN_DASHBOARD,
            AppScreen.ADMIN_STUDENTS,
            AppScreen.ADMIN_QUESTIONS,
            AppScreen.ADMIN_EXAMS,
            AppScreen.ADMIN_RESULTS,
            AppScreen.ADMIN_SCHOOLS,
            AppScreen.ADMIN_VOLUNTEERS,
            AppScreen.ADMIN_ANNOUNCEMENTS,
            AppScreen.ADMIN_AUDIT_LOGS,
            AppScreen.ADMIN_SETTINGS,
            AppScreen.ADMIN_USERS,
            AppScreen.ADMIN_USER_SHOW,
            AppScreen.ADMIN_USER_EDIT,
            AppScreen.ADMIN_USER_CHANGE_PASSWORD,
            AppScreen.ADMIN_REGISTRATION_STATS
        )
        assertEquals(15, adminScreens.size)
        assertTrue(adminScreens.contains(AppScreen.ADMIN_DASHBOARD))
        assertTrue(adminScreens.contains(AppScreen.ADMIN_USERS))
    }
}
