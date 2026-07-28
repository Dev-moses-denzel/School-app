package com.example.data.remote

import com.example.data.model.PortalBoard
import com.example.data.model.StudentResult
import com.example.data.model.SubjectScore
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import java.security.MessageDigest
import kotlin.math.roundToInt

class ResultPortalService {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val subjectListAdapter = moshi.adapter<List<SubjectScore>>(
        Types.newParameterizedType(List::class.java, SubjectScore::class.java)
    )

    fun getSupportedBoards(): List<PortalBoard> {
        return listOf(
            PortalBoard("NEB", "National Examination Board", "NEB-2026", "National Portal", "Board"),
            PortalBoard("CBSE", "Central Board of Secondary Education", "CBSE-IN", "Central Region", "Board"),
            PortalBoard("STU", "State Technological University", "STU-TECH", "Engineering & Science", "University"),
            PortalBoard("IHES", "Institute of Higher Education & Science", "IHES-UNIV", "Academic Campus", "University"),
            PortalBoard("CHSE", "Council for Higher Secondary Education", "CHSE-GOV", "Secondary Council", "School"),
            PortalBoard("MDSC", "Medical & Dental Sciences Board", "MDSC-HEALTH", "Medical Portal", "University")
        )
    }

    suspend fun queryOnlineResult(
        boardCode: String,
        rollNumber: String,
        registrationNumber: String,
        studentNameInput: String,
        examTerm: String
    ): Result<StudentResult> {
        // Simulate network connection delay
        delay(1200)

        val cleanRoll = rollNumber.trim().uppercase()
        val boards = getSupportedBoards()
        val selectedBoard = boards.find { it.code == boardCode } ?: boards.first()

        // 1. Check for Preloaded Known Roll Numbers
        val preloaded = getPreloadedResult(cleanRoll, selectedBoard, examTerm, studentNameInput)
        if (preloaded != null) {
            return Result.success(preloaded)
        }

        // 2. Generate dynamic realistic result for any custom Roll Number
        val generated = generateDynamicResult(
            board = selectedBoard,
            rollNumber = if (cleanRoll.isEmpty()) "ROLL" + (100000..999999).random() else cleanRoll,
            regNumber = if (registrationNumber.isBlank()) "REG" + System.currentTimeMillis().toString().takeLast(6) else registrationNumber,
            name = if (studentNameInput.isBlank()) "Student " + cleanRoll.takeLast(4) else studentNameInput,
            examTerm = if (examTerm.isBlank()) "Spring 2026 Final Exams" else examTerm
        )

        return Result.success(generated)
    }

    private fun getPreloadedResult(
        roll: String,
        board: PortalBoard,
        examTerm: String,
        customName: String
    ): StudentResult? {
        val term = if (examTerm.isBlank()) "Spring 2026 Final Semester" else examTerm

        return when (roll) {
            "STUDENT202601", "202601" -> {
                val subjects = listOf(
                    SubjectScore("CS-601", "Advanced Algorithms & Data Structures", 4, 28, 64, 92, 100, "A+", 4.0, true),
                    SubjectScore("CS-602", "Cloud Computing & Distributed Systems", 4, 27, 61, 88, 100, "A", 3.8, true),
                    SubjectScore("CS-603", "Artificial Intelligence & Machine Learning", 4, 29, 66, 95, 100, "A+", 4.0, true),
                    SubjectScore("CS-604", "Mobile Application Development (Android)", 3, 30, 65, 95, 100, "A+", 4.0, true),
                    SubjectScore("CS-605", "Database Systems & Big Data", 3, 26, 58, 84, 100, "A-", 3.5, true),
                    SubjectScore("CS-606", "Capstone Project & Thesis", 6, 29, 68, 97, 100, "A+", 4.0, true)
                )
                createResultObj(
                    rollNumber = "STUDENT202601",
                    regNumber = "REG-2022-8821",
                    studentName = if (customName.isNotBlank()) customName else "Alex Rivera",
                    institution = "Department of Computer Science & Engineering",
                    board = board.name,
                    course = "Bachelor of Science in Computer Science",
                    branch = "Software Engineering",
                    term = term,
                    session = "2022-2026",
                    subjects = subjects
                )
            }
            "STUDENT202602", "202602" -> {
                val subjects = listOf(
                    SubjectScore("BM-401", "Biomedical Instrumentation & Signals", 4, 25, 59, 84, 100, "A-", 3.5, true),
                    SubjectScore("BM-402", "Tissue Engineering & Biomaterials", 4, 28, 62, 90, 100, "A+", 4.0, true),
                    SubjectScore("BM-403", "Human Physiology & Neural Systems", 3, 27, 60, 87, 100, "A", 3.8, true),
                    SubjectScore("BM-404", "Medical Imaging Processing", 4, 26, 56, 82, 100, "B+", 3.3, true),
                    SubjectScore("BM-405", "Clinical Engineering Ethics", 2, 29, 65, 94, 100, "A+", 4.0, true)
                )
                createResultObj(
                    rollNumber = "STUDENT202602",
                    regNumber = "REG-2023-4109",
                    studentName = if (customName.isNotBlank()) customName else "Sophia Chen",
                    institution = "Faculty of Biomedical Sciences",
                    board = board.name,
                    course = "Bachelor of Technology in Biomedical Engineering",
                    branch = "Biomedical Technology",
                    term = term,
                    session = "2023-2027",
                    subjects = subjects
                )
            }
            "STUDENT202603", "202603" -> {
                val subjects = listOf(
                    SubjectScore("MTH-12", "Advanced Mathematics & Calculus", 5, 30, 66, 96, 100, "A+", 4.0, true),
                    SubjectScore("PHY-12", "Physics (Thermodynamics & Electromagnetism)", 5, 29, 64, 93, 100, "A+", 4.0, true),
                    SubjectScore("CHM-12", "Organic & Analytical Chemistry", 5, 28, 61, 89, 100, "A", 3.8, true),
                    SubjectScore("ENG-12", "English Literature & Composition", 4, 27, 63, 90, 100, "A+", 4.0, true),
                    SubjectScore("CSC-12", "Computer Science Principles", 4, 30, 68, 98, 100, "A+", 4.0, true)
                )
                createResultObj(
                    rollNumber = "STUDENT202603",
                    regNumber = "REG-2024-9912",
                    studentName = if (customName.isNotBlank()) customName else "Marcus Vance",
                    institution = "St. Jude Senior Academy",
                    board = board.name,
                    course = "Higher Secondary Certificate (Grade 12)",
                    branch = "Science Stream",
                    term = term,
                    session = "2024-2026",
                    subjects = subjects
                )
            }
            else -> null
        }
    }

    private fun generateDynamicResult(
        board: PortalBoard,
        rollNumber: String,
        regNumber: String,
        name: String,
        examTerm: String
    ): StudentResult {
        // Seed random generator with hash of rollNumber to keep it deterministic for same roll
        val seed = rollNumber.hashCode().toLong()
        val random = kotlin.random.Random(seed)

        val subjectTemplates = listOf(
            Triple("SUB-101", "Core Analytical Foundations", 4),
            Triple("SUB-102", "Advanced Applied Mathematics", 4),
            Triple("SUB-103", "Research Methodology & Ethics", 3),
            Triple("SUB-104", "Information Systems & Tech", 4),
            Triple("SUB-105", "Professional Communication", 2),
            Triple("SUB-106", "Elective Specialization Seminar", 3)
        )

        val subjects = subjectTemplates.map { (code, title, credits) ->
            val internal = random.nextInt(22, 30) // Max 30
            val external = random.nextInt(45, 68) // Max 70
            val total = internal + external
            val (grade, gpa, pass) = getGradeDetails(total)

            SubjectScore(
                code = code,
                name = title,
                credits = credits,
                internalMarks = internal,
                externalMarks = external,
                totalMarks = total,
                maxMarks = 100,
                grade = grade,
                gradePoint = gpa,
                isPass = pass
            )
        }

        return createResultObj(
            rollNumber = rollNumber,
            regNumber = regNumber,
            studentName = name,
            institution = "${board.name} Affiliated Center",
            board = board.name,
            course = "Bachelor Degree Program",
            branch = "General Major",
            term = examTerm,
            session = "2025-2026",
            subjects = subjects
        )
    }

    private fun createResultObj(
        rollNumber: String,
        regNumber: String,
        studentName: String,
        institution: String,
        board: String,
        course: String,
        branch: String,
        term: String,
        session: String,
        subjects: List<SubjectScore>
    ): StudentResult {
        val totalCredits = subjects.sumOf { it.credits }
        val earnedCredits = subjects.filter { it.isPass }.sumOf { it.credits }
        
        val weightedGpaSum = subjects.sumOf { it.gradePoint * it.credits }
        val cgpa = if (totalCredits > 0) (weightedGpaSum / totalCredits * 100).roundToInt() / 100.0 else 0.0

        val totalMarksObtained = subjects.sumOf { it.totalMarks }
        val maxTotalMarks = subjects.sumOf { it.maxMarks }
        val totalPercentage = if (maxTotalMarks > 0) ((totalMarksObtained.toDouble() / maxTotalMarks) * 10000).roundToInt() / 100.0 else 0.0

        val allPassed = subjects.all { it.isPass }
        val overallStatus = when {
            !allPassed -> "FAILED"
            cgpa >= 3.8 || totalPercentage >= 85.0 -> "PASSED WITH DISTINCTION"
            cgpa >= 3.0 || totalPercentage >= 65.0 -> "FIRST CLASS PASS"
            else -> "PASSED"
        }

        val division = when {
            cgpa >= 3.75 || totalPercentage >= 80.0 -> "First Class with Distinction"
            cgpa >= 3.0 || totalPercentage >= 60.0 -> "First Division"
            cgpa >= 2.25 || totalPercentage >= 50.0 -> "Second Division"
            else -> "Pass Division"
        }

        val id = "${rollNumber}_${term.replace(" ", "_")}"
        val subjectsJson = subjectListAdapter.toJson(subjects)
        val hash = generateHash("$id:$studentName:$cgpa:$totalPercentage")

        return StudentResult(
            id = id,
            rollNumber = rollNumber,
            registrationNumber = regNumber,
            studentName = studentName,
            institutionName = institution,
            boardOrUniversity = board,
            courseOrProgram = course,
            branchOrStream = branch,
            examTerm = term,
            sessionYear = session,
            cgpa = cgpa,
            totalPercentage = totalPercentage,
            totalCredits = totalCredits,
            earnedCredits = earnedCredits,
            overallStatus = overallStatus,
            division = division,
            publishDate = "July 28, 2026",
            verificationHash = hash,
            subjectsJson = subjectsJson
        )
    }

    private fun getGradeDetails(totalMarks: Int): Triple<String, Double, Boolean> {
        return when {
            totalMarks >= 90 -> Triple("A+", 4.0, true)
            totalMarks >= 85 -> Triple("A", 3.8, true)
            totalMarks >= 80 -> Triple("A-", 3.5, true)
            totalMarks >= 75 -> Triple("B+", 3.3, true)
            totalMarks >= 70 -> Triple("B", 3.0, true)
            totalMarks >= 65 -> Triple("B-", 2.7, true)
            totalMarks >= 60 -> Triple("C+", 2.3, true)
            totalMarks >= 50 -> Triple("C", 2.0, true)
            totalMarks >= 40 -> Triple("D", 1.0, true)
            else -> Triple("F", 0.0, false)
        }
    }

    private fun generateHash(input: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(input.toByteArray())
            digest.fold("") { str, it -> str + "%02x".format(it) }.take(16).uppercase()
        } catch (e: Exception) {
            "VERIFIED_AUTH_2026"
        }
    }
}
