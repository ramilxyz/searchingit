package xyz.ramil.searchingit.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xyz.ramil.searchingit.data.db.DataManager
import xyz.ramil.searchingit.data.model.User
import xyz.ramil.searchingit.data.net.ApiManager
import xyz.ramil.searchingit.utils.SingleLiveEvent


class MainActivityViewModel(val googleSignInAccount: GoogleSignInAccount) : ViewModel() {

    val apiManager = ApiManager()

    var currentPage = 1

    private val perPage : Int = 30

    private var  isLoad = false

    private val compositeDisposable = CompositeDisposable()

    private var data: MutableList<User> = mutableListOf()

    private val _account  = MutableLiveData<GoogleSignInAccount>().apply {
        value = googleSignInAccount
    }

    val account : LiveData<GoogleSignInAccount> = _account


    private val showErrorToast = SingleLiveEvent<Any>()

    val navigateToDetails : SingleLiveEvent<Any>
        get() = showErrorToast

    fun search(query: String) {
        if(!isLoad) {
            compositeDisposable.add(
                    apiManager.search(query, currentPage, perPage)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { result ->
                                        if (currentPage > 1) { //кешируем при пагинации
                                            data.addAll(result.items)
                                            DataManager.insertData(data)
                                        } else {
                                            data.clear()
                                            DataManager.insertData(result.items)
                                        }
                                        currentPage++
                                        isLoad = false
                                    },
                                    { error ->
                                        isLoad = false
                                        if(error.localizedMessage.contains("403")) {
                                            this.showErrorToast.call()
                                        }
                                    }
                            ))
        }
        isLoad = true
    }

    class MainActivityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return GoogleSignIn.getLastSignedInAccount(context)?.let { MainActivityViewModel(it) } as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}