package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class CbcSubject(
    val code: String,
    val name: String,
    val category: String, // "Core Learning Area", "Elective Area"
    val maxScore: Int = 100
)

data class CbcStudentScore(
    val subjectCode: String,
    val subjectName: String,
    val term1Score: Int,
    val term2Score: Int,
    val term3Score: Int
) {
    val annualAverage: Double
        get() = (term1Score + term2Score + term3Score) / 3.0

    val competencyRating: String
        get() = when {
            annualAverage >= 80 -> "EE" // Exceeding Expectations
            annualAverage >= 65 -> "ME" // Meeting Expectations
            annualAverage >= 50 -> "AE" // Approaching Expectations
            else -> "BE"               // Below Expectations
        }

    val ratingLabel: String
        get() = when (competencyRating) {
            "EE" -> "Exceeding Expectations"
            "ME" -> "Meeting Expectations"
            "AE" -> "Approaching Expectations"
            else -> "Below Expectations"
        }
}

data class TeacherInfo(
    val teacherId: String,          // e.g. "TSC-849201"
    val name: String,               // e.g. "Tr. John Mwangi"
    val email: String,              // e.g. "jmwangi@kenyasecondary.ac.ke"
    val phone: String,              // e.g. "+254 722 111 222"
    val assignedSubject: String,    // e.g. "Integrated Science & Mathematics"
    val assignedClass: String,      // e.g. "Grade 10 East"
    val pin: String = "1234",       // Teacher login PIN
    val bio: String = "Senior Science & Mathematics Master dedicated to empowering students through inquiry-based CBC practical learning and digital problem solving.",
    val officeHours: String = "Mon - Fri: 2:00 PM - 4:00 PM (Science Block Office 12)",
    val photoUrl: String = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2",
    val announcement: String = "Grade 10 East Science Lab Projects are due on Friday! Please ensure all experimental logs are signed in your digital portfolio."
)

data class StudentAttendance(
    val id: String,                 // e.g. "ADM/2026/001_2026-07-28"
    val admissionNo: String,
    val studentName: String,
    val date: String,               // e.g. "2026-07-28"
    val status: String,             // "PRESENT", "ABSENT", "LATE", "EXCUSED"
    val note: String = ""
)

data class StudentAccount(
    val admissionNo: String,        // e.g. "ADM/2026/001"
    val officialName: String,       // e.g. "Kiprono Brian Koech"
    val gradeLevel: String = "Grade 10",
    val stream: String = "East",
    val password: String = "1234",  // Student secret password/PIN
    val email: String = "",
    val phone: String = ""
)

data class StudentAssignment(
    val id: String,                 // e.g. "ASN-101"
    val title: String,              // e.g. "Term 2 Mid-Term Mathematics Examination"
    val type: String,               // "EXAM", "HOMEWORK", "ASSIGNMENT", "REPORT"
    val subjectName: String,        // e.g. "Mathematics"
    val teacherId: String,          // e.g. "TSC-849201"
    val teacherName: String,        // e.g. "Tr. John Mwangi"
    val targetAdmissionNo: String,  // "ALL_STUDENTS" or specific admissionNo like "ADM/2026/001"
    val description: String,        // Exam/Assignment instructions or question set
    val dueDate: String,            // e.g. "2026-08-05"
    val dateSent: String = "2026-07-28",
    val maxScore: Int = 100
)

data class PrincipalAccount(
    val username: String = "principal",
    val password: String = "principal123",
    val officialName: String = "Dr. Mary Wambui (PhD)",
    val title: String = "School Principal & Chief Executive Officer",
    val email: String = "principal@kenyasecondary.ac.ke",
    val phone: String = "+254 712 345 678",
    val lastLoginDate: String = "2026-07-28"
)

data class CbcClassStudent(
    val admissionNo: String,
    val studentName: String,
    val gradeLevel: String = "Grade 10",
    val stream: String = "East",
    val gender: String = "Male",
    val subjectScores: List<CbcStudentScore>,
    val enrollmentStatus: String = "ACTIVE", // "ACTIVE", "NEW_ADMIT", "LEFT_SCHOOL", "TRANSFERRED", "GRADUATED"
    val admissionDate: String = "2026-01-08",
    val departureDate: String = "",
    val departureReason: String = "",
    val clearanceStatus: String = "",
    val archivalNotes: String = ""
) {
    val term1Average: Double
        get() = if (subjectScores.isNotEmpty()) subjectScores.map { it.term1Score }.average() else 0.0

    val term2Average: Double
        get() = if (subjectScores.isNotEmpty()) subjectScores.map { it.term2Score }.average() else 0.0

    val term3Average: Double
        get() = if (subjectScores.isNotEmpty()) subjectScores.map { it.term3Score }.average() else 0.0

    val annualAverage: Double
        get() = if (subjectScores.isNotEmpty()) subjectScores.map { it.annualAverage }.average() else 0.0

    val overallRating: String
        get() = when {
            annualAverage >= 80 -> "EE"
            annualAverage >= 65 -> "ME"
            annualAverage >= 50 -> "AE"
            else -> "BE"
        }
}

data class ClassSubjectAnalytics(
    val subjectCode: String,
    val subjectName: String,
    val classAverageTerm1: Double,
    val classAverageTerm2: Double,
    val classAverageTerm3: Double,
    val overallClassAverage: Double,
    val highestScore: Int,
    val lowestScore: Int,
    val eeCount: Int,
    val meCount: Int,
    val aeCount: Int,
    val beCount: Int
)

data class ClassTeacherAssignment(
    val classId: String = "Grade 10 East",
    val currentTeacherId: String = "TSC-849201",
    val currentTeacherName: String = "Tr. John Mwangi",
    val currentTeacherSubject: String = "Integrated Science & Mathematics",
    val activeSessionId: String = "SESS-2026-TERM2-001",
    val activeTermSession: String = "Term 2 - 2026 Academic Session",
    val assignedDate: String = "2026-05-10"
)

data class TeacherProgressArchive(
    val sessionId: String,
    val classId: String,
    val teacherId: String,
    val teacherName: String,
    val teacherSubject: String,
    val termSession: String,
    val startDate: String,
    val endDate: String,
    val totalStudentsEvaluated: Int,
    val classAnnualAverage: Double,
    val topPerformers: String,
    val handoverNotes: String
)

data class SchoolAnnouncement(
    val id: String,
    val authorName: String = "Dr. Peter Otieno",
    val authorTitle: String = "School Principal & Chief Academic Officer",
    val title: String,
    val content: String,
    val date: String,
    val category: String = "PRINCIPAL_NOTICE", // "PRINCIPAL_NOTICE", "ACADEMIC_CALENDAR", "EXAM_SCHEDULE"
    val isUrgent: Boolean = false
)

data class TeacherDiscussionComment(
    val id: String,
    val authorName: String,
    val authorTeacherId: String,
    val commentText: String,
    val date: String
)

data class TeacherDiscussionTopic(
    val id: String,
    val authorName: String,
    val authorTeacherId: String,
    val subjectCategory: String,
    val title: String,
    val description: String,
    val date: String,
    val comments: List<TeacherDiscussionComment> = emptyList()
)

data class UserCredentialRecord(
    val userId: String,
    val name: String,
    val userType: String, // "STUDENT", "TEACHER", "ADMIN"
    val username: String,
    val password: String, // Viewable by Admin ONLY for identity confirmation/recovery assistance
    val emailOrPhone: String = ""
)

class KenyaCbcDataService {

    val cbcSubjects = listOf(
        CbcSubject("MAT-CBC", "Mathematics", "Core Learning Area"),
        CbcSubject("ENG-CBC", "English Language & Literature", "Core Learning Area"),
        CbcSubject("KIS-CBC", "Kiswahili na Fasihi", "Core Learning Area"),
        CbcSubject("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", "Core Learning Area"),
        CbcSubject("AGR-CBC", "Agriculture & Nutrition", "Core Learning Area"),
        CbcSubject("COMP-CBC", "Computer Studies & Digital Literacy", "Elective Area"),
        CbcSubject("ARTS-CBC", "Creative Arts & Sports", "Core Learning Area"),
        CbcSubject("SOC-CBC", "Social Studies & Citizenship", "Core Learning Area"),
        CbcSubject("BUS-CBC", "Business Studies & Financial Literacy", "Elective Area"),
        CbcSubject("RE-CBC", "Religious Education (CRE / IRE)", "Core Learning Area")
    )

    fun getDefaultClassRoster(): List<CbcClassStudent> {
        return listOf(
            // GRADE 10 EAST (Active & New Admit)
            CbcClassStudent(
                admissionNo = "ADM/2026/001",
                studentName = "Amani Kiprop",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Male",
                enrollmentStatus = "ACTIVE",
                admissionDate = "2026-01-08",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 88, 92, 95),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 82, 85, 88),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 78, 80, 84),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 85, 90, 92),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 90, 88, 94),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 92, 95, 98),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 84, 86, 88),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 80, 82, 85),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 86, 88, 90),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 88, 90, 92)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2026/002",
                studentName = "Zuri Wanjiku",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Female",
                enrollmentStatus = "ACTIVE",
                admissionDate = "2026-01-08",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 75, 78, 82),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 88, 90, 94),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 90, 92, 95),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 76, 80, 84),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 82, 85, 88),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 80, 84, 86),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 92, 94, 96),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 85, 88, 90),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 78, 82, 85),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 90, 92, 94)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2026/003",
                studentName = "Juma Omondi",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Male",
                enrollmentStatus = "ACTIVE",
                admissionDate = "2026-01-08",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 62, 65, 68),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 70, 72, 75),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 72, 74, 76),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 58, 62, 66),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 75, 78, 80),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 68, 70, 72),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 85, 88, 90),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 65, 68, 70),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 60, 64, 68),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 74, 76, 78)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2026/004",
                studentName = "Nekesa Mwangi",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Female",
                enrollmentStatus = "NEW_ADMIT",
                admissionDate = "2026-05-12",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 52, 55, 58),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 65, 68, 70),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 68, 70, 72),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 48, 52, 56),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 62, 65, 68),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 55, 58, 62),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 78, 80, 82),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 60, 62, 65),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 54, 58, 60),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 68, 70, 72)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2026/005",
                studentName = "Baraka Hassan",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Male",
                enrollmentStatus = "ACTIVE",
                admissionDate = "2026-01-08",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 94, 96, 98),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 86, 88, 90),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 82, 85, 88),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 92, 95, 97),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 88, 90, 92),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 96, 98, 100),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 80, 82, 84),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 84, 86, 88),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 90, 92, 94),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 86, 88, 90)
                )
            ),

            // GRADE 10 WEST
            CbcClassStudent(
                admissionNo = "ADM/2026/010",
                studentName = "Faith Wanjiku",
                gradeLevel = "Grade 10",
                stream = "West",
                gender = "Female",
                enrollmentStatus = "ACTIVE",
                admissionDate = "2026-01-08",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 80, 84, 87),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 85, 88, 91),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 80, 82, 85),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 78, 82, 86),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 84, 86, 89),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 82, 85, 88),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 88, 90, 92),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 82, 84, 87),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 80, 83, 86),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 85, 87, 90)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2026/011",
                studentName = "Emmanuel Wafula",
                gradeLevel = "Grade 10",
                stream = "West",
                gender = "Male",
                enrollmentStatus = "NEW_ADMIT",
                admissionDate = "2026-06-01",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 72, 75, 78),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 78, 80, 82),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 75, 77, 80),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 70, 74, 78),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 80, 82, 85),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 84, 86, 88),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 80, 82, 85),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 76, 78, 81),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 72, 75, 78),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 82, 84, 86)
                )
            ),

            // GRADE 9
            CbcClassStudent(
                admissionNo = "ADM/2026/020",
                studentName = "Purity Cherotich",
                gradeLevel = "Grade 9",
                stream = "A",
                gender = "Female",
                enrollmentStatus = "ACTIVE",
                admissionDate = "2026-01-08",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 82, 85, 88),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 86, 89, 92),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 84, 87, 90),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 80, 84, 88),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 86, 88, 91),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 90, 92, 95),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 88, 90, 92),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 82, 85, 88),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 84, 86, 89),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 88, 90, 92)
                )
            ),

            // DEPARTED / TRANSFERRED / LEFT SCHOOL STUDENTS (ARCHIVED HISTORY)
            CbcClassStudent(
                admissionNo = "ADM/2025/112",
                studentName = "Caleb Otieno",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Male",
                enrollmentStatus = "TRANSFERRED",
                admissionDate = "2025-01-10",
                departureDate = "2026-04-18",
                departureReason = "Family relocation to Mombasa County (Transferred to Coast High School)",
                clearanceStatus = "Cleared - Library books returned & Lab fees settled",
                archivalNotes = "Official transfer letter & NEMIS learner records issued by Principal. Academic records archived for future reference.",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 70, 72, 74),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 76, 78, 80),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 72, 74, 76),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 68, 70, 72),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 74, 76, 78),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 72, 75, 78),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 80, 82, 84),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 70, 72, 74),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 68, 70, 72),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 76, 78, 80)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2025/140",
                studentName = "Precious Chebet",
                gradeLevel = "Grade 9",
                stream = "West",
                gender = "Female",
                enrollmentStatus = "LEFT_SCHOOL",
                admissionDate = "2025-01-10",
                departureDate = "2026-03-05",
                departureReason = "Private home schooling transition due to family medical travel",
                clearanceStatus = "Cleared - School sports uniform & locker keys returned",
                archivalNotes = "Parent request letter filed under Admin Office Ref #2026/MED/009.",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 85, 88, 90),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 88, 90, 92),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 84, 86, 88),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 82, 85, 88),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 86, 88, 90),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 90, 92, 94),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 92, 94, 96),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 85, 87, 90),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 82, 84, 86),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 88, 90, 92)
                )
            ),
            CbcClassStudent(
                admissionNo = "ADM/2024/098",
                studentName = "Kevin Ndung'u",
                gradeLevel = "Grade 10",
                stream = "East",
                gender = "Male",
                enrollmentStatus = "GRADUATED_ALUMNI",
                admissionDate = "2024-01-12",
                departureDate = "2025-11-28",
                departureReason = "Successfully completed Senior Secondary CBC Education (Class of 2025)",
                clearanceStatus = "Fully Cleared - Leaving Certificate Issued",
                archivalNotes = "CBC Final Transcript & National Examination Certificate issued. Member of Alumni Association.",
                subjectScores = listOf(
                    CbcStudentScore("MAT-CBC", "Mathematics", 90, 92, 95),
                    CbcStudentScore("ENG-CBC", "English Language & Literature", 88, 90, 92),
                    CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 85, 88, 90),
                    CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 92, 94, 96),
                    CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 90, 92, 94),
                    CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 95, 96, 98),
                    CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 88, 90, 92),
                    CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 86, 88, 90),
                    CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 92, 94, 96),
                    CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 90, 92, 94)
                )
            )
        )
    }

    fun getDefaultTeachers(): List<TeacherInfo> {
        return listOf(
            TeacherInfo(
                teacherId = "TSC-849201",
                name = "Tr. John Mwangi",
                email = "jmwangi@kenyasecondary.ac.ke",
                phone = "+254 722 111 222",
                assignedSubject = "Integrated Science & Mathematics",
                assignedClass = "Grade 10 East",
                pin = "1234",
                bio = "Senior Science Master with 12 years of CBC instructional experience in practical experiments and computer simulation.",
                officeHours = "Mon - Fri: 2:00 PM - 4:00 PM (Science Block Room 102)",
                photoUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2",
                announcement = "Reminder: Grade 10 East practical science journals are due on Friday. Ensure all calculations are completed!"
            ),
            TeacherInfo(
                teacherId = "TSC-650192",
                name = "Tr. Grace Akinyi",
                email = "gakinyi@kenyasecondary.ac.ke",
                phone = "+254 733 444 555",
                assignedSubject = "English Literature & Languages",
                assignedClass = "Grade 10 West",
                pin = "5678",
                bio = "Head of Languages Department, specialized in CBC competency assessments, public speaking, and creative writing.",
                officeHours = "Tue & Thu: 10:00 AM - 12:00 PM (Library Wing B)",
                photoUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2",
                announcement = "The Inter-class Debate and Creative Writing Championship entries close this Thursday!"
            )
        )
    }

    fun getDefaultAttendance(roster: List<CbcClassStudent>, date: String = "2026-07-28"): List<StudentAttendance> {
        return roster.mapIndexed { index, student ->
            val status = when (index) {
                3 -> "ABSENT"
                4 -> "LATE"
                else -> "PRESENT"
            }
            val note = if (status == "ABSENT") "Sick leave - Permission note submitted" else if (status == "LATE") "Delayed by school bus route B" else ""
            StudentAttendance(
                id = "${student.admissionNo}_$date",
                admissionNo = student.admissionNo,
                studentName = student.studentName,
                date = date,
                status = status,
                note = note
            )
        }
    }

    fun getDefaultStudentAccounts(): List<StudentAccount> {
        return listOf(
            StudentAccount("ADM/2026/001", "Kiprono Brian Koech", "Grade 10", "East", "1234", "brian.kiprono@kenyasecondary.ac.ke", "+254 712 111 222"),
            StudentAccount("ADM/2026/002", "Achieng Sharon Odhiambo", "Grade 10", "East", "1234", "sharon.achieng@kenyasecondary.ac.ke", "+254 723 333 444"),
            StudentAccount("ADM/2026/003", "Mutua Kevin Kilonzo", "Grade 10", "East", "1234", "kevin.mutua@kenyasecondary.ac.ke", "+254 734 555 666"),
            StudentAccount("ADM/2026/004", "Wanjiku Faith Njeri", "Grade 10", "West", "1234", "faith.wanjiku@kenyasecondary.ac.ke", "+254 745 777 888"),
            StudentAccount("ADM/2026/005", "Wafula Emmanuel Juma", "Grade 10", "West", "1234", "emmanuel.wafula@kenyasecondary.ac.ke", "+254 756 999 000")
        )
    }

    fun getDefaultAssignments(): List<StudentAssignment> {
        return listOf(
            StudentAssignment(
                id = "ASN-9001",
                title = "Grade 10 Integrated Science Practical Experiment Assignment",
                type = "ASSIGNMENT",
                subjectName = "Integrated Science",
                teacherId = "TSC-849201",
                teacherName = "Tr. John Mwangi",
                targetAdmissionNo = "ALL_STUDENTS",
                description = "Complete Section A & B on Chemical Reactions and Biology specimen analysis. Write down all safety precautions observed during lab experimentation.",
                dueDate = "2026-08-02",
                dateSent = "2026-07-28"
            ),
            StudentAssignment(
                id = "ASN-9002",
                title = "Term 2 Mid-Term Mathematics & Algebra Examination Paper",
                type = "EXAM",
                subjectName = "Mathematics & Logic",
                teacherId = "TSC-849201",
                teacherName = "Tr. John Mwangi",
                targetAdmissionNo = "ALL_STUDENTS",
                description = "Comprehensive Mid-Term paper covering Quadratic Equations, Matrix Operations, and CBC Financial Mathematics applications. Show all working clearly.",
                dueDate = "2026-08-05",
                dateSent = "2026-07-27"
            ),
            StudentAssignment(
                id = "ASN-9003",
                title = "English Literature Essay - CBC Creative Writing Homework",
                type = "HOMEWORK",
                subjectName = "English Literature & Languages",
                teacherId = "TSC-650192",
                teacherName = "Tr. Grace Akinyi",
                targetAdmissionNo = "ADM/2026/001",
                description = "Write a 500-word argumentative essay on 'The Role of Digital Technology in Modern African Education'. Ensure proper paragraph structure.",
                dueDate = "2026-07-31",
                dateSent = "2026-07-26"
            ),
            StudentAssignment(
                id = "ASN-9004",
                title = "Official Mid-Year Academic CBC Progress & Result Report",
                type = "REPORT",
                subjectName = "All Core Learning Areas",
                teacherId = "TSC-849201",
                teacherName = "Tr. John Mwangi",
                targetAdmissionNo = "ALL_STUDENTS",
                description = "Official individual student performance transcript and competency rating summary issued by the Class Master.",
                dueDate = "2026-08-10",
                dateSent = "2026-07-28"
            )
        )
    }

    fun calculateClassSubjectAnalytics(roster: List<CbcClassStudent>): List<ClassSubjectAnalytics> {
        return cbcSubjects.map { subject ->
            val scoresForSubject = roster.mapNotNull { student ->
                student.subjectScores.find { it.subjectCode == subject.code }
            }

            if (scoresForSubject.isEmpty()) {
                ClassSubjectAnalytics(
                    subjectCode = subject.code,
                    subjectName = subject.name,
                    classAverageTerm1 = 0.0,
                    classAverageTerm2 = 0.0,
                    classAverageTerm3 = 0.0,
                    overallClassAverage = 0.0,
                    highestScore = 0,
                    lowestScore = 0,
                    eeCount = 0,
                    meCount = 0,
                    aeCount = 0,
                    beCount = 0
                )
            } else {
                val t1Avg = scoresForSubject.map { it.term1Score }.average()
                val t2Avg = scoresForSubject.map { it.term2Score }.average()
                val t3Avg = scoresForSubject.map { it.term3Score }.average()
                val overallAvg = scoresForSubject.map { it.annualAverage }.average()

                val allScores = scoresForSubject.flatMap { listOf(it.term1Score, it.term2Score, it.term3Score) }
                val highest = allScores.maxOrNull() ?: 0
                val lowest = allScores.minOrNull() ?: 0

                val ee = scoresForSubject.count { it.competencyRating == "EE" }
                val me = scoresForSubject.count { it.competencyRating == "ME" }
                val ae = scoresForSubject.count { it.competencyRating == "AE" }
                val be = scoresForSubject.count { it.competencyRating == "BE" }

                ClassSubjectAnalytics(
                    subjectCode = subject.code,
                    subjectName = subject.name,
                    classAverageTerm1 = t1Avg,
                    classAverageTerm2 = t2Avg,
                    classAverageTerm3 = t3Avg,
                    overallClassAverage = overallAvg,
                    highestScore = highest,
                    lowestScore = lowest,
                    eeCount = ee,
                    meCount = me,
                    aeCount = ae,
                    beCount = be
                )
            }
        }
    }

    fun getDefaultClassTeacherAssignment(): ClassTeacherAssignment {
        return ClassTeacherAssignment(
            classId = "Grade 10 East",
            currentTeacherId = "TSC-849201",
            currentTeacherName = "Tr. John Mwangi",
            currentTeacherSubject = "Integrated Science & Mathematics",
            activeSessionId = "SESS-2026-TERM2-001",
            activeTermSession = "Term 2 - 2026 Academic Session",
            assignedDate = "2026-05-10"
        )
    }

    fun getDefaultTeacherArchives(): List<TeacherProgressArchive> {
        return listOf(
            TeacherProgressArchive(
                sessionId = "SESS-2026-TERM1-009",
                classId = "Grade 10 East",
                teacherId = "TSC-774102",
                teacherName = "Tr. David Kamau",
                teacherSubject = "Social Studies & Kiswahili",
                termSession = "Term 1 - 2026 Academic Session",
                startDate = "2026-01-08",
                endDate = "2026-05-02",
                totalStudentsEvaluated = 45,
                classAnnualAverage = 81.4,
                topPerformers = "Amani Kiprop (88.5%), Baraka Hassan Ali (86.2%)",
                handoverNotes = "Class academic foundation in Social Studies was established successfully. Handed over class teacher leadership to Tr. John Mwangi for Term 2."
            )
        )
    }

    fun getDefaultSchoolAnnouncements(): List<SchoolAnnouncement> {
        return listOf(
            SchoolAnnouncement(
                id = "ANN-2026-001",
                authorName = "Dr. Peter Otieno",
                authorTitle = "School Principal & Chief Academic Officer",
                title = "Official Notice: Term 2 CBC Mid-Term Academic Progress & Parent Engagement",
                content = "Dear Teachers, Parents, and Students, Welcome to the mid-term evaluations. All CBC Formative Assessment marksheets must be finalized in the digital portal. Parent-Teacher Academic Conferences will commence next Friday.",
                date = "2026-07-28",
                category = "PRINCIPAL_NOTICE",
                isUrgent = true
            ),
            SchoolAnnouncement(
                id = "ANN-2026-002",
                authorName = "Tr. John Mwangi",
                authorTitle = "Senior Academic Dean & Science HOD",
                title = "National STEM Science & Innovation Exhibition Competition",
                content = "Students from Grade 9 and Grade 10 East are invited to register their CBC Science practical projects for the upcoming Regional STEM Fair. Please consult your Class Teacher for guidance.",
                date = "2026-07-25",
                category = "ACADEMIC_CALENDAR",
                isUrgent = false
            ),
            SchoolAnnouncement(
                id = "ANN-2026-003",
                authorName = "Dr. Peter Otieno",
                authorTitle = "School Principal",
                title = "Upgraded Digital Result Portal & Online Marksheet Verification",
                content = "We have launched the updated CBC Digital Assessment Hub. Students and guardians can now generate official QR-coded PDF transcripts directly online.",
                date = "2026-07-20",
                category = "PRINCIPAL_NOTICE",
                isUrgent = false
            )
        )
    }

    fun getDefaultTeacherDiscussionTopics(): List<TeacherDiscussionTopic> {
        return listOf(
            TeacherDiscussionTopic(
                id = "DISC-101",
                authorName = "Tr. John Mwangi",
                authorTeacherId = "TSC-849201",
                subjectCategory = "Integrated Science",
                title = "Standardizing Practical Lab Formative Marks for Grade 10 CBC",
                description = "Colleagues, let's discuss aligning our scoring rubrics for laboratory practical experiments across Grade 10 streams before uploading Term 2 marks. Are there specific criteria we should emphasize?",
                date = "2026-07-27",
                comments = listOf(
                    TeacherDiscussionComment(
                        id = "CMT-101-1",
                        authorName = "Tr. Grace Akinyi",
                        authorTeacherId = "TSC-982103",
                        commentText = "Agreed, Tr. John. We should ensure student safety protocols and hypothesis formulation carry equal weight with the final results.",
                        date = "2026-07-27"
                    ),
                    TeacherDiscussionComment(
                        id = "CMT-101-2",
                        authorName = "Tr. David Kamau",
                        authorTeacherId = "TSC-774102",
                        commentText = "I have uploaded a sample rubric template in our staff shared folder for review.",
                        date = "2026-07-28"
                    )
                )
            ),
            TeacherDiscussionTopic(
                id = "DISC-102",
                authorName = "Tr. Grace Akinyi",
                authorTeacherId = "TSC-982103",
                subjectCategory = "Pedagogy & Class Management",
                title = "Supporting Below Expectations (BE) Students in Formative Assessment",
                description = "Strategies and peer-mentorship plans for helping students performing in the BE category bridge core learning gaps ahead of Term 3 evaluations.",
                date = "2026-07-26",
                comments = listOf(
                    TeacherDiscussionComment(
                        id = "CMT-102-1",
                        authorName = "Tr. John Mwangi",
                        authorTeacherId = "TSC-849201",
                        commentText = "Assigning student study partners (EE + BE pairs) worked very well in Grade 10 East last term.",
                        date = "2026-07-26"
                    )
                )
            )
        )
    }

    fun getDefaultUserCredentials(): List<UserCredentialRecord> {
        return listOf(
            UserCredentialRecord("ADMIN-001", "School Administrator", "ADMIN", "admin", "admin123", "admin@kenyasecondary.ac.ke"),
            UserCredentialRecord("TSC-849201", "Tr. John Mwangi", "TEACHER", "TSC-849201", "1234", "jmwangi@kenyasecondary.ac.ke"),
            UserCredentialRecord("TSC-982103", "Tr. Grace Akinyi", "TEACHER", "TSC-982103", "1234", "gakinyi@kenyasecondary.ac.ke"),
            UserCredentialRecord("TSC-774102", "Tr. David Kamau", "TEACHER", "TSC-774102", "1234", "dkamau@kenyasecondary.ac.ke"),
            UserCredentialRecord("ADM/2026/001", "Baraka Hassan Ali", "STUDENT", "ADM/2026/001", "1234", "baraka@student.ac.ke"),
            UserCredentialRecord("ADM/2026/002", "Amani Kiprop", "STUDENT", "ADM/2026/002", "1234", "amani@student.ac.ke"),
            UserCredentialRecord("ADM/2026/003", "Zuri Wanjiku", "STUDENT", "ADM/2026/003", "1234", "zuri@student.ac.ke"),
            UserCredentialRecord("ADM/2026/004", "Jabari Omondi", "STUDENT", "ADM/2026/004", "1234", "jabari@student.ac.ke"),
            UserCredentialRecord("ADM/2026/005", "Faith Chebet", "STUDENT", "ADM/2026/005", "1234", "faith@student.ac.ke")
        )
    }
}
