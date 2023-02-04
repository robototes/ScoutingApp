package com.scouting.app.model

data class TemplateFormatMatch(
    var title: String,
    var saveOrderByKey: List<String>,
    var autoTemplateItems: List<TemplateItem>,
    var teleTemplateItems: List<TemplateItem>
)
