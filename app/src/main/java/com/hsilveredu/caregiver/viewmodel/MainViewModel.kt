package com.hsilveredu.caregiver

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hsilveredu.caregiver.database.AppDatabase
import com.hsilveredu.caregiver.database.Personal
import com.hsilveredu.caregiver.database.Question
import com.hsilveredu.caregiver.repository.MyRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val myRepo : MyRepository = MyRepository(AppDatabase.getInstance(getApplication<Application>().applicationContext))

    private var _solved = MutableLiveData<List<String>>()
    val solved: LiveData<List<String>> get() = _solved
    private var _quest = MutableLiveData<Event<Question>>()
    val quest: LiveData<Event<Question>> get() = _quest
    private var _checknotequest = MutableLiveData<Event<Question>>()
    val checknotequest: LiveData<Event<Question>> get() = _checknotequest
    private var index = 0
    //question list
    private var _questlist = MutableLiveData<List<Question>>()
    val questlist: LiveData<List<Question>> get() = _questlist
    //question index list
    private var _indexlist = MutableLiveData<List<Int>>()
    val indexlist: LiveData<List<Int>> get() = _indexlist

    private var _personal = MutableLiveData<Event<Personal>>()
    val personal: LiveData<Event<Personal>> get() = _personal
    private var _catelist = MutableLiveData<List<String>>()
    val catelist: LiveData<List<String>> get() = _catelist
    private var _examplelist = MutableLiveData<List<String>>()
    val examplelist: LiveData<List<String>> get() = _examplelist
    private var _pastlist = MutableLiveData<List<String>>()
    val pastlist: LiveData<List<String>> get() = _pastlist

    private var _category = MutableLiveData<String>()
    val category: LiveData<String> get() = _category

    private var _test_answer_list = MutableLiveData<List<Int>>()
    private var _test_qid = MutableLiveData<List<String>>()
    private var _test_my_answer_list = MutableList(80, { i -> 0 })
    private var _test_answer = MutableLiveData<Int>()
    val test_answer: LiveData<Int> get() = _test_answer

    private var _test_score = MutableLiveData<List<Int>>()
    val test_score: LiveData<List<Int>> get() = _test_score

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

    fun getSolved() {
        viewModelScope.launch {
            try {
                _solved.value = myRepo.getSolved()
                for (i in 1..solved.value!!.size) {
                    myRepo.updateSolved(solved.value!![i-1])
                }
            } catch (e: Exception) {
                Log.d("Try-Catch getSolved", e.toString())
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
                _quest.value = Event(questlist.value!![index])
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
                _quest.value = Event(questlist.value!![index])
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
                _quest.value = Event(questlist.value!![index])
            } catch (e: Exception) {
                Log.d("Try-Catch checknoteQuestion", e.toString())
            }
        }
    }

    fun indexQuestion(number: Int) {
        viewModelScope.launch {
            try {
                val r_index = indexlist.value!!.indexOf(number)
                _quest.value = Event(questlist.value!![r_index])
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
                    _quest.value = Event(questlist.value!![index])
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
                    _quest.value = Event(questlist.value!![index])
                } //implement for already last question case
            } catch (e: Exception) {
                Log.d("Try-Catch nextQuestion", e.toString())
            }
        }
    }

    fun getPersonal(qid: String) {
        viewModelScope.launch {
            try {
                delay(50)
                _personal.value = Event(myRepo.getAnswer(qid))
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

    fun insertAnswer(qid: String, answer: Int, m_answer: Int, correction: Boolean) {
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
                Log.d("value", _test_my_answer_list[number - 1].toString())
            } catch (e: Exception) {
                Log.d("Try-Catch testAnswer", e.toString())
            }
        }
    }

    fun gradingTest() {
        viewModelScope.launch {
            try {
                var writ_cor = 0
                var prac_cor = 0
                for (i in 0..34) {
                    val answer = _test_answer_list.value!![i]
                    val m_answer = _test_my_answer_list[i]
                    val qid = _test_qid.value!![i]
                    if (answer.equals(m_answer)) {
                        writ_cor += 1
                        myRepo.insertAnswer(qid, answer, m_answer, true)
                    } else {
                        myRepo.insertAnswer(qid, answer, m_answer, false)
                    }
                }
                for (i in 35..79) {
                    val answer = _test_answer_list.value!![i]
                    val m_answer = _test_my_answer_list[i]
                    val qid = _test_qid.value!![i]
                    if (answer.equals(m_answer)) {
                        prac_cor += 1
                        myRepo.insertAnswer(qid, answer, m_answer, true)
                    } else {
                        myRepo.insertAnswer(qid, answer, m_answer, false)
                    }
                }
                _test_score.value = listOf(writ_cor, prac_cor)
                _test_my_answer_list = MutableList(79, { i -> 0 })
            } catch (e: Exception) {
                Log.d("Try-Catch gradingTest", e.toString())
            }
        }
    }
}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}