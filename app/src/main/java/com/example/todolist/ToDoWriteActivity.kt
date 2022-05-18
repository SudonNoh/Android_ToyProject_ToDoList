package com.example.todolist

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ToDoWriteActivity : AppCompatActivity() {
    lateinit var contentEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_write)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        contentEditText = findViewById<EditText>(R.id.content_edit_text)


        findViewById<TextView>(R.id.make_todo).setOnClickListener {
            val body = hashMapOf<String, Any>()
            body["content"] = contentEditText.text
            body["is_complete"] = "False"

            val header = HashMap<String, String>()
//            val sp = this.getSharedPreferences(
//                "user_info",
//                Context.MODE_PRIVATE
//            )
//            val token = sp.getString("token", "")
            header["Authorization"] = "token " + "cfb42388ec5e95afea475a172d906a1e1ac2da2e"

            retrofitService.makeToDo(header, body).enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
        }
    }
}