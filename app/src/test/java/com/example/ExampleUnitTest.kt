package com.example

import com.example.data.model.StudentEntity
import com.example.data.model.ResultEntity
import org.junit.Assert.*
import org.junit.Test

/**
 * Local unit tests verifying student registration model and exam score calculations for BDJSO Room database.
 */
class ExampleUnitTest {

    @Test
    fun testStudentRegistrationEntityCreation() {
        val student = StudentEntity(
            registrationId = "BDJSO-2026-999999",
            fullName = "Anisur Rahman",
            banglaName = "আনিসুর রহমান",
            dob = "2012-04-10",
            gender = "Male",
            bloodGroup = "A+",
            className = "Class 7",
            categoryId = "JUNIOR",
            schoolName = "Government Laboratory High School",
            institutionType = "Bangla Medium",
            division = "Dhaka",
            district = "Dhaka",
            upazila = "Dhanmondi",
            studentRoll = "14",
            mobile = "01700000000",
            email = "anisur@bdjso.org",
            guardianName = "Md. Rahman",
            guardianRelation = "Father",
            guardianMobile = "01700000001",
            presentAddress = "Dhaka, Bangladesh",
            status = "VERIFIED"
        )

        assertEquals("BDJSO-2026-999999", student.registrationId)
        assertEquals("Anisur Rahman", student.fullName)
        assertEquals("JUNIOR", student.categoryId)
        assertEquals("VERIFIED", student.status)
    }

    @Test
    fun testResultEntityPercentageAndStatus() {
        val totalMarks = 40.0
        val obtainedMarks = 36.0
        val percentage = (obtainedMarks / totalMarks) * 100.0
        val selectionStatus = if (percentage >= 80.0) "SELECTED" else "NOT_SELECTED"

        val result = ResultEntity(
            examId = 1L,
            studentRegistrationId = "BDJSO-2026-999999",
            studentName = "Anisur Rahman",
            categoryId = "JUNIOR",
            schoolName = "Government Laboratory High School",
            district = "Dhaka",
            division = "Dhaka",
            physicsMarks = 12.0,
            chemistryMarks = 12.0,
            biologyMarks = 12.0,
            mathMarks = 0.0,
            totalMarks = obtainedMarks,
            percentage = percentage,
            rank = 1,
            selectionStatus = selectionStatus,
            isPublished = true
        )

        assertEquals(90.0, result.percentage, 0.01)
        assertEquals("SELECTED", result.selectionStatus)
        assertEquals(36.0, result.totalMarks, 0.01)
    }
}
