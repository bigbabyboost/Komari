package yokai.presentation.extension.repo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExtensionOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.icerock.moko.resources.compose.stringResource
import eu.kanade.tachiyomi.util.compose.LocalAlertDialog
import eu.kanade.tachiyomi.util.compose.LocalBackPress
import eu.kanade.tachiyomi.util.compose.currentOrThrow
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.coroutines.flow.collectLatest
import yokai.domain.ComposableAlertDialog
import yokai.domain.extension.repo.model.ExtensionRepo
import yokai.i18n.MR
import yokai.presentation.AppBarType
import yokai.presentation.YokaiScaffold
import yokai.presentation.component.EmptyScreen
import yokai.presentation.component.ToolTipButton
import yokai.presentation.extension.repo.component.ExtensionRepoInput
import yokai.presentation.extension.repo.component.ExtensionRepoItem
import android.R as AR

@Composable
fun ExtensionRepoScreen(
    title: String,
    viewModel: ExtensionRepoViewModel = viewModel(),
    repoUrl: String? = null,
) {
    val onBackPress = LocalBackPress.currentOrThrow
    val context = LocalContext.current
    val repoState by viewModel.repoState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val alertDialog = LocalAlertDialog.currentOrThrow

    YokaiScaffold(
        onNavigationIconClicked = onBackPress,
        title = title,
        appBarType = AppBarType.SMALL,
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
            state = rememberTopAppBarState(),
            canScroll = { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 },
        ),
        actions = {
            ToolTipButton(
                toolTipLabel = stringResource(MR.strings.refresh),
                icon = Icons.Outlined.Refresh,
                buttonClicked = {
                    context.toast("Refreshing...")  // TODO: Should be loading animation instead
                    viewModel.refreshRepos()
                },
            )
        },
    ) { innerPadding ->
        if (repoState is ExtensionRepoState.Loading) return@YokaiScaffold

        val repos = (repoState as ExtensionRepoState.Success).repos

        alertDialog.content?.let { it() }

        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            userScrollEnabled = true,
            verticalArrangement = Arrangement.Top,
            state = listState,
        ) {
            item {
                ExtensionRepoInput(
                    inputText = inputText,
                    inputHint = stringResource(MR.strings.label_add_repo),
                    onInputChange = { inputText = it },
                    onAddClick = { viewModel.addRepo(it) },
                )
            }

            if (repos.isEmpty()) {
                item {
                    EmptyScreen(
                        modifier = Modifier.fillParentMaxSize(),
                        image = Icons.Filled.ExtensionOff,
                        message = stringResource(MR.strings.information_empty_repos),
                    )
                }
                return@LazyColumn
            }

            repos.forEach { repo ->
                item {
                    ExtensionRepoItem(
                        extensionRepo = repo,
                        onDeleteClick = { repoToDelete ->
                            alertDialog.content = { ExtensionRepoDeletePrompt(repoToDelete, alertDialog, viewModel) }
                        },
                    )
                }
            }
        }
    }

    LaunchedEffect(repoUrl) {
        repoUrl?.let { viewModel.addRepo(repoUrl) }
    }
    
    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            if (event is ExtensionRepoEvent.LocalizedMessage)
                context.toast(event.stringRes)
            if (event is ExtensionRepoEvent.Success)
                inputText = ""
            if (event is ExtensionRepoEvent.ShowDialog)
                alertDialog.content = {
                    if (event.dialog is RepoDialog.Conflict) {
                        ExtensionRepoReplacePrompt(
                            oldRepo = event.dialog.oldRepo,
                            newRepo = event.dialog.newRepo,
                            onDismissRequest = { alertDialog.content = null },
                            onMigrate = { viewModel.replaceRepo(event.dialog.newRepo) },
                        )
                    }
                }
        }
    }
}

@Composable
fun ExtensionRepoReplacePrompt(
    oldRepo: ExtensionRepo,
    newRepo: ExtensionRepo,
    onDismissRequest: () -> Unit,
    onMigrate: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onMigrate()
                    onDismissRequest()
                },
            ) {
                Text(text = stringResource(MR.strings.action_replace_repo))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AR.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(MR.strings.action_replace_repo_title))
        },
        text = {
            Text(text = stringResource(MR.strings.action_replace_repo_message, newRepo.name, oldRepo.name))
        },
    )
}

@Composable
fun ExtensionRepoDeletePrompt(repoToDelete: String, alertDialog: ComposableAlertDialog, viewModel: ExtensionRepoViewModel) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Text(
                text = stringResource(MR.strings.confirm_delete_repo_title),
                fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
            )
        },
        text = {
            Text(
                text = stringResource(MR.strings.confirm_delete_repo, repoToDelete),
                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
            )
        },
        onDismissRequest = { alertDialog.content = null },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteRepo(repoToDelete)
                    alertDialog.content = null
                }
            ) {
                Text(
                    text = stringResource(MR.strings.delete),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { alertDialog.content = null }) {
                Text(
                    text = stringResource(MR.strings.cancel),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                )
            }
        },
    )
}
