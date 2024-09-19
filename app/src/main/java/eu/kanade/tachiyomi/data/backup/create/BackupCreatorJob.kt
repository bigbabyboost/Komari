package eu.kanade.tachiyomi.data.backup.create

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.touchlab.kermit.Logger
import com.hippo.unifile.UniFile
import eu.kanade.tachiyomi.data.backup.BackupNotifier
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.e
import eu.kanade.tachiyomi.util.system.localeContext
import eu.kanade.tachiyomi.util.system.notificationManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import uy.kohesive.injekt.injectLazy
import yokai.domain.backup.BackupPreferences
import yokai.domain.storage.StorageManager
import java.util.concurrent.*

class BackupCreatorJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val storageManager: StorageManager by injectLazy()
        val notifier = BackupNotifier(context.localeContext)
        val uri = inputData.getString(LOCATION_URI_KEY)?.toUri() ?: storageManager.getAutomaticBackupsDirectory()?.uri
        val options = inputData.getBooleanArray(BACKUP_FLAGS_KEY)?.let { BackupOptions.fromBooleanArray(it) }
            ?: BackupOptions()
        val isAutoBackup = inputData.getBoolean(IS_AUTO_BACKUP_KEY, true)

        notifier.showBackupProgress()
        return try {
            val location = BackupCreator(context).createBackup(uri!!, options, isAutoBackup)
            if (!isAutoBackup) notifier.showBackupComplete(UniFile.fromUri(context, location.toUri())!!)
            Result.success()
        } catch (e: Exception) {
            Logger.e(e)
            if (!isAutoBackup) notifier.showBackupError(e.message)
            Result.failure()
        } finally {
            context.notificationManager.cancel(Notifications.ID_BACKUP_PROGRESS)
        }
    }

    companion object {
        fun isManualJobRunning(context: Context): Boolean {
            val list = WorkManager.getInstance(context).getWorkInfosByTag(TAG_MANUAL).get()
            return list.find { it.state == WorkInfo.State.RUNNING } != null
        }

        fun setupTask(context: Context, prefInterval: Int? = null) {
            val preferences = Injekt.get<BackupPreferences>()
            val interval = prefInterval ?: preferences.backupInterval().get()
            val workManager = WorkManager.getInstance(context)
            if (interval > 0) {
                val request = PeriodicWorkRequestBuilder<BackupCreatorJob>(
                    interval.toLong(),
                    TimeUnit.HOURS,
                    10,
                    TimeUnit.MINUTES,
                )
                    .addTag(TAG_AUTO)
                    .setInputData(workDataOf(IS_AUTO_BACKUP_KEY to true))
                    .build()

                workManager.enqueueUniquePeriodicWork(TAG_AUTO, ExistingPeriodicWorkPolicy.UPDATE, request)
            } else {
                workManager.cancelUniqueWork(TAG_AUTO)
            }
        }

        fun startNow(context: Context, uri: Uri, options: BackupOptions) {
            val inputData = workDataOf(
                IS_AUTO_BACKUP_KEY to false,
                LOCATION_URI_KEY to uri.toString(),
                BACKUP_FLAGS_KEY to options.asBooleanArray(),
            )
            val request = OneTimeWorkRequestBuilder<BackupCreatorJob>()
                .addTag(TAG_MANUAL)
                .setInputData(inputData)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(TAG_MANUAL, ExistingWorkPolicy.KEEP, request)
        }
    }
}

private const val TAG_AUTO = "BackupCreator"
private const val TAG_MANUAL = "$TAG_AUTO:manual"

private const val IS_AUTO_BACKUP_KEY = "is_auto_backup" // Boolean
private const val LOCATION_URI_KEY = "location_uri" // String
private const val BACKUP_FLAGS_KEY = "backup_flags" // Int
