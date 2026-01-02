/*
 * Copyright (C) 2025 The AviumUI Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.avium.lockscreenedit.edit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.avium.lockscreenedit.R
import org.avium.lockscreenedit.viewmodel.LockscreenViewModel
import kotlin.random.Random
import kotlin.math.*
import kotlin.math.pow

@Composable
fun LockscreenEditScreen(
    navController: NavController,
    styleId: Int,
    viewModel: LockscreenViewModel = viewModel()
) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(Unit) {
        systemUiController.isSystemBarsVisible = false
    }

    val context = LocalContext.current
    
    var hourColor by remember { mutableStateOf(Color.White) }
    var minuteColor by remember { mutableStateOf(Color.White) }
    var showHourColorPicker by remember { mutableStateOf(false) }
    var showMinuteColorPicker by remember { mutableStateOf(false) }
    var showBlurDialog by remember { mutableStateOf(false) }
    var isBlurEnabled by remember { mutableStateOf(false) }
    
    val randomTime = remember {
        val hour = Random.nextInt(0, 24)
        val minute = Random.nextInt(0, 60)
        String.format("%02d:%02d", hour, minute)
    }
    
    val randomDate = remember {
         val months = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" )

        val weekdays = listOf(
            context.getString(R.string.monday),
            context.getString(R.string.tuesday), 
            context.getString(R.string.wednesday), 
            context.getString(R.string.thursday), 
            context.getString(R.string.friday), 
            context.getString(R.string.saturday), 
            context.getString(R.string.sunday)
        )
        val day = Random.nextInt(1, 29)
        val month = months[Random.nextInt(months.size)]
        val weekday = weekdays[Random.nextInt(weekdays.size)]
        "$month $day, $weekday"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.setting_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text(text = stringResource(id = R.string.cancel), color = Color.White)
            }
            Button(
                onClick = {
                    viewModel.onApplyWithColors(
                        context = context,
                        styleId = styleId,
                        hourColor = hourColor,
                        minuteColor = minuteColor,
                        isBlurEnabled = isBlurEnabled
                    )
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) {
                Text(text = stringResource(id = R.string.apply), color = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = (LocalConfiguration.current.screenHeightDp * 0.2f).dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = randomDate,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            val timeParts = randomTime.split(":")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timeParts[0],
                    color = if (isBlurEnabled) hourColor.copy(alpha = 0.6f) else hourColor,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = ":",
                    color = if (isBlurEnabled) Color.White.copy(alpha = 0.6f) else Color.White,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text(
                    text = timeParts[1],
                    color = if (isBlurEnabled) minuteColor.copy(alpha = 0.6f) else minuteColor,
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp)
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EditOption(
                text = stringResource(id = R.string.hour_color),
                onClick = { showHourColorPicker = true }
            )
            EditOption(
                text = stringResource(id = R.string.minute_color),
                onClick = { showMinuteColorPicker = true }
            )
            EditOption(
                text = stringResource(id = R.string.blur_clock),
                onClick = { showBlurDialog = true }
            )
        }
    }
    
    if (showHourColorPicker) {
        ColorPickerDialog(
            onColorSelected = { color ->
                hourColor = color
                showHourColorPicker = false
            },
            onDismiss = { showHourColorPicker = false }
        )
    }
    
    if (showMinuteColorPicker) {
        ColorPickerDialog(
            onColorSelected = { color ->
                minuteColor = color
                showMinuteColorPicker = false
            },
            onDismiss = { showMinuteColorPicker = false }
        )
    }
    
    if (showBlurDialog) {
        BlurClockDialog(
            isEnabled = isBlurEnabled,
            onToggle = { isBlurEnabled = it },
            onDismiss = { showBlurDialog = false }
        )
    }
}

@Composable
private fun EditOption(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun ColorPickerDialog(
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var hexInput by remember { mutableStateOf("") }
    var customColor by remember { mutableStateOf(Color.White) }
    var selectedColor by remember { mutableStateOf(Color.White) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.select_color),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                ColorWheel(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp),
                    onColorSelected = { color ->
                        selectedColor = color
                        hexInput = String.format("%08X", color.toArgb())
                    }
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(selectedColor)
                    )
                    Text(
                        text = "#${hexInput.takeLast(6)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                OutlinedTextField(
                    value = hexInput.takeLast(6),
                    onValueChange = { input ->
                        if (input.length <= 6) {
                            hexInput = input
                            try {
                                if (input.length == 6) {
                                    val colorValue = "FF$input".toLong(16)
                                    selectedColor = Color(colorValue)
                                }
                            } catch (e: Exception) {
                            }
                        }
                    },
                    label = { Text(stringResource(id = R.string.hex_color_input)) },
                    placeholder = { Text(stringResource(id = R.string.hex_color_placeholder)) },
                    leadingIcon = { Text("#") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Button(
                        onClick = { onColorSelected(selectedColor) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorWheel(
    modifier: Modifier = Modifier,
    onColorSelected: (Color) -> Unit
) {
    var selectedPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = androidx.compose.ui.geometry.Offset(
                            size.width / 2f,
                            size.height / 2f
                        )
                        val radius = min(size.width, size.height) / 2f
                        val distance = sqrt((offset.x - center.x).pow(2) + (offset.y - center.y).pow(2))
                        
                        if (distance <= radius) {
                            selectedPosition = offset
                            val angle = atan2(offset.y - center.y, offset.x - center.x)
                            val saturation = min(distance / radius, 1f)
                            val hue = (angle * 180f / PI.toFloat() + 360f) % 360f
                            
                            val color = androidx.compose.ui.graphics.Color.hsv(hue, saturation, 1f)
                            onColorSelected(color)
                        }
                    },
                    onDrag = { change, _ ->
                        val center = androidx.compose.ui.geometry.Offset(
                            size.width / 2f,
                            size.height / 2f
                        )
                        val radius = min(size.width, size.height) / 2f
                        val offset = change.position
                        val distance = sqrt((offset.x - center.x).pow(2) + (offset.y - center.y).pow(2))
                        
                        if (distance <= radius) {
                            selectedPosition = offset
                            val angle = atan2(offset.y - center.y, offset.x - center.x)
                            val saturation = min(distance / radius, 1f)
                            val hue = (angle * 180f / PI.toFloat() + 360f) % 360f
                            
                            val color = androidx.compose.ui.graphics.Color.hsv(hue, saturation, 1f)
                            onColorSelected(color)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val center = androidx.compose.ui.geometry.Offset(
                        size.width / 2f,
                        size.height / 2f
                    )
                    val radius = min(size.width, size.height) / 2f
                    val distance = sqrt((offset.x - center.x).pow(2) + (offset.y - center.y).pow(2))
                    
                    if (distance <= radius) {
                        selectedPosition = offset
                        val angle = atan2(offset.y - center.y, offset.x - center.x)
                        val saturation = min(distance / radius, 1f)
                        val hue = (angle * 180f / PI.toFloat() + 360f) % 360f
                        
                        val color = androidx.compose.ui.graphics.Color.hsv(hue, saturation, 1f)
                        onColorSelected(color)
                    }
                }
            }
    ) {
        val center = androidx.compose.ui.geometry.Offset(
            size.width / 2f,
            size.height / 2f
        )
        val radius = min(size.width, size.height) / 2f
        
        val colors = (0 until 360 step 30).map { hue ->
            androidx.compose.ui.graphics.Color.hsv(hue.toFloat(), 1f, 1f)
        }
        
        drawCircle(
            brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                colors = colors,
                center = center
            ),
            radius = radius,
            center = center
        )
        
        drawCircle(
            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                colors = listOf(
                    androidx.compose.ui.graphics.Color.White.copy(alpha = 1f),
                    androidx.compose.ui.graphics.Color.White.copy(alpha = 0f)
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
        
        if (selectedPosition != androidx.compose.ui.geometry.Offset.Zero) {
            drawCircle(
                color = androidx.compose.ui.graphics.Color.White,
                radius = 8.dp.toPx(),
                center = selectedPosition,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = androidx.compose.ui.graphics.Color.Black,
                radius = 6.dp.toPx(),
                center = selectedPosition,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
            )
        }
    }
}

@Composable
private fun BlurClockDialog(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.blur_clock),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = stringResource(id = R.string.blur_description),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.blur_effect),
                        fontSize = 16.sp
                    )
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onToggle
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}
