package ru.maplyb.navigation.gui.impl.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ColumnScope.SettingsScreen(
    pauseState: Boolean = false,
    updatePauseState: (Boolean) -> Unit = {},
    onDismissRequest: () -> Unit = {},
    pop: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            modifier = Modifier
                .clickable {
                    pop()
                },
            tint = Color.White,
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null
        )
        Spacer(Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .clickable {
                    onDismissRequest()
                },
            tint = Color.White,
            imageVector = Icons.Default.Close,
            contentDescription = null
        )
    }
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Учет паузы",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = Color.White,
            )
        )
        Spacer(Modifier.weight(1f))
        Switch(
            checked = pauseState,
            onCheckedChange = {
                updatePauseState(!pauseState)
            }
        )
    }
}

@Composable
@Preview
private fun PreviewSettingsScreen() {
    Column {
        SettingsScreen()
    }
}