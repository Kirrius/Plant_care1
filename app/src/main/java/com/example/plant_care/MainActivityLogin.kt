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
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Автоинкрементный id
    @ColumnInfo(name = "email") val email: String, // Уникальный email
    @ColumnInfo(name = "pass") val pass: String,
    @ColumnInfo(name = "login") val login: String
)

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).createFromAsset("users.sql").build()
                INSTANCE = instance
                instance
            }
        }
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

            if (email.isNotEmpty() && pass.isNotEmpty() && login.isNotEmpty()) {
                val user = User(email = email, pass = pass, login = login)
                lifecycleScope.launch {
                    try {
                        //db.userDao().insertUser(user)
                        Toast.makeText(this@MainActivityLogin, "User added", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivityLogin, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
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