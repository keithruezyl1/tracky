package com.tracky.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Tracky Select (Dropdown)
 * Distinct from TrackyInput, but styled similarly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackySelect(
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "Select"
) {
    var expanded by remember { mutableStateOf(false) }

    val filteredOptions = remember(value, options) {
        if (value.isEmpty()) {
            options.take(4)
        } else {
            options.filter { it.contains(value, ignoreCase = true) }.take(4)
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TrackyInput(
            value = value,
            onValueChange = { 
                onValueChange(it)
                expanded = true
            },
            readOnly = false,
            label = label,
            placeholder = placeholder,
            trailingIcon = {
                Box(modifier = Modifier.clickable { expanded = !expanded }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier.menuAnchor()
        )

        // Only show if we have options to show
        if (filteredOptions.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(TrackyColors.Surface)
                    .heightIn(max = 240.dp) // Limit height to avoid taking up full screen
            ) {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                style = TrackyTypography.BodyMedium,
                                color = TrackyColors.TextPrimary
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}
