package eu.kanade.tachiyomi.util.system

import androidx.annotation.StringRes
import dev.icerock.moko.resources.StringResource
import eu.kanade.tachiyomi.R
import Komari.i18n.MR
import Komari.util.lang.getString
import dev.icerock.moko.resources.compose.stringResource

enum class SideNavMode(val prefValue: Int, val stringRes: StringResource) {
    DEFAULT(0, MR.strings.default_behavior),
    NEVER(1, MR.strings.never),
    ALWAYS(2, MR.strings.always),
}
