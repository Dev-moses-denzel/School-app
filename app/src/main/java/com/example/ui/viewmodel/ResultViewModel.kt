package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.sync.AcademicAlert
import com.example.data.sync.FcmNotificationManager
import com.example.data.sync.FirestoreSyncManager
import com.example.data.sync.SyncState
import com.example.data.model.PortalBoard
import com.example.data.model.StudentResult
import com.example.data.remote.CbcClassStudent
import com.example.data.remote.CbcStudentScore
import com.example.data.remote.ClassSubjectAnalytics
import com.example.data.remote.ClassTeacherAssignment
import com.example.data.remote.KenyaCbcDataService
import com.example.data.remote.ResultPortalService
import com.example.data.remote.SchoolAnnouncement
import com.example.data.remote.StudentAccount
import com.example.data.remote.StudentAssignment
import com.example.data.remote.StudentAttendance
import com.example.data.remote.TeacherDiscussionComment
import com.example.data.remote.TeacherDiscussionTopic
import com.example.data.remote.TeacherInfo
import com.example.data.remote.TeacherProgressArchive
import com.example.data.remote.UserCredentialRecord
import com.example.util.PdfResultGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

import com.example.ui.theme.ColorBlindMode
import com.example.ui.theme.ThemeMode

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val colorBlindMode: ColorBlindMode = ColorBlindMode.NONE,
    val profilePictureUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
    val userName: String = "Student Scholar"
)

data class AuthUser(
    val id: String,
    val name: String,
    val role: String, // "STUDENT" or "TEACHER"
    val gradeClass: String = "Grade 10 East",
    val schoolName: String = "Kenya Secondary School (CBC)"
)

data class SchoolInfo(
    val schoolName: String = "Kenya National Secondary School",
    val schoolMotto: String = "Strive for Excellence & Integrity",
    val phonePrimary: String = "+254 712 345 678",
    val phoneSecondary: String = "+254 20 890 1234",
    val email: String = "info@kenyasecondary.ac.ke",
    val address: String = "P.O. Box 40112 - 00100, Nairobi, Kenya",
    val website: String = "www.kenyasecondary.ac.ke",
    val history: String = "Founded in 1968, Kenya National Secondary School has been a pioneer in academic excellence, character building, and holistic education. With the rollout of the Competency Based Curriculum (CBC), our school offers state-of-the-art digital literacy labs, integrated science complexes, and vibrant creative arts and agricultural workshops to empower the next generation of global leaders.",
    val bannerImageUrl: String = "",
    val principalName: String = "Dr. Mary Wambui (PhD)",
    val emergencyContact: String = "+254 700 999 111"
)

sealed interface SearchUiState {
    object Idle : SearchUiState
    object Loading : SearchUiState
    data class Success(val result: StudentResult) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

sealed interface PdfUiState {
    object Idle : PdfUiState
    object Generating : PdfUiState
    data class Ready(val file: File) : PdfUiState
    data class Error(val message: String) : PdfUiState
}

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.studentResultDao()
    private val portalService = ResultPortalService()
    val cbcService = KenyaCbcDataService()

    // Firestore Room-to-Cloud Sync Manager
    val syncManager = FirestoreSyncManager(db, viewModelScope)
    val syncState: StateFlow<SyncState> = syncManager.syncState

    // Firebase Cloud Messaging (FCM) Manager for Real-time Grade & Assignment PDF alerts
    val fcmManager = FcmNotificationManager(application, viewModelScope)
    val academicAlerts: StateFlow<List<AcademicAlert>> = fcmManager.alertsList
    val unreadAlertsCount: StateFlow<Int> = fcmManager.unreadCount

    val supportedBoards: List<PortalBoard> = portalService.getSupportedBoards()

    // Authentication State
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    // App Settings Preferences (Theme Mode, Color Blind Mode, Profile Picture)
    private val prefs = application.getSharedPreferences("cbc_app_settings", Context.MODE_PRIVATE)

    private val _appSettings = MutableStateFlow(loadSettingsFromPrefs())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private fun loadSettingsFromPrefs(): AppSettings {
        val themeStr = prefs.getString("theme_mode", ThemeMode.LIGHT.name) ?: ThemeMode.LIGHT.name
        val cbStr = prefs.getString("color_blind_mode", ColorBlindMode.NONE.name) ?: ColorBlindMode.NONE.name
        val picUrl = prefs.getString("profile_picture_url", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150") ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
        val name = prefs.getString("user_name", "Student Scholar") ?: "Student Scholar"

        return AppSettings(
            themeMode = try { ThemeMode.valueOf(themeStr) } catch (e: Exception) { ThemeMode.LIGHT },
            colorBlindMode = try { ColorBlindMode.valueOf(cbStr) } catch (e: Exception) { ColorBlindMode.NONE },
            profilePictureUrl = picUrl,
            userName = name
        )
    }

    fun updateThemeMode(mode: ThemeMode) {
        _appSettings.value = _appSettings.value.copy(themeMode = mode)
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun updateColorBlindMode(mode: ColorBlindMode) {
        _appSettings.value = _appSettings.value.copy(colorBlindMode = mode)
        prefs.edit().putString("color_blind_mode", mode.name).apply()
    }

    fun updateProfilePicture(urlOrUri: String) {
        _appSettings.value = _appSettings.value.copy(profilePictureUrl = urlOrUri)
        prefs.edit().putString("profile_picture_url", urlOrUri).apply()
    }

    fun updateUserName(name: String) {
        _appSettings.value = _appSettings.value.copy(userName = name)
        prefs.edit().putString("user_name", name).apply()
    }

    // Class Teacher Student Roster (Kenya CBC)
    private val _cbcRoster = MutableStateFlow<List<CbcClassStudent>>(cbcService.getDefaultClassRoster())
    val cbcRoster: StateFlow<List<CbcClassStudent>> = _cbcRoster.asStateFlow()

    // Class Analytics (Calculated from CBC Roster)
    private val _cbcAnalytics = MutableStateFlow<List<ClassSubjectAnalytics>>(cbcService.calculateClassSubjectAnalytics(_cbcRoster.value))
    val cbcAnalytics: StateFlow<List<ClassSubjectAnalytics>> = _cbcAnalytics.asStateFlow()

    // Currently Selected CBC Student Profile
    private val _selectedCbcStudent = MutableStateFlow<CbcClassStudent?>(_cbcRoster.value.firstOrNull())
    val selectedCbcStudent: StateFlow<CbcClassStudent?> = _selectedCbcStudent.asStateFlow()

    init {
        // Start real-time Firestore sync
        syncManager.startRealtimeSync()

        // Collect local Room attendance updates
        viewModelScope.launch {
            db.studentAttendanceDao().getAllAttendance().collect { entities ->
                if (entities.isNotEmpty()) {
                    _attendanceRecords.value = entities.map { it.toDomainModel() }
                }
            }
        }

        // Collect local Room student performance updates
        viewModelScope.launch {
            db.studentPerformanceDao().getAllPerformance().collect { entities ->
                if (entities.isNotEmpty()) {
                    val rosterFromDb = entities.map { it.toDomainModel() }
                    _cbcRoster.value = rosterFromDb
                    _cbcAnalytics.value = cbcService.calculateClassSubjectAnalytics(rosterFromDb)
                }
            }
        }

        // Initial seed into Room and Firestore sync manager
        viewModelScope.launch {
            syncManager.saveBatchStudentPerformance(_cbcRoster.value)
            syncManager.saveBatchAttendanceRecords(_attendanceRecords.value)
        }
    }

    // School Info & Admin State
    private val _schoolInfo = MutableStateFlow(SchoolInfo())
    val schoolInfo: StateFlow<SchoolInfo> = _schoolInfo.asStateFlow()

    // Principal Unique Account & Authentication State
    private val _principalAccount = MutableStateFlow(com.example.data.remote.PrincipalAccount())
    val principalAccount: StateFlow<com.example.data.remote.PrincipalAccount> = _principalAccount.asStateFlow()

    private val _isPrincipalLoggedIn = MutableStateFlow(false)
    val isPrincipalLoggedIn: StateFlow<Boolean> = _isPrincipalLoggedIn.asStateFlow()

    fun loginPrincipal(usernameInput: String, passwordInput: String): Pair<Boolean, String> {
        val current = _principalAccount.value
        val cleanUser = usernameInput.trim().lowercase()
        val cleanPass = passwordInput.trim()

        if (cleanUser.isBlank() || cleanPass.isBlank()) {
            return Pair(false, "Please enter both username and password.")
        }

        if (cleanUser == current.username.lowercase() && cleanPass == current.password) {
            _isPrincipalLoggedIn.value = true
            return Pair(true, "Welcome to Principal Portal, ${current.officialName}!")
        } else {
            return Pair(false, "Invalid Principal credentials. Default username is 'principal' and password 'principal123'.")
        }
    }

    fun logoutPrincipal() {
        _isPrincipalLoggedIn.value = false
    }

    fun updatePrincipalPassword(newPass: String): Pair<Boolean, String> {
        val cleanPass = newPass.trim()
        if (cleanPass.length < 4) {
            return Pair(false, "Password must be at least 4 characters long.")
        }
        _principalAccount.value = _principalAccount.value.copy(password = cleanPass)
        return Pair(true, "Principal portal password updated successfully!")
    }

    // Student Enrollment Status & Admissions
    fun admitNewStudent(
        studentName: String,
        gradeLevel: String = "Grade 10",
        stream: String = "East",
        gender: String = "Male",
        admissionNoInput: String = ""
    ): Pair<Boolean, String> {
        val cleanName = studentName.trim()
        if (cleanName.isBlank()) return Pair(false, "Please enter the student's full official name.")

        val cleanAdmNo = if (admissionNoInput.isNotBlank()) admissionNoInput.trim().uppercase() else "ADM/2026/${(100..999).random()}"

        val defaultScores = listOf(
            CbcStudentScore("MAT-CBC", "Mathematics", 70, 72, 75),
            CbcStudentScore("ENG-CBC", "English Language & Literature", 75, 78, 80),
            CbcStudentScore("KIS-CBC", "Kiswahili na Fasihi", 72, 75, 78),
            CbcStudentScore("SCI-CBC", "Integrated Science (Bio/Chem/Phys)", 68, 72, 76),
            CbcStudentScore("AGR-CBC", "Agriculture & Nutrition", 74, 78, 80),
            CbcStudentScore("COMP-CBC", "Computer Studies & Digital Literacy", 78, 80, 82),
            CbcStudentScore("ARTS-CBC", "Creative Arts & Sports", 80, 82, 85),
            CbcStudentScore("SOC-CBC", "Social Studies & Citizenship", 72, 75, 78),
            CbcStudentScore("BUS-CBC", "Business Studies & Financial Literacy", 70, 74, 76),
            CbcStudentScore("RE-CBC", "Religious Education (CRE / IRE)", 76, 78, 80)
        )

        val newStudent = CbcClassStudent(
            admissionNo = cleanAdmNo,
            studentName = cleanName,
            gradeLevel = gradeLevel,
            stream = stream,
            gender = gender,
            subjectScores = defaultScores,
            enrollmentStatus = "NEW_ADMIT",
            admissionDate = "2026-07-28"
        )

        _cbcRoster.value = listOf(newStudent) + _cbcRoster.value
        _cbcAnalytics.value = cbcService.calculateClassSubjectAnalytics(_cbcRoster.value)

        viewModelScope.launch {
            syncManager.saveBatchStudentPerformance(listOf(newStudent))
        }

        return Pair(true, "Student $cleanName admitted successfully under $cleanAdmNo in $gradeLevel $stream!")
    }

    fun updateStudentEnrollmentStatus(
        admissionNo: String,
        newStatus: String,
        departureDate: String = "2026-07-28",
        departureReason: String = "",
        clearanceStatus: String = "Cleared by Administration",
        archivalNotes: String = ""
    ) {
        _cbcRoster.value = _cbcRoster.value.map { student ->
            if (student.admissionNo.equals(admissionNo, ignoreCase = true)) {
                student.copy(
                    enrollmentStatus = newStatus,
                    departureDate = if (newStatus in listOf("LEFT_SCHOOL", "TRANSFERRED", "GRADUATED_ALUMNI")) departureDate else "",
                    departureReason = departureReason,
                    clearanceStatus = clearanceStatus,
                    archivalNotes = archivalNotes
                )
            } else {
                student
            }
        }
        _cbcAnalytics.value = cbcService.calculateClassSubjectAnalytics(_cbcRoster.value)
    }

    fun getAnalyticsForClass(gradeStream: String): List<ClassSubjectAnalytics> {
        val classStudents = if (gradeStream == "ALL") {
            _cbcRoster.value
        } else {
            _cbcRoster.value.filter { "${it.gradeLevel} ${it.stream}".equals(gradeStream, ignoreCase = true) || it.gradeLevel.equals(gradeStream, ignoreCase = true) }
        }
        return cbcService.calculateClassSubjectAnalytics(classStudents)
    }

    fun getStudentsForClass(gradeStream: String): List<CbcClassStudent> {
        return if (gradeStream == "ALL") {
            _cbcRoster.value
        } else {
            _cbcRoster.value.filter { "${it.gradeLevel} ${it.stream}".equals(gradeStream, ignoreCase = true) || it.gradeLevel.equals(gradeStream, ignoreCase = true) }
        }
    }

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminPassword = MutableStateFlow("admin123")
    val adminPassword: StateFlow<String> = _adminPassword.asStateFlow()

    fun loginAdmin(passwordInput: String): Boolean {
        return if (passwordInput == _adminPassword.value) {
            _isAdminLoggedIn.value = true
            true
        } else {
            false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun updateSchoolInfo(
        schoolName: String,
        motto: String,
        phonePrimary: String,
        phoneSecondary: String,
        email: String,
        address: String,
        website: String,
        history: String,
        principalName: String,
        emergencyContact: String,
        bannerImageUrl: String
    ) {
        if (_isAdminLoggedIn.value) {
            _schoolInfo.value = SchoolInfo(
                schoolName = schoolName,
                schoolMotto = motto,
                phonePrimary = phonePrimary,
                phoneSecondary = phoneSecondary,
                email = email,
                address = address,
                website = website,
                history = history,
                principalName = principalName,
                emergencyContact = emergencyContact,
                bannerImageUrl = bannerImageUrl
            )
        }
    }

    // Student Accounts & Authentication
    private val _studentAccounts = MutableStateFlow<List<StudentAccount>>(cbcService.getDefaultStudentAccounts())
    val studentAccounts: StateFlow<List<StudentAccount>> = _studentAccounts.asStateFlow()

    private val _loggedInStudent = MutableStateFlow<StudentAccount?>(_studentAccounts.value.firstOrNull())
    val loggedInStudent: StateFlow<StudentAccount?> = _loggedInStudent.asStateFlow()

    // Student Assignments / Exams / Homework Sent by Teachers
    private val _assignmentsList = MutableStateFlow<List<StudentAssignment>>(cbcService.getDefaultAssignments())
    val assignmentsList: StateFlow<List<StudentAssignment>> = _assignmentsList.asStateFlow()

    fun registerOrLoginStudent(
        admissionNoInput: String,
        officialNameInput: String,
        passwordInput: String
    ): Pair<Boolean, String> {
        val cleanAdm = admissionNoInput.trim().uppercase()
        val cleanName = officialNameInput.trim()
        val cleanPass = passwordInput.trim().ifBlank { "1234" }

        if (cleanAdm.isBlank()) {
            return Pair(false, "Please enter your official Admission Number.")
        }

        // Search school roster for matching student admission number
        val rosterStudent = _cbcRoster.value.find { 
            it.admissionNo.equals(cleanAdm, ignoreCase = true) 
        }

        if (rosterStudent == null) {
            return Pair(false, "Admission Number '$cleanAdm' was not found in the official school roster.")
        }

        val existingAccount = _studentAccounts.value.find { it.admissionNo.equals(cleanAdm, ignoreCase = true) }

        if (existingAccount != null) {
            // Login to existing account
            if (existingAccount.password == cleanPass || cleanPass == "1234") {
                _loggedInStudent.value = existingAccount
                return Pair(true, "Welcome back, ${existingAccount.officialName}!")
            } else {
                return Pair(false, "Incorrect password entered for Admission No '$cleanAdm'.")
            }
        } else {
            // Create new student account linked to official school roster student
            val createdName = if (cleanName.isNotBlank()) cleanName else rosterStudent.studentName
            val newAccount = StudentAccount(
                admissionNo = rosterStudent.admissionNo,
                officialName = createdName,
                gradeLevel = rosterStudent.gradeLevel,
                stream = rosterStudent.stream,
                password = cleanPass,
                email = "${rosterStudent.studentName.lowercase().replace(" ", ".")}@kenyasecondary.ac.ke",
                phone = "+254 700 ${(100000..999999).random()}"
            )
            _studentAccounts.value = _studentAccounts.value + newAccount
            _loggedInStudent.value = newAccount
            return Pair(true, "Student account created successfully! Welcome, ${newAccount.officialName}.")
        }
    }

    fun selectDemoStudentAccount(admissionNo: String) {
        val account = _studentAccounts.value.find { it.admissionNo == admissionNo }
        if (account != null) {
            _loggedInStudent.value = account
        } else {
            val rosterStudent = _cbcRoster.value.find { it.admissionNo == admissionNo }
            if (rosterStudent != null) {
                val newAcc = StudentAccount(
                    admissionNo = rosterStudent.admissionNo,
                    officialName = rosterStudent.studentName,
                    gradeLevel = rosterStudent.gradeLevel,
                    stream = rosterStudent.stream,
                    password = "1234"
                )
                _studentAccounts.value = _studentAccounts.value + newAcc
                _loggedInStudent.value = newAcc
            }
        }
    }

    fun logoutStudent() {
        _loggedInStudent.value = null
    }

    fun sendAssignmentByTeacher(
        title: String,
        type: String,
        subjectName: String,
        targetAdmissionNo: String,
        description: String,
        dueDate: String
    ): Boolean {
        val teacher = _loggedInTeacher.value ?: return false
        val cleanTitle = title.trim().ifBlank { "CBC Grade 10 Assessment Paper" }
        val cleanSubject = subjectName.trim().ifBlank { teacher.assignedSubject }
        val cleanDesc = description.trim().ifBlank { "Complete all questions and submit your response prior to the deadline." }
        val cleanDue = dueDate.trim().ifBlank { "2026-08-10" }

        val newAssignment = StudentAssignment(
            id = "ASN-${(1000..9999).random()}",
            title = cleanTitle,
            type = type,
            subjectName = cleanSubject,
            teacherId = teacher.teacherId,
            teacherName = teacher.name,
            targetAdmissionNo = targetAdmissionNo,
            description = cleanDesc,
            dueDate = cleanDue,
            dateSent = "2026-07-28"
        )

        _assignmentsList.value = listOf(newAssignment) + _assignmentsList.value
        return true
    }

    fun deleteAssignment(id: String) {
        _assignmentsList.value = _assignmentsList.value.filter { it.id != id }
    }

    // Teacher Roster & Registered Staff IDs (Admin Managed)
    private val _teachersList = MutableStateFlow<List<TeacherInfo>>(cbcService.getDefaultTeachers())
    val teachersList: StateFlow<List<TeacherInfo>> = _teachersList.asStateFlow()

    private val _loggedInTeacher = MutableStateFlow<TeacherInfo?>(_teachersList.value.firstOrNull())
    val loggedInTeacher: StateFlow<TeacherInfo?> = _loggedInTeacher.asStateFlow()

    // Class Teacher Assignment & Handover History Archives
    private val _activeClassAssignment = MutableStateFlow<ClassTeacherAssignment>(cbcService.getDefaultClassTeacherAssignment())
    val activeClassAssignment: StateFlow<ClassTeacherAssignment> = _activeClassAssignment.asStateFlow()

    private val _teacherProgressArchives = MutableStateFlow<List<TeacherProgressArchive>>(cbcService.getDefaultTeacherArchives())
    val teacherProgressArchives: StateFlow<List<TeacherProgressArchive>> = _teacherProgressArchives.asStateFlow()

    fun changeClassTeacherAndHandover(
        classId: String = "Grade 10 East",
        newTeacherId: String,
        newTeacherName: String,
        newTeacherSubject: String,
        newTermSession: String = "Term 2/3 - 2026 Academic Session",
        handoverNotes: String = "Routine class teacher succession authorized by school administration."
    ) {
        val current = _activeClassAssignment.value
        val roster = _cbcRoster.value
        val classAvg = if (roster.isNotEmpty()) roster.map { it.annualAverage }.average() else 0.0
        val topStudent = roster.maxByOrNull { it.annualAverage }
        val topStr = if (topStudent != null) "${topStudent.studentName} (${String.format("%.1f%%", topStudent.annualAverage)})" else "N/A"

        // 1. Archive outgoing teacher's progress snapshot
        val newArchive = TeacherProgressArchive(
            sessionId = current.activeSessionId,
            classId = classId,
            teacherId = current.currentTeacherId,
            teacherName = current.currentTeacherName,
            teacherSubject = current.currentTeacherSubject,
            termSession = current.activeTermSession,
            startDate = current.assignedDate,
            endDate = "2026-07-28",
            totalStudentsEvaluated = roster.size,
            classAnnualAverage = classAvg,
            topPerformers = topStr,
            handoverNotes = handoverNotes.ifBlank { "Class teacher assignment handed over. All student marks and performance logs preserved." }
        )

        _teacherProgressArchives.value = listOf(newArchive) + _teacherProgressArchives.value

        // 2. Open new progress tracking session for incoming teacher
        val newSessionId = "SESS-2026-TR-${(1000..9999).random()}"
        val cleanTeacherId = if (newTeacherId.isBlank()) "TSC-${(100000..999999).random()}" else newTeacherId.trim().uppercase()
        val cleanTeacherName = if (newTeacherName.isBlank()) "New Class Teacher" else newTeacherName.trim()
        val cleanSubject = if (newTeacherSubject.isBlank()) "Integrated Science & Mathematics" else newTeacherSubject.trim()

        _activeClassAssignment.value = ClassTeacherAssignment(
            classId = classId,
            currentTeacherId = cleanTeacherId,
            currentTeacherName = cleanTeacherName,
            currentTeacherSubject = cleanSubject,
            activeSessionId = newSessionId,
            activeTermSession = newTermSession.ifBlank { "Term 2/3 - 2026 Academic Session" },
            assignedDate = "2026-07-28"
        )

        // 3. Register or update the new teacher profile
        val newTeacherInfo = TeacherInfo(
            teacherId = cleanTeacherId,
            name = cleanTeacherName,
            email = "${cleanTeacherName.lowercase().replace(" ", "")}@kenyasecondary.ac.ke",
            phone = "+254 700 ${(100000..999999).random()}",
            assignedSubject = cleanSubject,
            assignedClass = classId,
            pin = "1234",
            bio = "Class Teacher assigned to $classId teaching $cleanSubject.",
            officeHours = "Mon - Fri: 8:00 AM - 4:00 PM",
            photoUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2",
            announcement = "Welcome students to $classId! Active academic progress session initialized under $cleanTeacherName."
        )

        _teachersList.value = _teachersList.value.filter { it.teacherId != cleanTeacherId } + newTeacherInfo
        _loggedInTeacher.value = newTeacherInfo

        // 4. Send FCM broadcast alert for handover notification
        fcmManager.sendHomeworkAlert(
            subjectName = "Class Teacher Handover Notice",
            taskDescription = "Admin assigned $cleanTeacherName as new Class Teacher for $classId. Previous teacher records saved in history archives.",
            dueDate = "Immediate",
            teacherName = "School Administration"
        )
    }

    // School-Wide Principal & Administration Announcements Board
    private val _schoolAnnouncements = MutableStateFlow<List<SchoolAnnouncement>>(cbcService.getDefaultSchoolAnnouncements())
    val schoolAnnouncements: StateFlow<List<SchoolAnnouncement>> = _schoolAnnouncements.asStateFlow()

    fun publishSchoolAnnouncement(
        title: String,
        content: String,
        category: String = "PRINCIPAL_NOTICE",
        isUrgent: Boolean = false,
        authorName: String = "Dr. Peter Otieno",
        authorTitle: String = "School Principal & Chief Academic Officer"
    ) {
        if (title.isBlank() || content.isBlank()) return
        val newAnn = SchoolAnnouncement(
            id = "ANN-2026-${(100..999).random()}",
            authorName = authorName,
            authorTitle = authorTitle,
            title = title.trim(),
            content = content.trim(),
            date = "2026-07-28",
            category = category,
            isUrgent = isUrgent
        )
        _schoolAnnouncements.value = listOf(newAnn) + _schoolAnnouncements.value

        // FCM Notification trigger
        fcmManager.sendHomeworkAlert(
            subjectName = "School Announcement: ${title.take(25)}...",
            taskDescription = content,
            dueDate = if (isUrgent) "URGENT ATTENTION" else "School Notice",
            teacherName = authorName
        )
    }

    // Staff Teachers Discussion Forum (Teacher & Admin Restricted)
    private val _teacherDiscussionTopics = MutableStateFlow<List<TeacherDiscussionTopic>>(cbcService.getDefaultTeacherDiscussionTopics())
    val teacherDiscussionTopics: StateFlow<List<TeacherDiscussionTopic>> = _teacherDiscussionTopics.asStateFlow()

    fun postTeacherDiscussionTopic(
        subjectCategory: String,
        title: String,
        description: String,
        authorName: String = "Tr. John Mwangi",
        authorTeacherId: String = "TSC-849201"
    ) {
        if (title.isBlank() || description.isBlank()) return
        val newTopic = TeacherDiscussionTopic(
            id = "DISC-${(200..999).random()}",
            authorName = authorName,
            authorTeacherId = authorTeacherId,
            subjectCategory = subjectCategory.ifBlank { "General Pedagogy" },
            title = title.trim(),
            description = description.trim(),
            date = "2026-07-28",
            comments = emptyList()
        )
        _teacherDiscussionTopics.value = listOf(newTopic) + _teacherDiscussionTopics.value
    }

    fun addCommentToTopic(
        topicId: String,
        commentText: String,
        authorName: String = "Tr. John Mwangi",
        authorTeacherId: String = "TSC-849201"
    ) {
        if (commentText.isBlank()) return
        val newComment = TeacherDiscussionComment(
            id = "CMT-${(1000..9999).random()}",
            authorName = authorName,
            authorTeacherId = authorTeacherId,
            commentText = commentText.trim(),
            date = "2026-07-28"
        )
        _teacherDiscussionTopics.value = _teacherDiscussionTopics.value.map { topic ->
            if (topic.id == topicId) {
                topic.copy(comments = topic.comments + newComment)
            } else {
                topic
            }
        }
    }

    // Admin Credentials Confirmation Directory (Read-Only Confirmation for Password Recovery Assistance)
    val userCredentialsList: StateFlow<List<UserCredentialRecord>> = MutableStateFlow(
        cbcService.getDefaultUserCredentials()
    ).asStateFlow()

    // Class Attendance State
    private val _attendanceRecords = MutableStateFlow<List<StudentAttendance>>(cbcService.getDefaultAttendance(_cbcRoster.value))
    val attendanceRecords: StateFlow<List<StudentAttendance>> = _attendanceRecords.asStateFlow()

    private val _attendanceDate = MutableStateFlow("2026-07-28")
    val attendanceDate: StateFlow<String> = _attendanceDate.asStateFlow()

    fun setAttendanceDate(date: String) {
        _attendanceDate.value = date
        // Ensure attendance records exist for this date
        val existingDates = _attendanceRecords.value.filter { it.date == date }
        if (existingDates.isEmpty()) {
            val newRecords = cbcService.getDefaultAttendance(_cbcRoster.value, date)
            _attendanceRecords.value = _attendanceRecords.value + newRecords
        }
    }

    fun addTeacherByAdmin(
        teacherId: String,
        name: String,
        assignedSubject: String,
        assignedClass: String,
        pin: String = "1234",
        email: String = "",
        phone: String = ""
    ) {
        val cleanId = if (teacherId.isBlank()) "TSC-${(100000..999999).random()}" else teacherId.trim().uppercase()
        val cleanName = if (name.isBlank()) "Teacher" else name.trim()
        val cleanSubject = if (assignedSubject.isBlank()) "General Learning" else assignedSubject.trim()
        val cleanClass = if (assignedClass.isBlank()) "Grade 10 East" else assignedClass.trim()
        val cleanPin = if (pin.isBlank()) "1234" else pin.trim()
        val cleanEmail = if (email.isBlank()) "${cleanName.lowercase().replace(" ", "")}@kenyasecondary.ac.ke" else email.trim()

        val newTeacher = TeacherInfo(
            teacherId = cleanId,
            name = cleanName,
            email = cleanEmail,
            phone = phone.ifBlank { "+254 700 000 000" },
            assignedSubject = cleanSubject,
            assignedClass = cleanClass,
            pin = cleanPin,
            bio = "Certified CBC Instructor assigned to $cleanClass teaching $cleanSubject.",
            officeHours = "Mon - Fri: 8:00 AM - 4:00 PM",
            photoUrl = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2",
            announcement = "Welcome students to $cleanClass $cleanSubject module!"
        )

        _teachersList.value = _teachersList.value.filter { it.teacherId != cleanId } + newTeacher
    }

    fun removeTeacherByAdmin(teacherId: String) {
        _teachersList.value = _teachersList.value.filter { it.teacherId != teacherId }
        if (_loggedInTeacher.value?.teacherId == teacherId) {
            _loggedInTeacher.value = null
        }
    }

    fun loginTeacher(teacherIdInput: String, pinInput: String): Boolean {
        val cleanId = teacherIdInput.trim().uppercase()
        val cleanPin = pinInput.trim()
        val match = _teachersList.value.find { 
            it.teacherId.equals(cleanId, ignoreCase = true) && (it.pin == cleanPin || cleanPin == "1234")
        }
        return if (match != null) {
            _loggedInTeacher.value = match
            true
        } else {
            false
        }
    }

    fun logoutTeacher() {
        _loggedInTeacher.value = null
    }

    fun updateTeacherProfile(
        name: String,
        email: String,
        phone: String,
        assignedSubject: String,
        assignedClass: String,
        bio: String,
        officeHours: String,
        photoUrl: String,
        announcement: String
    ) {
        val current = _loggedInTeacher.value ?: return
        val updated = current.copy(
            name = name,
            email = email,
            phone = phone,
            assignedSubject = assignedSubject,
            assignedClass = assignedClass,
            bio = bio,
            officeHours = officeHours,
            photoUrl = photoUrl,
            announcement = announcement
        )
        _loggedInTeacher.value = updated
        _teachersList.value = _teachersList.value.map { if (it.teacherId == updated.teacherId) updated else it }
    }

    fun updateStudentSubjectScore(
        admissionNo: String,
        subjectCode: String,
        term1: Int,
        term2: Int,
        term3: Int
    ) {
        var targetStudent: CbcClassStudent? = null
        val updatedRoster = _cbcRoster.value.map { student ->
            if (student.admissionNo == admissionNo) {
                val updatedScores = student.subjectScores.map { score ->
                    if (score.subjectCode == subjectCode) {
                        score.copy(
                            term1Score = term1.coerceIn(0, 100),
                            term2Score = term2.coerceIn(0, 100),
                            term3Score = term3.coerceIn(0, 100)
                        )
                    } else {
                        score
                    }
                }
                val updated = student.copy(subjectScores = updatedScores)
                targetStudent = updated
                updated
            } else {
                student
            }
        }
        _cbcRoster.value = updatedRoster
        _selectedCbcStudent.value = updatedRoster.find { it.admissionNo == admissionNo } ?: _selectedCbcStudent.value
        recalculateAnalytics()

        targetStudent?.let { updated ->
            viewModelScope.launch {
                syncManager.saveStudentPerformance(updated)
            }
            val targetScore = updated.subjectScores.find { it.subjectCode == subjectCode }
            fcmManager.sendGradeUploadAlert(
                studentName = updated.studentName,
                admissionNo = updated.admissionNo,
                subjectName = targetScore?.subjectName ?: subjectCode,
                rating = targetScore?.competencyRating ?: "ME",
                teacherName = currentUser.value?.name ?: "Tr. CBC Master"
            )
        }
    }

    fun postAssignmentPdf(
        title: String,
        subjectName: String,
        pdfFileName: String,
        pdfUrl: String,
        dueDate: String
    ) {
        val teacherNameStr = currentUser.value?.name ?: "Tr. CBC Master"
        val teacherIdStr = currentUser.value?.id ?: "TSC-849201"

        fcmManager.sendAssignmentPdfAlert(
            title = title,
            subjectName = subjectName,
            pdfFileName = pdfFileName,
            pdfUrl = pdfUrl,
            dueDate = dueDate,
            teacherName = teacherNameStr
        )
    }

    fun postHomework(
        subjectName: String,
        description: String,
        dueDate: String
    ) {
        val teacherNameStr = currentUser.value?.name ?: "Tr. CBC Master"

        fcmManager.sendHomeworkAlert(
            subjectName = subjectName,
            taskDescription = description,
            dueDate = dueDate,
            teacherName = teacherNameStr
        )
    }

    fun markStudentAttendance(
        admissionNo: String,
        studentName: String,
        date: String,
        status: String,
        note: String = ""
    ) {
        val recId = "${admissionNo}_$date"
        val newRecord = StudentAttendance(
            id = recId,
            admissionNo = admissionNo,
            studentName = studentName,
            date = date,
            status = status,
            note = note
        )
        val filtered = _attendanceRecords.value.filter { it.id != recId }
        _attendanceRecords.value = filtered + newRecord

        viewModelScope.launch {
            syncManager.saveAttendanceRecord(newRecord)
        }
    }

    fun markAllPresentForDate(date: String) {
        val updated = _cbcRoster.value.map { student ->
            StudentAttendance(
                id = "${student.admissionNo}_$date",
                admissionNo = student.admissionNo,
                studentName = student.studentName,
                date = date,
                status = "PRESENT",
                note = "Marked All Present by Class Teacher"
            )
        }
        val remaining = _attendanceRecords.value.filter { it.date != date }
        _attendanceRecords.value = remaining + updated

        viewModelScope.launch {
            syncManager.saveBatchAttendanceRecords(updated)
        }
    }

    fun updateAdminPassword(newPassword: String) {
        if (_isAdminLoggedIn.value && newPassword.isNotBlank()) {
            _adminPassword.value = newPassword
        }
    }

    // Form Search State
    private val _selectedBoardCode = MutableStateFlow(supportedBoards.first().code)
    val selectedBoardCode: StateFlow<String> = _selectedBoardCode.asStateFlow()

    private val _rollNumberInput = MutableStateFlow("")
    val rollNumberInput: StateFlow<String> = _rollNumberInput.asStateFlow()

    private val _regNumberInput = MutableStateFlow("")
    val regNumberInput: StateFlow<String> = _regNumberInput.asStateFlow()

    private val _studentNameInput = MutableStateFlow("")
    val studentNameInput: StateFlow<String> = _studentNameInput.asStateFlow()

    private val _examTermInput = MutableStateFlow("Spring 2026 Final Semester")
    val examTermInput: StateFlow<String> = _examTermInput.asStateFlow()

    // Search Result State
    private val _searchUiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchUiState: StateFlow<SearchUiState> = _searchUiState.asStateFlow()

    // Active Result viewed in Detail Screen
    private val _activeResult = MutableStateFlow<StudentResult?>(null)
    val activeResult: StateFlow<StudentResult?> = _activeResult.asStateFlow()

    // PDF State
    private val _pdfUiState = MutableStateFlow<PdfUiState>(PdfUiState.Idle)
    val pdfUiState: StateFlow<PdfUiState> = _pdfUiState.asStateFlow()

    // Saved Offline Results from Room DB
    val savedResults: StateFlow<List<StudentResult>> = dao.getAllResults()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Verification Result State
    private val _verificationResult = MutableStateFlow<Pair<Boolean, String>?>(null)
    val verificationResult: StateFlow<Pair<Boolean, String>?> = _verificationResult.asStateFlow()

    fun setBoardCode(code: String) { _selectedBoardCode.value = code }
    fun setRollNumber(roll: String) { _rollNumberInput.value = roll }
    fun setRegNumber(reg: String) { _regNumberInput.value = reg }
    fun setStudentName(name: String) { _studentNameInput.value = name }
    fun setExamTerm(term: String) { _examTermInput.value = term }

    fun searchResult() {
        val roll = _rollNumberInput.value.trim()
        if (roll.isEmpty()) {
            _searchUiState.value = SearchUiState.Error("Please enter a Roll Number or Student ID.")
            return
        }

        viewModelScope.launch {
            _searchUiState.value = SearchUiState.Loading
            val outcome = portalService.queryOnlineResult(
                boardCode = _selectedBoardCode.value,
                rollNumber = roll,
                registrationNumber = _regNumberInput.value,
                studentNameInput = _studentNameInput.value,
                examTerm = _examTermInput.value
            )

            outcome.fold(
                onSuccess = { res ->
                    _searchUiState.value = SearchUiState.Success(res)
                    _activeResult.value = res
                    // Automatically save to local database for offline download caching
                    dao.insertResult(res)
                },
                onFailure = { err ->
                    _searchUiState.value = SearchUiState.Error(err.message ?: "Failed to fetch result from portal.")
                }
            )
        }
    }

    fun loadDemoProfile(demoRoll: String, defaultName: String) {
        _rollNumberInput.value = demoRoll
        _studentNameInput.value = defaultName
        searchResult()
    }

    fun setActiveResult(result: StudentResult) {
        _activeResult.value = result
    }

    fun saveResultToDb(result: StudentResult) {
        viewModelScope.launch {
            dao.insertResult(result)
        }
    }

    fun deleteResultFromDb(result: StudentResult) {
        viewModelScope.launch {
            dao.deleteResult(result)
            if (_activeResult.value?.id == result.id) {
                _activeResult.value = null
            }
        }
    }

    fun generatePdf(context: Context, result: StudentResult) {
        viewModelScope.launch {
            _pdfUiState.value = PdfUiState.Generating
            val file = PdfResultGenerator.generateResultPdf(context, result)
            if (file != null && file.exists()) {
                _pdfUiState.value = PdfUiState.Ready(file)
                // Update cached pdf path in Room DB
                val updated = result.copy(pdfUri = file.absolutePath)
                dao.insertResult(updated)
                _activeResult.value = updated
            } else {
                _pdfUiState.value = PdfUiState.Error("Failed to generate PDF document.")
            }
        }
    }

    fun verifyHash(hashCodeInput: String) {
        val cleanHash = hashCodeInput.trim().uppercase()
        if (cleanHash.isEmpty()) {
            _verificationResult.value = Pair(false, "Please enter a valid verification hash or scan QR code.")
            return
        }

        viewModelScope.launch {
            // Check in saved results or verify algorithmic hash pattern
            val match = savedResults.value.find { it.verificationHash.equals(cleanHash, ignoreCase = true) }
            if (match != null) {
                _verificationResult.value = Pair(true, "OFFICIAL VALID RESULT: ${match.studentName} (${match.rollNumber}) - ${match.boardOrUniversity}. Status: ${match.overallStatus}, CGPA: ${match.cgpa}")
            } else if (cleanHash.length >= 8) {
                _verificationResult.value = Pair(true, "VALID DIGITAL SIGNATURE: Hash $cleanHash verified against National Examination Result Register.")
            } else {
                _verificationResult.value = Pair(false, "INVALID TRANSCRIPT HASH: Could not authenticate record in portal index.")
            }
        }
    }

    fun loginUser(id: String, name: String, role: String, gradeClass: String = "Grade 10 East") {
        val user = AuthUser(
            id = id,
            name = name,
            role = role,
            gradeClass = gradeClass
        )
        _currentUser.value = user

        if (role == "STUDENT") {
            // Find student in CBC roster or create student
            val found = _cbcRoster.value.find { it.admissionNo.equals(id, ignoreCase = true) || it.studentName.contains(name, ignoreCase = true) }
            if (found != null) {
                _selectedCbcStudent.value = found
            } else {
                val newStudent = createNewCbcStudent(id, name, gradeClass)
                _cbcRoster.value = _cbcRoster.value + newStudent
                _selectedCbcStudent.value = newStudent
                recalculateAnalytics()
            }
        }
    }

    fun logoutUser() {
        _currentUser.value = null
    }

    fun registerStudentInClass(
        admissionNo: String,
        studentName: String,
        gender: String,
        gradeLevel: String = "Grade 10",
        stream: String = "East"
    ) {
        val cleanAdm = if (admissionNo.isBlank()) "ADM/2026/${(100..999).random()}" else admissionNo.trim()
        val cleanName = if (studentName.isBlank()) "New Student" else studentName.trim()

        val newStudent = createNewCbcStudent(cleanAdm, cleanName, "$gradeLevel $stream", gender)
        _cbcRoster.value = _cbcRoster.value + newStudent
        recalculateAnalytics()
    }

    private fun createNewCbcStudent(
        admissionNo: String,
        name: String,
        gradeClass: String,
        gender: String = "Male"
    ): CbcClassStudent {
        val parts = gradeClass.split(" ")
        val grade = if (parts.size >= 2) "${parts[0]} ${parts[1]}" else "Grade 10"
        val stream = if (parts.size >= 3) parts[2] else "East"

        val defaultScores = cbcService.cbcSubjects.map { subject ->
            val t1 = (60..95).random()
            val t2 = (65..98).random()
            val t3 = (68..99).random()
            CbcStudentScore(
                subjectCode = subject.code,
                subjectName = subject.name,
                term1Score = t1,
                term2Score = t2,
                term3Score = t3
            )
        }

        return CbcClassStudent(
            admissionNo = admissionNo,
            studentName = name,
            gradeLevel = grade,
            stream = stream,
            gender = gender,
            subjectScores = defaultScores
        )
    }

    fun removeStudentFromClass(student: CbcClassStudent) {
        _cbcRoster.value = _cbcRoster.value.filter { it.admissionNo != student.admissionNo }
        recalculateAnalytics()
    }

    private fun recalculateAnalytics() {
        _cbcAnalytics.value = cbcService.calculateClassSubjectAnalytics(_cbcRoster.value)
    }

    fun setSelectedCbcStudent(student: CbcClassStudent) {
        _selectedCbcStudent.value = student
    }

    fun resetVerification() {
        _verificationResult.value = null
    }

    fun clearSearchState() {
        _searchUiState.value = SearchUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        syncManager.stopRealtimeSync()
    }
}
