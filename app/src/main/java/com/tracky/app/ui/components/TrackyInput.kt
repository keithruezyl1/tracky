package com.tracky.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.tracky.app.ui.theme.TrackyColors
import com.tracky.app.ui.theme.TrackyShapes
import com.tracky.app.ui.theme.TrackyTokens
import com.tracky.app.ui.theme.TrackyTypography

/**
 * Tracky Input Field
 * 
 * Height: 48dp
 * Radius: 14dp
 * Fill: Neutral/0
 * Border: 1dp Neutral/200
 * Focus: border changes to Brand/Primary
 */
@Composable
fun TrackyInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> TrackyColors.Error
            isFocused -> TrackyColors.BrandPrimary
            else -> TrackyColors.Border
        },
        animationSpec = tween(TrackyTokens.Animation.DurationMicroFast),
        label = "borderColor"
    )

    Column(modifier = modifier) {
        // Label
        if (label != null) {
            Text(
                text = label,
                style = TrackyTypography.BodyMedium,
                color = TrackyColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XS))
        }

        // Input field
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(TrackyTokens.Sizes.InputHeight)
                .clip(TrackyShapes.Medium)
                .background(TrackyColors.Surface)
                .border(
                    border = BorderStroke(
                        width = TrackyTokens.Sizes.BorderWidth,
                        color = borderColor
                    ),
                    shape = TrackyShapes.Medium
                ),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = TrackyTypography.BodyLarge.copy(
                color = TrackyColors.TextPrimary
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(TrackyColors.BrandPrimary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TrackyTokens.Spacing.M),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                        Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TrackyTypography.BodyLarge,
                                color = TrackyColors.TextTertiary
                            )
                        }
                        innerTextField()
                    }

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(TrackyTokens.Spacing.S))
                        trailingIcon()
                    }
                }
            }
        )

        // Error message
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(TrackyTokens.Spacing.XXS))
            Text(
                text = errorMessage,
                style = TrackyTypography.LabelSmall,
                color = TrackyColors.Error
            )
        }
    }
}

/**
 * Tracky Number Input
 * 
 * Specialized input for numeric values
 */
@Composable
fun TrackyNumberInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    suffix: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    allowDecimal: Boolean = true
) {
    TrackyInput(
        value = value,
        onValueChange = { newValue ->
            // Filter to only allow valid numeric input
            val filtered = if (allowDecimal) {
                newValue.filter { it.isDigit() || it == '.' }
                    .let { s ->
                        // Only allow one decimal point
                        val dotIndex = s.indexOf('.')
                        if (dotIndex >= 0) {
                            s.substring(0, dotIndex + 1) + s.substring(dotIndex + 1).replace(".", "")
                        } else s
                    }
            } else {
                newValue.filter { it.isDigit() }
            }
            onValueChange(filtered)
        },
        modifier = modifier,
        placeholder = placeholder,
        label = label,
        enabled = enabled,
        isError = isError,
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        trailingIcon = if (suffix != null) {
            {
                Text(
                    text = suffix,
                    style = TrackyTypography.BodyMedium,
                    color = TrackyColors.TextTertiary
                )
            }
        } else null
    )
}
