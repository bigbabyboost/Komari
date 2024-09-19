package Komari.presentation.settings.screen.data

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import eu.kanade.tachiyomi.R
import Komari.i18n.MR
import Komari.util.lang.getString
import dev.icerock.moko.resources.compose.stringResource
import eu.kanade.tachiyomi.util.storage.DiskUtil
import Komari.presentation.core.util.secondaryItemAlpha
import Komari.presentation.theme.Size
import Komari.presentation.theme.header
import java.io.File

@Composable
fun StorageInfo(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val storages = remember { DiskUtil.getExternalStorages(context) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Size.small),
    ) {
        storages.forEach {
            StorageInfo(it)
        }
    }
}

@Composable
private fun StorageInfo(
    file: File,
) {
    val context = LocalContext.current

    val available = remember(file) { DiskUtil.getAvailableStorageSpace(file) }
    val availableText = remember(available) { Formatter.formatFileSize(context, available) }
    val total = remember(file) { DiskUtil.getTotalStorageSpace(file) }
    val totalText = remember(total) { Formatter.formatFileSize(context, total) }

    Column(
        verticalArrangement = Arrangement.spacedBy(Size.tiny),
    ) {
        Text(
            text = file.absolutePath,
            style = MaterialTheme.typography.header,
        )

        Text(
            text = stringResource(MR.strings.available_disk_space_info, availableText, totalText),
            modifier = Modifier.secondaryItemAlpha(),
            style = MaterialTheme.typography.bodySmall,
        )

        LinearProgressIndicator(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .fillMaxWidth(),
            progress = { (1 - (available / total.toFloat())) },
        )
    }
}
