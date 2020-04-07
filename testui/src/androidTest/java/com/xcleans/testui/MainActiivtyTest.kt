package com.xcleans.testui

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.toPackage

import android.support.test.runner.AndroidJUnit4
import org.hamcrest.core.AllOf.allOf
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by mengliwei on 2020/4/2.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActiivtyTest {

    @Test
    fun testJumpActivity(){
        // 点击对应按钮
        onView(withId(R.layout.test)).
            perform(click()).
            check(doesNotExist())

        // 是否有对应的intent产生
        intending(allOf(
            toPackage(packageName()),    //  包路径
            hasComponent(hasShortClassName(shortName()))  //类的shortClassName
        ))



        // 点击返回键，检查是否回到当前界面
        pressBack()

        onView(withId(R.layout.test)).check(matches(isDisplayed()))

    }

}