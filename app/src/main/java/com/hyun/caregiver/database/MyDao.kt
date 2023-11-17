package com.hyun.caregiver.database

import androidx.room.*

@Dao
interface MyDao {
    /**             User                    **/
    @Insert(entity = User::class)
    fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: String): User

    /**             Question                **/
    @Query("SELECT * FROM questions")
    fun getAllQuestion(): List<Question>

    @Query("SELECT * FROM questions WHERE uid = :u_id")
    fun getQuestion(u_id : Int): Question

    @Query("UPDATE questions SET solved = 1 WHERE uid = :u_id")
    fun updateQuestion(u_id : Int)

    @Insert(entity = Question::class)
    fun insertQuestion(question: Question)

    @Query("SELECT uid FROM questions ORDER BY uid DESC LIMIT 1")
    fun getLastQuestion(): Int

    @Query("SELECT DISTINCT cid FROM questions WHERE classify = :classify")
    fun getCategory(classify: String): List<String>

    @Query("SELECT DISTINCT cid FROM questions JOIN personal ON questions.uid = personal.qid WHERE questions.classify = :classify AND personal.correction = 0")
    fun getChecknoteCategory(classify: String): List<String>

    @Query("SELECT * FROM questions JOIN personal ON questions.uid = personal.qid WHERE questions.cid = :category AND personal.correction = 0")
    fun getChecknoteQuestion(category: String): List<Question>

    //get questions by category
    @Query("SELECT * FROM questions WHERE cid = :category ORDER BY uid ASC")
    fun getCategoryQuestion(category: String): List<Question>

    /**             Personal                **/
    @Insert(entity = Personal::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAnswer(answer: Personal)

    @Query("SELECT * FROM personal WHERE qid = :q_id")
    fun getAnswer(q_id: Int): Personal
}