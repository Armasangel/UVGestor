package com.tuempresa.tuapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Item(val id: Int, val title: String, val subtitle: String, val imageUrl: String? = null)

class ItemsViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(sampleItems())
    val items: StateFlow<List<Item>> = _items

    private fun sampleItems(): List<Item> = List(20) { i ->
        Item(id = i + 1, title = "Item ${i+1}", subtitle = "Subtitulo ${i+1}")
    }
}
