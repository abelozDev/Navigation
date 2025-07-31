package ru.maplyb.navigation.gui.impl.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.Unit

@Composable
public fun iconExpand(): ImageVector {
        return Builder(name = "ExpandIcon", defaultWidth = 24.0.dp, defaultHeight =
                24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF1F2328)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(12.0f, 4.0f)
                lineTo(17.271f, 9.273f)
                lineTo(16.21f, 10.334f)
                lineTo(12.0f, 6.122f)
                lineTo(7.79f, 10.334f)
                lineTo(6.729f, 9.273f)
                lineTo(12.0f, 4.0f)
                close()
                moveTo(12.0f, 17.878f)
                lineTo(7.79f, 13.666f)
                lineTo(6.729f, 14.727f)
                lineTo(12.0f, 20.0f)
                lineTo(17.271f, 14.727f)
                lineTo(16.21f, 13.666f)
                lineTo(12.0f, 17.878f)
                close()
            }
        }
        .build()
    }


@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = iconExpand(), contentDescription = "")
    }
}
