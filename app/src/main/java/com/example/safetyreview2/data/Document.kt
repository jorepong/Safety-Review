package com.example.safetyreview2.data

import java.util.*

data class Document(
    var title: String? = null,
    var createdTime: Date? = null,
    var modifiedTime: Date? = null,
    var documentId: String? = null,
    var carbonCopy: ArrayList<String>? = null,
    var confirmer: ArrayList<String>? = null
) : java.io.Serializable