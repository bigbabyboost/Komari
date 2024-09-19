package komari.presentation.widget

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.core.graphics.drawable.toBitmap
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.fillMaxSize
import coil3.asDrawable
import coil3.executeBlocking
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Precision
import coil3.size.Scale
import coil3.transform.RoundedCornersTransformation
import eu.kanade.tachiyomi.core.security.SecurityPreferences
import eu.kanade.tachiyomi.domain.manga.models.Manga
import eu.kanade.tachiyomi.util.system.dpToPx
import eu.kanade.tachiyomi.util.system.launchIO
import kotlinx.coroutines.MainScope
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import komari.domain.manga.models.cover
import komari.presentation.widget.components.CoverHeight
import komari.presentation.widget.components.CoverWidth
import komari.presentation.widget.components.LockedWidget
import komari.presentation.widget.components.UpdatesWidget
import komari.presentation.widget.util.appWidgetBackgroundRadius
import komari.presentation.widget.util.calculateRowAndColumnCount
import java.util.*
import kotlin.math.min

class UpdatesGridGlanceWidget(
    private val app: Application = Injekt.get(),
    private val preferences: SecurityPreferences = Injekt.get(),
) : GlanceAppWidget() {
    private val coroutineScope = MainScope()

    private var data: List<Pair<Long, Bitmap?>>? = null

    override val sizeMode = SizeMode.Exact
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            // If app lock enabled, don't do anything
            if (preferences.useBiometrics().get()) {
                LockedWidget()
            } else {
                UpdatesWidget(data)
            }
        }
    }

    fun loadData(list: List<Pair<Manga, Long>>? = null) {
        coroutineScope.launchIO {
            // Don't show anything when lock is active
            if (preferences.useBiometrics().get()) {
                updateAll(app)
                return@launchIO
            }

            val manager = GlanceAppWidgetManager(app)
            val ids = manager.getGlanceIds(this@UpdatesGridGlanceWidget::class.java)
            if (ids.isEmpty()) return@launchIO

            val (rowCount, columnCount) = ids
                .flatMap { manager.getAppWidgetSizes(it) }
                .maxBy { it.height.value * it.width.value }
                .calculateRowAndColumnCount()
            // FIXME: Extract getRecentManga logic out of RecentsPresenter
            val processList = list ?: RecentsPresenter.getRecentManga(customAmount = min(50, rowCount * columnCount))

            data = prepareList(processList, rowCount * columnCount)
            ids.forEach { update(app, it) }
        }
    }

    private fun prepareList(processList: List<Pair<Manga, Long>>, take: Int): List<Pair<Long, Bitmap?>> {
        // Resize to cover size
        val widthPx = CoverWidth.value.toInt().dpToPx
        val heightPx = CoverHeight.value.toInt().dpToPx
        val roundPx = app.resources.getDimension(R.dimen.appwidget_inner_radius)
        return processList
//            .distinctBy { it.first.id }
            .sortedByDescending { it.second }
            .take(take)
            .map { it.first }
            .map { updatesView ->
                val request = ImageRequest.Builder(app)
                    .data(updatesView.cover())
                    .memoryCachePolicy(CachePolicy.DISABLED)
                    .precision(Precision.EXACT)
                    .size(widthPx, heightPx)
                    .scale(Scale.FILL)
                    .let {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                            it.transformations(RoundedCornersTransformation(roundPx))
                        } else {
                            it // Handled by system
                        }
                    }
                    .build()
                // FIXME: Make Manga independent from SManga
                Pair(updatesView.id!!, app.imageLoader.executeBlocking(request).image?.asDrawable(app.resources)?.toBitmap())
            }
    }

    companion object {
        val DateLimit: Calendar
            get() = Calendar.getInstance().apply {
                time = Date()
                add(Calendar.MONTH, -3)
            }
    }
}

val ContainerModifier = GlanceModifier
    .fillMaxSize()
    .background(ImageProvider(R.drawable.appwidget_background))
    .appWidgetBackground()
    .appWidgetBackgroundRadius()
