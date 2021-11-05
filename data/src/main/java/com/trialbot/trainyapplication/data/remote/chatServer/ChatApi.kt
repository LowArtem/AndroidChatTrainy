package com.trialbot.trainyapplication.data.remote.chatServer

import com.trialbot.trainyapplication.domain.model.*
import retrofit2.http.*
import java.util.*

interface ChatApi {

    // Messages

    @GET("./messages/GetAllMessages")
    suspend fun getAllMessages(@Query("chatId") chatId: Long): List<MessageDTO>

    @GET("./messages/GetMessagesByUser")
    suspend fun getMessagesByUser(
        @Query("Id") id: Long,
        @Query("Username") username: String
    ): List<MessageDTO>

    @GET("./messages/GetMessagesPage")
    suspend fun getMessagesPage(
        @Query("chatId") chatId: Long,
        @Query("messagesCount") messagesCount: Int,
        @Query("startIndex") startIndex: Int
    ): List<MessageDTO>

    @POST("messages/SaveMessage/{chatId}")
    suspend fun saveMessage(
        @Path("chatId") chatId: Long,
        @Body message: MessageWithAuthUser
    )

    @DELETE("./messages/DeleteMessage")
    suspend fun deleteMessage(
        @Query("chatId") chatId: Long,
        @Query("messageId") messageId: Long
    )

    // Users

    @GET("./users/GetUserByUsernameAndPassword")
    suspend fun getUserByUsernameAndPassword(
        @Query("username") username: String,
        @Query("password") password: String
    ): UserWithoutPassword

    @GET("./users/FindUserByUsername")
    suspend fun findUsersByUsername(@Query("username") username: String): List<UserWithoutPassword>

    @GET("./users/GetUserIsOnline")
    suspend fun getUserIsOnline(@Query("Id") id: Long): Boolean

    @GET("./users/GetUserLastDate")
    suspend fun getUserLastDate(@Query("Id") id: Long): Date

    @POST("./users/SaveUser")
    suspend fun saveUser(@Body user: UserFull): UserFull

    @DELETE("./users/DeleteUser")
    suspend fun deleteUser(@Body user: UserWithoutIcon)

    @PUT("users/UpdateUser/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserFull)

    // Chats

    @GET("./chats/GetAllChatsByUser")
    suspend fun getAllChatsByUser(@Query("userId") userId: Long): List<ChatInfo>

    @GET("chats/GetChat/{id}")
    suspend fun getChat(@Path("id") id: Long): ChatDetails

    @GET("./chats/GetChatMembers")
    suspend fun getChatMembers(@Query("chatId") chatId: Long): List<UserWithoutPassword>

    @POST("./chats/CreateChat")
    suspend fun createChat(@Body newChat: ChatCreating): Long

    @GET("./chats/CreateDialog")
    suspend fun createDialog(
        @Query("creatorId") creatorId: Long,
        @Query("secondUserId") secondUserId: Long
    ): Long

    @PUT("chats/Update/{id}")
    suspend fun updateChat(@Path("id") id: Long, @Body chat: ChatUpdating)

    @DELETE("./chats/DeleteChat")
    suspend fun deleteChat(
        @Query("chatId") chatId: Long,
        @Query("currentUserId") currentUserId: Long
    )

    @GET("./chats/FindChats")
    suspend fun findChats(@Query("name") name: String): List<ChatInfo>

    @POST("./chats/AddMember")
    suspend fun addMember(
        @Query("chatId") chatId: Long,
        @Query("userId") userId: Long
    )

    @DELETE("./chats/DeleteMember")
    suspend fun deleteMember(
        @Query("chatId") chatId: Long,
        @Query("userId") userId: Long
    )

    @GET("./chats/GetAdministratorIds")
    suspend fun getAdminIds(@Query("chatId") chatId: Long): List<Long>

    @POST("./chats/AddAdministrator")
    suspend fun addAdmin(
        @Query("chatId") chatId: Long,
        @Query("userId") userId: Long
    )

    @DELETE("./chats/DeleteAdministrator")
    suspend fun deleteAdmin(
        @Query("chatId") chatId: Long,
        @Query("adminId") adminId: Long
    )
}







