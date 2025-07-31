package ru.maplyb.navigation.gui.impl.ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
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
internal fun iconCollapsed(): ImageVector {
        return Builder(name = "Collapse-svgrepo-com", defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f).apply {
            path(fill = SolidColor(Color(0xFF1F2328)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(7.57f, 5.0f)
                lineTo(6.509f, 6.061f)
                lineTo(12.0f, 11.554f)
                lineTo(17.491f, 6.061f)
                lineTo(16.43f, 5.0f)
                lineTo(12.0f, 9.432f)
                lineTo(7.57f, 5.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFF1F2328)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(16.43f, 19.0f)
                lineTo(17.49f, 17.939f)
                lineTo(12.0f, 12.446f)
                lineTo(6.509f, 17.939f)
                lineTo(7.57f, 19.0f)
                lineTo(12.0f, 14.568f)
                lineTo(16.43f, 19.0f)
                close()
            }
        }
        .build()
    }


@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = iconCollapsed(), contentDescription = "")
    }
}
