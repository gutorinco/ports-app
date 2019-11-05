package br.com.suitesistemas.portsmobile.service.grid.item

import androidx.lifecycle.MutableLiveData
import br.com.suitesistemas.portsmobile.custom.extensions.responseHandle
import br.com.suitesistemas.portsmobile.custom.retrofit.RetrofitConfig
import br.com.suitesistemas.portsmobile.model.ApiResponse
import br.com.suitesistemas.portsmobile.model.entity.GridItem

class GridItemRepository(private val companyName: String) {

    fun findAllBy(gridId: Int): MutableLiveData<ApiResponse<MutableList<GridItem>?>> {
        val apiResponse = MutableLiveData<ApiResponse<MutableList<GridItem>?>>()
        val call = RetrofitConfig().gridItemService().findAllBy(companyName, gridId)

        call.responseHandle(200) {
            apiResponse.value = it
        }

        return apiResponse
    }

}