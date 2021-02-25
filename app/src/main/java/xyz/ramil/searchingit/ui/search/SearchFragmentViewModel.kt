package xyz.ramil.searchingit.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.ramil.searchingit.data.db.DataManager
import xyz.ramil.searchingit.data.model.User

class SearchFragmentViewModel : ViewModel() {

    val items : MutableLiveData<List<User>> = DataManager.getList()
}