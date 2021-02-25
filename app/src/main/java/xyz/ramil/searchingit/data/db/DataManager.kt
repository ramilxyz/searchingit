package xyz.ramil.searchingit.data.db

import androidx.lifecycle.MutableLiveData
import xyz.ramil.searchingit.data.model.User

class DataManager() {
    //имитация бд
    companion object {

        private var data: MutableList<User> = mutableListOf()

        private val dataLiveData = MutableLiveData<List<User>>()

        fun insertData(user: List<User>) {
            data = user as MutableList<User>
            data.sortedBy { it.login }
            dataLiveData.value = data
        }

        fun getList(): MutableLiveData<List<User>> {
            return dataLiveData
        }
    }
}