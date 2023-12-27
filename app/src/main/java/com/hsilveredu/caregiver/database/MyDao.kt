package com.hsilveredu.caregiver.database

import androidx.room.*

@Dao
interface MyDao {
    /**             Question                **/
    @Query("SELECT * FROM questions")
    fun getAllQuestion(): List<Question>

    @Query("SELECT * FROM questions WHERE qid = :q_id")
    fun getQuestion(q_id : String): Question

    @Query("UPDATE questions SET solved = 1 WHERE qid = :q_id")
    fun updateSolved(q_id : String)

    @Upsert(entity = Question::class)
    fun insertQuestion(question: Question)

    @Query("SELECT DISTINCT cid FROM questions WHERE classify = :classify")
    fun getCategory(classify: String): List<String>

    @Query("SELECT DISTINCT cid FROM questions JOIN personal ON questions.qid = personal.qid WHERE questions.classify = :classify AND personal.correction = 0")
    fun getChecknoteCategory(classify: String): List<String>

    @Query("SELECT * FROM questions JOIN personal ON questions.qid = personal.qid WHERE questions.cid = :category AND personal.correction = 0")
    fun getChecknoteQuestion(category: String): List<Question>

    //get questions by category
    @Query("SELECT * FROM questions WHERE cid = :category ORDER BY number ASC")
    fun getCategoryQuestion(category: String): List<Question>

    @Query("SELECT qid FROM questions WHERE cid = :category ORDER BY number ASC")
    fun getTestQid(category: String): List<String>

    @Query("SELECT answer FROM questions WHERE cid = :category ORDER BY number ASC")
    fun getTestAnswer(category: String): List<Int>

    /**             Personal                **/
    @Insert(entity = Personal::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(answer: Personal)

    @Query("SELECT * FROM personal WHERE qid = :q_id")
    fun getAnswer(q_id: String): Personal

    @Query("SELECT qid FROM personal")
    fun getSolved(): List<String>
}