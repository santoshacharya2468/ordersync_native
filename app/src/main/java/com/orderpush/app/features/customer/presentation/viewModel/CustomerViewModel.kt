package com.orderpush.app.features.customer.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orderpush.app.features.customer.data.model.Customer
import com.orderpush.app.features.customer.data.model.CustomerFilter
import com.orderpush.app.features.customer.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class  CustomerUIState{
    object Idle: CustomerUIState()
    object Loading: CustomerUIState()
    data class Success(val customers: List<Customer>): CustomerUIState()
    data class Error(val message: String): CustomerUIState()

}


@HiltViewModel
class CustomerViewModel @Inject constructor(private val repository: CustomerRepository) : ViewModel() {

    var _uiState = MutableStateFlow<CustomerUIState>(CustomerUIState.Idle)
    val uiState: StateFlow<CustomerUIState> = _uiState
    fun getCustomers(filter: CustomerFilter){
        viewModelScope.launch {
            _uiState.value = CustomerUIState.Loading
            try {
                val response = repository.getCustomers(filter)
                if (response.status == 200){
                    _uiState.value = CustomerUIState.Success(response.data!!)
                }
                else{
                    _uiState.value = CustomerUIState.Error(response.message ?: "Unknown error")
                }

            }
            catch (e: Exception){
                _uiState.value = CustomerUIState.Error(e.message ?: "Unknown error")
            }
        }
    }


}