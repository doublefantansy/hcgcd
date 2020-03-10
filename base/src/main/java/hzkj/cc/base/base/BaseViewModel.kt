package hzkj.cc.base.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hzkj.cc.base.TokenInvalidException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

abstract class BaseViewModel : ViewModel(), LifecycleObserver {
  val error by lazy { MutableLiveData<Int>() }
  val tokenTimeOut by lazy { MutableLiveData<Throwable>() }

  fun launchUI(
    block: suspend CoroutineScope.() -> Unit,
    tag: Int
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
}