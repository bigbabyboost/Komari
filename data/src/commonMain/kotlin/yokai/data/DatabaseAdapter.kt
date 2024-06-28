package yokai.data

import app.cash.sqldelight.ColumnAdapter
import eu.kanade.tachiyomi.source.model.UpdateStrategy
import java.util.*

val updateStrategyAdapter = object : ColumnAdapter<UpdateStrategy, Long> {
    override fun decode(databaseValue: Long): UpdateStrategy = UpdateStrategy.decode(databaseValue)

    override fun encode(value: UpdateStrategy): Long = UpdateStrategy.encode(value)
}

val dateAdapter = object : ColumnAdapter<Date, Long> {
    override fun decode(databaseValue: Long): Date = Date(databaseValue)
    override fun encode(value: Date): Long = value.time
}

private const val listOfStringsSeparator = ", "
val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(listOfStringsSeparator)
        }
    override fun encode(value: List<String>) = value.joinToString(separator = listOfStringsSeparator)
}
