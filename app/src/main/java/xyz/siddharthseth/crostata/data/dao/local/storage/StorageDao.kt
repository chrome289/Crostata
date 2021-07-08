package xyz.siddharthseth.crostata.data.dao.local.storage

import com.squareup.moshi.Moshi
import io.reactivex.Completable
import java.io.File

interface StorageDao {

    fun deleteSessionFromDisk(sessionId: String): Completable
    fun deleteWorkoutFromDisk(workoutId: String): Completable
    fun deleteRouteFromDisk(routeId: String): Completable

    fun writeContentToFile(file: File, jsonContent: String)
    fun readContentFromFile(fileName: String): String

    fun cleanup()

    val moshi: Moshi

}