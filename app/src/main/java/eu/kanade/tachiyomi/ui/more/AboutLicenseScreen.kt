package eu.kanade.tachiyomi.ui.more

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.util.htmlReadyLicenseContent
import dev.icerock.moko.resources.compose.stringResource
import eu.kanade.tachiyomi.util.compose.LocalBackPress
import eu.kanade.tachiyomi.util.compose.currentOrThrow
import komari.i18n.MR
import komari.presentation.AppBarType
import komari.presentation.KomariScaffold
import komari.util.Screen

class AboutLicenseScreen : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val backPress = LocalBackPress.currentOrThrow

        KomariScaffold(
            onNavigationIconClicked = backPress,
            title = stringResource(MR.strings.open_source_licenses),
            appBarType = AppBarType.SMALL,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        ) { innerPadding ->
            LibrariesContainer(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding,
                onLibraryClick = {
                    navigator.push(
                        AboutLibraryLicenseScreen(
                            it.name,
                            it.website,
                            it.licenses.firstOrNull()?.htmlReadyLicenseContent.orEmpty(),
                        ),
                    )
                }
            )
        }
    }
}
