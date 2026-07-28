package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.CbcClassStudent
import com.example.data.remote.CbcStudentScore
import com.example.data.remote.ClassSubjectAnalytics
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonFail
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ModalBottomSheet
import com.example.data.sync.AcademicAlert
import com.example.data.sync.AlertCategory
import com.example.data.sync.SyncState
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.viewmodel.AuthUser
import com.example.ui.viewmodel.ResultViewModel

@Composable
fun CbcPortalScreen(
    viewModel: ResultViewModel,
    onNavigateToLogin: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val cbcRoster by viewModel.cbcRoster.collectAsState()
    val cbcAnalytics by viewModel.cbcAnalytics.collectAsState()
    val selectedCbcStudent by viewModel.selectedCbcStudent.collectAsState()
    val syncState by viewModel.syncState.collectAsState()
    val academicAlerts by viewModel.academicAlerts.collectAsState()
    val unreadAlertsCount by viewModel.unreadAlertsCount.collectAsState()
    val activeClassAssignment by viewModel.activeClassAssignment.collectAsState()
    val teacherProgressArchives by viewModel.teacherProgressArchives.collectAsState()

    var activeTeacherTab by remember { mutableStateOf(0) } // 0 = Roster & Student Sign-In, 1 = Class Performance Visualizing Table
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showNotificationSheet by remember { mutableStateOf(false) }
    var showPostAssignmentDialog by remember { mutableStateOf(false) }
    var showPostHomeworkDialog by remember { mutableStateOf(false) }
    var showChangeTeacherDialog by remember { mutableStateOf(false) }
    var showPastTeacherArchivesDialog by remember { mutableStateOf(false) }

    // Dialog Input States
    var asnTitle by remember { mutableStateOf("") }
    var asnSubject by remember { mutableStateOf("Mathematics") }
    var asnPdfName by remember { mutableStateOf("Grade10_Math_Assignment.pdf") }
    var asnPdfUrl by remember { mutableStateOf("https://example.com/docs/assignment.pdf") }
    var asnDueDate by remember { mutableStateOf("2026-08-05") }

    var hwSubject by remember { mutableStateOf("English") }
    var hwDescription by remember { mutableStateOf("Complete Section B Exercises 1 to 10") }
    var hwDueDate by remember { mutableStateOf("2026-07-30") }

    // Teacher Handover Input States
    var handoverNewTeacherId by remember { mutableStateOf("TSC-982103") }
    var handoverNewTeacherName by remember { mutableStateOf("Tr. Grace Akinyi") }
    var handoverNewTeacherSubject by remember { mutableStateOf("Integrated Science & Biology") }
    var handoverTermSession by remember { mutableStateOf("Term 2/3 - 2026 Academic Session") }
    var handoverNotesInput by remember { mutableStateOf("Routine Class Teacher handover authorized by School Administration. Previous student evaluation marks and performance history preserved.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Authentication Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser?.role == "TEACHER") Navy900 else BluePrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldAccent,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(
                            imageVector = if (currentUser?.role == "TEACHER") Icons.Default.SupervisorAccount else Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser?.name ?: "Guest / Demo Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = currentUser?.role ?: "VISITOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = if (currentUser != null) "${currentUser?.id} • ${currentUser?.gradeClass}" else "Kenya Secondary CBC Portal",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    IconButton(
                        onClick = { showNotificationSheet = true },
                        modifier = Modifier.testTag("fcm_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadAlertsCount > 0) {
                                    Badge(
                                        containerColor = CrimsonFail,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadAlertsCount", fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (unreadAlertsCount > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "FCM Academic Alerts",
                                tint = if (unreadAlertsCount > 0) GoldAccent else Color.White
                            )
                        }
                    }

                    if (currentUser != null) {
                        IconButton(onClick = { viewModel.logoutUser() }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Sign Out",
                                tint = Color.White
                            )
                        }
                    } else {
                        Button(
                            onClick = onNavigateToLogin,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("login_redirect_button")
                        ) {
                            Text("SIGN IN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy900)
                        }
                    }
                }
            }
        }

        // Room-to-Firestore Live Sync Manager Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (syncState) {
                        is SyncState.Syncing -> Navy800
                        is SyncState.Success -> EmeraldContainer
                        is SyncState.Error -> CrimsonContainer
                        else -> SlateBorder
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (syncState) {
                            is SyncState.Syncing -> Icons.Default.Sync
                            is SyncState.Success -> Icons.Default.CloudDone
                            is SyncState.Error -> Icons.Default.CloudOff
                            else -> Icons.Default.Cloud
                        },
                        contentDescription = "Sync Status",
                        tint = when (syncState) {
                            is SyncState.Success -> EmeraldPass
                            is SyncState.Error -> CrimsonFail
                            else -> Color.White
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Room <-> Firestore Sync Engine",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (syncState) {
                                is SyncState.Success -> EmeraldPass
                                is SyncState.Error -> CrimsonFail
                                else -> Color.White
                            }
                        )
                        Text(
                            text = when (val state = syncState) {
                                is SyncState.Syncing -> "Synchronizing local Room database with Cloud Firestore..."
                                is SyncState.Success -> state.message
                                is SyncState.Error -> state.message
                                else -> "Local Room database active"
                            },
                            fontSize = 10.5.sp,
                            color = when (syncState) {
                                is SyncState.Success -> EmeraldPass.copy(alpha = 0.9f)
                                is SyncState.Error -> CrimsonFail.copy(alpha = 0.9f)
                                else -> Color.White.copy(alpha = 0.8f)
                            }
                        )
                    }
                }
            }
        }

        // 2. Unauthenticated Banner Notice
        if (currentUser == null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BlueContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kenya Secondary CBC Assessment Engine",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                            Text(
                                text = "Sign in as a Student to view your report card or Class Teacher to sign in students and analyze class performance tables.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // CLASS TEACHER SUCCESSION & ACTIVE HANDOVER SESSION CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Class Teacher Succession & Handover",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                                Text(
                                    text = "${activeClassAssignment.classId} • Session ID: ${activeClassAssignment.activeSessionId}",
                                    fontSize = 10.5.sp,
                                    color = SlateTextMuted
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = EmeraldContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPass, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ACTIVE SESSION",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPass
                                )
                            }
                        }
                    }

                    Divider(color = SlateBorder)

                    // Active Class Teacher Profile Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BlueContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = BluePrimary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeClassAssignment.currentTeacherName,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                            Text(
                                text = "TSC ID: ${activeClassAssignment.currentTeacherId} • ${activeClassAssignment.currentTeacherSubject}",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                            Text(
                                text = "Term: ${activeClassAssignment.activeTermSession} (Assigned: ${activeClassAssignment.assignedDate})",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BluePrimary
                            )
                        }
                    }

                    // Action Row: Admin Reassign Class Teacher & View Past Progress Archives
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showChangeTeacherDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reassign_class_teacher_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("REASSIGN TEACHER", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { showPastTeacherArchivesDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_past_teacher_archives_button"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Navy900),
                            border = BorderStroke(1.dp, BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PAST PROGRESS (${teacherProgressArchives.size})", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. STUDENT PORTAL VIEW (When logged in as Student or viewing Student)
        if (currentUser?.role == "STUDENT" || currentUser == null) {
            val student = selectedCbcStudent ?: cbcRoster.firstOrNull()

            if (student != null) {
                // Student Performance Card Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = student.studentName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                    Text(
                                        text = "Adm: ${student.admissionNo} • ${student.gradeLevel} ${student.stream}",
                                        fontSize = 12.sp,
                                        color = SlateTextMuted
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (student.overallRating == "EE" || student.overallRating == "ME") EmeraldContainer else BlueContainer
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = student.overallRating,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (student.overallRating == "EE" || student.overallRating == "ME") EmeraldPass else BluePrimary
                                        )
                                        Text(
                                            text = getRatingText(student.overallRating),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateTextMuted
                                        )
                                    }
                                }
                            }

                            Divider(color = SlateBorder)

                            // Term 1, Term 2, Term 3 Averages Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TermScoreColumn("TERM 1 AVG", student.term1Average)
                                TermScoreColumn("TERM 2 AVG", student.term2Average)
                                TermScoreColumn("TERM 3 AVG", student.term3Average)
                                TermScoreColumn("ANNUAL AVG", student.annualAverage, isBold = true)
                            }
                        }
                    }
                }

                // Header for Subjects
                item {
                    Text(
                        text = "Kenya CBC Secondary Subjects Assessment",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                }

                // Subject Items
                items(student.subjectScores) { subjectScore ->
                    CbcSubjectScoreCard(score = subjectScore)
                }
            }
        }

        // 4. CLASS TEACHER PORTAL VIEW
        if (currentUser?.role == "TEACHER") {
            // Teacher Dashboard Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Navy900)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Class Teacher Dashboard",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${currentUser?.gradeClass} • Kenya Secondary School",
                                    fontSize = 12.sp,
                                    color = GoldAccent
                                )
                            }

                            Button(
                                onClick = { showAddStudentDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("sign_in_student_button")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Navy900, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("SIGN IN STUDENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Navy900)
                            }
                        }

                        // Class Quick Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            QuickStatBox("ENROLLED STUDENTS", "${cbcRoster.size}")
                            QuickStatBox("CLASS ANNUAL AVG", String.format("%.1f%%", cbcRoster.map { it.annualAverage }.average()))
                            QuickStatBox("EE/ME PASS RATE", String.format("%.0f%%", (cbcRoster.count { it.overallRating == "EE" || it.overallRating == "ME" }.toDouble() / cbcRoster.size) * 100))
                        }
                    }
                }
            }

            // Teacher Real-Time FCM Alerts Broadcast Card
            if (currentUser?.role == "TEACHER") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy900),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Firebase Cloud Messaging (FCM) Alert Portal",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Broadcast instant FCM alerts to students when uploading grades, homework, or assignment PDFs.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { showPostAssignmentDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Upload PDF Assignment", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { showPostHomeworkDialog = true },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                                    border = BorderStroke(1.dp, GoldAccent),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Post Homework Task", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Teacher Tab Selection (Roster vs Visualizing Table)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    TabRow(
                        selectedTabIndex = activeTeacherTab,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            if (activeTeacherTab < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTeacherTab]),
                                    height = 3.dp,
                                    color = BluePrimary
                                )
                            }
                        }
                    ) {
                        Tab(
                            selected = activeTeacherTab == 0,
                            onClick = { activeTeacherTab = 0 },
                            modifier = Modifier.testTag("tab_class_roster")
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = if (activeTeacherTab == 0) BluePrimary else SlateTextMuted)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Class Roster (${cbcRoster.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Tab(
                            selected = activeTeacherTab == 1,
                            onClick = { activeTeacherTab = 1 },
                            modifier = Modifier.testTag("tab_visualizing_table")
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = if (activeTeacherTab == 1) BluePrimary else SlateTextMuted)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Visualizing Table", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // TAB 0: CLASS ROSTER & SIGNED IN STUDENTS
            if (activeTeacherTab == 0) {
                items(cbcRoster) { student ->
                    RosterStudentItemCard(
                        student = student,
                        onDelete = { viewModel.removeStudentFromClass(student) },
                        onSelect = {
                            viewModel.setSelectedCbcStudent(student)
                        }
                    )
                }
            }

            // TAB 1: VISUALIZING TABLE FOR CLASS SUBJECT PERFORMANCE THROUGHOUT THE YEAR
            if (activeTeacherTab == 1) {
                // Class Visualizing Table Header
                item {
                    Text(
                        text = "Overall Class Subject Performance Table (Year Progression)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                }

                // Subject Analytics Table Header Row
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Navy900)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SUBJECT", modifier = Modifier.weight(2.2f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("TERM 1", modifier = Modifier.weight(1.0f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                            Text("TERM 2", modifier = Modifier.weight(1.0f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                            Text("TERM 3", modifier = Modifier.weight(1.0f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                            Text("ANNUAL", modifier = Modifier.weight(1.1f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent, textAlign = TextAlign.End)
                        }
                    }
                }

                // Table Items
                items(cbcAnalytics) { analytics ->
                    ClassAnalyticsRowCard(analytics = analytics)
                }

                // Visual Bar Chart Overview for Terms
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
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Annual Progress Bar Chart Visualizer",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Navy900
                                )
                            }

                            Divider(color = SlateBorder)

                            cbcAnalytics.forEach { analytics ->
                                SubjectProgressBarItem(analytics = analytics)
                            }
                        }
                    }
                }
            }
        }
    }

    // Sign In / Register New Student Dialog for Class Teacher
    if (showAddStudentDialog) {
        var newAdmissionNo by remember { mutableStateOf("ADM/2026/${(100..999).random()}") }
        var newStudentName by remember { mutableStateOf("") }
        var newGender by remember { mutableStateOf("Male") }

        AlertDialog(
            onDismissRequest = { showAddStudentDialog = false },
            title = {
                Text(
                    text = "Sign In Student to Class Roster",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newAdmissionNo,
                        onValueChange = { newAdmissionNo = it },
                        label = { Text("Admission Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = newStudentName,
                        onValueChange = { newStudentName = it },
                        label = { Text("Student Full Name") },
                        placeholder = { Text("e.g. Kiprono Langat") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { newGender = "Male" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = if (newGender == "Male") ButtonDefaults.outlinedButtonColors(containerColor = BlueContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Male")
                        }

                        OutlinedButton(
                            onClick = { newGender = "Female" },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = if (newGender == "Female") ButtonDefaults.outlinedButtonColors(containerColor = BlueContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Female")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newStudentName.isNotBlank()) {
                            viewModel.registerStudentInClass(
                                admissionNo = newAdmissionNo,
                                studentName = newStudentName,
                                gender = newGender,
                                gradeLevel = currentUser?.gradeClass?.split(" ")?.firstOrNull() ?: "Grade 10",
                                stream = currentUser?.gradeClass?.split(" ")?.lastOrNull() ?: "East"
                            )
                            showAddStudentDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("REGISTER STUDENT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStudentDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // FCM Assignment PDF Upload Dialog
    if (showPostAssignmentDialog) {
        AlertDialog(
            onDismissRequest = { showPostAssignmentDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = BluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload Assignment PDF & Alert Students", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = asnTitle,
                        onValueChange = { asnTitle = it },
                        label = { Text("Assignment Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = asnSubject,
                        onValueChange = { asnSubject = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = asnPdfName,
                        onValueChange = { asnPdfName = it },
                        label = { Text("PDF File Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = asnPdfUrl,
                        onValueChange = { asnPdfUrl = it },
                        label = { Text("PDF Storage / Download Link") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = asnDueDate,
                        onValueChange = { asnDueDate = it },
                        label = { Text("Submission Due Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (asnTitle.isNotBlank()) {
                            viewModel.postAssignmentPdf(
                                title = asnTitle,
                                subjectName = asnSubject,
                                pdfFileName = asnPdfName,
                                pdfUrl = asnPdfUrl,
                                dueDate = asnDueDate
                            )
                            showPostAssignmentDialog = false
                            asnTitle = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("UPLOAD & BROADCAST FCM ALERT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostAssignmentDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // FCM Homework Post Dialog
    if (showPostHomeworkDialog) {
        AlertDialog(
            onDismissRequest = { showPostHomeworkDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post Homework & Alert Students", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = hwSubject,
                        onValueChange = { hwSubject = it },
                        label = { Text("Subject Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = hwDescription,
                        onValueChange = { hwDescription = it },
                        label = { Text("Homework Task Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    OutlinedTextField(
                        value = hwDueDate,
                        onValueChange = { hwDueDate = it },
                        label = { Text("Due Date") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (hwSubject.isNotBlank() && hwDescription.isNotBlank()) {
                            viewModel.postHomework(
                                subjectName = hwSubject,
                                description = hwDescription,
                                dueDate = hwDueDate
                            )
                            showPostHomeworkDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("POST & BROADCAST FCM ALERT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPostHomeworkDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // Admin Reassign Class Teacher & Handover Dialog
    if (showChangeTeacherDialog) {
        AlertDialog(
            onDismissRequest = { showChangeTeacherDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = BluePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin: Reassign Class Teacher & Handover", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BlueContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "NOTICE: All student scores, marks, and evaluations recorded by outgoing teacher '${activeClassAssignment.currentTeacherName}' remain permanently preserved in the class database. A new progress tracking session will open for the replacement teacher.",
                            fontSize = 11.sp,
                            color = BluePrimary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = handoverNewTeacherName,
                        onValueChange = { handoverNewTeacherName = it },
                        label = { Text("New Class Teacher Name") },
                        placeholder = { Text("e.g. Tr. Grace Akinyi") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = handoverNewTeacherId,
                        onValueChange = { handoverNewTeacherId = it },
                        label = { Text("TSC Staff Number") },
                        placeholder = { Text("e.g. TSC-982103") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = handoverNewTeacherSubject,
                        onValueChange = { handoverNewTeacherSubject = it },
                        label = { Text("Assigned Subject Specialty") },
                        placeholder = { Text("e.g. Integrated Science & Biology") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = handoverTermSession,
                        onValueChange = { handoverTermSession = it },
                        label = { Text("Academic Progress Session Name") },
                        placeholder = { Text("e.g. Term 2/3 - 2026 Academic Session") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = handoverNotesInput,
                        onValueChange = { handoverNotesInput = it },
                        label = { Text("Admin Handover Notes & Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (handoverNewTeacherName.isNotBlank()) {
                            viewModel.changeClassTeacherAndHandover(
                                classId = activeClassAssignment.classId,
                                newTeacherId = handoverNewTeacherId,
                                newTeacherName = handoverNewTeacherName,
                                newTeacherSubject = handoverNewTeacherSubject,
                                newTermSession = handoverTermSession,
                                handoverNotes = handoverNotesInput
                            )
                            showChangeTeacherDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REASSIGN & OPEN NEW SESSION")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeTeacherDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // Past Class Teacher Progress Archives Dialog
    if (showPastTeacherArchivesDialog) {
        AlertDialog(
            onDismissRequest = { showPastTeacherArchivesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = Navy900)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Past Class Teacher Progress Archives", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "The new class teacher (${activeClassAssignment.currentTeacherName}) and school administration can review past academic progress logged by previous class teachers for ${activeClassAssignment.classId}:",
                        fontSize = 11.5.sp,
                        color = SlateTextMuted
                    )

                    if (teacherProgressArchives.isEmpty()) {
                        Text(text = "No previous teacher progress archives found.", fontSize = 12.sp, color = SlateTextMuted)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(teacherProgressArchives) { archive ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = SlateSurface),
                                    border = BorderStroke(1.dp, SlateBorder)
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
                                            Text(
                                                text = archive.teacherName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Navy900
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = GoldAccent.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = archive.termSession,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Navy900,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "TSC ID: ${archive.teacherId} • Subject: ${archive.teacherSubject}",
                                            fontSize = 10.5.sp,
                                            color = SlateTextMuted
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Period: ${archive.startDate} to ${archive.endDate}",
                                                fontSize = 10.sp,
                                                color = BluePrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "Class Avg: ${String.format("%.1f%%", archive.classAnnualAverage)}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPass
                                            )
                                        }

                                        Text(
                                            text = "Top Performers: ${archive.topPerformers}",
                                            fontSize = 10.sp,
                                            color = Navy900
                                        )

                                        Divider(color = SlateBorder.copy(alpha = 0.5f))

                                        Text(
                                            text = "Handover Notes: ${archive.handoverNotes}",
                                            fontSize = 10.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPastTeacherArchivesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                ) {
                    Text("CLOSE ARCHIVES")
                }
            }
        )
    }

    // FCM Real-time Academic Alerts Modal Bottom Sheet
    @OptIn(ExperimentalMaterial3Api::class)
    if (showNotificationSheet) {
        ModalBottomSheet(
            onDismissRequest = { showNotificationSheet = false },
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FCM Academic Alerts (${academicAlerts.size})",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    }
                    IconButton(onClick = { showNotificationSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider()

                if (academicAlerts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No real-time academic alerts yet.", color = SlateTextMuted)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(academicAlerts) { alert ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (alert.isRead) SlateSurface else BlueContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = when (alert.category) {
                                            AlertCategory.GRADE_UPLOAD -> Icons.Default.Analytics
                                            AlertCategory.ASSIGNMENT_PDF_UPLOAD -> Icons.Default.PictureAsPdf
                                            AlertCategory.HOMEWORK_ASSIGNED -> Icons.Default.Assignment
                                            else -> Icons.Default.Notifications
                                        },
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = alert.title,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Navy900
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = alert.body,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (alert.attachmentUrl.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = GoldAccent.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = "📎 Attached PDF: ${alert.attachmentUrl}",
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Navy900,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
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

@Composable
fun CbcSubjectScoreCard(score: CbcStudentScore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = score.subjectName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                    Text(
                        text = "Code: ${score.subjectCode}",
                        fontSize = 10.5.sp,
                        color = SlateTextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = getRatingBgColor(score.competencyRating)
                ) {
                    Text(
                        text = "${score.competencyRating} (${score.ratingLabel})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = getRatingTextColor(score.competencyRating),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Divider(color = SlateBorder, thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "Term 1: ${score.term1Score}%", fontSize = 11.5.sp, color = SlateTextMuted)
                Text(text = "Term 2: ${score.term2Score}%", fontSize = 11.5.sp, color = SlateTextMuted)
                Text(text = "Term 3: ${score.term3Score}%", fontSize = 11.5.sp, color = SlateTextMuted)
                Text(
                    text = "Annual: ${String.format("%.1f%%", score.annualAverage)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
        }
    }
}

@Composable
fun ClassAnalyticsRowCard(analytics: ClassSubjectAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = analytics.subjectName,
                modifier = Modifier.weight(2.2f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Navy900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = String.format("%.0f%%", analytics.classAverageTerm1),
                modifier = Modifier.weight(1.0f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = SlateTextMuted
            )
            Text(
                text = String.format("%.0f%%", analytics.classAverageTerm2),
                modifier = Modifier.weight(1.0f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = SlateTextMuted
            )
            Text(
                text = String.format("%.0f%%", analytics.classAverageTerm3),
                modifier = Modifier.weight(1.0f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = SlateTextMuted
            )
            Text(
                text = String.format("%.1f%%", analytics.overallClassAverage),
                modifier = Modifier.weight(1.1f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = BluePrimary
            )
        }
    }
}

@Composable
fun SubjectProgressBarItem(analytics: ClassSubjectAnalytics) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = analytics.subjectName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Navy900
            )
            Text(
                text = "Annual Class Avg: ${String.format("%.1f%%", analytics.overallClassAverage)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BluePrimary
            )
        }

        LinearProgressIndicator(
            progress = (analytics.overallClassAverage / 100.0).toFloat().coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = BluePrimary,
            trackColor = BlueContainer
        )
    }
}

@Composable
fun RosterStudentItemCard(
    student: CbcClassStudent,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BlueContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.padding(9.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.studentName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                Text(
                    text = "Adm: ${student.admissionNo} • ${student.gender}",
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = getRatingBgColor(student.overallRating)
            ) {
                Text(
                    text = "${student.overallRating} (${String.format("%.0f%%", student.annualAverage)})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = getRatingTextColor(student.overallRating),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonFail, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun TermScoreColumn(title: String, score: Double, isBold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = String.format("%.1f%%", score),
            fontSize = if (isBold) 14.sp else 12.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isBold) BluePrimary else Navy900
        )
    }
}

@Composable
fun QuickStatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
    }
}

private fun getRatingText(rating: String): String {
    return when (rating) {
        "EE" -> "Exceeding Expectations"
        "ME" -> "Meeting Expectations"
        "AE" -> "Approaching Expectations"
        else -> "Below Expectations"
    }
}

private fun getRatingBgColor(rating: String): Color {
    return when (rating) {
        "EE", "ME" -> EmeraldContainer
        "AE" -> BlueContainer
        else -> CrimsonContainer
    }
}

private fun getRatingTextColor(rating: String): Color {
    return when (rating) {
        "EE", "ME" -> EmeraldPass
        "AE" -> BluePrimary
        else -> CrimsonFail
    }
}
