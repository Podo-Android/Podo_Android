package com.meong.podoandroid.network

import com.meong.podoandroid.ui.map.get.GetLocationListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NetworkService {

    @GET("v2/local/search/keyword.json")
    fun getLocationList(
            @Header("Authorization") header: String,
            @Query("query") query: String
    ): Call<GetLocationListResponse>

}