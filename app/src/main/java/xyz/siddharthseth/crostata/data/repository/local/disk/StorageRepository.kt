package xyz.siddharthseth.crostata.data.repository.local.disk

import android.app.Application
import com.squareup.moshi.Moshi
import io.reactivex.Completable
import xyz.siddharthseth.crostata.data.dao.local.storage.StorageDao
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class StorageRepository(var app: Application) : StorageDao {
    override fun cleanup() {
        val file = File(app.filesDir.path)
        file.deleteRecursively()
    }

    override fun deleteSessionFromDisk(sessionId: String): Completable {
        return Completable.fromCallable {
            val file = File(app.filesDir, sessionId)
            val a = file.delete()
            a
        }
    }

    override fun deleteWorkoutFromDisk(workoutId: String): Completable {
        return Completable.fromCallable {
            val file = File(app.filesDir, workoutId)
            val a = file.delete()
            a
        }
    }

    override fun deleteRouteFromDisk(routeId: String): Completable {
        return Completable.fromCallable {
            val file = File(app.filesDir, routeId)
            val a = file.delete()
            a
        }

    }


    override fun writeContentToFile(file: File, jsonContent: String) {
        val fileOutputStream = FileOutputStream(file, false)
        fileOutputStream.write(jsonContent.toByteArray())
        fileOutputStream.flush()
        fileOutputStream.close()
    }

    override fun readContentFromFile(fileName: String): String {
        val file = File(app.filesDir, fileName)
        val fileInputStream = FileInputStream(file)
        return fileInputStream.reader().readText()
    }


    fun getSessionFile(sessionId: String): File {
        return File(app.filesDir, sessionId)
    }

    fun getRouteFile(routeId: String): File {
        return File(app.filesDir, routeId)
    }

    fun getWorkoutFile(workoutId: String): File {
        return File(app.filesDir, workoutId)
    }

    override val moshi: Moshi = Moshi.Builder().build()
}