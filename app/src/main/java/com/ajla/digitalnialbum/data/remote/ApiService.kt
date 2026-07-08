package com.ajla.digitalnialbum.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/all-players")
    suspend fun getAllPlayersRaw(): ResponseBody

    @GET("api/grbovi")
    suspend fun getAllCrestsRaw(): ResponseBody

    @GET("api/random-players-unique/{count}")
    suspend fun openPacketRaw(@Path("count") count: Int): ResponseBody
}