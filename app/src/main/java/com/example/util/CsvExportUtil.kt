package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.ExamAttemptEntity
import com.example.data.model.ResultEntity
import com.example.data.model.StudentEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for exporting BDJSO Olympiad data to standard CSV format (RFC 4180).
 */
object CsvExportUtil {

    private fun escape(value: Any?): String {
        if (value == null) return ""
        val str = value.toString()
        return if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            "\"" + str.replace("\"", "\"\"") + "\""
        } else {
            str
        }
    }

    /**
     * Generates CSV for Student Registration records.
     */
    fun generateStudentsCsv(
        students: List<StudentEntity>,
        categoryFilter: String = "ALL",
        statusFilter: String = "ALL"
    ): String {
        val filtered = students.filter { s ->
            val matchCategory = categoryFilter == "ALL" || s.categoryId.equals(categoryFilter, ignoreCase = true)
            val matchStatus = statusFilter == "ALL" || s.status.equals(statusFilter, ignoreCase = true)
            matchCategory && matchStatus
        }

        val sb = StringBuilder()
        // Header
        sb.append(
            listOf(
                "Registration ID",
                "Full Name",
                "Bangla Name",
                "Category",
                "Class",
                "Gender",
                "Date of Birth",
                "School / Institution",
                "District",
                "Division",
                "Mobile",
                "Email",
                "Guardian Name",
                "Guardian Mobile",
                "Status",
                "Registered At"
            ).joinToString(separator = ",", transform = ::escape)
        ).append("\r\n")

        // Rows
        for (st in filtered) {
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(st.registeredAt))
            sb.append(
                listOf(
                    st.registrationId,
                    st.fullName,
                    st.banglaName,
                    st.categoryId,
                    st.className,
                    st.gender,
                    st.dob,
                    st.schoolName,
                    st.district,
                    st.division,
                    st.mobile,
                    st.email,
                    st.guardianName,
                    st.guardianMobile,
                    st.status,
                    dateStr
                ).joinToString(separator = ",", transform = ::escape)
            ).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Generates CSV for Examination Results.
     */
    fun generateResultsCsv(
        results: List<ResultEntity>,
        categoryFilter: String = "ALL",
        selectionFilter: String = "ALL"
    ): String {
        val filtered = results.filter { r ->
            val matchCategory = categoryFilter == "ALL" || r.categoryId.equals(categoryFilter, ignoreCase = true)
            val matchSelection = selectionFilter == "ALL" || r.selectionStatus.equals(selectionFilter, ignoreCase = true)
            matchCategory && matchSelection
        }

        val sb = StringBuilder()
        // Header
        sb.append(
            listOf(
                "Rank",
                "Registration ID",
                "Student Name",
                "Category",
                "School / Institution",
                "District",
                "Division",
                "Physics Marks",
                "Chemistry Marks",
                "Biology Marks",
                "Math Marks",
                "Total Marks",
                "Percentage (%)",
                "Selection Status",
                "Exam ID",
                "Published"
            ).joinToString(separator = ",", transform = ::escape)
        ).append("\r\n")

        // Rows
        for (res in filtered) {
            sb.append(
                listOf(
                    res.rank,
                    res.studentRegistrationId,
                    res.studentName,
                    res.categoryId,
                    res.schoolName,
                    res.district,
                    res.division,
                    String.format(Locale.US, "%.1f", res.physicsMarks),
                    String.format(Locale.US, "%.1f", res.chemistryMarks),
                    String.format(Locale.US, "%.1f", res.biologyMarks),
                    String.format(Locale.US, "%.1f", res.mathMarks),
                    String.format(Locale.US, "%.1f", res.totalMarks),
                    String.format(Locale.US, "%.1f", res.percentage),
                    res.selectionStatus,
                    res.examId,
                    if (res.isPublished) "YES" else "NO"
                ).joinToString(separator = ",", transform = ::escape)
            ).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Generates a Combined Comprehensive Report with Student Profile + Examination Scores.
     */
    fun generateCombinedReportCsv(
        students: List<StudentEntity>,
        results: List<ResultEntity>
    ): String {
        val resultMap = results.associateBy { it.studentRegistrationId }

        val sb = StringBuilder()
        sb.append(
            listOf(
                "Registration ID",
                "Student Name",
                "Category",
                "School",
                "District",
                "Division",
                "Verification Status",
                "Exam Total Marks",
                "Percentage",
                "Official Rank",
                "Olympiad Merit Status"
            ).joinToString(separator = ",", transform = ::escape)
        ).append("\r\n")

        for (st in students) {
            val res = resultMap[st.registrationId]
            sb.append(
                listOf(
                    st.registrationId,
                    st.fullName,
                    st.categoryId,
                    st.schoolName,
                    st.district,
                    st.division,
                    st.status,
                    res?.let { String.format(Locale.US, "%.1f", it.totalMarks) } ?: "N/A",
                    res?.let { String.format(Locale.US, "%.1f%%", it.percentage) } ?: "N/A",
                    res?.rank?.toString() ?: "Unranked",
                    res?.selectionStatus ?: "PENDING_EXAM"
                ).joinToString(separator = ",", transform = ::escape)
            ).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Generates CSV for Pending Exam Submissions Moderation.
     */
    fun generateSubmissionsCsv(
        attempts: List<ExamAttemptEntity>,
        students: List<StudentEntity>
    ): String {
        val studentMap = students.associateBy { it.registrationId }
        val sb = StringBuilder()
        sb.append(
            listOf(
                "Submission ID",
                "Exam ID",
                "Student Reg ID",
                "Student Name",
                "Score",
                "Correct Count",
                "Wrong Count",
                "Unattempted",
                "Moderation Status",
                "Submitted Time"
            ).joinToString(separator = ",", transform = ::escape)
        ).append("\r\n")

        for (att in attempts) {
            val studentName = studentMap[att.studentRegistrationId]?.fullName ?: "Unknown Candidate"
            val timeStr = if (att.submitTime > 0) {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(att.submitTime))
            } else {
                "In-Progress"
            }

            sb.append(
                listOf(
                    att.id,
                    att.examId,
                    att.studentRegistrationId,
                    studentName,
                    String.format(Locale.US, "%.1f", att.score),
                    att.correctCount,
                    att.wrongCount,
                    att.unattemptedCount,
                    att.status,
                    timeStr
                ).joinToString(separator = ",", transform = ::escape)
            ).append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Saves CSV content to cache directory and shares via Intent.ACTION_SEND.
     */
    fun shareCsv(context: Context, filename: String, content: String) {
        try {
            val cacheDir = File(context.cacheDir, "exports")
            if (!cacheDir.exists()) cacheDir.mkdirs()

            val file = File(cacheDir, filename)
            file.writeText(content, Charsets.UTF_8)

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "BDJSO Report Export - $filename")
                putExtra(Intent.EXTRA_TEXT, "Attached is the BDJSO Olympiad report export ($filename).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Export BDJSO CSV Report"))
        } catch (e: Exception) {
            // Fallback to sending text content directly
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "BDJSO Report Export - $filename")
                putExtra(Intent.EXTRA_TEXT, content)
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Export BDJSO CSV Report"))
        }
    }

    /**
     * Writes CSV content directly to a user-chosen Document Uri.
     */
    fun writeCsvToDocument(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray(Charsets.UTF_8))
                outputStream.flush()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
