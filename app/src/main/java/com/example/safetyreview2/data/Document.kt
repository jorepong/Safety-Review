package com.example.safetyreview2.data

import java.util.*

data class Document(
    var title: String? = null,
    var createdTime: Date? = null,
    var modifiedTime: Date? = null,
    var documentId: String? = null,
    var carbonCopy: ArrayList<User>? = null,
    var confirmer: ArrayList<User>? = null,
    var writer: String? = null,
    var checklist: ArrayList<ChecklistItem>? = null
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Document) return false
        return title == other.title &&
                documentId == other.documentId &&
                carbonCopy == other.carbonCopy &&
                confirmer == other.confirmer &&
                writer == other.writer
    }
}