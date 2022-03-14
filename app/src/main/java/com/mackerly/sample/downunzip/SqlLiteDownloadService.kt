package com.mackerly.sample.downunzip

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SqlLiteDownloadService {
    @GET("/{releaseYear}/{fileName}")
    fun requestZipFile(
        @Path("releaseYear") releaseYear: Int,
        @Path("fileName") fileName: String)
    : Call<ResponseBody>
}