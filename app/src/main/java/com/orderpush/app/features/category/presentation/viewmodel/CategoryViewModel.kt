package com.orderpush.app.features.category.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.core.network.isSuccess
import com.orderpush.app.features.category.data.model.request.CategoryFilter
import com.orderpush.app.features.category.data.model.response.Category
import com.orderpush.app.features.category.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class  CategoryUIState{
    object Idle: CategoryUIState()
    object Loading: CategoryUIState()
    data class Success(val data: List<Category>): CategoryUIState()
}

@HiltViewModel
class CategoryViewModel @Inject constructor(private  val repository: CategoryRepository): ViewModel() {
    var _uiState= MutableStateFlow<CategoryUIState>(CategoryUIState.Idle)
    val uiState=_uiState.asStateFlow()

    fun getCategories(){
        _uiState.value=CategoryUIState.Loading
        viewModelScope.launch {
            val response=repository.getCategory(CategoryFilter())
            if(response.isSuccess()){
                _uiState.value=CategoryUIState.Success(response.data!!)
            }else{
                _uiState.value=CategoryUIState.Idle
            }
        }
    }
}