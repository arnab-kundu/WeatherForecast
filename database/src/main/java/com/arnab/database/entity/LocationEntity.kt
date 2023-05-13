package com.arnab.database.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "LocationTable", indices = [Index(value = ["latitude", "longitude"], unique = true)])
data class LocationEntity(
    var name: String?,
    var latitude: Double,
    var longitude: Double,
    var lastUpdateTimeStamp: Long = System.currentTimeMillis()
) : Parcelable {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
}