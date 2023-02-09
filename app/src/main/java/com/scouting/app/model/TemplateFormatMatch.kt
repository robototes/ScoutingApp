package com.scouting.app.model

data class TemplateFormatMatch(
    var title: String,
    var isMatchTemplate: Boolean = true,
    var saveOrderByKey: List<String>,
    var autoTemplateItems: List<TemplateItem>,
    var teleTemplateItems: List<TemplateItem>
)
