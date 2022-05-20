package com.example.todolist

import retrofit2.Call
import retrofit2.http.*

class ToDo(
    val id: Int,
    val content: String,
    val is_complete: Boolean,
    val created: String
)

interface RetrofitService {

    @GET("to-do/search/")
    fun searchToDoList(
        @Query("keyword") keyword: String,
        @HeaderMap headers: Map<String, String>
    ): Call<ArrayList<ToDo>>

    @POST("to-do/")
    @FormUrlEncoded
    fun makeToDo(
//    아래와 같이 "Tpdp" 객체를 보내도 되지만 "created" 부분은 서버에서 자동으로 만들어지는
//    필드이기 때문이 넣어줄 필요가 없다.
//    @Body todo: ToDo
        @HeaderMap headers: Map<String, String>,
        @FieldMap params: HashMap<String, Any>
    ): Call<Any>

    @GET("to-do/")
    fun getToDoList(
        @HeaderMap headers: Map<String, String>,
    ): Call<ArrayList<ToDo>>

    @PUT("to-do/complete/{todoId}")
    fun changeToDoComplete(
        @HeaderMap headers: Map<String, String>,
        @Path("todoId") todoId: Int
    ): Call<Any>
}