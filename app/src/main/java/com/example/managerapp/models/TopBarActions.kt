package com.example.managerapp.models

data class TopBarActions(
    val onAdd: (() -> Unit)? = null,
    val onClear: (() -> Unit)? = null,
    val onDownload: (() -> Unit)? = null
)