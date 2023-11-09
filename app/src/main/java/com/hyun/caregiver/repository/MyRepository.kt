package com.hyun.caregiver.repository

import android.util.Log
import com.hyun.caregiver.database.*
import com.hyun.caregiver.retrofit.RetrofitClient
import com.google.gson.JsonArray
import kotlinx.coroutines.*

class MyRepository(database: AppDatabase) {
    private val myDao = database.myDao()

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
                    insertQuestion(data)
                }
            }catch (e: Exception) {
                Log.d("Try-catch repository", e.toString())
            }
        }
    }

    suspend fun insertQuestion(json : JsonArray) {
        withContext(Dispatchers.IO) {
            for (data in json) {
//                            Log.d("data", data.toList.toString())
                val dataList = data.asJsonArray
                val question = Question(
                    uid = dataList.get(0).asInt,
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

    suspend fun getAnswer(qid: Int): Personal {
        val result: Personal
        withContext(Dispatchers.Default) {
            result = myDao.getAnswer(qid)
        }
        return result
    }

    suspend fun insertAnswer(qid: Int, answer: Int, m_answer: Int, correction: Boolean) {
        withContext(Dispatchers.IO) {
            myDao.insertAnswer(Personal(qid, answer, m_answer, correction))
            myDao.updateQuestion(qid)
        }
    }
}