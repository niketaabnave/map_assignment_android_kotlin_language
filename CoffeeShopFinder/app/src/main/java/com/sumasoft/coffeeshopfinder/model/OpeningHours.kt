package com.sumasoft.coffeeshopfinder.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by sumasoft on 26/09/17.
 */

class OpeningHours {


    /**
     *
     * @return
     * The openNow
     */
    /**
     *
     * @param openNow
     * The open_now
     */
    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null
    /**
     *
     * @return
     * The weekdayText
     */
    /**
     *
     * @param weekdayText
     * The weekday_text
     */
    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<Any> = ArrayList()

}
