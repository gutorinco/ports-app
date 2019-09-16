package br.com.suitesistemas.portsmobile.firebase

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ApplicationLifecycleManager : Application.ActivityLifecycleCallbacks {

    private companion object {
        var visibleActivityCount = 0
        var foregroundActivityCount = 0
    }

    override fun onActivityPaused(activity: Activity?) {
        foregroundActivityCount =- 1
    }

    override fun onActivityResumed(activity: Activity?) {
        foregroundActivityCount =+ 1
    }

    override fun onActivityStarted(activity: Activity?) {
        visibleActivityCount =+ 1
    }

    override fun onActivityStopped(activity: Activity?) {
        visibleActivityCount =- 1
    }

    override fun onActivityDestroyed(activity: Activity?) = TODO("not implemented")

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = TODO("not implemented")

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) = TODO("not implemented")

}