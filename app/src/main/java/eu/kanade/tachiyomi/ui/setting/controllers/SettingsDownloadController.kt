package eu.kanade.tachiyomi.ui.setting.controllers

import android.content.Intent
import androidx.preference.PreferenceScreen
import com.bluelinelabs.conductor.Controller
import eu.kanade.tachiyomi.R
import komari.i18n.MR
import komari.util.lang.getString
import dev.icerock.moko.resources.compose.stringResource
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import eu.kanade.tachiyomi.data.preference.changesIn
import eu.kanade.tachiyomi.ui.setting.SettingsLegacyController
import eu.kanade.tachiyomi.ui.setting.bindTo
import eu.kanade.tachiyomi.ui.setting.defaultValue
import eu.kanade.tachiyomi.ui.setting.infoPreference
import eu.kanade.tachiyomi.ui.setting.intListPreference
import eu.kanade.tachiyomi.ui.setting.multiSelectListPreferenceMat
import eu.kanade.tachiyomi.ui.setting.preferenceCategory
import eu.kanade.tachiyomi.ui.setting.summaryMRes as summaryRes
import eu.kanade.tachiyomi.ui.setting.switchPreference
import eu.kanade.tachiyomi.ui.setting.titleMRes as titleRes
import eu.kanade.tachiyomi.ui.setting.triStateListPreference
import eu.kanade.tachiyomi.util.lang.addBetaTag
import eu.kanade.tachiyomi.util.view.withFadeTransaction
import uy.kohesive.injekt.injectLazy
import komari.domain.download.DownloadPreferences
import eu.kanade.tachiyomi.data.preference.PreferenceKeys as Keys

class SettingsDownloadController : SettingsLegacyController() {

    private val db: DatabaseHelper by injectLazy()
    private val downloadPreferences: DownloadPreferences by injectLazy()

    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = MR.strings.downloads

        switchPreference {
            key = Keys.downloadOnlyOverWifi
            titleRes = MR.strings.only_download_over_wifi
            defaultValue = true
        }
        switchPreference {
            bindTo(preferences.saveChaptersAsCBZ())
            titleRes = MR.strings.save_chapters_as_cbz
        }
        switchPreference {
            bindTo(preferences.splitTallImages())
            titleRes = MR.strings.split_tall_images
            summaryRes = MR.strings.split_tall_images_summary
        }
        switchPreference {
            bindTo(downloadPreferences.downloadWithId())
            title = context.getString(MR.strings.download_with_id).addBetaTag(context)
            summaryRes = MR.strings.download_with_id_details
        }

        val dbCategories = db.getCategories().executeAsBlocking()
        val categories = listOf(Category.createDefault(context)) + dbCategories

        preferenceCategory {
            titleRes = MR.strings.remove_after_read

            switchPreference {
                key = Keys.removeAfterMarkedAsRead
                titleRes = MR.strings.remove_when_marked_as_read
                defaultValue = false
            }
            intListPreference(activity) {
                bindTo(preferences.removeAfterReadSlots())
                titleRes = MR.strings.remove_after_read
                entriesRes = arrayOf(
                    MR.strings.never,
                    MR.strings.last_read_chapter,
                    MR.strings.second_to_last,
                    MR.strings.third_to_last,
                    MR.strings.fourth_to_last,
                    MR.strings.fifth_to_last,
                )
                entryRange = -1..4
                defaultValue = -1
            }
            multiSelectListPreferenceMat(activity) {
                bindTo(preferences.removeExcludeCategories())
                titleRes = MR.strings.pref_remove_exclude_categories
                entries = categories.map { it.name }
                entryValues = categories.map { it.id.toString() }
                noSelectionRes = MR.strings.none
                preferences.removeAfterReadSlots().changesIn(viewScope) { isVisible = it != -1 }
            }
            switchPreference {
                bindTo(preferences.removeBookmarkedChapters())
                titleRes = MR.strings.allow_deleting_bookmarked_chapters
            }
        }

        preferenceCategory {
            titleRes = MR.strings.download_new_chapters

            switchPreference {
                bindTo(preferences.downloadNewChapters())
                titleRes = MR.strings.download_new_chapters
            }
            triStateListPreference(activity) {
                preferences.apply {
                    bindTo(downloadNewChaptersInCategories(), excludeCategoriesInDownloadNew())
                }
                titleRes = MR.strings.categories
                entries = categories.map { it.name }
                entryValues = categories.map { it.id.toString() }
                allSelectionRes = MR.strings.all

                preferences.downloadNewChapters().changesIn(viewScope) { isVisible = it }
            }
        }

        preferenceCategory {
            titleRes = MR.strings.download_ahead

            intListPreference(activity) {
                bindTo(preferences.autoDownloadWhileReading())
                titleRes = MR.strings.auto_download_while_reading
                entries = listOf(
                    context.getString(MR.strings.never),
                    context.getString(MR.plurals.next_unread_chapters, 2, 2),
                    context.getString(MR.plurals.next_unread_chapters, 3, 3),
                    context.getString(MR.plurals.next_unread_chapters, 5, 5),
                    context.getString(MR.plurals.next_unread_chapters, 10, 10),
                )
                entryValues = listOf(0, 2, 3, 5, 10)
            }
            infoPreference(MR.strings.download_ahead_info)
        }

        preferenceCategory {
            titleRes = MR.strings.automatic_removal

            intListPreference(activity) {
                bindTo(preferences.deleteRemovedChapters())
                titleRes = MR.strings.delete_removed_chapters
                summary = activity?.getString(MR.strings.delete_downloaded_if_removed_online)
                entriesRes = arrayOf(
                    MR.strings.ask_on_chapters_page,
                    MR.strings.always_keep,
                    MR.strings.always_delete,
                )
                entryRange = 0..2
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    private fun navigateTo(controller: Controller) {
        router.pushController(controller.withFadeTransaction())
    }
}
