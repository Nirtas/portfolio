package util

import bitmap.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeBytes
import kotlin.reflect.KProperty1

object FileUtils {
    const val ONE_COLOR_DIRECTORY_PATH: String = "one_color"
    const val REVERSED_DIRECTORY_PATH: String = "reversed"
    const val CORRELATION_DIRECTORY_PATH: String = "correlation"
    const val HISTOGRAM_DIRECTORY_PATH: String = "histogram"

    inline fun <reified T : Any> writeCSV(path: String, data: List<T>, delimiter: String = ";") {
        val folder: String = File(path).parent
        if (!File(folder).exists()) {
            Files.createDirectories(Path.of(folder))
        }
        val writer = FileOutputStream(path).bufferedWriter()
        val fields: Array<Field> = T::class.java.declaredFields
        var header: String = ""
        for (field in fields) {
            header += field.name
            if (field != fields.last()) {
                header += delimiter
            }
        }
        writer.write(header)
        data.forEach { instance ->
            writer.newLine()
            var line: String = ""
            for (field in fields) {
                line += readInstanceProperty(instance, field.name)
                if (field != fields.last()) {
                    line += delimiter
                }
            }
            writer.write(line)
        }
        writer.flush()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified R> readInstanceProperty(instance: Any, propertyName: String): R {
        val property = instance::class.members.first { it.name == propertyName } as KProperty1<Any, *>
        return property.get(instance) as R
    }

    fun saveBitmapToFile(bitmap: Bitmap, filePath: String) {
        val directory: Path? = Path.of(filePath).parent
        if (directory != null && !Files.exists(directory)) {
            Files.createDirectories(directory)
        }
        if (Files.exists(Path.of(filePath))) {
            Files.delete(Path.of(filePath))
        }
        Files.createFile(Path.of(filePath)).writeBytes(bitmap.toByteArray())
    }
}