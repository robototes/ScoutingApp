package com.scouting.app.model

data class TemplateFormatMatch(
    var title: String,
    var autoDuration: Int,
    var teleDuration: Int,
    var endDuration: Int,
    var autoTemplateItems: List<TemplateItem>,
    var teleTemplateItems: List<TemplateItem>,
    var endTemplateItems: List<TemplateItem>
)
