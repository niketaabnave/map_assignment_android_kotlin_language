package com.sumasoft.coffeeshopfinder.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



/**
 * Created by sumasoft on 26/09/17.
 */
class CoffeeShopResponse {
    /**
     *
     * @return
     * The htmlAttributions
     */
    /**
     *
     * @param htmlAttributions
     * The html_attributions
     */
    @SerializedName("html_attributions")
    @Expose
    var htmlAttributions: List<Any> = ArrayList()
    /**
     *
     * @return
     * The nextPageToken
     */
    /**
     *
     * @param nextPageToken
     * The next_page_token
     */
    @SerializedName("next_page_token")
    @Expose
    var nextPageToken: String? = null
    /**
     *
     * @return
     * The results
     */
    /**
     *
     * @param results
     * The results
     */
    @SerializedName("results")
    @Expose
    var results: List<Result> = ArrayList<Result>()
    /**
     *
     * @return
     * The status
     */
    /**
     *
     * @param status
     * The status
     */
    @SerializedName("status")
    @Expose
    var status: String? = null
}