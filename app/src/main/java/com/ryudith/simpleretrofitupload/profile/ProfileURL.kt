package com.ryudith.simpleretrofitupload.profile

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileURL {
    @Multipart
    @POST("upload_file.php")
    suspend fun updateProfile (
        @Part("name") name : String,
        @Part("email") email : String,
        @Part profile : MultipartBody.Part,
        @Part photo : List<MultipartBody.Part>
    ) : ProfileResponse
}