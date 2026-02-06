package com.tracky.app.ui.screens.entrydetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.tracky.app.ui.components.TrackyBodyText
import com.tracky.app.ui.components.TrackyBottomSheet
import com.tracky.app.ui.components.TrackyDivider
import com.tracky.app.ui.components.TrackyInput
import com.tracky.app.ui.components.TrackySheetActions
import com.tracky.app.ui.theme.TrackyTokens

/**
 * Entry Actions Bottom Sheet
 * 
 * Actions available per PRD:
 * - Edit entry
 * - Adjust calories/macros (override)
 * - Change date/time
 * - Save entry template
 * - Delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryActionsSheet(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onAdjust: () -> Unit,
    onChangeDateTime: () -> Unit,
    onSaveTemplate: () -> Unit,
    onDelete: () -> Unit
) {
    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Actions"
    ) {
        Column {
            ActionItem(
                icon = Icons.Outlined.Edit,
                label = "Edit Entry",
                onClick = {
                    onDismiss()
                    onEdit()
                }
            )

            ActionItem(
                icon = Icons.Outlined.Tune,
                label = "Adjust Calories/Macros",
                onClick = {
                    onDismiss()
                    onAdjust()
                }
            )

            ActionItem(
                icon = Icons.Outlined.CalendarToday,
                label = "Change Date/Time",
                onClick = {
                    onDismiss()
                    onChangeDateTime()
                }
            )

            ActionItem(
                icon = Icons.Outlined.Bookmark,
                label = "Save as Template",
                onClick = {
                    onDismiss()
                    onSaveTemplate()
                }
            )

            TrackyDivider()

            ActionItem(
                icon = Icons.Outlined.Delete,
                label = "Delete Entry",
                isDestructive = true,
                onClick = {
                    onDismiss()
                    onDelete()
                }
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))
        }
    }
}

@Composable
private fun ActionItem(
    icon: ImageVector,
    label: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = TrackyTokens.Spacing.M,
                vertical = TrackyTokens.Spacing.M
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) TrackyTokens.Colors.Error else TrackyTokens.Colors.TextSecondary
        )
        Spacer(modifier = Modifier.width(TrackyTokens.Spacing.M))
        TrackyBodyText(
            text = label,
            color = if (isDestructive) TrackyTokens.Colors.Error else TrackyTokens.Colors.TextPrimary
        )
    }
}

/**
 * Save as Template Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveTemplateSheet(
    suggestedName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var templateName by remember { mutableStateOf(suggestedName) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Save as Template"
    ) {
        Column {
            TrackyInput(
                value = templateName,
                onValueChange = { templateName = it },
                label = "Template Name",
                placeholder = "Enter a name for this template"
            )

            TrackySheetActions(
                primaryText = "Save Template",
                onPrimaryClick = { onSave(templateName) },
                primaryEnabled = templateName.isNotBlank()
            )
        }
    }
}

/**
 * Change Date/Time Sheet (simplified version)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeDateTimeSheet(
    currentDate: String,
    currentTime: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var date by remember { mutableStateOf(currentDate) }
    var time by remember { mutableStateOf(currentTime.take(5)) }

    TrackyBottomSheet(
        onDismissRequest = onDismiss,
        title = "Change Date/Time"
    ) {
        Column {
            TrackyInput(
                value = date,
                onValueChange = { date = it },
                label = "Date (YYYY-MM-DD)",
                placeholder = "2024-01-15"
            )

            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.M))

            TrackyInput(
                value = time,
                onValueChange = { time = it },
                label = "Time (HH:MM)",
                placeholder = "12:30"
            )

            TrackySheetActions(
                primaryText = "Save",
                onPrimaryClick = { onSave(date, "$time:00") },
                primaryEnabled = date.isNotBlank() && time.isNotBlank()
            )
        }
    }
}
