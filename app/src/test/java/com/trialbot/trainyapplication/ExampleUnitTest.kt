package com.trialbot.trainyapplication

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun drawable_ids_checking() {
        var founded = false
        for (i in R.drawable.av_done .. R.drawable.rounded_corner) {
            if (i == 2131165285) {
                founded = true
                break
            }
        }

        assertEquals(true, founded)
    }
}