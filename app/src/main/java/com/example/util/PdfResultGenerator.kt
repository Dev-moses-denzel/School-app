package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.local.Converters
import com.example.data.model.StudentResult
import com.example.data.model.SubjectScore
import com.example.data.remote.CbcClassStudent
import com.example.data.remote.StudentAssignment
import com.example.data.remote.StudentAttendance
import java.io.File
import java.io.FileOutputStream

object PdfResultGenerator {

    private val converters = Converters()

    fun generateResultPdf(context: Context, result: StudentResult): File? {
        val pdfDocument = PdfDocument()

        // Standard A4 dimensions in 72 dpi: 595 x 842 points
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Paints setup
        val navyColor = Color.rgb(15, 23, 42) // #0F172A
        val blueAccent = Color.rgb(37, 99, 235) // #2563EB
        val goldColor = Color.rgb(217, 119, 6) // #D97706
        val emeraldPass = Color.rgb(5, 150, 105) // #059669
        val crimsonFail = Color.rgb(220, 38, 38)
        val lightBg = Color.rgb(248, 250, 252) // #F8FAFC
        val grayBorder = Color.rgb(226, 232, 240) // #E2E8F0
        val textDark = Color.rgb(30, 41, 59) // #1E293B

        val paint = Paint().apply { isAntiAlias = true }
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = textDark
            textSize = 10f
            typeface = Typeface.DEFAULT
        }
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = navyColor
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val subtitlePaint = Paint().apply {
            isAntiAlias = true
            color = blueAccent
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // 1. Draw Page Outer Border & Header Banner
        paint.color = blueAccent
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 12f, paint)

        // Watermark Seal / Outer Box
        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRect(20f, 20f, (pageWidth - 20).toFloat(), (pageHeight - 20).toFloat(), paint)

        // 2. Institution Header Title
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(result.boardOrUniversity.uppercase(), (pageWidth / 2).toFloat(), 55f, titlePaint)

        subtitlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(result.institutionName, (pageWidth / 2).toFloat(), 72f, subtitlePaint)

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 9f
        textPaint.color = Color.DKGRAY
        canvas.drawText("OFFICIAL ONLINE EXAMINATION RESULT & ACADEMIC TRANSCRIPT", (pageWidth / 2).toFloat(), 88f, textPaint)

        // Divider Line
        paint.color = blueAccent
        paint.strokeWidth = 2f
        canvas.drawLine(40f, 98f, (pageWidth - 40).toFloat(), 98f, paint)

        // 3. Student Personal Details Card Box
        paint.color = lightBg
        paint.style = Paint.Style.FILL
        val detailsRect = RectF(40f, 110f, (pageWidth - 40).toFloat(), 215f)
        canvas.drawRoundRect(detailsRect, 8f, 8f, paint)

        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(detailsRect, 8f, 8f, paint)

        // Student Info Text
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.color = textDark
        textPaint.textSize = 9.5f

        val col1X = 55f
        val col2X = 310f
        var startY = 130f
        val lineSpacing = 16f

        fun drawField(colX: Float, y: Float, label: String, value: String) {
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("$label: ", colX, y, textPaint)
            val labelWidth = textPaint.measureText("$label: ")
            textPaint.typeface = Typeface.DEFAULT
            canvas.drawText(value, colX + labelWidth, y, textPaint)
        }

        drawField(col1X, startY, "Student Name", result.studentName)
        drawField(col2X, startY, "Roll Number", result.rollNumber)

        startY += lineSpacing
        drawField(col1X, startY, "Registration No", result.registrationNumber)
        drawField(col2X, startY, "Exam Session", result.sessionYear)

        startY += lineSpacing
        drawField(col1X, startY, "Program / Course", result.courseOrProgram)
        drawField(col2X, startY, "Branch / Stream", result.branchOrStream)

        startY += lineSpacing
        drawField(col1X, startY, "Exam Term", result.examTerm)
        drawField(col2X, startY, "Result Date", result.publishDate)

        // 4. Subject Performance Table Header
        var tableY = 235f
        paint.color = navyColor
        paint.style = Paint.Style.FILL
        val tableHeaderRect = RectF(40f, tableY, (pageWidth - 40).toFloat(), tableY + 24f)
        canvas.drawRoundRect(tableHeaderRect, 4f, 4f, paint)

        val headerPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        canvas.drawText("CODE", 50f, tableY + 16f, headerPaint)
        canvas.drawText("SUBJECT NAME", 110f, tableY + 16f, headerPaint)
        canvas.drawText("CREDITS", 310f, tableY + 16f, headerPaint)
        canvas.drawText("INT", 370f, tableY + 16f, headerPaint)
        canvas.drawText("EXT", 410f, tableY + 16f, headerPaint)
        canvas.drawText("TOTAL", 450f, tableY + 16f, headerPaint)
        canvas.drawText("GRADE", 500f, tableY + 16f, headerPaint)

        tableY += 24f

        // Table Rows
        val subjects: List<SubjectScore> = converters.toSubjectList(result.subjectsJson)
        val rowHeight = 20f

        subjects.forEachIndexed { index, sub ->
            val rowRect = RectF(40f, tableY, (pageWidth - 40).toFloat(), tableY + rowHeight)
            paint.color = if (index % 2 == 0) Color.WHITE else lightBg
            paint.style = Paint.Style.FILL
            canvas.drawRect(rowRect, paint)

            paint.color = grayBorder
            paint.style = Paint.Style.STROKE
            canvas.drawRect(rowRect, paint)

            textPaint.color = textDark
            textPaint.typeface = Typeface.DEFAULT
            textPaint.textSize = 8.5f

            canvas.drawText(sub.code, 50f, tableY + 14f, textPaint)
            
            // Truncate long subject name
            var subName = sub.name
            if (subName.length > 30) subName = subName.take(28) + "..."
            canvas.drawText(subName, 110f, tableY + 14f, textPaint)

            canvas.drawText(sub.credits.toString(), 320f, tableY + 14f, textPaint)
            canvas.drawText(sub.internalMarks.toString(), 375f, tableY + 14f, textPaint)
            canvas.drawText(sub.externalMarks.toString(), 415f, tableY + 14f, textPaint)
            
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${sub.totalMarks}/${sub.maxMarks}", 450f, tableY + 14f, textPaint)

            textPaint.color = if (sub.isPass) emeraldPass else crimsonFail
            canvas.drawText(sub.grade, 505f, tableY + 14f, textPaint)

            tableY += rowHeight
        }

        // 5. Overall Performance Summary Banner
        tableY += 15f
        paint.color = if (result.overallStatus.contains("FAIL")) Color.rgb(254, 242, 242) else Color.rgb(240, 253, 244)
        paint.style = Paint.Style.FILL
        val summaryRect = RectF(40f, tableY, (pageWidth - 40).toFloat(), tableY + 80f)
        canvas.drawRoundRect(summaryRect, 6f, 6f, paint)

        paint.color = if (result.overallStatus.contains("FAIL")) crimsonFail else emeraldPass
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRoundRect(summaryRect, 6f, 6f, paint)

        // Cumulative GPA & Overall Status
        val statusTitlePaint = Paint().apply {
            isAntiAlias = true
            color = if (result.overallStatus.contains("FAIL")) crimsonFail else emeraldPass
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        canvas.drawText("RESULT STATUS: ${result.overallStatus}", 55f, tableY + 26f, statusTitlePaint)

        textPaint.color = textDark
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("Cumulative GPA (CGPA): ", 55f, tableY + 48f, textPaint)
        titlePaint.textSize = 14f
        titlePaint.color = navyColor
        canvas.drawText(String.format("%.2f / 4.00", result.cgpa), 190f, tableY + 48f, titlePaint)

        canvas.drawText("Total Percentage: ", 320f, tableY + 48f, textPaint)
        canvas.drawText(String.format("%.2f%%", result.totalPercentage), 430f, tableY + 48f, titlePaint)

        canvas.drawText("Division: ${result.division}", 55f, tableY + 68f, textPaint)
        canvas.drawText("Credits Earned: ${result.earnedCredits}/${result.totalCredits}", 320f, tableY + 68f, textPaint)

        // 6. Security Stamp & Digital Verification Block
        tableY += 105f
        paint.color = lightBg
        paint.style = Paint.Style.FILL
        val verifyRect = RectF(40f, tableY, 260f, tableY + 90f)
        canvas.drawRoundRect(verifyRect, 4f, 4f, paint)

        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(verifyRect, 4f, 4f, paint)

        textPaint.textSize = 8f
        textPaint.color = Color.GRAY
        canvas.drawText("[DIGITAL VERIFICATION QR STAMP]", 50f, tableY + 20f, textPaint)
        textPaint.textSize = 7.5f
        canvas.drawText("Scan to verify authenticity on portal", 50f, tableY + 34f, textPaint)
        textPaint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        textPaint.color = blueAccent
        canvas.drawText("HASH: ${result.verificationHash}", 50f, tableY + 54f, textPaint)

        // Official Signature Box
        val sigX = 360f
        paint.color = blueAccent
        paint.strokeWidth = 1f
        canvas.drawLine(sigX, tableY + 55f, (pageWidth - 50).toFloat(), tableY + 55f, paint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = navyColor
        textPaint.textSize = 9f
        canvas.drawText("Controller of Examinations", sigX + 15f, tableY + 70f, textPaint)
        textPaint.typeface = Typeface.DEFAULT
        textPaint.color = Color.DKGRAY
        textPaint.textSize = 8f
        canvas.drawText("National Academic Verification System", sigX + 10f, tableY + 82f, textPaint)

        // Footer disclaimer
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 7.5f
        textPaint.color = Color.GRAY
        canvas.drawText("This transcript is computer generated and officially verified by Result Hub Engine.", (pageWidth / 2).toFloat(), 820f, textPaint)

        pdfDocument.finishPage(page)

        // Save PDF to downloads/cache folder
        return try {
            val outputDir = File(context.cacheDir, "downloaded_results")
            if (!outputDir.exists()) outputDir.mkdirs()

            val pdfFile = File(outputDir, "Result_${result.rollNumber}_${result.examTerm.replace(" ", "_")}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun generateAssignmentPdf(
        context: Context,
        assignment: StudentAssignment,
        schoolName: String = "MoI Forces Academy Secondary",
        schoolMotto: String = "Education for Excellence & Service"
    ): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val navyColor = Color.rgb(15, 23, 42)
        val blueAccent = Color.rgb(37, 99, 235)
        val goldColor = Color.rgb(217, 119, 6)
        val lightBg = Color.rgb(248, 250, 252)
        val grayBorder = Color.rgb(226, 232, 240)
        val textDark = Color.rgb(30, 41, 59)

        val paint = Paint().apply { isAntiAlias = true }
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = textDark
            textSize = 10f
            typeface = Typeface.DEFAULT
        }
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = navyColor
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Top Accent
        paint.color = blueAccent
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 12f, paint)

        // Border
        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRect(20f, 20f, (pageWidth - 20).toFloat(), (pageHeight - 20).toFloat(), paint)

        // Header
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(schoolName.uppercase(), (pageWidth / 2).toFloat(), 55f, titlePaint)

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 9f
        textPaint.color = goldColor
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("\"$schoolMotto\"", (pageWidth / 2).toFloat(), 72f, textPaint)

        textPaint.color = Color.DKGRAY
        textPaint.typeface = Typeface.DEFAULT
        canvas.drawText("OFFICIAL KENYA CBC ACADEMIC PAPER & EXAM PORTAL", (pageWidth / 2).toFloat(), 88f, textPaint)

        // Divider
        paint.color = blueAccent
        paint.strokeWidth = 2f
        canvas.drawLine(40f, 98f, (pageWidth - 40).toFloat(), 98f, paint)

        // Metadata Box
        paint.color = lightBg
        paint.style = Paint.Style.FILL
        val cardRect = RectF(40f, 110f, (pageWidth - 40).toFloat(), 210f)
        canvas.drawRoundRect(cardRect, 8f, 8f, paint)

        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(cardRect, 8f, 8f, paint)

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = 11.5f
        textPaint.color = navyColor
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("PAPER TITLE: ${assignment.title}", 55f, 135f, textPaint)

        textPaint.textSize = 10f
        textPaint.color = textDark
        canvas.drawText("Paper Category: ${assignment.type}", 55f, 155f, textPaint)
        canvas.drawText("Subject Area: ${assignment.subjectName}", 300f, 155f, textPaint)

        canvas.drawText("Subject Teacher: ${assignment.teacherName}", 55f, 175f, textPaint)
        canvas.drawText("Target Audience: ${if (assignment.targetAdmissionNo == "ALL_STUDENTS") "Entire Grade 10 Class" else assignment.targetAdmissionNo}", 300f, 175f, textPaint)

        canvas.drawText("Date Issued: ${assignment.dateSent}", 55f, 195f, textPaint)
        textPaint.color = Color.rgb(220, 38, 38)
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Submission Due Date: ${assignment.dueDate}", 300f, 195f, textPaint)

        // Content
        var curY = 235f
        titlePaint.textAlign = Paint.Align.LEFT
        titlePaint.textSize = 12f
        titlePaint.color = blueAccent
        canvas.drawText("INSTRUCTIONS & QUESTION PAPER CONTENT", 40f, curY, titlePaint)

        curY += 12f
        paint.color = blueAccent
        paint.strokeWidth = 1f
        canvas.drawLine(40f, curY, (pageWidth - 40).toFloat(), curY, paint)

        curY += 25f
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.color = textDark
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.DEFAULT

        val lines = assignment.description.chunked(78)
        for (line in lines) {
            canvas.drawText(line, 45f, curY, textPaint)
            curY += 18f
        }

        curY += 25f
        // Rubric box
        paint.color = lightBg
        paint.style = Paint.Style.FILL
        val notesRect = RectF(40f, curY, (pageWidth - 40).toFloat(), curY + 95f)
        canvas.drawRoundRect(notesRect, 6f, 6f, paint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = navyColor
        textPaint.textSize = 9.5f
        canvas.drawText("CBC ASSESSMENT CRITERIA:", 55f, curY + 22f, textPaint)

        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 8.5f
        textPaint.color = Color.DKGRAY
        canvas.drawText("1. Exceeding Expectations (EE): High level accuracy, thorough steps & clear reasoning.", 55f, curY + 40f, textPaint)
        canvas.drawText("2. Meeting Expectations (ME): Sound concepts and complete answers.", 55f, curY + 56f, textPaint)
        canvas.drawText("3. Please submit your answers before ${assignment.dueDate}.", 55f, curY + 72f, textPaint)

        val sigY = curY + 150f
        paint.color = blueAccent
        paint.strokeWidth = 1.5f
        canvas.drawLine(360f, sigY, (pageWidth - 50).toFloat(), sigY, paint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = navyColor
        textPaint.textSize = 9f
        canvas.drawText("Subject Master Signature", 370f, sigY + 18f, textPaint)
        textPaint.typeface = Typeface.DEFAULT
        textPaint.color = Color.GRAY
        textPaint.textSize = 8f
        canvas.drawText(assignment.teacherName, 370f, sigY + 32f, textPaint)

        // Footer
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 7.5f
        textPaint.color = Color.GRAY
        canvas.drawText("Kenya Competency-Based Curriculum (CBC) Official Academic Portal System", (pageWidth / 2).toFloat(), 820f, textPaint)

        pdfDocument.finishPage(page)

        return try {
            val outputDir = File(context.cacheDir, "downloaded_assignments")
            if (!outputDir.exists()) outputDir.mkdirs()

            val pdfFile = File(outputDir, "Assignment_${assignment.id}_${assignment.title.take(12).replace(" ", "_")}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    fun generateCbcStudentReportPdf(
        context: Context,
        student: CbcClassStudent,
        schoolName: String = "MoI Forces Academy Secondary",
        schoolMotto: String = "Education for Excellence & Service",
        attendanceRecords: List<StudentAttendance> = emptyList()
    ): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val navyColor = Color.rgb(15, 23, 42)
        val blueAccent = Color.rgb(37, 99, 235)
        val goldColor = Color.rgb(217, 119, 6)
        val emeraldPass = Color.rgb(5, 150, 105)
        val lightBg = Color.rgb(248, 250, 252)
        val grayBorder = Color.rgb(226, 232, 240)
        val textDark = Color.rgb(30, 41, 59)

        val paint = Paint().apply { isAntiAlias = true }
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = textDark
            textSize = 9.5f
            typeface = Typeface.DEFAULT
        }
        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = navyColor
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Top Accent
        paint.color = blueAccent
        paint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 12f, paint)

        // Border
        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawRect(20f, 20f, (pageWidth - 20).toFloat(), (pageHeight - 20).toFloat(), paint)

        // Header
        titlePaint.textAlign = Paint.Align.CENTER
        canvas.drawText(schoolName.uppercase(), (pageWidth / 2).toFloat(), 52f, titlePaint)

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 9f
        textPaint.color = goldColor
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("\"$schoolMotto\"", (pageWidth / 2).toFloat(), 68f, textPaint)

        textPaint.color = Color.DKGRAY
        textPaint.typeface = Typeface.DEFAULT
        canvas.drawText("OFFICIAL KENYA CBC STUDENT INDIVIDUAL ACADEMIC TRANSCRIPT", (pageWidth / 2).toFloat(), 84f, textPaint)

        paint.color = blueAccent
        paint.strokeWidth = 2f
        canvas.drawLine(40f, 94f, (pageWidth - 40).toFloat(), 94f, paint)

        // Student Info Header Box
        paint.color = lightBg
        paint.style = Paint.Style.FILL
        val cardRect = RectF(40f, 102f, (pageWidth - 40).toFloat(), 175f)
        canvas.drawRoundRect(cardRect, 8f, 8f, paint)

        paint.color = grayBorder
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(cardRect, 8f, 8f, paint)

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.textSize = 11f
        textPaint.color = navyColor
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("STUDENT NAME: ${student.studentName.uppercase()}", 55f, 122f, textPaint)

        textPaint.textSize = 9.5f
        textPaint.color = textDark
        canvas.drawText("Admission No: ${student.admissionNo}", 55f, 140f, textPaint)
        canvas.drawText("Class & Stream: ${student.gradeLevel} ${student.stream}", 300f, 140f, textPaint)

        val totalAtt = attendanceRecords.size
        val presentAtt = attendanceRecords.count { it.status == "PRESENT" }
        val attPct = if (totalAtt > 0) (presentAtt.toDouble() / totalAtt * 100).toInt() else 100

        canvas.drawText("Annual Average: ${String.format("%.1f%%", student.annualAverage)}", 55f, 158f, textPaint)
        canvas.drawText("Overall CBC Competency: ${student.overallRating}", 300f, 158f, textPaint)

        // Attendance pill
        textPaint.color = emeraldPass
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Attendance Record: $presentAtt/$totalAtt Days ($attPct% Present)", 55f, 172f, textPaint)

        // Subject Table
        var tableY = 205f
        paint.color = navyColor
        paint.style = Paint.Style.FILL
        canvas.drawRect(40f, tableY, (pageWidth - 40).toFloat(), tableY + 24f, paint)

        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 9f
        canvas.drawText("SUBJECT / LEARNING AREA", 50f, tableY + 16f, textPaint)
        canvas.drawText("TERM 1", 260f, tableY + 16f, textPaint)
        canvas.drawText("TERM 2", 330f, tableY + 16f, textPaint)
        canvas.drawText("TERM 3", 400f, tableY + 16f, textPaint)
        canvas.drawText("ANNUAL", 470f, tableY + 16f, textPaint)
        canvas.drawText("RATING", 525f, tableY + 16f, textPaint)

        tableY += 24f
        textPaint.color = textDark
        textPaint.typeface = Typeface.DEFAULT

        student.subjectScores.forEachIndexed { idx, sub ->
            paint.color = if (idx % 2 == 0) Color.WHITE else lightBg
            paint.style = Paint.Style.FILL
            canvas.drawRect(40f, tableY, (pageWidth - 40).toFloat(), tableY + 22f, paint)

            canvas.drawText(sub.subjectName, 50f, tableY + 15f, textPaint)
            canvas.drawText("${sub.term1Score}%", 260f, tableY + 15f, textPaint)
            canvas.drawText("${sub.term2Score}%", 330f, tableY + 15f, textPaint)
            canvas.drawText("${sub.term3Score}%", 400f, tableY + 15f, textPaint)

            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("${String.format("%.0f", sub.annualAverage)}%", 470f, tableY + 15f, textPaint)

            textPaint.color = if (sub.competencyRating == "EE" || sub.competencyRating == "ME") emeraldPass else goldColor
            canvas.drawText(sub.competencyRating, 530f, tableY + 15f, textPaint)

            textPaint.color = textDark
            textPaint.typeface = Typeface.DEFAULT
            tableY += 22f
        }

        // Summary Box
        tableY += 20f
        paint.color = lightBg
        paint.style = Paint.Style.FILL
        val sumRect = RectF(40f, tableY, (pageWidth - 40).toFloat(), tableY + 70f)
        canvas.drawRoundRect(sumRect, 6f, 6f, paint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = navyColor
        canvas.drawText("CBC PERFORMANCE KEY:", 50f, tableY + 20f, textPaint)

        textPaint.typeface = Typeface.DEFAULT
        textPaint.textSize = 8.5f
        textPaint.color = Color.DKGRAY
        canvas.drawText("EE: Exceeding Expectations (80 - 100%) | ME: Meeting Expectations (65 - 79%)", 50f, tableY + 38f, textPaint)
        canvas.drawText("AE: Approaching Expectations (50 - 64%) | BE: Below Expectations (0 - 49%)", 50f, tableY + 54f, textPaint)

        // Signatures
        val sigY = tableY + 110f
        paint.color = blueAccent
        paint.strokeWidth = 1.5f
        canvas.drawLine(50f, sigY, 220f, sigY, paint)
        canvas.drawLine(360f, sigY, (pageWidth - 50).toFloat(), sigY, paint)

        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.color = navyColor
        textPaint.textSize = 9f
        canvas.drawText("Class Teacher Signature", 60f, sigY + 16f, textPaint)
        canvas.drawText("Principal Official Seal", 370f, sigY + 16f, textPaint)

        // Footer
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = 7.5f
        textPaint.color = Color.GRAY
        canvas.drawText("Official Document issued by Kenya CBC Results Engine", (pageWidth / 2).toFloat(), 820f, textPaint)

        pdfDocument.finishPage(page)

        return try {
            val outputDir = File(context.cacheDir, "downloaded_reports")
            if (!outputDir.exists()) outputDir.mkdirs()

            val pdfFile = File(outputDir, "CBC_Report_${student.admissionNo.replace("/", "_")}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
