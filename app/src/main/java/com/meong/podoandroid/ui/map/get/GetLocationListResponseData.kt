package com.meong.podoandroid.ui.map.get

import android.view.View

data class GetLocationListResponseData(

        var address_name: String?,
        var road_address_name: String?,
        var category_group_code: String?,
        var category_group_name: String?,
        var category_name: String?,
        var distance: String?,
        var id: String?,
        var phone: String?,
        var place_name: String?,
        var place_url: String?,
        var x: String?, //long
        var y: String?, // lat
        var dot: Boolean? = false
)

