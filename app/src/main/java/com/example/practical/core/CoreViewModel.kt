package com.example.practical.core

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tap.support.ktx.getSelectedLanguage


open class CoreViewModel : ViewModel() {

    var savedInstanceState: Bundle? = null
    var mActivity: CoreActivity<*, *>? = null
    val progressBar = ObservableField(View.GONE)
    val title = MutableLiveData<String>()
    val isBackEnabled = MutableLiveData<Boolean>()
    val isEmpty = MutableLiveData<Boolean>()
    val isSearchVisible = ObservableBoolean(false)
    val showClockOption = ObservableBoolean(false)
    val isOptionClick = MutableLiveData<Boolean>()
    var isEmptyView = ObservableBoolean(false)
    var isLanguageAR = ObservableBoolean(false)
    var showListProgress = ObservableBoolean(false)
    val isHomeVisible = ObservableBoolean(false)


    val showContent = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val networkError = MutableLiveData<() -> Unit>()

    val toastMessage = MutableLiveData<String>()
    val snackMessage = MutableLiveData<String>()

    val APPUSER: String = "AP"
    val STAFFUSER: String = "ST"
    var refreshListener = ObservableField<(SwipeRefreshLayout) -> Unit>({})

    init {
        if (CoreApp.INSTANCE.getSelectedLanguage().equals("ar")) {
            isLanguageAR.set(true)
        } else {
            isLanguageAR.set(false)
        }
    }

    fun showToast(message: String?) {
        toastMessage.value = message
    }

    fun showSnackBar(message: String?) {
        snackMessage.value = message
    }

    fun content(loading: Boolean = false) {
        isLoading.value = loading
    }

    fun loading(withContent: Boolean = true) {
        isLoading.value = withContent
    }

    fun toolbarTitle(string: String) {
        title.value = string
    }

    fun onOptionClick(view: View) {
        if (isOptionClick.value == null)
            isOptionClick.value = true
        else isOptionClick.value = !(isOptionClick.value as Boolean)
    }
}