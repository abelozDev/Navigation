package ru.maplyb.navigation.gui.impl.presentation.statistic

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import ru.maplyb.navigation.gui.impl.domain.model.StatisticModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StatisticContent(
    statistic: StatisticModel?,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        val text = if (statistic == null) "Статистика пуста" else "Статистика есть"
        Text(
            text = text,
            fontSize = 48.sp
        )
    }
}