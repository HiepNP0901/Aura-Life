package com.drs.auralife.ui.film.play

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drs.auralife.data.model.movie.Item

class SelectedItemViewModel: ViewModel() {
    private val _selectedItem = MutableLiveData<Item>()
    val selectedItem: LiveData<Item> = _selectedItem

    fun setSelectedItem(currentEpisode: Item) {
        _selectedItem.value = currentEpisode
    }
}