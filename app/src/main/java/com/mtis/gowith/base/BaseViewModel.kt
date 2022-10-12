package com.mtis.gowith.base

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mtis.gowith.domain.utils.ErrorType
import com.mtis.gowith.domain.utils.RemoteErrorEmitter
import com.mtis.gowith.widget.utils.ScreenState
import com.mtis.gowith.widget.utils.SingleLiveEvent

abstract class BaseViewModel: ViewModel(), RemoteErrorEmitter {
    val mutableProgress = MutableLiveData<Int>(View.GONE)
    val mutableScreenState = SingleLiveEvent<ScreenState>()
    val mutableErrorMessage = SingleLiveEvent<String>()
    val mutableSuccessMessage = MutableLiveData<String>()
    val mutableErrorType = SingleLiveEvent<ErrorType>()

    override fun onError(errorType: ErrorType) {
        mutableErrorType.postValue(errorType)
    }

    override fun onError(msg: String) {
        mutableErrorMessage.postValue(msg)
    }
}