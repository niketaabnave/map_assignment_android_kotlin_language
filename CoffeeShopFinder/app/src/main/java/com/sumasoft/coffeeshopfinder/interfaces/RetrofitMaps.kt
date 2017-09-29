package com.sumasoft.coffeeshopfinder.interfaces

import com.sumasoft.coffeeshopfinder.model.CoffeeShopResponse

import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

/**
 * Created by sumasoft on 26/09/17.
 */

interface RetrofitMaps {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of places.
     */
    @GET("api/place/nearbysearch/json?sensor=true&key=AIzaSyAnt89d0PEIVt6EEdH5JYnD68x2N5fc7nY")
    fun getNearbyPlaces(@Query("type") type: String, @Query("location") location: String, @Query("radius") radius: Int): Call<CoffeeShopResponse>
}

