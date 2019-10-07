package br.com.suitesistemas.portsmobile.custom.observer

import androidx.lifecycle.Observer
import br.com.suitesistemas.portsmobile.model.ApiResponse

fun <K> observerHandler(success: (obj: K) -> Unit,
                        failure: (error: String) -> Unit,
                        finished: (() -> Unit)? = null): Observer<in ApiResponse<K?>> {
    return Observer { observer ->
        if (observer.messageError == null) {
            observer.data?.let {
                success(it)
            }
            finished?.invoke()
        } else {
            failure(observer.messageError!!)
            finished?.invoke()
        }
    }
}