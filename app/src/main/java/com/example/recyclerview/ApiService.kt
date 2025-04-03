package com.example.recyclerview

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {
    @FormUrlEncoded
    @POST("get_orders.php")
    fun getOrders(
        @Field("order_id") orderId:String
    ): Call<List<D_order>>
}