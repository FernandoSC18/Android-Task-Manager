package com.fscsoftware.managetasks.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "files")
data class Files (
    @PrimaryKey(autoGenerate = true) val id: Int?
    , @ColumnInfo(name = "file_type") val fileType: String?
    , @ColumnInfo(name = "file_mime_type") val fileMimeType: String?
    , @ColumnInfo(name = "file_size") val fileSize: Int?
    , @ColumnInfo(name = "file_name") val fileName: String?
    , @ColumnInfo(name = "file_desc") val fileDesc: String?
    , @ColumnInfo(name = "file_data",typeAffinity = ColumnInfo.BLOB) val fileData: ByteArray?
    , @ColumnInfo(name = "created_date") val createdDate: Long?
    , @ColumnInfo(name = "updated_date") val updatedDate: Long?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Files

        if (id != other.id) return false
        if (fileType != other.fileType) return false
        if (fileMimeType != other.fileMimeType) return false
        if (fileSize != other.fileSize) return false
        if (fileName != other.fileName) return false
        if (fileDesc != other.fileDesc) return false
        if (fileData != null) {
            if (other.fileData == null) return false
            if (!fileData.contentEquals(other.fileData)) return false
        } else if (other.fileData != null) return false
        if (createdDate != other.createdDate) return false
        if (updatedDate != other.updatedDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (fileType?.hashCode() ?: 0)
        result = 31 * result + (fileMimeType?.hashCode() ?: 0)
        result = 31 * result + (fileSize ?: 0)
        result = 31 * result + (fileName?.hashCode() ?: 0)
        result = 31 * result + (fileDesc?.hashCode() ?: 0)
        result = 31 * result + (fileData?.contentHashCode() ?: 0)
        result = 31 * result + (createdDate?.hashCode() ?: 0)
        result = 31 * result + (updatedDate?.hashCode() ?: 0)
        return result
    }
}