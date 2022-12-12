package com.example.safetyreview2.data

import android.net.Uri

data class ChecklistItem(
    var isDummy: Boolean = false,
    var checkState: Int = 0,
    var textBoxText: String = "",
    var imageURL: String = ""
) : java.io.Serializable