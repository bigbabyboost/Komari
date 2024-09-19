package eu.kanade.tachiyomi.ui.recents

import androidx.annotation.StringRes
import dev.icerock.moko.resources.StringResource
import eu.kanade.tachiyomi.R
import yokai.i18n.MR
import yokai.util.lang.getString
import dev.icerock.moko.resources.compose.stringResource

enum class RecentsViewType(val mainValue: Int, val stringRes: StringResource) {
    GroupedAll(0, MR.strings.grouped),
    UngroupedAll(1, MR.strings.all),
    History(2, MR.strings.history),
    Updates(3, MR.strings.updates),
    ;

    val isAll get() = this == GroupedAll || this == UngroupedAll
    val isHistory get() = this == History
    val isUpdates get() = this == Updates

    companion object {
        fun valueOf(value: Int?) = entries.find { it.mainValue == value } ?: GroupedAll
    }
}
