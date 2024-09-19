package eu.kanade.tachiyomi.ui.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import com.google.android.material.textview.MaterialTextView
import dev.icerock.moko.resources.compose.stringResource
import eu.kanade.tachiyomi.util.compose.LocalBackPress
import eu.kanade.tachiyomi.util.compose.currentOrThrow
import Komari.i18n.MR
import Komari.presentation.AppBarType
import Komari.presentation.KomariScaffold
import Komari.presentation.component.ToolTipButton
import Komari.util.Screen

class AboutLibraryLicenseScreen(
    private val name: String,
    private val website: String?,
    private val license: String,
) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val backPress = LocalBackPress.currentOrThrow
        val uriHandler = LocalUriHandler.current

        KomariScaffold(
            onNavigationIconClicked = {
                when {
                    navigator.canPop -> navigator.pop()
                    else -> backPress()
                }
            },
            title = name,
            appBarType = AppBarType.SMALL,
            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
                state = rememberTopAppBarState(),
            ),
            actions = {
                if (website != null) {
                    ToolTipButton(
                        toolTipLabel = stringResource(MR.strings.website),
                        icon = Icons.Outlined.Public,
                        buttonClicked = { uriHandler.openUri(website) },
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                HtmlLicenseText(html = license)
            }
        }
    }

    @Composable
    private fun HtmlLicenseText(html: String) {
        AndroidView(
            factory = {
                MaterialTextView(it)
            },
            update = {
                it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
            },
        )
    }
}
