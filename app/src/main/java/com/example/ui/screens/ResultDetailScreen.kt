package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.remember
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.Converters
import com.example.data.model.StudentResult
import com.example.data.model.SubjectScore
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.viewmodel.PdfUiState
import com.example.ui.viewmodel.ResultViewModel
import com.example.util.FileHelper
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultDetailScreen(
    viewModel: ResultViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activeResult by viewModel.activeResult.collectAsState()
    val pdfState by viewModel.pdfUiState.collectAsState()

    val result = activeResult ?: return

    val converters = remember { Converters() }
    val subjects: List<SubjectScore> = converters.toSubjectList(result.subjectsJson)

    // Handle PDF Generation Ready Callback
    LaunchedEffect(pdfState) {
        if (pdfState is PdfUiState.Ready) {
            val file = (pdfState as PdfUiState.Ready).file
            FileHelper.openPdfFile(context, file)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Official Result Marksheet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = result.rollNumber,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val pdfFile = getExistingOrGeneratePdf(context, result)
                        FileHelper.sharePdfFile(context, pdfFile, result)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                    IconButton(onClick = {
                        val pdfFile = getExistingOrGeneratePdf(context, result)
                        FileHelper.printPdfFile(context, pdfFile, "Result_${result.rollNumber}")
                    }) {
                        Icon(Icons.Default.Print, contentDescription = "Print", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy900)
            )
        },
        bottomBar = {
            // Floating Download & Print Bar
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.generatePdf(context, result) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("download_pdf_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                    ) {
                        if (pdfState is PdfUiState.Generating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Official PDF...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("DOWNLOAD OFFICIAL PDF REPORT CARD", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val pdfFile = getExistingOrGeneratePdf(context, result)
                                FileHelper.printPdfFile(context, pdfFile, "Result_${result.rollNumber}")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Print Result", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                FileHelper.copyToClipboard(context, result.verificationHash, "Verification Code")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Hash", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateSurface)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Official Transcript Card Document
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Institution Header Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.university_banner_1785236368209),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                alpha = 0.25f
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Navy900.copy(alpha = 0.85f))
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = result.boardOrUniversity.uppercase(),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = result.institutionName,
                                    fontSize = 12.sp,
                                    color = GoldAccent,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "OFFICIAL STATEMENT OF MARKS • ${result.examTerm}",
                                    fontSize = 9.5.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // 2. Student Info Grid Box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BlueContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = result.studentName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                }

                                Divider(color = SlateBorder)

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        DetailPair("Roll Number", result.rollNumber)
                                        DetailPair("Registration No", result.registrationNumber)
                                        DetailPair("Course Program", result.courseOrProgram)
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        DetailPair("Academic Session", result.sessionYear)
                                        DetailPair("Branch / Major", result.branchOrStream)
                                        DetailPair("Publish Date", result.publishDate)
                                    }
                                }
                            }
                        }

                        // 3. Overall Result Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (result.overallStatus.contains("FAIL")) CrimsonContainer else EmeraldContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (result.overallStatus.contains("FAIL")) Icons.Default.CheckCircle else Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = if (result.overallStatus.contains("FAIL")) CrimsonFail else EmeraldPass,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = result.overallStatus,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (result.overallStatus.contains("FAIL")) CrimsonFail else EmeraldPass
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Division: ${result.division}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = String.format("%.2f", result.cgpa),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Navy900
                                    )
                                    Text(
                                        text = "CGPA / 4.00 (${String.format("%.1f%%", result.totalPercentage)})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SlateTextMuted
                                    )
                                }
                            }
                        }

                        // 4. Subject Wise Marks Table Header
                        Text(
                            text = "Subject Marks Breakdown",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )

                        // Table Headers
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Navy900, shape = RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("CODE", modifier = Modifier.weight(1.1f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("SUBJECT NAME", modifier = Modifier.weight(2.5f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("INT/EXT", modifier = Modifier.weight(1.3f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                            Text("TOTAL", modifier = Modifier.weight(1.1f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
                            Text("GRADE", modifier = Modifier.weight(1.0f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.End)
                        }

                        // Table Items
                        subjects.forEachIndexed { index, sub ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) Color.White else SlateSurface)
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(sub.code, modifier = Modifier.weight(1.1f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text(sub.name, modifier = Modifier.weight(2.5f), fontSize = 11.sp, color = Navy900)
                                Text("${sub.internalMarks}/${sub.externalMarks}", modifier = Modifier.weight(1.3f), fontSize = 11.sp, textAlign = TextAlign.Center, color = SlateTextMuted)
                                Text("${sub.totalMarks}/${sub.maxMarks}", modifier = Modifier.weight(1.1f), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text(
                                    text = sub.grade,
                                    modifier = Modifier.weight(1.0f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    color = if (sub.isPass) EmeraldPass else CrimsonFail
                                )
                            }
                            Divider(color = SlateBorder, thickness = 0.5.dp)
                        }

                        // 5. Verification Hash Stamp Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SlateSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SlateBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Official Security Verification Code",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900
                                    )
                                    Text(
                                        text = result.verificationHash,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = BluePrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                IconButton(onClick = {
                                    FileHelper.copyToClipboard(context, result.verificationHash, "Security Hash")
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SlateTextMuted)
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
fun DetailPair(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(text = label, fontSize = 10.sp, color = SlateTextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Navy900)
    }
}

private fun getExistingOrGeneratePdf(context: Context, result: StudentResult): File {
    if (!result.pdfUri.isNullOrEmpty()) {
        val f = File(result.pdfUri)
        if (f.exists()) return f
    }
    return com.example.util.PdfResultGenerator.generateResultPdf(context, result) ?: File(context.cacheDir, "result.pdf")
}
