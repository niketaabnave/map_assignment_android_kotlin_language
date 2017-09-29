package com.sumasoft.coffeeshopfinder.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by sumasoft on 26/09/17.
 */

class Location {
    /**
     *
     * @return
     * The lat
     */
    /**
     *
     * @param lat
     * The lat
     */
    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    /**
     *
     * @return
     * The lng
     */
    /**
     *
     * @param lng
     * The lng
     */
    @SerializedName("lng")
    @Expose
    var lng: Double? = null
}
