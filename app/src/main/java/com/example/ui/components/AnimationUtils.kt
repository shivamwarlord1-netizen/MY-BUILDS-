package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Adds a responsive, tactile spring bounce animation on press without hijacking
 * LazyColumn scroll gestures or hanging on quick taps.
 *
 * When [onClick] is provided, registers standard Compose [clickable] backed by a
 * shared [MutableInteractionSource] so drags/scrolls cancel cleanly and taps always fire.
 * When [onClick] is null (for components like FilterChip/Button with their own onClick),
 * use [pressBounce] with the component's own [MutableInteractionSource] instead.
 */
fun Modifier.bounceClick(
  scaleDown: Float = 0.94f,
  onClick: (() -> Unit)? = null
): Modifier = composed {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val haptic = LocalHapticFeedback.current

  val scale by animateFloatAsState(
    targetValue = if (isPressed) scaleDown else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "bounce_scale"
  )

  val base = this.scale(scale)
  if (onClick != null) {
    base.clickable(
      interactionSource = interactionSource,
      indication = LocalIndication.current
    ) {
      haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
      onClick()
    }
  } else {
    base
  }
}

/**
 * Scales a component smoothly when pressed using its existing [InteractionSource],
 * ideal for Material 3 FilterChip, AssistChip, Card, or Button composables.
 */
fun Modifier.pressBounce(
  interactionSource: InteractionSource,
  scaleDown: Float = 0.94f
): Modifier = composed {
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed) scaleDown else 1f,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioMediumBouncy,
      stiffness = Spring.StiffnessMediumLow
    ),
    label = "press_bounce_scale"
  )
  this.scale(scale)
}

/**
 * Creates a soft breathing pulse scale for mindfulness indicators and live status badges.
 */
@Composable
fun rememberBreathingScale(
  minScale: Float = 0.96f,
  maxScale: Float = 1.04f,
  durationMillis: Int = 3000
): Float {
  val infiniteTransition = rememberInfiniteTransition(label = "breathing_pulse")
  val scale by infiniteTransition.animateFloat(
    initialValue = minScale,
    targetValue = maxScale,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breathing_scale"
  )
  return scale
}
