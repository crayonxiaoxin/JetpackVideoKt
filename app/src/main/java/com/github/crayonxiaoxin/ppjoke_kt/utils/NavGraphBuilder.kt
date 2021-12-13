package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.content.ComponentName
import androidx.fragment.app.FragmentActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.github.crayonxiaoxin.lib_common.global.AppGlobals

object NavGraphBuilder {
    fun build(controller: NavController, activity: FragmentActivity, containerId: Int) {
        val provider = controller.navigatorProvider
        val fragmentNavigator = provider.getNavigator(FragmentNavigator::class.java)
        val activityNavigator = provider.getNavigator(ActivityNavigator::class.java)
        provider.addNavigator(fragmentNavigator)
        provider.addNavigator(activityNavigator)
        val navGraph = NavGraph(NavGraphNavigator(provider))
        val destConfig = AppConfig.getDestConfig()
        destConfig.values.forEach {
            if (it.isFragment) {
                val dest = fragmentNavigator.createDestination()
                dest.className = it.clazzName
                dest.id = it.id
                dest.addDeepLink(it.pageUrl)
                navGraph.addDestination(dest)
            } else {
                val dest = activityNavigator.createDestination()
                dest.setComponentName(
                    ComponentName(
                        AppGlobals.application.packageName,
                        it.clazzName
                    )
                )
                dest.id = it.id
                dest.addDeepLink(it.pageUrl)
                navGraph.addDestination(dest)
            }
            if (it.asStarter) {
                navGraph.startDestination = it.id
            }
        }
        controller.graph = navGraph
    }
}