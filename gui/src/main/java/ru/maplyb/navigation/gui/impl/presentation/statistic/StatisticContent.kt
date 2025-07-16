package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticContent(
    sheetState : SheetState,
    statistic: StatisticModel?,
    onDismissRequest: () -> Unit,
    clear: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column {
            val text = if (statistic == null) "Статистика пуста" else "Пройдено: ${statistic.leftToDo}"
            Text(
                text = text,
                fontSize = 48.sp
            )
            Spacer(Modifier.height(16.dp))
            if (statistic != null) {
                Button(
                    onClick = {
                        clear()
                    },
                    content = {
                        Text(
                            text = "Очистить",
                            fontSize = 24.sp
                        )
                    }
                )
            }
        }
    }
}