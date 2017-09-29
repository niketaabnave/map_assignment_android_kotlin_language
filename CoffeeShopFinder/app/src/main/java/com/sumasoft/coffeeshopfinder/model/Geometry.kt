package com.sumasoft.coffeeshopfinder.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by sumasoft on 26/09/17.
 */
class Geometry {
    /**
     *
     * @return
     * The location
     */
    /**
     *
     * @param location
     * The location
     */
    @SerializedName("location")
    @Expose
    var location: Location? = null
}
