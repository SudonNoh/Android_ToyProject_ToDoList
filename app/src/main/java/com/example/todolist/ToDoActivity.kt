package com.example.todolist

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ToDoActivity : AppCompatActivity() {
    lateinit var todoRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        findViewById<ImageView>(R.id.write).setOnClickListener {
            startActivity(Intent(this, ToDoWriteActivity::class.java))
        }
        todoRecyclerView = findViewById(R.id.todo_list)
        getToDoList()
        findViewById<EditText>(R.id.search_edittext).doAfterTextChanged {
            searchToDoList(it.toString())
        }
    }

    fun searchToDoList(keyword: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        header["Authorization"] = "token " + "d29cb15006f4e4aa65ccce8be01b4c9a48541abd"
        retrofitService.searchToDoList(keyword, header).enqueue(object : Callback<ArrayList<ToDo>> {
            override fun onResponse(
                call: Call<ArrayList<ToDo>>,
                response: Response<ArrayList<ToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body()
                    makeToDoList(todoList!!)
                }
            }

            override fun onFailure(call: Call<ArrayList<ToDo>>, t: Throwable) {
            }
        })
    }

    fun makeToDoList(todoList: ArrayList<ToDo>) {
        todoRecyclerView.adapter = ToDoListRecyclerViewAdapter(
            todoList!!,
            LayoutInflater.from(this@ToDoActivity),
            this@ToDoActivity
        )
    }

    fun changeToDoComplete(todoId: Int, activity: ToDoActivity) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        header["Authorization"] = "token " + "d29cb15006f4e4aa65ccce8be01b4c9a48541abd"

        retrofitService.changeToDoComplete(header, todoId).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                // ??????????????? ????????? ????????? ???????????? ???????????? ???????????? ?????? ?????????
                // ????????? ?????? ????????? ????????? ?????? ??????. ????????? onClickListener ?????? getToDoList() ?????????
                // ?????? ??????????????? ????????? ????????? ?????? ?????? ???????????? ??????. ????????? change ??? ????????? ??????
                // List ??? ?????? ?????? ????????? ??????.
                activity.getToDoList()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                activity.getToDoList()
            }
        })
    }

    fun getToDoList() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        header["Authorization"] = "token " + "d29cb15006f4e4aa65ccce8be01b4c9a48541abd"
//        header["Authorization"] = "token " + "cfb42388ec5e95afea475a172d906a1e1ac2da2e"

        retrofitService.getToDoList(header).enqueue(object : Callback<ArrayList<ToDo>> {
            override fun onResponse(
                call: Call<ArrayList<ToDo>>,
                response: Response<ArrayList<ToDo>>
            ) {
                if (response.isSuccessful) {
                    val todoList = response.body()
                    makeToDoList(todoList!!)
                }
            }

            override fun onFailure(call: Call<ArrayList<ToDo>>, t: Throwable) {
            }
        })
    }
}

class ToDoListRecyclerViewAdapter(
    val todoList: ArrayList<ToDo>,
    val inflater: LayoutInflater,
    // resource ??? imageView ??? ???????????? ???????????? ?????? ??????
    val activity: ToDoActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // ????????? ?????? Content ??? ???????????? ?????? date ??? ????????????.
    var previousDate: String = ""

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView

        init {
            dateTextView = itemView.findViewById(R.id.date)
        }
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView
        val isComplete: ImageView

        init {
            content = itemView.findViewById(R.id.content)
            isComplete = itemView.findViewById(R.id.is_complete)
            isComplete.setOnClickListener {
                activity.changeToDoComplete(todoList[adapterPosition].id, activity)
            }
        }
    }

    // DateViewHolder ??? ContentViewHolder ??? ????????? ?????? ???????????? ??????
    override fun getItemViewType(position: Int): Int {
        val todo = todoList[position]
        // 2022-03-04T11:37:17.394810Z ??? ?????? ????????? DATE ?????? T ??? ???????????? ???????????? ??????
        // ???????????? ??? ??? ??? ?????? ?????? ?????? ????????? ??? ????????????
        val tempDate = todo.created.split("T")[0]
        // ????????? ?????? ??? previousDate ??? ?????? ""(??????)??????.
        // ????????? tempDate ??? ?????? ????????? previousDate ??? ?????? 1??? ?????? ?????????.
        // ????????? ?????? ????????? item ??? ???????????? 0??? ?????????????????? viewHolder ??? ??? ?????? ????????? ????????? ??? ??????.
        if (previousDate == tempDate) {
            return 0
        } else {
            previousDate = tempDate
            return 1
        }
    }

    // View ??? ??? ?????? ???????????? ViewHolder ??? ??? ??? ????????? ????????? ??????.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> return DateViewHolder(inflater.inflate(R.layout.todo_date, parent, false))
            else -> return ContentViewHolder(inflater.inflate(R.layout.todo_content, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = todoList[position]
        // == ?????? ??????, is ????????? ??????
        if (holder is DateViewHolder) {
            (holder as DateViewHolder).dateTextView.text = todo.created.split("T")[0]
        } else {
            (holder as ContentViewHolder).content.text = todo.content
            if (todo.is_complete) {
                (holder as ContentViewHolder).isComplete.setImageDrawable(
                    activity.resources.getDrawable(
                        R.drawable.btn_radio_check, activity.theme
                    )
                )
            } else {
                (holder as ContentViewHolder).isComplete.setImageDrawable(
                    activity.resources.getDrawable(
                        R.drawable.btn_radio, activity.theme
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return todoList.size
    }
}