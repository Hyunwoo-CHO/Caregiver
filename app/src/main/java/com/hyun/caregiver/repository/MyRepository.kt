package com.hyun.caregiver.repository

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.hyun.caregiver.database.*
import com.hyun.caregiver.retrofit.RetrofitClient
import com.google.gson.JsonArray
import kotlinx.coroutines.*

class MyRepository(database: AppDatabase) {
    private val myDao = database.myDao()
    private val db = Firebase.database
    private val myRef = db.getReference("Caregiver")
    private var uid = ""

    fun storeUID(uid: String) {
        this.uid = uid
    }

    //store the personal data in Firebase realtime database
    private fun writePersonal(data: Personal) {
        myRef.child("user").child("personal").setValue(data)
    }

    suspend fun insertUser(uid: String, nickname: String, email: String, profile: String) {
        withContext(Dispatchers.IO) {
            myDao.insertUser(User(uid, nickname, email, profile, false))
        }
    }

    suspend fun checkUser(id: String) : User {
        val result: User
        withContext(Dispatchers.IO) {
            result = myDao.getUser(id)
        }
        return result
    }

    suspend fun fetchDataFromNetwork() {
        //to get new questions in background thread by asynchronous way (call.enqueue)
        val uid = myDao.getLastQuestion() + 2
        val url = "values/caregiver!A" + uid.toString() + "%3AM?key=AIzaSyDREBAoY8TdTbZGnPT7twXjCWdxGwApJKI"
        val apiService = RetrofitClient.RetrofitInstance.getRetrofitInstance().create(RetrofitClient.ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getQuestion(url)
                if (response.isSuccessful) {
                    val data = response.body()!!.get("values").asJsonArray
                    Log.d("data", data.toString())
                    insertQuestion(data, uid - 1)
                }
            }catch (e: Exception) {
                Log.d("Try-catch repository", e.toString())
            }
        }
    }

    suspend fun insertQuestion(json : JsonArray, uid: Int) {
        withContext(Dispatchers.IO) {
            var iter = uid
            for (data in json) {
                val dataList = data.asJsonArray
                val question = Question(
                    uid = iter,
                    number = dataList.get(0).asInt,
                    title = dataList.get(1).toString().substring(1, dataList.get(1).toString().length - 1),
                    img = dataList.get(2).toString().substring(1, dataList.get(2).toString().length - 1),
                    opt_a = dataList.get(3).toString().substring(1, dataList.get(3).toString().length - 1),
                    opt_b = dataList.get(4).toString().substring(1, dataList.get(4).toString().length - 1),
                    opt_c = dataList.get(5).toString().substring(1, dataList.get(5).toString().length - 1),
                    opt_d = dataList.get(6).toString().substring(1, dataList.get(6).toString().length - 1),
                    opt_e = dataList.get(7).toString().substring(1, dataList.get(7).toString().length - 1),
                    answer = dataList.get(8).asInt,
                    comment = dataList.get(9).toString().substring(1, dataList.get(9).toString().length - 1),
                    comment_img = dataList.get(10).toString().substring(1, dataList.get(10).toString().length - 1),
                    cid = dataList.get(11).toString().substring(1, dataList.get(11).toString().length - 1),
                    classify = dataList.get(12).toString().substring(1, dataList.get(12).toString().length - 1),
                    solved = false)
                myDao.insertQuestion(question)
                iter += 1
            }
        }
    }

    suspend fun getCategory(classify: String): List<String> {
        val result: List<String>
        withContext(Dispatchers.Default) {
            result = myDao.getCategory(classify)
        }
        return result
    }

    suspend fun getChecknoteCategory(classify: String): List<String> {
        val result: List<String>
        withContext(Dispatchers.Default) {
            result = myDao.getChecknoteCategory(classify)
        }
        return result
    }

    suspend fun categoryQuestion(category: String): List<Question> {
        val result: List<Question>
        withContext(Dispatchers.IO){
            result = myDao.getCategoryQuestion(category)
        }
        return result
    }

    suspend fun checknoteQuestion(category: String): List<Question> {
        val result: List<Question>
        withContext(Dispatchers.IO){
            result = myDao.getChecknoteQuestion(category)
        }
        return result
    }

    suspend fun getTestAnswer(category: String): List<Int> {
        val result: List<Int>
        withContext(Dispatchers.IO){
            result = myDao.getTestAnswer(category)
        }
        return result
    }

    suspend fun getTestQid(category: String): List<Int> {
        val result: List<Int>
        withContext(Dispatchers.IO){
            result = myDao.getTestQid(category)
        }
        return result
    }

    suspend fun getAnswer(qid: Int): Personal {
        val result: Personal
        withContext(Dispatchers.IO) {
            result = myDao.getAnswer(qid)
        }
        return result
    }

    suspend fun insertAnswer(qid: Int, answer: Int, m_answer: Int, correction: Boolean) {
        withContext(Dispatchers.IO) {
            myDao.insertAnswer(Personal(qid, answer, m_answer, correction))
            writePersonal(Personal(qid, answer, m_answer, correction))
            myDao.updateQuestion(qid)
        }
    }
}
