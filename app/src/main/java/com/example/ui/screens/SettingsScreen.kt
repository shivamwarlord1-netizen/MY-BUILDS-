package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.bounceClick
import com.example.ui.theme.kinetiqColors
import com.example.ui.viewmodel.WellnessUiState
import com.example.ui.viewmodel.WellnessViewModel
import java.util.Locale

@Composable
fun SettingsScreen(
  uiState: WellnessUiState,
  viewModel: WellnessViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val profile = uiState.userProfile
  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors

  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showExportDialog by remember { mutableStateOf(false) }
  var exportedJsonText by remember { mutableStateOf("") }
  var showDeleteConfirmDialog by remember { mutableStateOf(false) }
  var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
  var showHealthConnectDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(
          text = "PREFERENCES & SOVEREIGNTY",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
          ),
          color = colorScheme.primary
        )
        Text(
          text = if (profile.language == "hi") "सेटिंग्स और गोपनीयता" else "KinetiQ Settings",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Black,
            color = colorScheme.onBackground
          )
        )
        Text(
          text = if (profile.language == "hi") "व्यक्तिगत लक्ष्य, थीम और ऑन-डिवाइस डेटा नियंत्रण।" else "Personal telemetry targets, premium themes, and on-device data sovereignty.",
          style = MaterialTheme.typography.bodySmall,
          color = colorScheme.onSurfaceVariant
        )
      }
    }

    // 1. Personal Profile & Targets
    item {
      SettingsSectionCard(
        title = if (profile.language == "hi") "व्यक्तिगत प्रोफ़ाइल और लक्ष्य" else "Personal Telemetry & Goals",
        icon = Icons.Default.Person
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            val safeName = if (profile.displayName.equals("Wellness Seeker", ignoreCase = true)) {
              "Athlete"
            } else {
              profile.displayName
            }
            Text(
              text = safeName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
              )
            )
            Text(
              text = "Steps: ${profile.targetSteps} • Water: ${profile.targetWaterMl}ml • Sleep: ${profile.targetSleepHours}h",
              style = MaterialTheme.typography.bodySmall,
              color = colorScheme.onSurfaceVariant
            )
          }

          Button(
            onClick = { showEditProfileDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = colorScheme.primary,
              contentColor = colorScheme.onPrimary
            ),
            modifier = Modifier
              .testTag("edit_targets_button")
              .bounceClick { showEditProfileDialog = true }
          ) {
            Text(if (profile.language == "hi") "संपादित करें" else "Edit", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 2. Premium Theme Palette Selection
    item {
      SettingsSectionCard(
        title = "App Theme & Visual Styling",
        icon = Icons.Default.Palette
      ) {
        Text(
          text = "Select your preferred visual aesthetic:",
          style = MaterialTheme.typography.bodySmall,
          color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val themes = listOf(
            "dark" to "Obsidian Purple",
            "ruby" to "Ruby Velvet (Red)",
            "simple" to "Simple",
            "light" to "Twilight Light",
            "system" to "System Auto"
          )
          items(themes) { (code, label) ->
            val isSelected = profile.darkModeSetting.equals(code, ignoreCase = true)
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.updateUserProfile(profile.copy(darkModeSetting = code)) },
              label = {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                  color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = colorScheme.primary,
                containerColor = semanticColors.surface1
              ),
              border = androidx.compose.foundation.BorderStroke(
                0.8.dp,
                if (isSelected) colorScheme.primary else colorScheme.outline
              ),
              modifier = Modifier.testTag("theme_chip_$code")
            )
          }
        }
      }
    }

    // 3. Language Selection
    item {
      SettingsSectionCard(
        title = if (profile.language == "hi") "भाषा चयन (Language)" else "Language Selection",
        icon = Icons.Default.Language
      ) {
        Text(
          text = if (profile.language == "hi") "अपनी पसंदीदा भाषा चुनें:" else "Choose companion language:",
          style = MaterialTheme.typography.bodySmall,
          color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          val isEn = profile.language == "en"
          val isHi = profile.language == "hi"
          FilterChip(
            selected = isEn,
            onClick = { viewModel.updateUserProfile(profile.copy(language = "en")) },
            label = { Text("English", color = if (isEn) colorScheme.onPrimary else colorScheme.onSurface) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = colorScheme.primary,
              containerColor = semanticColors.surface1
            ),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, if (isEn) colorScheme.primary else colorScheme.outline)
          )
          FilterChip(
            selected = isHi,
            onClick = { viewModel.updateUserProfile(profile.copy(language = "hi")) },
            label = { Text("हिंदी (Hindi)", color = if (isHi) colorScheme.onPrimary else colorScheme.onSurface) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = colorScheme.primary,
              containerColor = semanticColors.surface1
            ),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, if (isHi) colorScheme.primary else colorScheme.outline)
          )
        }
      }
    }

    // 4. Notifications & Cues
    item {
      SettingsSectionCard(
        title = if (profile.language == "hi") "दैनिक अनुस्मारक" else "Gentle Telemetry Reminders",
        icon = Icons.Default.Notifications
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = if (profile.language == "hi") "अनुस्मारक सक्षम करें" else "Enable Daytime Telemetry Cues",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
              )
            )
            Text(
              text = "On-device reminders for hydration sips and evening digital wind-down",
              style = MaterialTheme.typography.bodySmall,
              color = colorScheme.onSurfaceVariant
            )
          }
          Switch(
            checked = profile.notificationsEnabled,
            onCheckedChange = { isEnabled ->
              viewModel.updateUserProfile(profile.copy(notificationsEnabled = isEnabled))
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = colorScheme.onPrimary,
              checkedTrackColor = colorScheme.primary,
              uncheckedThumbColor = colorScheme.onSurfaceVariant,
              uncheckedTrackColor = semanticColors.surface1
            )
          )
        }
      }
    }

    // 5. Data Sovereignty: Export & Delete
    item {
      SettingsSectionCard(
        title = if (profile.language == "hi") "डेटा संप्रभुता और गोपनीयता" else "Data Sovereignty & Privacy",
        icon = Icons.Default.Lock
      ) {
        Text(
          text = "Your health records are stored strictly on this device in SQLite. No tracking, zero cloud telemetry, complete sovereignty.",
          style = MaterialTheme.typography.bodySmall,
          color = colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = {
            viewModel.exportData { json ->
              exportedJsonText = json
              showExportDialog = true
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("export_data_button")
            .bounceClick {
              viewModel.exportData { json ->
                exportedJsonText = json
                showExportDialog = true
              }
            },
          colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Export JSON", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
          onClick = { showDeleteConfirmDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("delete_data_button")
            .bounceClick { showDeleteConfirmDialog = true },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorScheme.error
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.error.copy(alpha = 0.5f))
        ) {
          Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(if (profile.language == "hi") "सभी डेटा हटाएं" else "Erase All Personal Health Logs")
        }
      }
    }

    // 6. About & Legal
    item {
      SettingsSectionCard(
        title = "About KinetiQ",
        icon = Icons.Default.Security
      ) {
        val activeThemeLabel = when (profile.darkModeSetting.lowercase(Locale.ROOT)) {
          "simple" -> "Simple"
          "ruby", "red" -> "Ruby Velvet"
          "light" -> "Twilight Light"
          "system" -> "System Auto"
          else -> "Obsidian Dark"
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showPrivacyPolicyDialog = true }
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Privacy Policy & Zero-Cloud Guarantee", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface)
            Text("Read", color = colorScheme.primary, style = MaterialTheme.typography.labelMedium)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Software Version", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface)
            Text("KinetiQ 1.0.0 ($activeThemeLabel)", color = colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Cost Model", style = MaterialTheme.typography.bodyMedium, color = colorScheme.onSurface)
            Text("100% Free Forever • Zero Ads", color = colorScheme.primary, style = MaterialTheme.typography.labelMedium)
          }
        }
      }
    }

    item {
      MedicalDisclaimerBanner()
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Edit Targets Dialog
  if (showEditProfileDialog) {
    EditProfileTargetsDialog(
      currentProfile = profile,
      onDismiss = { showEditProfileDialog = false },
      onSave = { updated ->
        viewModel.updateUserProfile(updated)
        showEditProfileDialog = false
      }
    )
  }

  // Export JSON Dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      containerColor = colorScheme.surfaceContainerHigh,
      titleContentColor = colorScheme.onSurface,
      title = { Text("Exported Telemetry (JSON)") },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = exportedJsonText,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            color = colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("KinetiQ Health Data", exportedJsonText)
            clipboard.setPrimaryClip(clip)
            showExportDialog = false
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
          )
        ) {
          Text("Copy to Clipboard", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close", color = colorScheme.onSurfaceVariant)
        }
      }
    )
  }

  // Delete Data Confirmation Dialog
  if (showDeleteConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmDialog = false },
      containerColor = colorScheme.surfaceContainerHigh,
      titleContentColor = colorScheme.error,
      title = { Text("Erase All Health Data?") },
      text = {
        Text(
          "This will irreversibly delete all recorded steps, hydration logs, sleep records, and habit completions from your local database. Your targets will reset to defaults.",
          color = colorScheme.onSurface
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.purgeData()
            showDeleteConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
        ) {
          Text("Confirm Erase")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmDialog = false }) {
          Text("Cancel", color = colorScheme.onSurfaceVariant)
        }
      }
    )
  }

  // Privacy Policy Dialog
  if (showPrivacyPolicyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyPolicyDialog = false },
      containerColor = colorScheme.surfaceContainerHigh,
      titleContentColor = colorScheme.onSurface,
      title = { Text("Privacy Policy & Medical Boundary") },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text(
            text = """
              1. Local-First Architecture:
              All data logged in KinetiQ (activity, hydration, sleep, habits) is stored exclusively in your device's local private SQLite Room database.

              2. Zero Cloud Transmissions:
              KinetiQ does not transmit personal health data, device identifiers, or usage logs to external cloud servers, advertisers, or analytics trackers.

              3. User Data Sovereignty:
              You may export your complete data history as formatted JSON at any time or completely erase all records in a single tap.

              4. Non-Medical Software Disclosure:
              KinetiQ is designed for general lifestyle wellness and personal habit cultivation. It is NOT a medical device, clinical decision support system, or medical diagnostic tool. Never rely on KinetiQ for urgent medical conditions. In emergencies, contact 112 / 911 or visit an emergency room.
            """.trimIndent(),
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSurface,
            lineHeight = 18.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showPrivacyPolicyDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary, contentColor = colorScheme.onPrimary)
        ) {
          Text("Understood", fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}

@Composable
private fun SettingsSectionCard(
  title: String,
  icon: ImageVector,
  content: @Composable () -> Unit
) {
  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, colorScheme.outline.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = semanticColors.surface1)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(colorScheme.primary.copy(alpha = 0.14f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
          )
        )
      }

      Spacer(modifier = Modifier.height(14.dp))
      content()
    }
  }
}

@Composable
private fun EditProfileTargetsDialog(
  currentProfile: UserProfile,
  onDismiss: () -> Unit,
  onSave: (UserProfile) -> Unit
) {
  val colorScheme = MaterialTheme.colorScheme
  val initialName = if (currentProfile.displayName.equals("Wellness Seeker", ignoreCase = true)) {
    "Athlete"
  } else {
    currentProfile.displayName
  }
  var name by remember { mutableStateOf(initialName) }
  var steps by remember { mutableStateOf(currentProfile.targetSteps.toString()) }
  var water by remember { mutableStateOf(currentProfile.targetWaterMl.toString()) }
  var sleepHours by remember { mutableStateOf(currentProfile.targetSleepHours.toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = colorScheme.surfaceContainerHigh,
    titleContentColor = colorScheme.onSurface,
    textContentColor = colorScheme.onSurfaceVariant,
    title = { Text("Customize Goals & Profile") },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text("Display Name:", color = colorScheme.onSurface)
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Daily Step Target:", color = colorScheme.onSurface)
        OutlinedTextField(
          value = steps,
          onValueChange = { steps = it.filter { c -> c.isDigit() } },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Daily Water Target (ml):", color = colorScheme.onSurface)
        OutlinedTextField(
          value = water,
          onValueChange = { water = it.filter { c -> c.isDigit() } },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Nightly Sleep Target (hours):", color = colorScheme.onSurface)
        OutlinedTextField(
          value = sleepHours,
          onValueChange = { sleepHours = it },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val s = steps.toIntOrNull() ?: 8000
          val w = water.toIntOrNull() ?: 2250
          val sl = sleepHours.toFloatOrNull() ?: 8.0f
          onSave(
            currentProfile.copy(
              displayName = if (name.isNotBlank()) name.trim() else "Friend",
              targetSteps = s.coerceIn(2000, 30000),
              targetWaterMl = w.coerceIn(1000, 6000),
              targetSleepHours = sl.coerceIn(5f, 12f)
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary, contentColor = colorScheme.onPrimary)
      ) {
        Text("Save Goals", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = colorScheme.onSurfaceVariant)
      }
    }
  )
}
