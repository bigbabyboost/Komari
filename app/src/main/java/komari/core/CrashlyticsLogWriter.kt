package yokai.core

import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

class CrashlyticsLogWriter : LogWriter() {
    override fun isLoggable(tag: String, severity: Severity): Boolean = severity >= Severity.Info

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        try {
            Firebase.crashlytics.log(DefaultFormatter.formatMessage(severity, Tag(tag), Message(message)))
            if (throwable != null && severity >= Severity.Error) {
                Firebase.crashlytics.recordException(throwable)
            }
        } catch (_: Exception) {
            // Probably crashlytics not yet initialized or disabled
        }
    }
}
