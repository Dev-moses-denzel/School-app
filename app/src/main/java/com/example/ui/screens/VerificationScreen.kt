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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonFail
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPass
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextMuted
import com.example.ui.viewmodel.ResultViewModel

@Composable
fun VerificationScreen(viewModel: ResultViewModel) {
    var hashInput by remember { mutableStateOf("") }
    val verificationResult by viewModel.verificationResult.collectAsState()

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
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Official Result Verifier",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Authenticate downloaded marksheet PDF documents using the digital security QR hash code.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Enter Security Code / Hash",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )

                OutlinedTextField(
                    value = hashInput,
                    onValueChange = { hashInput = it },
                    placeholder = { Text("Paste Security Code or SHA Hash...") },
                    leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = BluePrimary) },
                    trailingIcon = {
                        if (hashInput.isNotEmpty()) {
                            IconButton(onClick = { hashInput = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("verify_hash_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Button(
                    onClick = { viewModel.verifyHash(hashInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("verify_hash_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VERIFY AUTHENTICITY", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (verificationResult != null) {
            val (isValid, message) = verificationResult!!

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isValid) EmeraldContainer else CrimsonContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint = if (isValid) EmeraldPass else CrimsonFail,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isValid) "AUTHENTIC RECORD CONFIRMED" else "VERIFICATION FAILED",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isValid) EmeraldPass else CrimsonFail
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
