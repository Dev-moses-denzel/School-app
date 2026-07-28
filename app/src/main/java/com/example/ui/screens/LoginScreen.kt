package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.viewmodel.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: ResultViewModel,
    onLoginSuccess: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Student, 1 = Class Teacher

    // Student Login State
    var studentAdmissionNo by remember { mutableStateOf("ADM/2026/001") }
    var studentName by remember { mutableStateOf("Amani Kiprop") }
    var studentGradeClass by remember { mutableStateOf("Grade 10 East") }

    // Teacher Login State
    var teacherTscNo by remember { mutableStateOf("TSC/482910") }
    var teacherName by remember { mutableStateOf("Mr. David Omondi") }
    var teacherClassAssigned by remember { mutableStateOf("Grade 10 East") }

    val gradeClassOptions = listOf(
        "Grade 7 North", "Grade 8 South", "Grade 9 West",
        "Grade 10 East", "Grade 11 Alpha", "Grade 12 Omega"
    )

    var studentGradeExpanded by remember { mutableStateOf(false) }
    var teacherGradeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateSurface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Campus Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.university_banner_1785236368209),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    alpha = 0.3f
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldAccent,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Kenya CBC",
                            tint = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Kenya CBC Portal Login",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "Secondary School Competency Based Curriculum (CBC)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Student vs Teacher Role Switcher Tabs
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 3.dp,
                                color = BluePrimary
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("tab_student_login")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (selectedTab == 0) BluePrimary else SlateTextMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Student Portal",
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 0) BluePrimary else SlateTextMuted
                            )
                        }
                    }

                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("tab_teacher_login")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = if (selectedTab == 1) BluePrimary else SlateTextMuted
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Class Teacher",
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == 1) BluePrimary else SlateTextMuted
                            )
                        }
                    }
                }
            }
        }

        // Form Card based on selected Role
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
                if (selectedTab == 0) {
                    // STUDENT LOGIN FORM
                    Text(
                        text = "Sign In as Secondary Student",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )

                    OutlinedTextField(
                        value = studentAdmissionNo,
                        onValueChange = { studentAdmissionNo = it },
                        label = { Text("Admission / Student Number *") },
                        placeholder = { Text("e.g. ADM/2026/001") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_admission_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Full Name *") },
                        placeholder = { Text("e.g. Amani Kiprop") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_login_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenuBox(
                        expanded = studentGradeExpanded,
                        onExpandedChange = { studentGradeExpanded = !studentGradeExpanded }
                    ) {
                        OutlinedTextField(
                            value = studentGradeClass,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("CBC Grade & Stream") },
                            leadingIcon = { Icon(Icons.Default.Class, contentDescription = null, tint = BluePrimary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentGradeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = studentGradeExpanded,
                            onDismissRequest = { studentGradeExpanded = false }
                        ) {
                            gradeClassOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        studentGradeClass = option
                                        studentGradeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Quick Select Sample Student Chips
                    Text(
                        text = "Quick Demo Student Profiles:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextMuted
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BlueContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    studentAdmissionNo = "ADM/2026/001"
                                    studentName = "Amani Kiprop"
                                    studentGradeClass = "Grade 10 East"
                                }
                        ) {
                            Text(
                                text = "Amani Kiprop",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = BlueContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    studentAdmissionNo = "ADM/2026/002"
                                    studentName = "Zuri Wanjiku"
                                    studentGradeClass = "Grade 10 East"
                                }
                        ) {
                            Text(
                                text = "Zuri Wanjiku",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.loginUser(
                                id = studentAdmissionNo,
                                name = studentName,
                                role = "STUDENT",
                                gradeClass = studentGradeClass
                            )
                            onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("student_login_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        Icon(Icons.Default.School, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ENTER STUDENT CBC PORTAL", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                } else {
                    // CLASS TEACHER LOGIN FORM
                    Text(
                        text = "Sign In as Class Teacher / Educator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )

                    OutlinedTextField(
                        value = teacherTscNo,
                        onValueChange = { teacherTscNo = it },
                        label = { Text("TSC Registration Number *") },
                        placeholder = { Text("e.g. TSC/482910") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_tsc_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Teacher Name *") },
                        placeholder = { Text("e.g. Mr. David Omondi") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("teacher_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenuBox(
                        expanded = teacherGradeExpanded,
                        onExpandedChange = { teacherGradeExpanded = !teacherGradeExpanded }
                    ) {
                        OutlinedTextField(
                            value = teacherClassAssigned,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Class Assigned to Teacher") },
                            leadingIcon = { Icon(Icons.Default.Class, contentDescription = null, tint = BluePrimary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = teacherGradeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = teacherGradeExpanded,
                            onDismissRequest = { teacherGradeExpanded = false }
                        ) {
                            gradeClassOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        teacherClassAssigned = option
                                        teacherGradeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Quick Select Sample Teacher
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BlueContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                teacherTscNo = "TSC/482910"
                                teacherName = "Mr. David Omondi"
                                teacherClassAssigned = "Grade 10 East"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPass)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quick Fill: Mr. David Omondi (Class Teacher Grade 10 East)",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BluePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            viewModel.loginUser(
                                id = teacherTscNo,
                                name = teacherName,
                                role = "TEACHER",
                                gradeClass = teacherClassAssigned
                            )
                            onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("teacher_login_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                    ) {
                        Icon(Icons.Default.SupervisorAccount, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ENTER CLASS TEACHER DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
