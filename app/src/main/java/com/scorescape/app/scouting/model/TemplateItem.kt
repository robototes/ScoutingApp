package com.scorescape.app.scouting.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.gson.annotations.Expose

data class TemplateItem(
    @Expose
    var text: String,
    @Expose
    var type: TemplateTypes,
    // Require unique key separate from index to allow drag and drop
    @Expose
    var id: String,
    var itemState: MutableState<*>? = null
)
