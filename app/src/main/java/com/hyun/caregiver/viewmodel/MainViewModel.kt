package com.hyun.caregiver

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hyun.caregiver.database.AppDatabase
import com.hyun.caregiver.database.Personal
import com.hyun.caregiver.database.Question
import com.hyun.caregiver.repository.MyRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val myRepo : MyRepository = MyRepository(AppDatabase.getInstance(getApplication<Application>().applicationContext))

    private var _qnum = MutableLiveData<Int>()
    val qnum: LiveData<Int> get() = _qnum
    private var _quest = MutableLiveData<Question>()
    val quest: LiveData<Question> get() = _quest
    private var _checknotequest = MutableLiveData<Question>()
    val checknotequest: LiveData<Question> get() = _checknotequest
    private var index = 0
    //question list
    private var _questlist = MutableLiveData<List<Question>>()
    val questlist: LiveData<List<Question>> get() = _questlist
    //question index list
    private var _indexlist = MutableLiveData<List<Int>>()
    val indexlist: LiveData<List<Int>> get() = _indexlist

    private var _personal = MutableLiveData<Personal>()
    val personal: LiveData<Personal> get() = _personal
    private var _catelist = MutableLiveData<List<String>>()
    val catelist: LiveData<List<String>> get() = _catelist
    private var _examplelist = MutableLiveData<List<String>>()
    val examplelist: LiveData<List<String>> get() = _examplelist
    private var _pastlist = MutableLiveData<List<String>>()
    val pastlist: LiveData<List<String>> get() = _pastlist

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
    // use in main activity
    fun getJsonFile() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                myRepo.fetchDataFromNetwork()
            } catch (e: Exception) {
                Log.d("Try-Catch readJsonFile", e.toString())
            }
        }
    }

    fun getCategory(classify: String) {
        viewModelScope.launch {
            try {
                _catelist.value = myRepo.getCategory(classify)
            } catch (e: Exception) {
                Log.d("Try-Catch getCategory", e.toString())
            }
        }
    }

    fun getChecknoteCategory(classify: String) {
        viewModelScope.launch {
            try {
                if (classify == "예상문제") {
                    _examplelist.value = myRepo.getChecknoteCategory(classify)
                } else {
                    _pastlist.value = myRepo.getChecknoteCategory(classify)
                }
            } catch (e: Exception) {
                Log.d("Try-Catch getCategory", e.toString())
            }
        }
    }

    // use in example fragment
    fun categoryQuestion(category: String) {
        viewModelScope.launch {
            try {
                _questlist.value = myRepo.categoryQuestion(category) //result -> List<Question> question list
                index = questlist.value!!.indexOfFirst {
                    !it.solved
                }
                _indexlist.value = (1..questlist.value!!.size).toList()
                _qnum.value = index + 1
                _quest.value = questlist.value!![index]
            } catch (e: Exception) {
                Log.d("Try-Catch categoryQuestion", e.toString())
            }
        }
    }

    fun checknoteQuestion(category: String) {
        viewModelScope.launch {
            try {
                _questlist.value = myRepo.checknoteQuestion(category) //result -> List<Question> question list
                index = 0
                _indexlist.value = (1..questlist.value!!.size).toList()
                _qnum.value = index + 1
                _quest.value = questlist.value!![index]
            } catch (e: Exception) {
                Log.d("Try-Catch checknoteQuestion", e.toString())
            }
        }
    }

    fun indexQuestion(r_index: Int) {
        viewModelScope.launch {
            try {
                _qnum.value = r_index
                _quest.value = questlist.value!![r_index - 1]
                index = r_index - 1
            } catch (e: Exception) {
                Log.d("Try-Catch indexQuestion", e.toString())
            }
        }
    }

    fun previousQuestion() {
        viewModelScope.launch {
            try {
                if (index >= 1) { //check last question
                    index = index - 1
                    _qnum.value = index + 1
                    _quest.value = questlist.value!![index]
                } //implement for already last question case
            } catch (e: Exception) {
                Log.d("Try-Catch previousQuestion", e.toString())
            }
        }
    }

    fun nextQuestion() {
        viewModelScope.launch {
            try {
                if (index + 1 <= questlist.value!!.indexOfLast { true }) { //check last question
                    index = index + 1
                    _qnum.value = index + 1
                    _quest.value = questlist.value!![index]
                } //implement for already last question case
            } catch (e: Exception) {
                Log.d("Try-Catch nextQuestion", e.toString())
            }
        }
    }

    fun getPersonal(qid: Int) {
        viewModelScope.launch {
            try {
                _personal.value = myRepo.getAnswer(qid)
            } catch (e: Exception) {
                Log.d("Try-Catch getPersonal", e.toString())
            }
        }
    }

    fun insertAnswer(qid: Int, answer: Int, m_answer: Int, correction: Boolean) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                myRepo.insertAnswer(qid, answer, m_answer, correction)
                _questlist.value!![index].solved = true
            } catch (e: Exception) {
                Log.d("Try-Catch insertAnswer", e.toString())
            }
        }
    }
}
//class MainViewModelFactory(val application: Application) : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return MainViewModel(application) as T
//    }
//}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}