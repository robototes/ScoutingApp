package com.scouting.app.model

import androidx.compose.runtime.MutableState
import com.google.gson.annotations.Expose

data class TemplateItem(
    // Require separate unique key from item index to allow drag and drop
    var id: String,
    var text: String,
    var text2: String? = null,
    var text3: String? = null,
    var type: TemplateTypes,
    // The type of the item will always be non null so by using the type
    // we can determine which of these values we know will not be null
    var itemValueInt: MutableState<Int>? = null,
    var itemValue2Int: MutableState<Int>? = null,
    var itemValue3Int: MutableState<Int>? = null,
    var itemValueBoolean: MutableState<Boolean>? = null,
    var itemValueString: MutableState<String>? = null,
    var saveKey: String,
    var saveKey2: String? = null,
    var saveKey3: String? = null
)
