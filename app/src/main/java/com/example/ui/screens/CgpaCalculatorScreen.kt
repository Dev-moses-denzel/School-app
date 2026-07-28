package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import kotlin.math.roundToInt

@Composable
fun CgpaCalculatorScreen() {
    var completedCreditsInput by remember { mutableStateOf("60") }
    var currentCgpaInput by remember { mutableStateOf("3.80") }
    var newTermCreditsInput by remember { mutableStateOf("18") }
    var expectedGpaInput by remember { mutableStateOf("3.90") }

    var calculatedCgpa by remember { mutableStateOf<Double?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateSurface)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy900)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BluePrimary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "GPA & CGPA Target Predictor",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Estimate cumulative CGPA based on future target term grades.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Current Academic Record", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = completedCreditsInput,
                        onValueChange = { completedCreditsInput = it },
                        label = { Text("Completed Credits") },
                        modifier = Modifier.weight(1f).testTag("completed_credits_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = currentCgpaInput,
                        onValueChange = { currentCgpaInput = it },
                        label = { Text("Current CGPA") },
                        modifier = Modifier.weight(1f).testTag("current_cgpa_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Upcoming / Target Term", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy900)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTermCreditsInput,
                        onValueChange = { newTermCreditsInput = it },
                        label = { Text("Term Credits") },
                        modifier = Modifier.weight(1f).testTag("new_term_credits_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = expectedGpaInput,
                        onValueChange = { expectedGpaInput = it },
                        label = { Text("Target Term GPA") },
                        modifier = Modifier.weight(1f).testTag("expected_gpa_input"),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Button(
                    onClick = {
                        val prevCredits = completedCreditsInput.toDoubleOrNull() ?: 0.0
                        val prevCgpa = currentCgpaInput.toDoubleOrNull() ?: 0.0
                        val termCredits = newTermCreditsInput.toDoubleOrNull() ?: 0.0
                        val termGpa = expectedGpaInput.toDoubleOrNull() ?: 0.0

                        val totalCredits = prevCredits + termCredits
                        if (totalCredits > 0) {
                            val totalPoints = (prevCredits * prevCgpa) + (termCredits * termGpa)
                            calculatedCgpa = (totalPoints / totalCredits * 100).roundToInt() / 100.0
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("calculate_cgpa_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.Functions, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CALCULATE NEW CGPA", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (calculatedCgpa != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BlueContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "ESTIMATED CUMULATIVE CGPA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.2f / 4.00", calculatedCgpa),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = BluePrimary
                    )
                }
            }
        }
    }
}
