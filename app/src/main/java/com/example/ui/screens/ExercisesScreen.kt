package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.exercise.Exercise
import com.example.data.exercise.ExerciseDatabase
import com.example.data.exercise.ExerciseTier
import com.example.data.exercise.MuscleGroup
import com.example.ui.components.AnimatedExerciseFigure
import com.example.ui.components.InteractiveAnatomyHeatmap
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.bounceClick
import com.example.ui.components.pressBounce
import com.example.ui.theme.kinetiqColors
import com.example.ui.viewmodel.WellnessViewModel

/**
 * KinetiQ Visual Exercise Catalog:
 * - Static crisp anatomical previews by default (NO auto-playing animations on load/scroll)
 * - Explicit user Play/Pause animation toggle + Setup/Peak pose preview
 * - Interactive Anatomy Heatmap overlay (Anterior/Posterior primary & synergist breakdown)
 * - Full-width responsive Inspection Modal with pro form cues and step logging
 */
@Composable
fun ExercisesScreen(
  viewModel: WellnessViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTier by remember { mutableStateOf(ExerciseTier.BEGINNER) }
  var selectedGroup by remember { mutableStateOf<MuscleGroup?>(null) }
  var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
  // User-initiated playback only: null means ALL cards remain calm/static until tapped
  var playingExerciseId by remember { mutableStateOf<String?>(null) }

  val beginnerCount = remember { ExerciseDatabase.getExercisesByTier(ExerciseTier.BEGINNER).size }
  val intermediateCount = remember { ExerciseDatabase.getExercisesByTier(ExerciseTier.INTERMEDIATE).size }

  val filteredExercises = remember(selectedTier, selectedGroup) {
    ExerciseDatabase.getExercisesByTierAndGroup(selectedTier, selectedGroup)
  }

  LaunchedEffect(selectedTier, filteredExercises) {
    if (playingExerciseId != null && filteredExercises.none { it.id == playingExerciseId }) {
      playingExerciseId = null
    }
  }

  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors
  val beginnerAccent = semanticColors.water
  val onBeginnerAccent = if (semanticColors.isLight) Color.White else semanticColors.background
  val tierAccent = if (selectedTier == ExerciseTier.BEGINNER) beginnerAccent else colorScheme.primary
  val onTierAccent = if (selectedTier == ExerciseTier.BEGINNER) onBeginnerAccent else colorScheme.onPrimary

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header
    item(key = "exercises_header") {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(tierAccent.copy(alpha = 0.18f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = tierAccent,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "WORKOUT MOVEMENTS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
              ),
              color = tierAccent
            )
          }
          Spacer(modifier = Modifier.height(3.dp))
          Text(
            text = "Exercise Demonstrations",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Black,
              color = colorScheme.onBackground
            )
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Live Exercise Counter Badge
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = colorScheme.surface,
          border = BorderStroke(0.8.dp, tierAccent.copy(alpha = 0.55f))
        ) {
          Text(
            text = "${filteredExercises.size} MOVES",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.8.sp
            ),
            color = tierAccent,
            modifier = Modifier
              .padding(horizontal = 9.dp, vertical = 6.dp)
              .testTag("exercises_move_count")
          )
        }
      }
    }

    // Dedicated Beginner & Intermediate Subtab Row
    item(key = "exercise_tier_subtabs") {
      val selectedTabIndex = selectedTier.ordinal
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surface,
        border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.55f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        TabRow(
          selectedTabIndex = selectedTabIndex,
          containerColor = Color.Transparent,
          contentColor = colorScheme.onSurface,
          divider = {},
          indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                  .tabIndicatorOffset(tabPositions[selectedTabIndex])
                  .padding(horizontal = 18.dp)
                  .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)),
                height = 3.dp,
                color = tierAccent
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("exercise_tier_tab_row")
        ) {
          val tabs = listOf(
            Triple(ExerciseTier.BEGINNER, beginnerCount, beginnerAccent),
            Triple(ExerciseTier.INTERMEDIATE, intermediateCount, colorScheme.primary)
          )
          tabs.forEach { (tier, count, tabColor) ->
            val isSelected = selectedTier == tier
            Tab(
              selected = isSelected,
              onClick = {
                if (selectedTier != tier) {
                  playingExerciseId = null
                  selectedTier = tier
                }
              },
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("exercise_tier_tab_${tier.name.lowercase()}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = if (tier == ExerciseTier.BEGINNER) Icons.Default.Shield else Icons.Default.TrendingUp,
                  contentDescription = null,
                  tint = if (isSelected) tabColor else colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = tier.displayName,
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    fontSize = 13.5.sp
                  ),
                  color = if (isSelected) colorScheme.onSurface else colorScheme.onSurfaceVariant,
                  maxLines = 1
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) tabColor.copy(alpha = 0.18f) else colorScheme.surfaceVariant,
                  border = BorderStroke(
                    0.8.dp,
                    if (isSelected) tabColor.copy(alpha = 0.55f) else colorScheme.outline.copy(alpha = 0.45f)
                  )
                ) {
                  Text(
                    text = "$count",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    ),
                    color = if (isSelected) tabColor else colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Distinct Space Banner for Beginner vs Intermediate
    item(key = "tier_space_banner_${selectedTier.name}") {
      ExerciseTierSpaceBanner(
        tier = selectedTier,
        accentColor = tierAccent
      )
    }

    // Muscle Group Filter Chips (scoped to active Beginner / Intermediate space)
    item(key = "muscle_group_filters") {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        item(key = "filter_all") {
          val isAllSelected = selectedGroup == null
          val interactionSource = remember { MutableInteractionSource() }
          FilterChip(
            selected = isAllSelected,
            onClick = { selectedGroup = null },
            interactionSource = interactionSource,
            label = {
              Text(
                text = "All",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAllSelected) onTierAccent else colorScheme.onSurface
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = tierAccent,
              containerColor = colorScheme.surface
            ),
            border = BorderStroke(
              width = 0.8.dp,
              color = if (isAllSelected) tierAccent else colorScheme.outline.copy(alpha = 0.7f)
            ),
            modifier = Modifier
              .testTag("filter_chip_all")
              .pressBounce(interactionSource, scaleDown = 0.94f)
          )
        }

        items(MuscleGroup.values(), key = { it.name }) { group ->
          val isSelected = selectedGroup == group
          val interactionSource = remember { MutableInteractionSource() }
          FilterChip(
            selected = isSelected,
            onClick = { selectedGroup = group },
            interactionSource = interactionSource,
            label = {
              Text(
                text = group.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) onTierAccent else colorScheme.onSurface
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = tierAccent,
              containerColor = colorScheme.surface
            ),
            border = BorderStroke(
              width = 0.8.dp,
              color = if (isSelected) tierAccent else colorScheme.outline.copy(alpha = 0.7f)
            ),
            modifier = Modifier
              .testTag("filter_chip_${group.name.lowercase()}")
              .pressBounce(interactionSource, scaleDown = 0.94f)
          )
        }
      }
    }

    if (filteredExercises.isEmpty() && selectedGroup != null) {
      item(key = "empty_tier_group_${selectedTier.name}_${selectedGroup?.name}") {
        EmptyTierGroupCard(
          currentTier = selectedTier,
          muscleGroup = selectedGroup!!,
          accentColor = tierAccent,
          onAccentColor = onTierAccent,
          onSwitchTier = { targetTier ->
            playingExerciseId = null
            selectedTier = targetTier
          },
          onResetGroup = { selectedGroup = null }
        )
      }
    } else {
      // Exercise Cards for Active Subtab
      items(filteredExercises, key = { "${selectedTier.name}_${it.id}" }) { exercise ->
        VisualLeapExerciseCard(
          exercise = exercise,
          accentColor = tierAccent,
          onAccentColor = onTierAccent,
          isPlaying = playingExerciseId == exercise.id,
          onTogglePlay = {
            playingExerciseId = if (playingExerciseId == exercise.id) null else exercise.id
          },
          onClick = {
            playingExerciseId = null
            selectedExercise = exercise
          }
        )
      }
    }

    item(key = "exercises_disclaimer") {
      MedicalDisclaimerBanner()
      Spacer(modifier = Modifier.height(16.dp))
    }
  }

  // Visual Technique Inspection Modal
  if (selectedExercise != null) {
    VisualExerciseInspectionModal(
      exercise = selectedExercise!!,
      accentColor = tierAccent,
      onAccentColor = onTierAccent,
      onDismiss = { selectedExercise = null },
      onLogActivity = {
        viewModel.logSteps(800)
        selectedExercise = null
      }
    )
  }
}

@Composable
private fun ExerciseTierSpaceBanner(
  tier: ExerciseTier,
  accentColor: Color
) {
  val colorScheme = MaterialTheme.colorScheme
  val isBeginner = tier == ExerciseTier.BEGINNER

  val badgeLabel = if (isBeginner) "FOUNDATION SPACE" else "PROGRESSION SPACE"
  val title = if (isBeginner) {
    "Form First & Joint-Friendly Control"
  } else {
    "Compound Strength & Core Power"
  }
  val subtitle = if (isBeginner) {
    "Guided cable and isolation movements designed to lock in posture, mind-muscle connection, and smooth tempo."
  } else {
    "Loaded multi-joint lifts and athletic core builders for progressive overload, including 2 advanced barbell challenges."
  }
  val cues = if (isBeginner) {
    listOf("Tempo 2-1-2", "Rest 60–90s", "Moderate Load")
  } else {
    listOf("Tempo 3-1-1", "Rest 90–120s", "Progressive Load")
  }

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = colorScheme.surfaceVariant,
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.42f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("exercise_space_banner_${tier.name.lowercase()}")
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              accentColor.copy(alpha = 0.14f),
              Color.Transparent
            )
          )
        )
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = accentColor.copy(alpha = 0.16f),
            border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isBeginner) Icons.Default.Shield else Icons.Default.TrendingUp,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = badgeLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp,
                  letterSpacing = 0.9.sp
                ),
                color = accentColor
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.5.sp
          ),
          color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 12.sp,
            lineHeight = 17.sp
          ),
          color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          cues.forEach { cue ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = colorScheme.surface,
              border = BorderStroke(0.8.dp, colorScheme.outline.copy(alpha = 0.45f))
            ) {
              Text(
                text = cue,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 10.sp
                ),
                color = colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun EmptyTierGroupCard(
  currentTier: ExerciseTier,
  muscleGroup: MuscleGroup,
  accentColor: Color,
  onAccentColor: Color,
  onSwitchTier: (ExerciseTier) -> Unit,
  onResetGroup: () -> Unit
) {
  val otherTier = if (currentTier == ExerciseTier.BEGINNER) {
    ExerciseTier.INTERMEDIATE
  } else {
    ExerciseTier.BEGINNER
  }
  val otherCount = remember(otherTier, muscleGroup) {
    ExerciseDatabase.getExercisesByTierAndGroup(otherTier, muscleGroup).size
  }
  val colorScheme = MaterialTheme.colorScheme

  Surface(
    shape = RoundedCornerShape(18.dp),
    color = colorScheme.surfaceVariant,
    border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.6f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("empty_tier_group_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(
        text = "No ${currentTier.displayName} ${muscleGroup.displayName} Moves",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (otherCount > 0) {
          "All $otherCount ${muscleGroup.displayName} movements are organized inside the ${otherTier.displayName} space."
        } else {
          "Try resetting the muscle group filter to explore all ${currentTier.displayName} exercises."
        },
        style = MaterialTheme.typography.bodySmall,
        color = colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(12.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (otherCount > 0) {
          Button(
            onClick = { onSwitchTier(otherTier) },
            colors = ButtonDefaults.buttonColors(
              containerColor = accentColor,
              contentColor = onAccentColor
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .heightIn(min = 48.dp)
              .testTag("switch_to_${otherTier.name.lowercase()}_button")
          ) {
            Text(
              text = "Open ${otherTier.displayName} (${otherCount})",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
        OutlinedButton(
          onClick = onResetGroup,
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, colorScheme.outline.copy(alpha = 0.6f)),
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Text(
            text = "Show All",
            color = colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

/**
 * Visual Exercise Card with calm non-auto-playing human demonstration,
 * explicit Play/Pause + Setup/Peak static pose toggles, and Muscle Heatmap overlay.
 */
@Composable
private fun VisualLeapExerciseCard(
  exercise: Exercise,
  accentColor: Color,
  onAccentColor: Color,
  isPlaying: Boolean,
  onTogglePlay: () -> Unit,
  onClick: () -> Unit
) {
  var showHeatmapOverlay by remember(exercise.id) { mutableStateOf(false) }
  var previewPeakPose by remember(exercise.id) { mutableStateOf(false) }

  val colorScheme = MaterialTheme.colorScheme
  val semanticColors = MaterialTheme.kinetiqColors
  val pillBg = colorScheme.surfaceVariant.copy(alpha = 0.96f)
  val borderSubtle = colorScheme.outline.copy(alpha = 0.72f)
  val isAdvanced = exercise.difficulty.equals("Advanced", ignoreCase = true)
  val difficultyBadgeColor = if (isAdvanced) semanticColors.habits else accentColor

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("exercise_card_${exercise.id}")
      .border(1.dp, borderSubtle, RoundedCornerShape(20.dp))
      .bounceClick(scaleDown = 0.985f) { onClick() },
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Visual Stage: Human Anatomical Demo OR Muscle Heatmap Overlay
      Box(modifier = Modifier.fillMaxWidth()) {
        if (!showHeatmapOverlay) {
          AnimatedExerciseFigure(
            exerciseId = exercise.id,
            height = 176.dp,
            primaryColor = accentColor,
            isPlaying = isPlaying,
            staticPhase = if (previewPeakPose) 1f else 0f
          )

          // Top-left playback & pose preview controls
          Row(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(9.dp),
              color = if (isPlaying) accentColor else pillBg,
              border = BorderStroke(
                width = 0.8.dp,
                color = if (isPlaying) accentColor else borderSubtle
              ),
              modifier = Modifier
                .heightIn(min = 32.dp)
                .clip(RoundedCornerShape(9.dp))
                .clickable { onTogglePlay() }
                .testTag("play_toggle_${exercise.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlaying) "Pause ${exercise.name}" else "Animate ${exercise.name}",
                  tint = if (isPlaying) onAccentColor else accentColor,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (isPlaying) "PAUSE" else "ANIMATE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    letterSpacing = 0.6.sp
                  ),
                  color = if (isPlaying) onAccentColor else colorScheme.onSurface
                )
              }
            }

            if (!isPlaying) {
              Surface(
                shape = RoundedCornerShape(9.dp),
                color = pillBg,
                border = BorderStroke(
                  width = 0.8.dp,
                  color = if (previewPeakPose) accentColor.copy(alpha = 0.85f) else borderSubtle
                ),
                modifier = Modifier
                  .heightIn(min = 32.dp)
                  .clip(RoundedCornerShape(9.dp))
                  .clickable { previewPeakPose = !previewPeakPose }
                  .testTag("phase_toggle_${exercise.id}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = if (previewPeakPose) "View setup pose" else "View peak squeeze pose",
                    tint = if (previewPeakPose) accentColor else colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = if (previewPeakPose) "PEAK" else "SETUP",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 8.5.sp,
                      letterSpacing = 0.5.sp
                    ),
                    color = if (previewPeakPose) accentColor else colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        } else {
          InteractiveAnatomyHeatmap(
            exercise = exercise,
            height = 176.dp,
            primaryColor = accentColor,
            secondaryColor = colorScheme.tertiary
          )
        }

        // Top-right Overlay Switcher Pill (Pose Demo <-> Muscle Heatmap)
        Surface(
          shape = RoundedCornerShape(9.dp),
          color = if (showHeatmapOverlay) accentColor else pillBg,
          border = BorderStroke(
            width = 0.8.dp,
            color = if (showHeatmapOverlay) accentColor else borderSubtle
          ),
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .heightIn(min = 32.dp)
            .clip(RoundedCornerShape(9.dp))
            .clickable {
              if (!showHeatmapOverlay && isPlaying) {
                onTogglePlay()
              }
              showHeatmapOverlay = !showHeatmapOverlay
            }
            .testTag("heatmap_toggle_${exercise.id}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (showHeatmapOverlay) Icons.Default.FitnessCenter else Icons.Default.AccessibilityNew,
              contentDescription = if (showHeatmapOverlay) "Show exercise pose" else "Show muscle heatmap",
              tint = if (showHeatmapOverlay) onAccentColor else colorScheme.onSurface,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (showHeatmapOverlay) "POSE" else "HEATMAP",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.6.sp
              ),
              color = if (showHeatmapOverlay) onAccentColor else colorScheme.onSurface
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Title & Primary Target + Sets & Reps Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = exercise.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              fontSize = 16.5.sp
            ),
            color = colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "TARGET: ${exercise.primaryMuscle.uppercase()}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              letterSpacing = 0.7.sp
            ),
            color = accentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Sets & Reps Pill
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = accentColor.copy(alpha = 0.14f),
          border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = null,
              tint = accentColor,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = exercise.recommendedSetsReps,
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              ),
              color = accentColor
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. Compact Meta Badges + Open Details Affordance
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = colorScheme.surface
          ) {
            Text(
              text = exercise.equipment,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isAdvanced) difficultyBadgeColor.copy(alpha = 0.16f) else colorScheme.surface,
            border = if (isAdvanced) BorderStroke(0.8.dp, difficultyBadgeColor.copy(alpha = 0.5f)) else null
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = difficultyBadgeColor,
                modifier = Modifier.size(11.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = exercise.difficulty,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = if (isAdvanced) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isAdvanced) difficultyBadgeColor else colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Guide",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = accentColor
            )
          )
          Spacer(modifier = Modifier.width(2.dp))
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }
  }
}

/**
 * Spacious Full-Width Inspection Dialog:
 * - User-controlled Play/Pause, Setup/Peak inspection, 0.5x/1.0x speed, and Muscle Heatmap tab
 * - Clean non-overlapping controls below the stage
 * - Top execution cues, breathing cadence, mistakes to avoid, and 48.dp-tall action footer
 */
@Composable
private fun VisualExerciseInspectionModal(
  exercise: Exercise,
  accentColor: Color,
  onAccentColor: Color,
  onDismiss: () -> Unit,
  onLogActivity: () -> Unit
) {
  var isPlaying by remember(exercise.id) { mutableStateOf(false) }
  var previewPeakPose by remember(exercise.id) { mutableStateOf(false) }
  var isSlowMotion by remember(exercise.id) { mutableStateOf(false) }
  var showModalHeatmap by remember(exercise.id) { mutableStateOf(false) }

  val colorScheme = MaterialTheme.colorScheme
  val borderSubtle = colorScheme.outline.copy(alpha = 0.65f)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .widthIn(max = 560.dp)
        .heightIn(max = 720.dp)
        .border(1.dp, borderSubtle, RoundedCornerShape(24.dp)),
      shape = RoundedCornerShape(24.dp),
      color = colorScheme.surfaceVariant,
      tonalElevation = 6.dp
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Modal Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = exercise.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = colorScheme.onSurface
              ),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${exercise.muscleGroup.displayName.uppercase()} • ${exercise.recommendedSetsReps}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = accentColor
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(48.dp)
              .testTag("modal_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close exercise details",
              tint = colorScheme.onSurfaceVariant
            )
          }
        }

        HorizontalDivider(color = borderSubtle.copy(alpha = 0.5f))

        // Scrollable Modal Body
        Column(
          modifier = Modifier
            .weight(1f, fill = false)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
          // Tab Switcher: Movement Demo vs Muscle Heatmap
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (!showModalHeatmap) accentColor else colorScheme.surface,
              border = BorderStroke(0.8.dp, if (!showModalHeatmap) accentColor else borderSubtle),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 42.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { showModalHeatmap = false }
                .testTag("modal_tab_demo")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.FitnessCenter,
                  contentDescription = null,
                  tint = if (!showModalHeatmap) onAccentColor else colorScheme.onSurface,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Movement Demo",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (!showModalHeatmap) onAccentColor else colorScheme.onSurface
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (showModalHeatmap) accentColor else colorScheme.surface,
              border = BorderStroke(0.8.dp, if (showModalHeatmap) accentColor else borderSubtle),
              modifier = Modifier
                .weight(1f)
                .heightIn(min = 42.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  isPlaying = false
                  showModalHeatmap = true
                }
                .testTag("modal_tab_heatmap")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.AccessibilityNew,
                  contentDescription = null,
                  tint = if (showModalHeatmap) onAccentColor else colorScheme.onSurface,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Muscle Heatmap",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (showModalHeatmap) onAccentColor else colorScheme.onSurface
                )
              }
            }
          }

          if (!showModalHeatmap) {
            AnimatedExerciseFigure(
              exerciseId = exercise.id,
              height = 204.dp,
              primaryColor = accentColor,
              isPlaying = isPlaying,
              staticPhase = if (previewPeakPose) 1f else 0f,
              speedMultiplier = if (isSlowMotion) 0.5f else 1.0f
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Control Bar directly below stage (zero canvas overlap)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isPlaying) accentColor else colorScheme.surface,
                border = BorderStroke(0.9.dp, if (isPlaying) accentColor else borderSubtle),
                modifier = Modifier
                  .weight(1f)
                  .heightIn(min = 40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { isPlaying = !isPlaying }
                  .testTag("modal_play_toggle")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause animation" else "Play animation",
                    tint = if (isPlaying) onAccentColor else accentColor,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(5.dp))
                  Text(
                    text = if (isPlaying) "Pause Demo" else "Play Demo",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isPlaying) onAccentColor else colorScheme.onSurface
                  )
                }
              }

              if (!isPlaying) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = colorScheme.surface,
                  border = BorderStroke(
                    0.9.dp,
                    if (previewPeakPose) accentColor else borderSubtle
                  ),
                  modifier = Modifier
                    .heightIn(min = 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { previewPeakPose = !previewPeakPose }
                    .testTag("modal_phase_toggle")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.SwapHoriz,
                      contentDescription = "Toggle Setup or Peak pose",
                      tint = accentColor,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = if (previewPeakPose) "Peak" else "Setup",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = colorScheme.onSurface
                    )
                  }
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSlowMotion) accentColor.copy(alpha = 0.16f) else colorScheme.surface,
                border = BorderStroke(
                  0.9.dp,
                  if (isSlowMotion) accentColor else borderSubtle
                ),
                modifier = Modifier
                  .heightIn(min = 40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { isSlowMotion = !isSlowMotion }
                  .testTag("modal_speed_toggle")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.SlowMotionVideo,
                    contentDescription = "Toggle playback speed",
                    tint = if (isSlowMotion) accentColor else colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = if (isSlowMotion) "0.5×" else "1.0×",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSlowMotion) accentColor else colorScheme.onSurface
                  )
                }
              }
            }
          } else {
            InteractiveAnatomyHeatmap(
              exercise = exercise,
              height = 204.dp,
              primaryColor = accentColor,
              secondaryColor = colorScheme.tertiary
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Pro Execution Cues
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = colorScheme.surface,
            border = BorderStroke(0.8.dp, borderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "PRO EXECUTION CUES",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.5.sp,
                  letterSpacing = 1.sp
                ),
                color = accentColor
              )
              Spacer(modifier = Modifier.height(8.dp))

              exercise.executionSteps.take(3).forEach { cue ->
                Row(
                  modifier = Modifier.padding(vertical = 3.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Box(
                    modifier = Modifier
                      .padding(top = 5.dp)
                      .size(5.dp)
                      .background(accentColor, CircleShape)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = cue,
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Medium,
                      fontSize = 12.5.sp,
                      lineHeight = 17.sp
                    ),
                    color = colorScheme.onSurface
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Breathing Cadence Pill
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = colorScheme.surface,
            border = BorderStroke(0.8.dp, borderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Air,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = exercise.breathingPattern,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                ),
                color = colorScheme.onSurfaceVariant
              )
            }
          }

          if (exercise.commonMistakes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = colorScheme.surface,
              border = BorderStroke(0.8.dp, colorScheme.tertiary.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = colorScheme.tertiary,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "AVOID COMMON MISTAKES",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp,
                      letterSpacing = 0.8.sp
                    ),
                    color = colorScheme.tertiary
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                exercise.commonMistakes.take(2).forEach { mistake ->
                  Text(
                    text = "• $mistake",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 11.5.sp,
                      lineHeight = 16.sp
                    ),
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                  )
                }
              }
            }
          }
        }

        HorizontalDivider(color = borderSubtle.copy(alpha = 0.5f))

        // Sticky Modal Footer with 48.dp touch targets
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, borderSubtle),
            modifier = Modifier.heightIn(min = 48.dp)
          ) {
            Text(
              text = "Back",
              color = colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )
          }

          Button(
            onClick = onLogActivity,
            colors = ButtonDefaults.buttonColors(
              containerColor = accentColor,
              contentColor = onAccentColor
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
              .testTag("modal_complete_button")
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Completed (+800 steps)",
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
    }
  }
}
