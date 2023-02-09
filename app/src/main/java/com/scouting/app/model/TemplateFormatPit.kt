package com.scouting.app.model

data class TemplateFormatPit(
    var title: String,
    var isMatchTemplate: Boolean = false,
    var saveOrderByKey: List<String>,
    var templateItems: List<TemplateItem>
)