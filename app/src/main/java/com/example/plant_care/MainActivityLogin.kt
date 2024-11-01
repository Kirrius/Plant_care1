package com.example.plant_care

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import androidx.room.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.launch

@Entity(tableName = "login")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "pass") val pass: String,
    @ColumnInfo(name = "login") val login: String
)

@Dao
interface login {
    @Insert(entity = StatisticDbEntity::class)
    fun insertNewStatisticData(statistic: StatisticDbEntity)

    @Query("SELECT statistic.id, result_name, difficulty_name, mistakes, points FROM statistic\n" +
            "INNER JOIN results ON statistic.result_id = results.id\n" +
            "INNER JOIN difficulty_levels ON statistic.difficult_id = difficulty_levels.id;")
    fun getAllStatisticData(): List<StatisticInfoTuple>

    @Query("DELETE FROM statistic WHERE id = :statisticId")
    fun deleteStatisticDataById(statisticId: Long)
}

@Database(
    version = 1,
    entities = [
        User::class,
    ]
            abstract class AppDatabase : RoomDatabase() {

        abstract fun getStatisticDao(): login

    }
)

class StatisticRepository(private val statisticDao: StatisticDao) {

    suspend fun insertNewStatisticData(statisticDbEntity: StatisticDbEntity) {
        withContext(Dispatchers.IO) {
            statisticDao.insertNewStatisticData(statisticDbEntity)
        }
    }

    suspend fun getAllStatisticData(): List<StatisticInfoTuple> {
        return withContext(Dispatchers.IO) {
            return@withContext statisticDao.getAllStatisticData()
        }
    }

    suspend fun removeStatisticDataById(id: Long) {
        withContext(Dispatchers.IO) {
            statisticDao.deleteStatisticDataById(id)
        }
    }
}

data class StatisticInfoTuple(
    val id: Long,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "pass") val pass: String,
    @ColumnInfo(name = "login") val login: String
)

fun insertNewStatisticDataInDatabase(mistakes: Long, points: Long) {
    viewModelScope.launch {
        val newStatistic = Statistic(currentResult, currentDifficultyLevel, mistakes, points)
        statisticRepository.insertNewStatisticData(newStatistic.toStatisticDbEntity())
    }
}
class MainActivityLogin : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var emailEditText: EditText
    private lateinit var passEditText:  EditText
    private lateinit var loginEditText: EditText
    private lateinit var submitButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_login)
        //enableEdgeToEdge()

        db = AppDatabase.getDatabase(this)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passEditText = findViewById<EditText>(R.id.passEditText)
        val loginEditText = findViewById<EditText>(R.id.loginEditText)
        val submitButton = findViewById<Button>(R.id.regbutton)

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val pass = passEditText.text.toString()
            val login = loginEditText.text.toString()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun login(v: View) {
        val log = Intent (this, MainActivity::class.java)
        startActivity(log)
    }
}