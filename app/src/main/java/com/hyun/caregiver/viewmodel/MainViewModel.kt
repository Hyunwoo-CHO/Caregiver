package com.hyun.caregiver

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hyun.caregiver.database.AppDatabase
import com.hyun.caregiver.database.Personal
import com.hyun.caregiver.database.Question
import com.hyun.caregiver.database.User
import com.hyun.caregiver.repository.MyRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val myRepo : MyRepository = MyRepository(AppDatabase.getInstance(getApplication<Application>().applicationContext))

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

    private var _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    private var _test_answer_list = MutableLiveData<List<Int>>()
    private var _test_qid = MutableLiveData<List<Int>>()
    private var _test_my_answer_list = MutableList(80, { i -> 0 })
    private var _test_answer = MutableLiveData<Int>()
    val test_answer: LiveData<Int> get() = _test_answer

    private var _test_score = MutableLiveData<Int>()
    val test_score: LiveData<Int> get() = _test_score

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

    fun storeUID(uid: String) {
        viewModelScope.launch {
            myRepo.storeUID(uid)
        }
    }

//    fun insertUser(id: String) {
//        viewModelScope.launch {
//            try {
//                myRepo.insertUser(id)
//            } catch (e: Exception) {
//                Log.d("Try-Catch insertUser", e.toString())
//            }
//        }
//    }

    fun checkUser(id: String) {
        var result: User
        viewModelScope.launch {
            try {
                result = myRepo.checkUser(id)
            } catch (e: Exception) {
                Log.d("Try-Catch checkUser", e.toString())
            }
        }
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
                _category.value = category
                _questlist.value = myRepo.categoryQuestion(category) //result -> List<Question> question list
                index = questlist.value!!.indexOfFirst {
                    !it.solved
                }
                _indexlist.value = questlist.value!!.map { it.number }
                _quest.value = questlist.value!![index]
            } catch (e: Exception) {
                Log.d("Try-Catch categoryQuestion", e.toString())
            }
        }
    }

    fun testQuestion(category: String) {
        viewModelScope.launch {
            try {
                _category.value = category
                _questlist.value = myRepo.categoryQuestion(category) //result -> List<Question> question list
                _test_answer_list.value = myRepo.getTestAnswer(category)
                _test_qid.value = myRepo.getTestQid(category)
                index = 0
                _indexlist.value = questlist.value!!.map { it.number }
                _quest.value = questlist.value!![index]
            } catch (e: Exception) {
                Log.d("Try-Catch testQuestion", e.toString())
            }
        }
    }

    fun checknoteQuestion(category: String) {
        viewModelScope.launch {
            try {
                _questlist.value = myRepo.checknoteQuestion(category) //result -> List<Question> question list
                index = 0
                _indexlist.value = questlist.value!!.map { it.number }
                _quest.value = questlist.value!![index]
            } catch (e: Exception) {
                Log.d("Try-Catch checknoteQuestion", e.toString())
            }
        }
    }

    fun indexQuestion(number: Int) {
        viewModelScope.launch {
            try {
                val r_index = indexlist.value!!.indexOf(number)
                _quest.value = questlist.value!![r_index]
                index = r_index
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
                delay(50)
                _personal.value = myRepo.getAnswer(qid)
            } catch (e: Exception) {
                Log.d("Try-Catch getPersonal", e.toString())
            }
        }
    }

    fun getTestAnswer(number: Int) {
        viewModelScope.launch {
            try {
                delay(50)
                _test_answer.value = _test_my_answer_list[number - 1]
            } catch (e: Exception) {
                Log.d("Try-Catch getTestAnswer", e.toString())
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

    fun testAnswer(number: Int, answer: Int) {
        viewModelScope.launch {
            try {
                _test_my_answer_list.set(number - 1, answer)
            } catch (e: Exception) {
                Log.d("Try-Catch testAnswer", e.toString())
            }
        }
    }

    fun gradingTest() {
        var correct = 0
        var wrong = 0
        viewModelScope.launch {
            for (i in 0..79) {
                val answer = _test_answer_list.value!![i]
                val m_answer = _test_my_answer_list[i]
                val qid = _test_qid.value!![i]
                if (answer.equals(m_answer)) {
                    correct += 1
                    myRepo.insertAnswer(qid, answer, m_answer, true)
                } else {
                    wrong += 1
                    myRepo.insertAnswer(qid, answer, m_answer, false)
                }
            }
            _test_score.value = correct
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