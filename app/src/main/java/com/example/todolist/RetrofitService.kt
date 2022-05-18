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

    @POST("to-do/")
    @FormUrlEncoded
    fun makeToDo(
//    아래와 같이 "Tpdp" 객체를 보내도 되지만 "created" 부분은 서버에서 자동으로 만들어지는
//    필드이기 때문이 넣어줄 필요가 없다.
//    @Body todo: ToDo
        @HeaderMap headers: Map<String, String>,
        @FieldMap params: HashMap<String, Any>
    ): Call<Any>
}