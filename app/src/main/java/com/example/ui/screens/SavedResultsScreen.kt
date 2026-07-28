package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentResult
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.viewmodel.ResultViewModel
import com.example.util.FileHelper
import com.example.util.PdfResultGenerator
import java.io.File

@Composable
fun SavedResultsScreen(
    viewModel: ResultViewModel,
    onNavigateToDetail: (StudentResult) -> Unit
) {
    val context = LocalContext.current
    val savedResults by viewModel.savedResults.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredResults = savedResults.filter { res ->
        searchQuery.isEmpty() ||
                res.studentName.contains(searchQuery, ignoreCase = true) ||
                res.rollNumber.contains(searchQuery, ignoreCase = true) ||
                res.boardOrUniversity.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Filter TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter saved marksheets by name, roll no, or board...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BluePrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("saved_search_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text(
            text = "Downloaded Results Library (${filteredResults.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Navy900
        )

        if (filteredResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = SlateTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isEmpty()) "No saved offline marksheets." else "No marksheets match '$searchQuery'",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = SlateTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredResults, key = { it.id }) { result ->
                    SavedResultCardItem(
                        result = result,
                        onOpen = {
                            viewModel.setActiveResult(result)
                            onNavigateToDetail(result)
                        },
                        onShare = {
                            val file = if (!result.pdfUri.isNullOrEmpty() && File(result.pdfUri).exists()) {
                                File(result.pdfUri)
                            } else {
                                PdfResultGenerator.generateResultPdf(context, result) ?: File(context.cacheDir, "res.pdf")
                            }
                            FileHelper.sharePdfFile(context, file, result)
                        },
                        onDelete = {
                            viewModel.deleteResultFromDb(result)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedResultCardItem(
    result: StudentResult,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = BlueContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = BluePrimary,
                        modifier = Modifier.padding(9.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.studentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${result.boardOrUniversity} • Roll: ${result.rollNumber}",
                        fontSize = 11.5.sp,
                        color = SlateTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlueContainer
                ) {
                    Text(
                        text = "CGPA ${result.cgpa}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${result.examTerm} • ${result.overallStatus}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldPass
                )

                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share PDF", tint = BluePrimary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onOpen, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Open", tint = Navy900, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
