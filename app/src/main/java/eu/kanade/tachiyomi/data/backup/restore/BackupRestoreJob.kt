package eu.kanade.tachiyomi.data.backup.restore

import android.content.Context
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import co.touchlab.kermit.Logger
import eu.kanade.tachiyomi.data.backup.BackupConst
import eu.kanade.tachiyomi.data.backup.BackupNotifier
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.util.system.jobIsRunning
import eu.kanade.tachiyomi.util.system.localeContext
import eu.kanade.tachiyomi.util.system.tryToSetForeground
import eu.kanade.tachiyomi.util.system.withIOContext
import kotlinx.coroutines.CancellationException
import komari.i18n.MR
import komari.util.lang.getString

class BackupRestoreJob(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val notifier = BackupNotifier(context.localeContext)
    private val restorer = BackupRestorer(context, notifier)

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification = notifier.showRestoreProgress(progress = -1).build()
        val id = Notifications.ID_RESTORE_PROGRESS
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(id, notification)
        }
    }

    override suspend fun doWork(): Result {
        val uriPath = inputData.getString(BackupConst.EXTRA_URI) ?: return Result.failure()
        val uri = Uri.parse(uriPath) ?: return Result.failure()

        tryToSetForeground()

        return withIOContext {
            try {
                restorer.restore(uri)
                Result.success()
            } catch (e: Exception) {
                if (e is CancellationException) {
                    notifier.showRestoreError(context.getString(MR.strings.restoring_backup_canceled))
                    Result.success()
                } else {
                    Logger.e(e) { "Failed to restore backup" }
                    restorer.writeErrorLog()
                    notifier.showRestoreError(e.message)
                    Result.failure()
                }
            }
        }
    }

    companion object {
        private const val TAG = "BackupRestorer"

        fun start(context: Context, uri: Uri) {
            val request = OneTimeWorkRequestBuilder<BackupRestoreJob>()
                .addTag(TAG)
                .setInputData(workDataOf(BackupConst.EXTRA_URI to uri.toString()))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
        }

        fun stop(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(TAG)
        }

        fun isRunning(context: Context) = WorkManager.getInstance(context).jobIsRunning(TAG)
    }
}
