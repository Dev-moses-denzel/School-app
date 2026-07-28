package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.remote.CbcClassStudent
import com.example.data.remote.StudentAssignment
import com.example.data.remote.StudentAttendance
import com.example.data.remote.TeacherInfo
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy900
import com.example.ui.theme.OrangeContainer
import com.example.ui.theme.OrangeWarning
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.viewmodel.ResultViewModel
import com.example.util.PdfResultGenerator

@Composable
fun SchoolMenuScreen(
    viewModel: ResultViewModel
) {
    val context = LocalContext.current
    val schoolInfo by viewModel.schoolInfo.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val teachersList by viewModel.teachersList.collectAsState()
    val loggedInTeacher by viewModel.loggedInTeacher.collectAsState()
    val cbcRoster by viewModel.cbcRoster.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val attendanceDate by viewModel.attendanceDate.collectAsState()

    // Student State
    val studentAccounts by viewModel.studentAccounts.collectAsState()
    val loggedInStudent by viewModel.loggedInStudent.collectAsState()
    val assignmentsList by viewModel.assignmentsList.collectAsState()

    // New State Collections
    val schoolAnnouncements by viewModel.schoolAnnouncements.collectAsState()
    val teacherDiscussionTopics by viewModel.teacherDiscussionTopics.collectAsState()
    val userCredentialsList by viewModel.userCredentialsList.collectAsState()

    // Top Level Tabs: 0 = Student Portal, 1 = Teacher Portal, 2 = School Info & Principal Announcements, 3 = Admin Section
    var activeTab by remember { mutableStateOf(0) }

    // Principal Announcement Modal Dialog State
    var showPostAnnouncementDialog by remember { mutableStateOf(false) }
    var annTitleInput by remember { mutableStateOf("") }
    var annContentInput by remember { mutableStateOf("") }
    var annCategoryInput by remember { mutableStateOf("PRINCIPAL_NOTICE") }
    var annIsUrgentInput by remember { mutableStateOf(false) }

    // Staff Teacher Discussion Forum States
    var showNewTopicDialog by remember { mutableStateOf(false) }
    var topicCategoryInput by remember { mutableStateOf("Integrated Science") }
    var topicTitleInput by remember { mutableStateOf("") }
    var topicDescInput by remember { mutableStateOf("") }
    var activeTopicForCommentId by remember { mutableStateOf<String?>(null) }
    var commentTextInput by remember { mutableStateOf("") }

    // Admin Credentials Directory Search
    var credentialSearchQuery by remember { mutableStateOf("") }
    var credentialRoleFilter by remember { mutableStateOf("ALL") } // ALL, STUDENT, TEACHER, ADMIN

    // Student Login / Creation Inputs
    var studentAdmissionInput by remember { mutableStateOf("") }
    var studentNameInput by remember { mutableStateOf("") }
    var studentPassInput by remember { mutableStateOf("") }
    var studentLoginErrorMsg by remember { mutableStateOf<String?>(null) }

    // Teacher Login Inputs
    var teacherIdLoginInput by remember { mutableStateOf("") }
    var teacherPinLoginInput by remember { mutableStateOf("") }
    var teacherLoginError by remember { mutableStateOf<String?>(null) }

    // Teacher Sub-Tabs: 0 = Profile Page, 1 = Marks Editor, 2 = Attendance Marker, 3 = Send Assignment/Exam PDF
    var teacherSubTab by remember { mutableStateOf(0) }

    // Teacher Profile Edit Fields
    var editTeacherName by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.name ?: "") }
    var editTeacherEmail by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.email ?: "") }
    var editTeacherPhone by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.phone ?: "") }
    var editTeacherBio by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.bio ?: "") }
    var editTeacherOfficeHours by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.officeHours ?: "") }
    var editTeacherPhotoUrl by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.photoUrl ?: "") }
    var editTeacherAnnouncement by remember(loggedInTeacher) { mutableStateOf(loggedInTeacher?.announcement ?: "") }

    // Student Marks Editing State
    var selectedStudentForScores by remember(cbcRoster) { mutableStateOf(cbcRoster.firstOrNull()) }

    // Teacher Send Assignment/Exam PDF State
    var assignTitleInput by remember { mutableStateOf("") }
    var assignTypeInput by remember { mutableStateOf("ASSIGNMENT") } // EXAM, HOMEWORK, ASSIGNMENT, REPORT
    var assignSubjectInput by remember { mutableStateOf("") }
    var assignTargetStudentInput by remember { mutableStateOf("ALL_STUDENTS") }
    var assignDescInput by remember { mutableStateOf("") }
    var assignDueDateInput by remember { mutableStateOf("2026-08-05") }

    // Admin Password Login State
    var adminPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var adminLoginErrorMsg by remember { mutableStateOf<String?>(null) }

    // Admin Form Fields
    var editSchoolName by remember(schoolInfo) { mutableStateOf(schoolInfo.schoolName) }
    var editMotto by remember(schoolInfo) { mutableStateOf(schoolInfo.schoolMotto) }
    var editPhone1 by remember(schoolInfo) { mutableStateOf(schoolInfo.phonePrimary) }
    var editPhone2 by remember(schoolInfo) { mutableStateOf(schoolInfo.phoneSecondary) }
    var editEmail by remember(schoolInfo) { mutableStateOf(schoolInfo.email) }
    var editAddress by remember(schoolInfo) { mutableStateOf(schoolInfo.address) }
    var editWebsite by remember(schoolInfo) { mutableStateOf(schoolInfo.website) }
    var editHistory by remember(schoolInfo) { mutableStateOf(schoolInfo.history) }
    var editPrincipal by remember(schoolInfo) { mutableStateOf(schoolInfo.principalName) }
    var editEmergency by remember(schoolInfo) { mutableStateOf(schoolInfo.emergencyContact) }
    var editBannerUrl by remember(schoolInfo) { mutableStateOf(schoolInfo.bannerImageUrl) }

    // Admin Add Teacher Form Fields
    var newTeacherId by remember { mutableStateOf("") }
    var newTeacherName by remember { mutableStateOf("") }
    var newTeacherSubject by remember { mutableStateOf("") }
    var newTeacherClass by remember { mutableStateOf("") }
    var newTeacherPin by remember { mutableStateOf("1234") }

    // Admin Change Password
    var newAdminPasswordInput by remember { mutableStateOf("") }
    var showChangePasswordSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. SCHOOL HERO BANNER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (schoolInfo.bannerImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(schoolInfo.bannerImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "School Banner Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            error = painterResource(id = R.drawable.university_banner_1785236368209)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.university_banner_1785236368209),
                            contentDescription = "School Banner Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            alpha = 0.4f
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GoldAccent,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Navy900,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = schoolInfo.schoolName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "\"${schoolInfo.schoolMotto}\"",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GoldAccent
                        )
                    }
                }
            }
        }

        // 2. TOP LEVEL TAB BAR
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                TabRow(
                    selectedTabIndex = activeTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        if (activeTab < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                                height = 3.dp,
                                color = BluePrimary
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        modifier = Modifier.testTag("tab_student_portal")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = if (activeTab == 0) BluePrimary else SlateTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Student Portal",
                                fontSize = 11.sp,
                                fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 0) BluePrimary else SlateTextMuted
                            )
                        }
                    }

                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        modifier = Modifier.testTag("tab_teacher_portal")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = if (activeTab == 1) BluePrimary else SlateTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (loggedInTeacher != null) "Teacher Page" else "Teacher Portal",
                                fontSize = 11.sp,
                                fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 1) BluePrimary else SlateTextMuted
                            )
                        }
                    }

                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        modifier = Modifier.testTag("tab_school_info")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = if (activeTab == 2) BluePrimary else SlateTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "School Info",
                                fontSize = 11.sp,
                                fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 2) BluePrimary else SlateTextMuted
                            )
                        }
                    }

                    Tab(
                        selected = activeTab == 3,
                        onClick = { activeTab = 3 },
                        modifier = Modifier.testTag("tab_admin_portal")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (activeTab == 3) BluePrimary else SlateTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAdminLoggedIn) "Admin (Active)" else "Admin Section",
                                fontSize = 11.sp,
                                fontWeight = if (activeTab == 3) FontWeight.Bold else FontWeight.Medium,
                                color = if (activeTab == 3) BluePrimary else SlateTextMuted
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 0: STUDENT PORTAL & LIVE ACCOUNTS
        // ==========================================
        if (activeTab == 0) {
            if (loggedInStudent == null) {
                // STUDENT ACCOUNT CREATION & LOGIN FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = BlueContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Student Account Portal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    Text("Register your student account or log in using your official school Admission Number & Full Name.", fontSize = 11.5.sp, color = SlateTextMuted)
                                }
                            }

                            Divider(color = SlateBorder)

                            OutlinedTextField(
                                value = studentAdmissionInput,
                                onValueChange = {
                                    studentAdmissionInput = it
                                    studentLoginErrorMsg = null
                                },
                                label = { Text("Official School Admission No. *") },
                                placeholder = { Text("e.g. ADM/2026/001") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_admission_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = studentNameInput,
                                onValueChange = {
                                    studentNameInput = it
                                    studentLoginErrorMsg = null
                                },
                                label = { Text("Official Registered Student Name *") },
                                placeholder = { Text("e.g. Kiprono Brian Koech") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_name_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = studentPassInput,
                                onValueChange = {
                                    studentPassInput = it
                                    studentLoginErrorMsg = null
                                },
                                label = { Text("Personal Secret Password / PIN *") },
                                placeholder = { Text("e.g. 1234") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("student_password_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            if (studentLoginErrorMsg != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CrimsonContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = studentLoginErrorMsg!!,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonFail,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val (success, msg) = viewModel.registerOrLoginStudent(
                                        studentAdmissionInput,
                                        studentNameInput,
                                        studentPassInput
                                    )
                                    if (success) {
                                        studentLoginErrorMsg = null
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    } else {
                                        studentLoginErrorMsg = msg
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("student_login_register_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.School, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("CREATE ACCOUNT / LOGIN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Quick Demo - Login as Registered Student:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)

                            cbcRoster.take(4).forEach { student ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BlueContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectDemoStudentAccount(student.admissionNo)
                                            Toast.makeText(context, "Logged in as ${student.studentName}", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(student.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                            Text("Admission: ${student.admissionNo} | ${student.gradeLevel} ${student.stream}", fontSize = 11.sp, color = SlateTextMuted)
                                        }
                                        Text("ACCESS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val student = loggedInStudent!!
                val studentRosterMatch = cbcRoster.find { it.admissionNo == student.admissionNo } ?: cbcRoster.first()
                val studentAttendance = attendanceRecords.filter { it.admissionNo == student.admissionNo }
                val studentAssignments = assignmentsList.filter {
                    it.targetAdmissionNo == "ALL_STUDENTS" || it.targetAdmissionNo == student.admissionNo
                }

                // 1. STUDENT LOGGED IN CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy900)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldAccent,
                                    modifier = Modifier.size(50.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Navy900,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.officialName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Admission No: ${student.admissionNo}", fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.SemiBold)
                                    Text("Class: ${student.gradeLevel} ${student.stream}", fontSize = 11.5.sp, color = Color.White.copy(alpha = 0.8f))
                                }

                                Button(
                                    onClick = { viewModel.logoutStudent() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonFail),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("LOGOUT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 2. LIVE ATTENDANCE REFLECTION CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EventAvailable, contentDescription = null, tint = BluePrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Live Attendance Status", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                }

                                val totalDays = studentAttendance.size
                                val presentDays = studentAttendance.count { it.status == "PRESENT" }
                                val attPct = if (totalDays > 0) (presentDays.toDouble() / totalDays * 100).toInt() else 100

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (attPct >= 80) EmeraldContainer else OrangeContainer
                                ) {
                                    Text(
                                        text = "$attPct% Present",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (attPct >= 80) EmeraldPass else OrangeWarning,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Divider(color = SlateBorder)

                            val todayRecord = studentAttendance.find { it.date == attendanceDate } ?: studentAttendance.lastOrNull()

                            if (todayRecord != null) {
                                val statusColor = when (todayRecord.status) {
                                    "PRESENT" -> EmeraldPass
                                    "ABSENT" -> CrimsonFail
                                    "LATE" -> OrangeWarning
                                    else -> BluePrimary
                                }
                                val statusBg = when (todayRecord.status) {
                                    "PRESENT" -> EmeraldContainer
                                    "ABSENT" -> CrimsonContainer
                                    "LATE" -> OrangeContainer
                                    else -> BlueContainer
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = statusBg,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (todayRecord.status) {
                                                "PRESENT" -> Icons.Default.CheckCircle
                                                "ABSENT" -> Icons.Default.Cancel
                                                else -> Icons.Default.Schedule
                                            },
                                            contentDescription = null,
                                            tint = statusColor,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Attendance for Date: ${todayRecord.date}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Navy900
                                            )
                                            Text(
                                                text = "STATUS: ${todayRecord.status}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = statusColor
                                            )
                                            if (todayRecord.note.isNotBlank()) {
                                                Text(
                                                    text = "Teacher Remark: ${todayRecord.note}",
                                                    fontSize = 11.sp,
                                                    color = SlateTextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Text("Attendance History Log:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)

                            studentAttendance.takeLast(5).reversed().forEach { rec ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SlateSurface, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(rec.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    }

                                    Text(
                                        text = rec.status,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = when (rec.status) {
                                            "PRESENT" -> EmeraldPass
                                            "ABSENT" -> CrimsonFail
                                            else -> OrangeWarning
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. EXAMS, HOMEWORK & ASSIGNMENTS (PDF INBOX) CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Article, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Teacher Sent Papers & Assignments (${studentAssignments.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            }

                            Text("Exams, homework, and reports sent by your subject teachers to your student account arrive here as downloadable PDFs.", fontSize = 11.5.sp, color = SlateTextMuted)

                            Divider(color = SlateBorder)

                            if (studentAssignments.isEmpty()) {
                                Text("No pending assignments or exams sent to your account.", fontSize = 12.sp, color = SlateTextMuted)
                            } else {
                                studentAssignments.forEach { asn ->
                                    val (typeBg, typeColor) = when (asn.type) {
                                        "EXAM" -> Pair(CrimsonContainer, CrimsonFail)
                                        "HOMEWORK" -> Pair(OrangeContainer, OrangeWarning)
                                        "REPORT" -> Pair(EmeraldContainer, EmeraldPass)
                                        else -> Pair(BlueContainer, BluePrimary)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = SlateSurface,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = typeBg
                                                ) {
                                                    Text(
                                                        text = asn.type,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = typeColor,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                    )
                                                }

                                                Text(
                                                    text = "Due: ${asn.dueDate}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = CrimsonFail
                                                )
                                            }

                                            Text(asn.title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                            Text("Subject: ${asn.subjectName} | Teacher: ${asn.teacherName}", fontSize = 11.sp, color = SlateTextMuted)
                                            Text(asn.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 3)

                                            Button(
                                                onClick = {
                                                    val pdfFile = PdfResultGenerator.generateAssignmentPdf(
                                                        context = context,
                                                        assignment = asn,
                                                        schoolName = schoolInfo.schoolName,
                                                        schoolMotto = schoolInfo.schoolMotto
                                                    )
                                                    if (pdfFile != null && pdfFile.exists()) {
                                                        try {
                                                            val uri = FileProvider.getUriForFile(
                                                                context,
                                                                "${context.packageName}.provider",
                                                                pdfFile
                                                            )
                                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                                setDataAndType(uri, "application/pdf")
                                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                            }
                                                            context.startActivity(Intent.createChooser(intent, "Open PDF Assignment"))
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "PDF saved to Downloads folder!", Toast.LENGTH_LONG).show()
                                                        }
                                                    } else {
                                                        Toast.makeText(context, "Error generating assignment PDF.", Toast.LENGTH_SHORT).show()
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(42.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("DOWNLOAD / VIEW PDF PAPER", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. INDIVIDUAL CBC ACADEMIC TRANSCRIPT & REPORT PDF CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Grade, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("My CBC Academic Performance & Results", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            }

                            Divider(color = SlateBorder)

                            studentRosterMatch.subjectScores.forEach { score ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SlateSurface, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(score.subjectName, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                        Text("Term 1: ${score.term1Score}% | Term 2: ${score.term2Score}% | Term 3: ${score.term3Score}%", fontSize = 10.5.sp, color = SlateTextMuted)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (score.competencyRating == "EE" || score.competencyRating == "ME") EmeraldContainer else OrangeContainer
                                    ) {
                                        Text(
                                            text = score.competencyRating,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (score.competencyRating == "EE" || score.competencyRating == "ME") EmeraldPass else OrangeWarning,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    val pdfFile = PdfResultGenerator.generateCbcStudentReportPdf(
                                        context = context,
                                        student = studentRosterMatch,
                                        schoolName = schoolInfo.schoolName,
                                        schoolMotto = schoolInfo.schoolMotto,
                                        attendanceRecords = studentAttendance
                                    )
                                    if (pdfFile != null && pdfFile.exists()) {
                                        try {
                                            val uri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.provider",
                                                pdfFile
                                            )
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, "application/pdf")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            context.startActivity(Intent.createChooser(intent, "Open CBC Transcript PDF"))
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "CBC Transcript PDF generated and saved!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("DOWNLOAD MY OFFICIAL CBC REPORT CARD (PDF)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 1: TEACHER PORTAL & ACTIONS
        // ==========================================
        if (activeTab == 1) {
            if (loggedInTeacher == null) {
                // TEACHER LOGIN FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = BlueContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Teacher Staff Portal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    Text("Enter your registered Teacher ID (TSC No.) & PIN to mark attendance, edit scores, send assignments/exams PDF to student accounts.", fontSize = 11.5.sp, color = SlateTextMuted)
                                }
                            }

                            Divider(color = SlateBorder)

                            OutlinedTextField(
                                value = teacherIdLoginInput,
                                onValueChange = {
                                    teacherIdLoginInput = it
                                    teacherLoginError = null
                                },
                                label = { Text("Teacher Staff ID / TSC Number *") },
                                placeholder = { Text("e.g. TSC-849201") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("teacher_id_login_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = teacherPinLoginInput,
                                onValueChange = {
                                    teacherPinLoginInput = it
                                    teacherLoginError = null
                                },
                                label = { Text("Teacher PIN / Password *") },
                                placeholder = { Text("e.g. 1234") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary) },
                                visualTransformation = PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("teacher_pin_login_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            if (teacherLoginError != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CrimsonContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = teacherLoginError!!,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonFail,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val success = viewModel.loginTeacher(teacherIdLoginInput, teacherPinLoginInput)
                                    if (success) {
                                        teacherLoginError = null
                                        Toast.makeText(context, "Welcome Teacher!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        teacherLoginError = "Invalid Teacher ID or PIN. Check credentials or ask Admin."
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("teacher_login_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("LOGIN TO TEACHER PORTAL", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Quick Demo - Select Registered Teacher ID:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)

                            teachersList.forEach { teacher ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BlueContainer,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.loginTeacher(teacher.teacherId, teacher.pin)
                                            Toast.makeText(context, "Logged in as ${teacher.name}", Toast.LENGTH_SHORT).show()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(teacher.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                            Text("TSC ID: ${teacher.teacherId} | ${teacher.assignedClass} (${teacher.assignedSubject})", fontSize = 11.sp, color = SlateTextMuted)
                                        }
                                        Text("LOGIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val teacher = loggedInTeacher!!

                // TEACHER HEADER CARD
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy900)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = GoldAccent,
                                modifier = Modifier.size(54.dp)
                            ) {
                                if (teacher.photoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = teacher.photoUrl,
                                        contentDescription = teacher.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.clip(CircleShape)
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Navy900, modifier = Modifier.padding(12.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(teacher.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("TSC ID: ${teacher.teacherId} | ${teacher.assignedClass}", fontSize = 11.5.sp, color = GoldAccent)
                                Text("Subject: ${teacher.assignedSubject}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            }

                            Button(
                                onClick = { viewModel.logoutTeacher() },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonFail),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LOGOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // TEACHER SUB TABS (0 = Profile, 1 = Edit Marks, 2 = Attendance, 3 = Send Paper PDF)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (teacherSubTab == 0) BluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { teacherSubTab = 0 }
                            ) {
                                Text(
                                    "Profile",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (teacherSubTab == 0) Color.White else SlateTextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (teacherSubTab == 1) BluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { teacherSubTab = 1 }
                            ) {
                                Text(
                                    "Marks",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (teacherSubTab == 1) Color.White else SlateTextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (teacherSubTab == 2) BluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { teacherSubTab = 2 }
                            ) {
                                Text(
                                    "Attendance",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (teacherSubTab == 2) Color.White else SlateTextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (teacherSubTab == 3) BluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { teacherSubTab = 3 }
                            ) {
                                Text(
                                    "Send PDF",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (teacherSubTab == 3) Color.White else SlateTextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (teacherSubTab == 4) BluePrimary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { teacherSubTab = 4 }
                            ) {
                                Text(
                                    "Staff Forum",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (teacherSubTab == 4) Color.White else SlateTextMuted,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
                                )
                            }
                        }
                    }
                }

                // SUB-TAB 0: TEACHER PROFILE EDIT
                if (teacherSubTab == 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Customize Public Teacher Profile", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)

                                OutlinedTextField(
                                    value = editTeacherName,
                                    onValueChange = { editTeacherName = it },
                                    label = { Text("Teacher Name") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = editTeacherEmail,
                                    onValueChange = { editTeacherEmail = it },
                                    label = { Text("Teacher Official Email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = editTeacherPhone,
                                    onValueChange = { editTeacherPhone = it },
                                    label = { Text("Teacher Phone") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = editTeacherBio,
                                    onValueChange = { editTeacherBio = it },
                                    label = { Text("Teacher Bio") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    minLines = 2
                                )

                                OutlinedTextField(
                                    value = editTeacherAnnouncement,
                                    onValueChange = { editTeacherAnnouncement = it },
                                    label = { Text("Broadcast Class Announcement") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    minLines = 2
                                )

                                Button(
                                    onClick = {
                                        viewModel.updateTeacherProfile(
                                            name = editTeacherName,
                                            email = editTeacherEmail,
                                            phone = editTeacherPhone,
                                            assignedSubject = teacher.assignedSubject,
                                            assignedClass = teacher.assignedClass,
                                            bio = editTeacherBio,
                                            officeHours = editTeacherOfficeHours,
                                            photoUrl = editTeacherPhotoUrl,
                                            announcement = editTeacherAnnouncement
                                        )
                                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("SAVE PROFILE CHANGES", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // SUB-TAB 1: EDIT MARKS
                if (teacherSubTab == 1) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Select Student to Edit Marks:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)

                                cbcRoster.forEach { student ->
                                    val isSel = selectedStudentForScores?.admissionNo == student.admissionNo
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSel) BlueContainer else SlateSurface,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedStudentForScores = student }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                tint = if (isSel) BluePrimary else SlateTextMuted
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(student.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                                Text("Admission No: ${student.admissionNo}", fontSize = 11.sp, color = SlateTextMuted)
                                            }
                                            if (isSel) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = BluePrimary)
                                            }
                                        }
                                    }
                                }

                                if (selectedStudentForScores != null) {
                                    Divider(color = SlateBorder)
                                    Text("Editing Marks for: ${selectedStudentForScores!!.studentName}", fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = BluePrimary)

                                    selectedStudentForScores!!.subjectScores.forEach { subScore ->
                                        var t1 by remember(subScore) { mutableStateOf(subScore.term1Score.toString()) }
                                        var t2 by remember(subScore) { mutableStateOf(subScore.term2Score.toString()) }
                                        var t3 by remember(subScore) { mutableStateOf(subScore.term3Score.toString()) }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(SlateSurface, RoundedCornerShape(10.dp))
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(subScore.subjectName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedTextField(
                                                    value = t1,
                                                    onValueChange = { t1 = it },
                                                    label = { Text("T1 %") },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true
                                                )
                                                OutlinedTextField(
                                                    value = t2,
                                                    onValueChange = { t2 = it },
                                                    label = { Text("T2 %") },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true
                                                )
                                                OutlinedTextField(
                                                    value = t3,
                                                    onValueChange = { t3 = it },
                                                    label = { Text("T3 %") },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    singleLine = true
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    val v1 = t1.toIntOrNull() ?: subScore.term1Score
                                                    val v2 = t2.toIntOrNull() ?: subScore.term2Score
                                                    val v3 = t3.toIntOrNull() ?: subScore.term3Score
                                                    viewModel.updateStudentSubjectScore(
                                                        selectedStudentForScores!!.admissionNo,
                                                        subScore.subjectCode,
                                                        v1, v2, v3
                                                    )
                                                    Toast.makeText(context, "Saved ${subScore.subjectName} marks!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                                            ) {
                                                Text("UPDATE SUBJECT SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // SUB-TAB 2: ATTENDANCE MARKER
                if (teacherSubTab == 2) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Online Attendance Marker", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                        Text("Date: $attendanceDate", fontSize = 11.5.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.markAllPresentForDate(attendanceDate)
                                            Toast.makeText(context, "All students marked PRESENT!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPass)
                                    ) {
                                        Text("MARK ALL PRESENT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text("Changes made here immediately reflect in individual student accounts!", fontSize = 11.5.sp, color = SlateTextMuted)

                                Divider(color = SlateBorder)

                                cbcRoster.forEach { student ->
                                    val currentRec = attendanceRecords.find { it.admissionNo == student.admissionNo && it.date == attendanceDate }
                                    val status = currentRec?.status ?: "PRESENT"

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(SlateSurface, RoundedCornerShape(10.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(student.studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                            Text("Admission No: ${student.admissionNo}", fontSize = 11.sp, color = SlateTextMuted)
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            FilterChip(
                                                selected = status == "PRESENT",
                                                onClick = { viewModel.markStudentAttendance(student.admissionNo, student.studentName, attendanceDate, "PRESENT") },
                                                label = { Text("P", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = EmeraldPass,
                                                    selectedLabelColor = Color.White
                                                )
                                            )

                                            FilterChip(
                                                selected = status == "ABSENT",
                                                onClick = { viewModel.markStudentAttendance(student.admissionNo, student.studentName, attendanceDate, "ABSENT", "Sick leave note") },
                                                label = { Text("A", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = CrimsonFail,
                                                    selectedLabelColor = Color.White
                                                )
                                            )

                                            FilterChip(
                                                selected = status == "LATE",
                                                onClick = { viewModel.markStudentAttendance(student.admissionNo, student.studentName, attendanceDate, "LATE", "Bus delay") },
                                                label = { Text("L", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = OrangeWarning,
                                                    selectedLabelColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // SUB-TAB 3: SEND EXAM / HOMEWORK / ASSIGNMENT / RESULT PDF TO STUDENT ACCOUNTS
                if (teacherSubTab == 3) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = BluePrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Send Exam / Homework / PDF to Students", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                }

                                Text("The assignment or exam paper will be delivered directly as a downloadable PDF to the targeted student's account inbox.", fontSize = 11.5.sp, color = SlateTextMuted)

                                Divider(color = SlateBorder)

                                OutlinedTextField(
                                    value = assignTitleInput,
                                    onValueChange = { assignTitleInput = it },
                                    label = { Text("Paper / Assignment Title *") },
                                    placeholder = { Text("e.g. Term 2 Algebra Exam Paper") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                Text("Paper Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("ASSIGNMENT", "EXAM", "HOMEWORK", "REPORT").forEach { type ->
                                        FilterChip(
                                            selected = assignTypeInput == type,
                                            onClick = { assignTypeInput = type },
                                            label = { Text(type, fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = BluePrimary,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = assignSubjectInput,
                                    onValueChange = { assignSubjectInput = it },
                                    label = { Text("Subject Area") },
                                    placeholder = { Text(teacher.assignedSubject) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                Text("Target Recipient Student:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (assignTargetStudentInput == "ALL_STUDENTS") BlueContainer else SlateSurface,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { assignTargetStudentInput = "ALL_STUDENTS" }
                                    ) {
                                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text("ALL GRADE 10 STUDENTS (Entire Class)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                        }
                                    }

                                    cbcRoster.forEach { std ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (assignTargetStudentInput == std.admissionNo) BlueContainer else SlateSurface,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { assignTargetStudentInput = std.admissionNo }
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Text("${std.studentName} (${std.admissionNo})", fontSize = 12.sp, color = Navy900)
                                            }
                                        }
                                    }
                                }

                                OutlinedTextField(
                                    value = assignDueDateInput,
                                    onValueChange = { assignDueDateInput = it },
                                    label = { Text("Submission Due Date") },
                                    placeholder = { Text("2026-08-05") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = assignDescInput,
                                    onValueChange = { assignDescInput = it },
                                    label = { Text("Instructions / Questions Body") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    minLines = 3
                                )

                                Button(
                                    onClick = {
                                        val sent = viewModel.sendAssignmentByTeacher(
                                            title = assignTitleInput,
                                            type = assignTypeInput,
                                            subjectName = assignSubjectInput,
                                            targetAdmissionNo = assignTargetStudentInput,
                                            description = assignDescInput,
                                            dueDate = assignDueDateInput
                                        )
                                        if (sent) {
                                            Toast.makeText(context, "Paper sent to student account & PDF generated!", Toast.LENGTH_SHORT).show()
                                            assignTitleInput = ""
                                            assignDescInput = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("SEND TO STUDENT ACCOUNT & GENERATE PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // SUB-TAB 4: STAFF TEACHERS DISCUSSION & PROBLEM SOLVING FORUM
                if (teacherSubTab == 4) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Article,
                                            contentDescription = null,
                                            tint = BluePrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Staff Teachers Discussion Lounge",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Navy900
                                            )
                                            Text(
                                                text = "Confidential forum for staff to collaborate & resolve problems",
                                                fontSize = 11.sp,
                                                color = SlateTextMuted
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { showNewTopicDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("start_teacher_discussion_button")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("NEW TOPIC", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = BlueContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "STAFF ONLY ZONE: Visible strictly to registered Teachers and School Administration. Use this forum to discuss student performance challenges, practical lab rubrics, and academic planning.",
                                            fontSize = 10.5.sp,
                                            color = BluePrimary
                                        )
                                    }
                                }

                                Divider(color = SlateBorder)

                                if (teacherDiscussionTopics.isEmpty()) {
                                    Text(
                                        text = "No staff discussion topics started yet. Click 'NEW TOPIC' above to start a discussion.",
                                        fontSize = 12.sp,
                                        color = SlateTextMuted,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    )
                                } else {
                                    teacherDiscussionTopics.forEach { topic ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = SlateSurface),
                                            border = BorderStroke(1.dp, SlateBorder)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(14.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = GoldAccent.copy(alpha = 0.25f)
                                                    ) {
                                                        Text(
                                                            text = topic.subjectCategory,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Navy900,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                        )
                                                    }

                                                    Text(
                                                        text = "Posted: ${topic.date}",
                                                        fontSize = 10.sp,
                                                        color = SlateTextMuted
                                                    )
                                                }

                                                Text(
                                                    text = topic.title,
                                                    fontSize = 14.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Navy900
                                                )

                                                Text(
                                                    text = topic.description,
                                                    fontSize = 12.5.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    lineHeight = 18.sp
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "By ${topic.authorName} (${topic.authorTeacherId})",
                                                        fontSize = 10.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = BluePrimary
                                                    )

                                                    Text(
                                                        text = "${topic.comments.size} Staff Comments",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Navy900
                                                    )
                                                }

                                                Divider(color = SlateBorder.copy(alpha = 0.5f))

                                                // Existing Comments
                                                if (topic.comments.isNotEmpty()) {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        topic.comments.forEach { cmt ->
                                                            Surface(
                                                                shape = RoundedCornerShape(8.dp),
                                                                color = Color.White,
                                                                border = BorderStroke(1.dp, SlateBorder)
                                                            ) {
                                                                Column(
                                                                    modifier = Modifier.padding(10.dp),
                                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                                ) {
                                                                    Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                                    ) {
                                                                        Text(
                                                                            text = cmt.authorName,
                                                                            fontSize = 11.sp,
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = Navy900
                                                                        )
                                                                        Text(
                                                                            text = cmt.date,
                                                                            fontSize = 9.5.sp,
                                                                            color = SlateTextMuted
                                                                        )
                                                                    }
                                                                    Text(
                                                                        text = cmt.commentText,
                                                                        fontSize = 11.5.sp,
                                                                        color = MaterialTheme.colorScheme.onSurface
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                // Inline Comment Box
                                                Column(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    OutlinedTextField(
                                                        value = if (activeTopicForCommentId == topic.id) commentTextInput else "",
                                                        onValueChange = {
                                                            activeTopicForCommentId = topic.id
                                                            commentTextInput = it
                                                        },
                                                        placeholder = { Text("Write staff comment or problem solution...", fontSize = 11.sp) },
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = RoundedCornerShape(8.dp),
                                                        singleLine = true
                                                    )

                                                    Button(
                                                        onClick = {
                                                            if (commentTextInput.isNotBlank() && activeTopicForCommentId == topic.id) {
                                                                viewModel.addCommentToTopic(
                                                                    topicId = topic.id,
                                                                    commentText = commentTextInput,
                                                                    authorName = teacher.name,
                                                                    authorTeacherId = teacher.teacherId
                                                                )
                                                                commentTextInput = ""
                                                                activeTopicForCommentId = null
                                                                Toast.makeText(context, "Comment posted to staff forum!", Toast.LENGTH_SHORT).show()
                                                            }
                                                        },
                                                        modifier = Modifier.align(Alignment.End),
                                                        colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text("ADD COMMENT", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 2: PUBLIC SCHOOL INFO & ANNOUNCEMENTS
        // ==========================================
        if (activeTab == 2) {
            // SCHOOL-WIDE PRINCIPAL ANNOUNCEMENTS BOARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Principal's School Announcements",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                    Text(
                                        text = "Official online school communications & planning notices",
                                        fontSize = 11.sp,
                                        color = SlateTextMuted
                                    )
                                }
                            }

                            Button(
                                onClick = { showPostAnnouncementDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("publish_principal_announcement_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("POST NOTICE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(color = SlateBorder)

                        if (schoolAnnouncements.isEmpty()) {
                            Text(
                                text = "No official principal announcements currently published.",
                                fontSize = 12.sp,
                                color = SlateTextMuted
                            )
                        } else {
                            schoolAnnouncements.forEach { ann ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (ann.isUrgent) CrimsonContainer.copy(alpha = 0.3f) else SlateSurface
                                    ),
                                    border = BorderStroke(1.dp, if (ann.isUrgent) CrimsonFail else SlateBorder)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (ann.isUrgent) CrimsonFail else BluePrimary
                                                ) {
                                                    Text(
                                                        text = if (ann.isUrgent) "URGENT NOTICE" else ann.category,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = ann.date,
                                                    fontSize = 10.sp,
                                                    color = SlateTextMuted
                                                )
                                            }
                                        }

                                        Text(
                                            text = ann.title,
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )

                                        Text(
                                            text = ann.content,
                                            fontSize = 12.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 18.sp
                                        )

                                        Divider(color = SlateBorder.copy(alpha = 0.5f))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = BluePrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${ann.authorName} • ${ann.authorTitle}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BluePrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${schoolInfo.phonePrimary}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CALL SCHOOL", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${schoolInfo.email}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EMAIL US", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Official Contact Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )

                        Divider(color = SlateBorder)

                        ContactRowItem(icon = Icons.Default.Phone, title = "Primary Telephone", value = schoolInfo.phonePrimary)
                        ContactRowItem(icon = Icons.Default.Call, title = "Secondary / Admissions Line", value = schoolInfo.phoneSecondary)
                        ContactRowItem(icon = Icons.Default.Email, title = "Official Email", value = schoolInfo.email)
                        ContactRowItem(icon = Icons.Default.LocationOn, title = "School Physical Address", value = schoolInfo.address)
                        ContactRowItem(icon = Icons.Default.Language, title = "Official Website", value = schoolInfo.website)
                        ContactRowItem(icon = Icons.Default.Person, title = "School Principal / Head", value = schoolInfo.principalName)

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CrimsonContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = CrimsonFail)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("24/7 Emergency Security Line", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = CrimsonFail)
                                    Text(schoolInfo.emergencyContact, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = CrimsonFail)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("School History & Vision", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        }

                        Divider(color = SlateBorder)

                        Text(
                            text = schoolInfo.history,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // ==========================================
        // TAB 3: ADMIN PORTAL
        // ==========================================
        if (activeTab == 3) {
            if (!isAdminLoggedIn) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = BlueContainer,
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Administrator Verification", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    Text("Enter system admin password to register teacher accounts & modify school info.", fontSize = 11.5.sp, color = SlateTextMuted)
                                }
                            }

                            Divider(color = SlateBorder)

                            OutlinedTextField(
                                value = adminPasswordInput,
                                onValueChange = {
                                    adminPasswordInput = it
                                    adminLoginErrorMsg = null
                                },
                                label = { Text("Admin Master Password") },
                                placeholder = { Text("Default: admin123") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary) },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_password_input"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            if (adminLoginErrorMsg != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CrimsonContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = adminLoginErrorMsg!!,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CrimsonFail,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val success = viewModel.loginAdmin(adminPasswordInput)
                                    if (success) {
                                        adminLoginErrorMsg = null
                                        adminPasswordInput = ""
                                    } else {
                                        adminLoginErrorMsg = "Incorrect Password. Default password is: admin123"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("admin_login_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AUTHENTICATE AS ADMIN", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            } else {
                // ADMIN CONTROL PANEL
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy900)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Admin Control Session Active", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Full rights to edit school info & manage teacher roster", fontSize = 11.sp, color = GoldAccent)
                                }
                            }

                            Button(
                                onClick = { viewModel.logoutAdmin() },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonFail),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("LOGOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // REGISTER NEW TEACHER ACCOUNT
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Register New Teacher Staff ID", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)

                            OutlinedTextField(
                                value = newTeacherId,
                                onValueChange = { newTeacherId = it },
                                label = { Text("Teacher Staff ID / TSC No.") },
                                placeholder = { Text("e.g. TSC-778899") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newTeacherName,
                                onValueChange = { newTeacherName = it },
                                label = { Text("Teacher Name") },
                                placeholder = { Text("e.g. Tr. Mary Wambui") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newTeacherSubject,
                                onValueChange = { newTeacherSubject = it },
                                label = { Text("Assigned Subject") },
                                placeholder = { Text("e.g. Kiswahili & Social Studies") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newTeacherClass,
                                onValueChange = { newTeacherClass = it },
                                label = { Text("Assigned Class / Stream") },
                                placeholder = { Text("Grade 10 East") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newTeacherPin,
                                onValueChange = { newTeacherPin = it },
                                label = { Text("Initial Login PIN") },
                                placeholder = { Text("1234") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (newTeacherName.isNotBlank()) {
                                        viewModel.addTeacherByAdmin(
                                            teacherId = newTeacherId,
                                            name = newTeacherName,
                                            assignedSubject = newTeacherSubject,
                                            assignedClass = newTeacherClass,
                                            pin = newTeacherPin
                                        )
                                        Toast.makeText(context, "Teacher registered successfully!", Toast.LENGTH_SHORT).show()
                                        newTeacherId = ""
                                        newTeacherName = ""
                                        newTeacherSubject = ""
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("REGISTER TEACHER ACCOUNT", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // REGISTERED TEACHERS LIST
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Registered Teacher Accounts (${teachersList.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)

                            teachersList.forEach { tch ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SlateSurface, RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(tch.name, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                        Text("TSC ID: ${tch.teacherId} | PIN: ${tch.pin}", fontSize = 11.5.sp, color = BluePrimary, fontWeight = FontWeight.Bold)
                                        Text("Class: ${tch.assignedClass} (${tch.assignedSubject})", fontSize = 11.sp, color = SlateTextMuted)
                                    }

                                    IconButton(onClick = {
                                        viewModel.removeTeacherByAdmin(tch.teacherId)
                                        Toast.makeText(context, "Removed teacher", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = CrimsonFail)
                                    }
                                }
                            }
                        }
                    }
                }

                // EDIT SCHOOL INFO FORM
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Edit Public School Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)

                            OutlinedTextField(value = editSchoolName, onValueChange = { editSchoolName = it }, label = { Text("School Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(value = editMotto, onValueChange = { editMotto = it }, label = { Text("School Motto") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(value = editPhone1, onValueChange = { editPhone1 = it }, label = { Text("Primary Phone") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(value = editEmail, onValueChange = { editEmail = it }, label = { Text("Official Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(value = editAddress, onValueChange = { editAddress = it }, label = { Text("Physical Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(value = editHistory, onValueChange = { editHistory = it }, label = { Text("School History") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), minLines = 3)

                            Button(
                                onClick = {
                                    viewModel.updateSchoolInfo(
                                        schoolName = editSchoolName,
                                        motto = editMotto,
                                        phonePrimary = editPhone1,
                                        phoneSecondary = editPhone2,
                                        email = editEmail,
                                        address = editAddress,
                                        website = editWebsite,
                                        history = editHistory,
                                        principalName = editPrincipal,
                                        emergencyContact = editEmergency,
                                        bannerImageUrl = editBannerUrl
                                    )
                                    Toast.makeText(context, "School details updated!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SAVE SCHOOL DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // USER CREDENTIALS DIRECTORY (READ-ONLY FOR PASSWORD RECOVERY CONFIRMATION)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Navy900, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("User Credentials Directory (Password Recovery)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                    Text("Read-only access to confirm forgotten usernames and passwords for students, teachers, and admin.", fontSize = 11.sp, color = SlateTextMuted)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldAccent.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, GoldAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Navy900, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PROTECTED READ-ONLY MODE: Admin can view usernames & passwords strictly for confirmation when users forget credentials. Names and usernames CANNOT be modified here to ensure account safety. Handover and succession power remain fully active.",
                                        fontSize = 10.5.sp,
                                        color = Navy900,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            // Search & Filter Bar
                            OutlinedTextField(
                                value = credentialSearchQuery,
                                onValueChange = { credentialSearchQuery = it },
                                placeholder = { Text("Search by Name, Username, Adm No, or Staff ID...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SlateTextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("ALL", "STUDENT", "TEACHER", "ADMIN").forEach { role ->
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (credentialRoleFilter == role) Navy900 else SlateSurface,
                                        modifier = Modifier.clickable { credentialRoleFilter = role }
                                    ) {
                                        Text(
                                            text = role,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (credentialRoleFilter == role) Color.White else Navy900,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Divider(color = SlateBorder)

                            val filteredCredentials = userCredentialsList.filter { cred ->
                                val matchesFilter = (credentialRoleFilter == "ALL" || cred.userType == credentialRoleFilter)
                                val matchesSearch = credentialSearchQuery.isBlank() ||
                                        cred.name.contains(credentialSearchQuery, ignoreCase = true) ||
                                        cred.username.contains(credentialSearchQuery, ignoreCase = true) ||
                                        cred.userId.contains(credentialSearchQuery, ignoreCase = true)
                                matchesFilter && matchesSearch
                            }

                            if (filteredCredentials.isEmpty()) {
                                Text("No user credentials matched search query.", fontSize = 12.sp, color = SlateTextMuted)
                            } else {
                                filteredCredentials.forEach { cred ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SlateSurface,
                                        border = BorderStroke(1.dp, SlateBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(cred.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = when (cred.userType) {
                                                        "ADMIN" -> CrimsonContainer
                                                        "TEACHER" -> BlueContainer
                                                        else -> EmeraldContainer
                                                    }
                                                ) {
                                                    Text(
                                                        text = cred.userType,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (cred.userType) {
                                                            "ADMIN" -> CrimsonFail
                                                            "TEACHER" -> BluePrimary
                                                            else -> EmeraldPass
                                                        },
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text("Username / ID:", fontSize = 10.sp, color = SlateTextMuted)
                                                    Text(cred.username, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Navy900)
                                                }

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text("Password / PIN:", fontSize = 10.sp, color = SlateTextMuted)
                                                    Text(cred.password, fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = CrimsonFail)
                                                }
                                            }

                                            Text("Ref Code: ${cred.userId} • Handover & Succession Ready", fontSize = 9.5.sp, color = SlateTextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // PRINCIPAL ANNOUNCEMENT DIALOG
    if (showPostAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = { showPostAnnouncementDialog = false },
            title = { Text("Publish School Announcement", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Navy900) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("This notice will be displayed online for all students, parents, teachers, and school administration.", fontSize = 11.5.sp, color = SlateTextMuted)

                    OutlinedTextField(
                        value = annTitleInput,
                        onValueChange = { annTitleInput = it },
                        label = { Text("Notice Title *") },
                        placeholder = { Text("e.g. End of Term 2 Examination Timetable") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = annContentInput,
                        onValueChange = { annContentInput = it },
                        label = { Text("Announcement Body / Details *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mark as Urgent Notice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        Switch(
                            checked = annIsUrgentInput,
                            onCheckedChange = { annIsUrgentInput = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (annTitleInput.isNotBlank() && annContentInput.isNotBlank()) {
                            viewModel.publishSchoolAnnouncement(
                                title = annTitleInput,
                                content = annContentInput,
                                category = if (annIsUrgentInput) "URGENT_NOTICE" else annCategoryInput,
                                isUrgent = annIsUrgentInput,
                                authorName = schoolInfo.principalName.ifBlank { "Dr. Peter Otieno" },
                                authorTitle = "School Principal & Management"
                            )
                            Toast.makeText(context, "School announcement posted online successfully!", Toast.LENGTH_SHORT).show()
                            showPostAnnouncementDialog = false
                            annTitleInput = ""
                            annContentInput = ""
                            annIsUrgentInput = false
                        } else {
                            Toast.makeText(context, "Please enter both title and content", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("PUBLISH NOW", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostAnnouncementDialog = false }) {
                    Text("CANCEL", color = SlateTextMuted, fontSize = 12.sp)
                }
            }
        )
    }

    // NEW STAFF DISCUSSION TOPIC DIALOG
    if (showNewTopicDialog) {
        AlertDialog(
            onDismissRequest = { showNewTopicDialog = false },
            title = { Text("New Staff Discussion Topic", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Navy900) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Start a confidential problem-solving thread for teaching staff.", fontSize = 11.5.sp, color = SlateTextMuted)

                    OutlinedTextField(
                        value = topicCategoryInput,
                        onValueChange = { topicCategoryInput = it },
                        label = { Text("Subject / Category *") },
                        placeholder = { Text("e.g. Integrated Science / Grade 7") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = topicTitleInput,
                        onValueChange = { topicTitleInput = it },
                        label = { Text("Discussion Topic Title *") },
                        placeholder = { Text("e.g. Standardizing Grade 8 Agriculture Practicals") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = topicDescInput,
                        onValueChange = { topicDescInput = it },
                        label = { Text("Detailed Description / Problem Statement *") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val currentTeacher = loggedInTeacher
                        if (topicTitleInput.isNotBlank() && topicDescInput.isNotBlank() && currentTeacher != null) {
                            viewModel.postTeacherDiscussionTopic(
                                title = topicTitleInput,
                                description = topicDescInput,
                                subjectCategory = topicCategoryInput,
                                authorName = currentTeacher.name,
                                authorTeacherId = currentTeacher.teacherId
                            )
                            Toast.makeText(context, "Discussion topic created in Staff Lounge!", Toast.LENGTH_SHORT).show()
                            showNewTopicDialog = false
                            topicTitleInput = ""
                            topicDescInput = ""
                        } else {
                            Toast.makeText(context, "Please complete all fields & ensure you are logged in as Teacher", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CREATE TOPIC", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewTopicDialog = false }) {
                    Text("CANCEL", color = SlateTextMuted, fontSize = 12.sp)
                }
            }
        )
    }
}

@Composable
fun ContactRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = CircleShape,
            color = BlueContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SlateTextMuted)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Navy900)
        }
    }
}
