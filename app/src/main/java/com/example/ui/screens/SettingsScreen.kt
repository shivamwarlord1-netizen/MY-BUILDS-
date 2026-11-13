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
import com.example.ui.theme.KinetiqBlack
import com.example.ui.theme.KinetiqBorderGlow
import com.example.ui.theme.KinetiqBorderSubtle
import com.example.ui.theme.KinetiqCardSurface
import com.example.ui.theme.KinetiqElevatedCard
import com.example.ui.theme.KinetiqObsidianSurface
import com.example.ui.theme.KinetiqPurpleDeep
import com.example.ui.theme.KinetiqPurpleLavender
import com.example.ui.theme.KinetiqPurpleNeon
import com.example.ui.theme.KinetiqPurplePrimary
import com.example.ui.theme.KinetiqTextMuted
import com.example.ui.theme.KinetiqTextSubtle
import com.example.ui.theme.KinetiqTextWhite
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
          color = KinetiqPurpleNeon
        )
        Text(
          text = if (profile.language == "hi") "सेटिंग्स और गोपनीयता" else "KinetiQ Settings",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Black,
            color = KinetiqTextWhite
          )
        )
        Text(
          text = if (profile.language == "hi") "व्यक्तिगत लक्ष्य, थीम और ऑन-डिवाइस डेटा नियंत्रण।" else "Personal telemetry targets, premium themes, and on-device data sovereignty.",
          style = MaterialTheme.typography.bodySmall,
          color = KinetiqTextMuted
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
                color = KinetiqTextWhite
              )
            )
            Text(
              text = "Steps: ${profile.targetSteps} • Water: ${profile.targetWaterMl}ml • Sleep: ${profile.targetSleepHours}h",
              style = MaterialTheme.typography.bodySmall,
              color = KinetiqTextMuted
            )
          }

          Button(
            onClick = { showEditProfileDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = KinetiqPurpleNeon,
              contentColor = KinetiqBlack
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
          color = KinetiqTextMuted
        )
        Spacer(modifier = Modifier.height(10.dp))
        androidx.compose.foundation.lazy.LazyRow(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val themes = listOf(
            "dark" to "Obsidian Purple",
            "ruby" to "Ruby Velvet (Red)",
            "system" to "System Auto",
            "light" to "Twilight Light"
          )
          items(themes) { (code, label) ->
            val isSelected = profile.darkModeSetting == code
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.updateUserProfile(profile.copy(darkModeSetting = code)) },
              label = {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  color = if (isSelected) KinetiqBlack else KinetiqTextWhite
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                containerColor = KinetiqObsidianSurface
              ),
              border = androidx.compose.foundation.BorderStroke(
                0.8.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else KinetiqBorderSubtle
              ),
              modifier = Modifier.bounceClick(scaleDown = 0.92f) {
                viewModel.updateUserProfile(profile.copy(darkModeSetting = code))
              }
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
          color = KinetiqTextMuted
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          val isEn = profile.language == "en"
          val isHi = profile.language == "hi"
          FilterChip(
            selected = isEn,
            onClick = { viewModel.updateUserProfile(profile.copy(language = "en")) },
            label = { Text("English", color = if (isEn) KinetiqBlack else KinetiqTextWhite) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = KinetiqPurpleNeon,
              containerColor = KinetiqObsidianSurface
            ),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, if (isEn) KinetiqPurpleNeon else KinetiqBorderSubtle)
          )
          FilterChip(
            selected = isHi,
            onClick = { viewModel.updateUserProfile(profile.copy(language = "hi")) },
            label = { Text("हिंदी (Hindi)", color = if (isHi) KinetiqBlack else KinetiqTextWhite) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = KinetiqPurpleNeon,
              containerColor = KinetiqObsidianSurface
            ),
            border = androidx.compose.foundation.BorderStroke(0.8.dp, if (isHi) KinetiqPurpleNeon else KinetiqBorderSubtle)
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
                color = KinetiqTextWhite
              )
            )
            Text(
              text = "On-device reminders for hydration sips and evening digital wind-down",
              style = MaterialTheme.typography.bodySmall,
              color = KinetiqTextMuted
            )
          }
          Switch(
            checked = profile.notificationsEnabled,
            onCheckedChange = { isEnabled ->
              viewModel.updateUserProfile(profile.copy(notificationsEnabled = isEnabled))
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = KinetiqBlack,
              checkedTrackColor = KinetiqPurpleNeon,
              uncheckedThumbColor = KinetiqTextMuted,
              uncheckedTrackColor = KinetiqObsidianSurface
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
          color = KinetiqTextMuted
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
            containerColor = KinetiqPurpleDeep,
            contentColor = KinetiqPurpleLavender
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
            contentColor = MaterialTheme.colorScheme.error
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
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
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { showPrivacyPolicyDialog = true }
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Privacy Policy & Zero-Cloud Guarantee", style = MaterialTheme.typography.bodyMedium, color = KinetiqTextWhite)
            Text("Read", color = KinetiqPurpleNeon, style = MaterialTheme.typography.labelMedium)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Software Version", style = MaterialTheme.typography.bodyMedium, color = KinetiqTextWhite)
            Text("KinetiQ 1.0.0 (Obsidian Dark)", color = KinetiqTextMuted, style = MaterialTheme.typography.labelMedium)
          }

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Cost Model", style = MaterialTheme.typography.bodyMedium, color = KinetiqTextWhite)
            Text("100% Free Forever • Zero Ads", color = KinetiqPurpleNeon, style = MaterialTheme.typography.labelMedium)
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
      containerColor = KinetiqElevatedCard,
      titleContentColor = KinetiqTextWhite,
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
            color = KinetiqTextMuted
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
            containerColor = KinetiqPurpleNeon,
            contentColor = KinetiqBlack
          )
        ) {
          Text("Copy to Clipboard", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Close", color = KinetiqTextMuted)
        }
      }
    )
  }

  // Delete Data Confirmation Dialog
  if (showDeleteConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirmDialog = false },
      containerColor = KinetiqElevatedCard,
      titleContentColor = MaterialTheme.colorScheme.error,
      title = { Text("Erase All Health Data?") },
      text = {
        Text(
          "This will irreversibly delete all recorded steps, hydration logs, sleep records, and habit completions from your local database. Your targets will reset to defaults.",
          color = KinetiqTextWhite
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.purgeData()
            showDeleteConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Confirm Erase")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirmDialog = false }) {
          Text("Cancel", color = KinetiqTextMuted)
        }
      }
    )
  }

  // Privacy Policy Dialog
  if (showPrivacyPolicyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyPolicyDialog = false },
      containerColor = KinetiqElevatedCard,
      titleContentColor = KinetiqTextWhite,
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
            color = KinetiqTextWhite,
            lineHeight = 18.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showPrivacyPolicyDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = KinetiqPurpleNeon, contentColor = KinetiqBlack)
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
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, KinetiqBorderSubtle, RoundedCornerShape(22.dp)),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(KinetiqPurplePrimary.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = KinetiqPurpleNeon,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = KinetiqTextWhite
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
    containerColor = KinetiqElevatedCard,
    titleContentColor = KinetiqTextWhite,
    textContentColor = KinetiqTextMuted,
    title = { Text("Customize Goals & Profile") },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text("Display Name:", color = KinetiqTextWhite)
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Daily Step Target:", color = KinetiqTextWhite)
        OutlinedTextField(
          value = steps,
          onValueChange = { steps = it.filter { c -> c.isDigit() } },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Daily Water Target (ml):", color = KinetiqTextWhite)
        OutlinedTextField(
          value = water,
          onValueChange = { water = it.filter { c -> c.isDigit() } },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text("Nightly Sleep Target (hours):", color = KinetiqTextWhite)
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
        colors = ButtonDefaults.buttonColors(containerColor = KinetiqPurpleNeon, contentColor = KinetiqBlack)
      ) {
        Text("Save Goals", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = KinetiqTextMuted)
      }
    }
  )
}
