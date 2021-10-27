package com.trialbot.trainyapplication.data.remote.chatServer

import com.trialbot.trainyapplication.data.model.*
import retrofit2.http.*

interface ChatApi {

    // Messages

    @GET("./messages/GetAllMessages")
    suspend fun getAllMessages(): List<MessageDTO>

    @GET("./messages/GetMessagesByUser")
    suspend fun getMessagesByUser(
        @Query("Id") id: Long,
        @Query("Username") username: String
    ): List<MessageDTO>

    @POST("./messages/SaveMessage")
    suspend fun saveMessage(@Body message: MessageWithAuthUser)

    // Users

    @GET("./users/GetUserByUsernameAndPassword")
    suspend fun getUserByUsernameAndPassword(
        @Query("username") username: String,
        @Query("password") password: String
    ): UserWithoutPassword

    @GET("./users/GetUserIsOnline")
    suspend fun getUserIsOnline(@Query("Id") id: Long): Boolean

    @POST("./users/SaveUser")
    suspend fun saveUser(@Body user: UserFull): UserFull

    @DELETE("./users/DeleteUser")
    suspend fun deleteUser(@Body user: UserWithoutIcon)

    @PUT("users/UpdateUser/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserFull)
}