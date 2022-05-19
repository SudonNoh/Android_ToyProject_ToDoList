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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
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
    }

    fun changeToDoComplete(todoId: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        val header = HashMap<String, String>()
        header["Authorization"] = "token " + "d29cb15006f4e4aa65ccce8be01b4c9a48541abd"

        retrofitService.changeToDoComplete(header, todoId).enqueue(object : Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
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
                    todoRecyclerView.adapter = ToDoListRecyclerViewAdapter(
                        todoList!!,
                        LayoutInflater.from(this@ToDoActivity),
                        this@ToDoActivity
                    )
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
    // resource 는 imageView 의 이미지를 변경하기 위해 사용
    val activity: ToDoActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // 날짜에 따라 Content 를 구분하기 위해 date 를 사용한다.
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
                activity.changeToDoComplete(todoList[adapterPosition].id)
            }
        }
    }

    // DateViewHolder 와 ContentViewHolder 중 무엇을 쓸지 결정하는 함수
    override fun getItemViewType(position: Int): Int {
        val todo = todoList[position]
        // 2022-03-04T11:37:17.394810Z 와 같은 형태의 DATE 값을 T 를 기준으로 나누어서 준다
        // 나누어진 값 중 첫 번째 값인 날짜 부분만 을 받아온다
        val tempDate = todo.created.split("T")[0]
        // 첫번째 회차 때 previousDate 의 값은 ""(공백)이다.
        // 그러면 tempDate 로 받은 날짜를 previousDate 에 넣고 1을 리턴 받는다.
        // 추후에 같은 날짜의 item 이 들어오면 0을 반환함으로써 viewHolder 을 두 개로 나누어 동작할 수 있다.
        if (previousDate == tempDate) {
            return 0
        } else {
            previousDate = tempDate
            return 1
        }
    }

    // View 가 두 개인 경우에는 ViewHolder 를 두 개 만들어 주어야 한다.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> return DateViewHolder(inflater.inflate(R.layout.todo_date, parent, false))
            else -> return ContentViewHolder(inflater.inflate(R.layout.todo_content, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val todo = todoList[position]
        // == 값을 비고, is 타입을 비교
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