package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.education.EducationLibrary
import com.example.data.education.HealthArticle
import com.example.ui.components.MedicalDisclaimerBanner
import com.example.ui.components.bounceClick
import com.example.ui.theme.KinetiqBlack
import com.example.ui.theme.KinetiqBorderGlow
import com.example.ui.theme.KinetiqBorderSubtle
import com.example.ui.theme.KinetiqCardSurface
import com.example.ui.theme.KinetiqElevatedCard
import com.example.ui.theme.KinetiqObsidianSurface
import com.example.ui.theme.KinetiqPurpleLavender
import com.example.ui.theme.KinetiqPurpleNeon
import com.example.ui.theme.KinetiqPurplePrimary
import com.example.ui.theme.KinetiqTextMuted
import com.example.ui.theme.KinetiqTextSubtle
import com.example.ui.theme.KinetiqTextWhite

@Composable
fun LearnScreen(modifier: Modifier = Modifier) {
  var selectedCategory by remember { mutableStateOf("All") }
  var selectedArticle by remember { mutableStateOf<HealthArticle?>(null) }

  val categories = EducationLibrary.getCategories()
  val filteredArticles = EducationLibrary.getArticlesByCategory(selectedCategory)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .background(KinetiqPurplePrimary.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.MenuBook,
            contentDescription = null,
            tint = KinetiqPurpleNeon
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "KinetiQ Health Library",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              color = KinetiqTextWhite
            )
          )
          Text(
            text = "Evidence-based clinical guidelines reviewed against authoritative sources.",
            style = MaterialTheme.typography.bodySmall,
            color = KinetiqTextMuted
          )
        }
      }
    }

    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { cat ->
          val isSelected = selectedCategory.equals(cat, ignoreCase = true)
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = cat },
            label = { Text(cat, fontSize = 12.sp, color = if (isSelected) KinetiqBlack else KinetiqTextWhite) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = KinetiqPurpleNeon,
              containerColor = KinetiqObsidianSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
              0.8.dp,
              if (isSelected) KinetiqPurpleNeon else KinetiqBorderSubtle
            ),
            modifier = Modifier.bounceClick(scaleDown = 0.92f) { selectedCategory = cat }
          )
        }
      }
    }

    items(filteredArticles) { article ->
      ArticleCard(
        article = article,
        onClick = { selectedArticle = article }
      )
    }

    item {
      MedicalDisclaimerBanner()
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Article Reader Modal
  if (selectedArticle != null) {
    ArticleReaderDialog(
      article = selectedArticle!!,
      onDismiss = { selectedArticle = null }
    )
  }
}

@Composable
private fun ArticleCard(
  article: HealthArticle,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, KinetiqBorderSubtle, RoundedCornerShape(22.dp))
      .bounceClick(scaleDown = 0.97f) { onClick() },
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = KinetiqCardSurface)
  ) {
    Column(modifier = Modifier.padding(20.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = KinetiqObsidianSurface,
          border = androidx.compose.foundation.BorderStroke(0.8.dp, KinetiqBorderSubtle)
        ) {
          Text(
            text = article.category.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              fontSize = 9.sp
            ),
            color = KinetiqPurpleNeon,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = KinetiqTextSubtle,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${article.readTimeMinutes} min read",
            style = MaterialTheme.typography.labelSmall,
            color = KinetiqTextSubtle
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = article.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = KinetiqTextWhite
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = article.summary,
        style = MaterialTheme.typography.bodySmall,
        color = KinetiqTextMuted,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Verified,
          contentDescription = null,
          tint = KinetiqPurpleNeon,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Clinical Review: ${article.lastReviewedDate} • ${article.sourceOrg}",
          style = MaterialTheme.typography.labelSmall,
          color = KinetiqTextSubtle,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
private fun ArticleReaderDialog(
  article: HealthArticle,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = KinetiqElevatedCard,
    titleContentColor = KinetiqTextWhite,
    textContentColor = KinetiqTextWhite,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = article.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = KinetiqTextMuted)
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = KinetiqPurpleNeon,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Clinical Review Date: ${article.lastReviewedDate}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = KinetiqPurpleLavender
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = article.fullContent,
          style = MaterialTheme.typography.bodyMedium,
          color = KinetiqTextWhite,
          lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = KinetiqObsidianSurface,
          border = androidx.compose.foundation.BorderStroke(1.dp, KinetiqBorderGlow.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Key Clinical Takeaways:",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = KinetiqPurpleNeon
            )
            Spacer(modifier = Modifier.height(6.dp))
            article.keyTakeaways.forEach { takeaway ->
              Text(
                text = "✓ $takeaway",
                style = MaterialTheme.typography.bodySmall,
                color = KinetiqTextWhite,
                modifier = Modifier.padding(vertical = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = KinetiqObsidianSurface,
          border = androidx.compose.foundation.BorderStroke(0.6.dp, KinetiqBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Authoritative Source & Reference:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = KinetiqTextSubtle
            )
            Text(
              text = "${article.sourceOrg}\n${article.sourceReference}",
              style = MaterialTheme.typography.labelSmall,
              color = KinetiqTextMuted
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
          containerColor = KinetiqPurpleNeon,
          contentColor = KinetiqBlack
        )
      ) {
        Text("Done Reading", fontWeight = FontWeight.Bold)
      }
    }
  )
}
