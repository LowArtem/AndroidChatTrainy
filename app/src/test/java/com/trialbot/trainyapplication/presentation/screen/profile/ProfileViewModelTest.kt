//package com.trialbot.trainyapplication.presentation.screen.profile
//
//import org.junit.Assert.assertEquals
//import org.junit.Test
//import java.text.SimpleDateFormat
//import java.util.*
//
//class ProfileViewModelTest {
//
//    @Test
//    fun lastSeenCounterToday() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2021-12-06 10:05:59")
//
//        val expected: String = "at 10:05"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//
//    @Test
//    fun lastSeenCounterYesterday() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2021-12-05 06:03:59")
//
//        val expected: String = "Yesterday at 06:03"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//
//    @Test
//    fun lastSeenCounterThisWeek() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2021-11-30 14:00:59")
//
//        val expected: String = "30.11"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//
//    @Test
//    fun lastSeenCounterThisMonth() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2021-11-18 18:00:59")
//
//        val expected: String = "18.11"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//
//    @Test
//    fun lastSeenCounterThisYear() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2021-06-12 14:00:59")
//
//        val expected: String = "06.2021"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//
//    @Test
//    fun lastSeenCounterOneYearAgo() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2020-12-05 14:00:59")
//
//        val expected: String = "12.2020"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//
//    @Test
//    fun lastSeenCounterFiveYearsAgo() {
//        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//        val date: Date? = format.parse("2016-12-05 14:00:59")
//
//        val expected: String = "12.2016"
//        assertEquals(expected, ProfileViewModel.lastSeenCounter(date!!))
//    }
//}