package com.example.mysolelife

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("v3/e4f5a09c-7bd1-4e20-89a0-dbd17da8ae54")
    fun getHouseList(): Call<HouseDto>
}