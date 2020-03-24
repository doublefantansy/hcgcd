package hzkj.cc.base.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hzkj.cc.base.TokenInvalidException
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel() {
    val error by lazy { MutableLiveData<Any>() }
    val tokenTimeOut by lazy { MutableLiveData<Throwable>() }
    fun launchUI(
            block: suspend CoroutineScope.() -> Unit,
            tag: Any
    ) = viewModelScope.launch(Dispatchers.Main) {
        try {
            withTimeout(60000) {
                try {
                    block()
                } catch (e: TokenInvalidException) {
                    tokenTimeOut.value = e
                }
            }
        } catch (e: Exception) {
            error.value = tag
        }
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}