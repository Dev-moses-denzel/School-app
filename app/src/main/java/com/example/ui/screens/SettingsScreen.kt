package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.ColorBlindMode
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.getFailColor
import com.example.ui.theme.getFailContainerColor
import com.example.ui.theme.getPassColor
import com.example.ui.theme.getPassContainerColor
import com.example.ui.viewmodel.ResultViewModel

data class AvatarPreset(
    val id: String,
    val title: String,
    val url: String
)

val PRESET_AVATARS = listOf(
    AvatarPreset("1", "Scholar Student", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"),
    AvatarPreset("2", "Creative Student", "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150"),
    AvatarPreset("3", "Teacher Educator", "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150"),
    AvatarPreset("4", "STEM Researcher", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150"),
    AvatarPreset("5", "Graduate Student", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150")
)

@Composable
fun SettingsScreen(
    viewModel: ResultViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()

    var customPhotoUrlInput by remember(settings.profilePictureUrl) { mutableStateOf(settings.profilePictureUrl) }
    var editNameInput by remember(settings.userName) { mutableStateOf(settings.userName) }

    // Launcher to pick photo from local device gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it.toString())
            Toast.makeText(context, "Profile picture updated from device gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HEADER HERO BANNER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Navy900),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(76.dp),
                            border = BorderStroke(3.dp, GoldAccent)
                        ) {
                            if (settings.profilePictureUrl.isNotBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(settings.profilePictureUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "User Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Navy900,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = GoldAccent,
                            modifier = Modifier
                                .size(26.dp)
                                .clickable { galleryLauncher.launch("image/*") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change photo",
                                tint = Navy900,
                                modifier = Modifier.padding(5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = settings.userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Theme: ${settings.themeMode.name} | Vision: ${settings.colorBlindMode.name}",
                            fontSize = 11.5.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Customize themes, dark mode & color blind options below.",
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // 2. PROFILE PICTURE & AVATARS CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Profile Picture & User Info", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = {
                            editNameInput = it
                            viewModel.updateUserName(it)
                        },
                        label = { Text("Your Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text("Choose an Avatar Preset:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(PRESET_AVATARS) { preset ->
                            val isSelected = settings.profilePictureUrl == preset.url
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    viewModel.updateProfilePicture(preset.url)
                                    Toast.makeText(context, "Selected ${preset.title}", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    modifier = Modifier.size(56.dp),
                                    border = BorderStroke(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) BluePrimary else SlateBorder
                                    )
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(preset.url)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = preset.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_profile_photo_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PICK FROM DEVICE", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = customPhotoUrlInput,
                        onValueChange = {
                            customPhotoUrlInput = it
                        },
                        label = { Text("Or Paste Image Link (URL)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (customPhotoUrlInput.isNotBlank()) {
                                    viewModel.updateProfilePicture(customPhotoUrlInput)
                                    Toast.makeText(context, "Profile Picture URL updated!", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Apply URL", tint = BluePrimary)
                            }
                        }
                    )
                }
            }
        }

        // 3. APP THEME CUSTOMIZATION CARD (LIGHT vs DARK vs SYSTEM)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Canvas Theme", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Text(
                        text = "Don't like white themes? Easily switch to Dark Mode or System Theme.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Light Option
                        val isLightSelected = settings.themeMode == ThemeMode.LIGHT
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateThemeMode(ThemeMode.LIGHT) }
                                .testTag("theme_option_light"),
                            shape = RoundedCornerShape(12.dp),
                            color = SlateSurface,
                            border = BorderStroke(
                                width = if (isLightSelected) 2.dp else 1.dp,
                                color = if (isLightSelected) BluePrimary else SlateBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = if (isLightSelected) BluePrimary else SlateTextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Light Theme", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateTextDark)
                                Text("Clean White", fontSize = 10.sp, color = SlateTextMuted)
                            }
                        }

                        // Dark Option
                        val isDarkSelected = settings.themeMode == ThemeMode.DARK
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateThemeMode(ThemeMode.DARK) }
                                .testTag("theme_option_dark"),
                            shape = RoundedCornerShape(12.dp),
                            color = Navy900,
                            border = BorderStroke(
                                width = if (isDarkSelected) 2.dp else 1.dp,
                                color = if (isDarkSelected) GoldAccent else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = GoldAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Dark Theme", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Navy Canvas", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        // System Option
                        val isSystemSelected = settings.themeMode == ThemeMode.SYSTEM
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateThemeMode(ThemeMode.SYSTEM) }
                                .testTag("theme_option_system"),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                width = if (isSystemSelected) 2.dp else 1.dp,
                                color = if (isSystemSelected) BluePrimary else SlateBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.SettingsSuggest,
                                    contentDescription = null,
                                    tint = if (isSystemSelected) BluePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("System", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Auto Device", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // 4. COLOR BLIND & ACCESSIBILITY MODE CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = GoldAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Color Blind & Vision Accessibility", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Text(
                        text = "Select a specialized palette designed for Red-Green color deficiency (Deuteranopia/Protanopia) or extreme high contrast.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ColorBlindOptionRow(
                            title = "Standard Colors",
                            description = "Default Emerald Green (Pass) & Crimson Red (Fail) indicators.",
                            isSelected = settings.colorBlindMode == ColorBlindMode.NONE,
                            onClick = { viewModel.updateColorBlindMode(ColorBlindMode.NONE) },
                            testTag = "cb_mode_none"
                        )

                        ColorBlindOptionRow(
                            title = "Deuteranopia Friendly (Red-Green)",
                            description = "Uses Cobalt Blue (Pass) & Deep Purple (Fail) with clear shape symbols.",
                            isSelected = settings.colorBlindMode == ColorBlindMode.DEUTERANOPIA,
                            onClick = { viewModel.updateColorBlindMode(ColorBlindMode.DEUTERANOPIA) },
                            testTag = "cb_mode_deuteranopia"
                        )

                        ColorBlindOptionRow(
                            title = "Protanopia Friendly",
                            description = "Uses Cyan Blue (Pass) & Deep Rose (Fail) for distinct color perception.",
                            isSelected = settings.colorBlindMode == ColorBlindMode.PROTANOPIA,
                            onClick = { viewModel.updateColorBlindMode(ColorBlindMode.PROTANOPIA) },
                            testTag = "cb_mode_protanopia"
                        )

                        ColorBlindOptionRow(
                            title = "High Contrast Mode",
                            description = "Maximized black background & vivid yellow accents for low vision.",
                            isSelected = settings.colorBlindMode == ColorBlindMode.HIGH_CONTRAST,
                            onClick = { viewModel.updateColorBlindMode(ColorBlindMode.HIGH_CONTRAST) },
                            testTag = "cb_mode_high_contrast"
                        )
                    }
                }
            }
        }

        // 5. LIVE VISUAL PREVIEW CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Live Palette & Status Preview", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Text(
                        text = "Here is how grades, passing marks, and status badges appear with your active theme and vision settings:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val passColor = getPassColor(settings.colorBlindMode)
                    val passBg = getPassContainerColor(settings.colorBlindMode)
                    val failColor = getFailColor(settings.colorBlindMode)
                    val failBg = getFailContainerColor(settings.colorBlindMode)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pass Badge Preview
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = passBg
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = passColor)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("PASSED / EE", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = passColor)
                                Text("Score: 88%", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        // Fail Badge Preview
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = failBg
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = failColor)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("ATTENTION / BE", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = failColor)
                                Text("Needs Support", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorBlindOptionRow(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BlueContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BluePrimary else SlateBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(22.dp)
            ) {
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.padding(3.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) BluePrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
