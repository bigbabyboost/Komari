package Komari.presentation.component.preference.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SwitchPreferenceWidget(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    checked: Boolean = false,
    onCheckedChanged: (Boolean) -> Unit,
) {
    TextPreferenceWidget(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        icon = icon,
        widget = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                modifier = Modifier.padding(start = TrailingWidgetBuffer),
            )
        },
        onPreferenceClick = { onCheckedChanged(!checked) },
    )
}
