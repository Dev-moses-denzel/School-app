package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.StudentResult
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateBorder
import com.example.ui.viewmodel.ResultViewModel
import com.example.ui.viewmodel.SearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ResultViewModel,
    onNavigateToDetail: (StudentResult) -> Unit,
    onNavigateToSaved: () -> Unit
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val selectedBoardCode by viewModel.selectedBoardCode.collectAsState()
    val rollNumberInput by viewModel.rollNumberInput.collectAsState()
    val regNumberInput by viewModel.regNumberInput.collectAsState()
    val studentNameInput by viewModel.studentNameInput.collectAsState()
    val examTermInput by viewModel.examTermInput.collectAsState()
    val searchState by viewModel.searchUiState.collectAsState()
    val savedResults by viewModel.savedResults.collectAsState()

    var boardExpanded by remember { mutableStateOf(false) }
    val boards = viewModel.supportedBoards
    val currentBoard = boards.find { it.code == selectedBoardCode } ?: boards.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Campus Banner Background Image
                    Image(
                        painter = painterResource(id = R.drawable.university_banner_1785236368209),
                        contentDescription = "University Campus",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        alpha = 0.35f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = GoldAccent,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Academic Logo",
                                        tint = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Result Hub",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Online Marksheets & Official Transcripts",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            // User Profile Picture Avatar
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                modifier = Modifier.size(38.dp),
                                border = BorderStroke(1.5.dp, GoldAccent)
                            ) {
                                if (appSettings.profilePictureUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(appSettings.profilePictureUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Navy900,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Direct University Portal Integration & PDF Downloader",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Demo Profiles Quick Selector Chips
        item {
            Column {
                Text(
                    text = "Quick Demo Student Profiles",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        DemoChip(
                            name = "Alex Rivera (CS)",
                            roll = "STUDENT202601",
                            onClick = { viewModel.loadDemoProfile("STUDENT202601", "Alex Rivera") }
                        )
                    }
                    item {
                        DemoChip(
                            name = "Sophia Chen (Biomed)",
                            roll = "STUDENT202602",
                            onClick = { viewModel.loadDemoProfile("STUDENT202602", "Sophia Chen") }
                        )
                    }
                    item {
                        DemoChip(
                            name = "Marcus Vance (HighSchool)",
                            roll = "STUDENT202603",
                            onClick = { viewModel.loadDemoProfile("STUDENT202603", "Marcus Vance") }
                        )
                    }
                }
            }
        }

        // 3. Search Query Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = BluePrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Find Online Examination Result",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Board / University Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = boardExpanded,
                        onExpandedChange = { boardExpanded = !boardExpanded }
                    ) {
                        OutlinedTextField(
                            value = currentBoard.name,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Institution / Board") },
                            leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = BluePrimary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = boardExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("board_selector"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        ExposedDropdownMenu(
                            expanded = boardExpanded,
                            onDismissRequest = { boardExpanded = false }
                        ) {
                            boards.forEach { board ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(board.name, fontWeight = FontWeight.SemiBold)
                                            Text(board.category + " • " + board.location, fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setBoardCode(board.code)
                                        boardExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Roll Number Field
                    OutlinedTextField(
                        value = rollNumberInput,
                        onValueChange = { viewModel.setRollNumber(it) },
                        label = { Text("Roll Number / Student ID *") },
                        placeholder = { Text("e.g. STUDENT202601 or 10928374") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = BluePrimary) },
                        trailingIcon = {
                            if (rollNumberInput.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setRollNumber("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("roll_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Optional Student Name Field
                    OutlinedTextField(
                        value = studentNameInput,
                        onValueChange = { viewModel.setStudentName(it) },
                        label = { Text("Student Name (Optional)") },
                        placeholder = { Text("e.g. Alex Rivera") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Exam Term Field
                    OutlinedTextField(
                        value = examTermInput,
                        onValueChange = { viewModel.setExamTerm(it) },
                        label = { Text("Exam Term / Semester") },
                        leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, tint = BluePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Fetch Button
                    Button(
                        onClick = { viewModel.searchResult() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("fetch_result_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        if (searchState is SearchUiState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Connecting to Portal...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("FETCH ONLINE RESULT", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Error Banner
                    if (searchState is SearchUiState.Error) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = (searchState as SearchUiState.Error).message,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Search Success Preview Card (Auto Navigate Action)
        item {
            AnimatedVisibility(
                visible = searchState is SearchUiState.Success,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (searchState is SearchUiState.Success) {
                    val result = (searchState as SearchUiState.Success).result
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(result) }
                            .testTag("result_found_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPass,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Success",
                                    tint = Color.White,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Result Ready: ${result.studentName}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPass
                                )
                                Text(
                                    text = "Roll No: ${result.rollNumber} • CGPA: ${result.cgpa} (${result.overallStatus})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = { onNavigateToDetail(result) }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "View Details",
                                    tint = EmeraldPass
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Recent Saved Downloads Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Offline Marksheets (${savedResults.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (savedResults.isNotEmpty()) {
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary,
                        modifier = Modifier.clickable { onNavigateToSaved() }
                    )
                }
            }
        }

        if (savedResults.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No saved offline marksheets yet.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Searched online results will be saved here automatically for instant offline downloading.",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(savedResults.take(3)) { res ->
                SavedResultItemCard(
                    result = res,
                    onClick = {
                        viewModel.setActiveResult(res)
                        onNavigateToDetail(res)
                    }
                )
            }
        }
    }
}

@Composable
fun DemoChip(name: String, roll: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = BlueContainer,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BluePrimary,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(3.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                Text(text = roll, fontSize = 9.5.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SavedResultItemCard(result: StudentResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BlueContainer,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.studentName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${result.boardOrUniversity} • Roll: ${result.rollNumber}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${result.examTerm} • CGPA: ${result.cgpa}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldPass
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "View",
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
