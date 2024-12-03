package com.example.classcash

object Routes {
    var splashscreen = "splashscreen"
    var login = "login"
    var dashboard = "dashboard"
    var studentadd = "studentadd"
    var event = "event"
    var analytics = "analytics"
    var fund = "fund"
    var recommend = "recommend"
    var trview = "trview"
    var notification = "notification"
    var pbox = "pbox/{studentId}"
    var withdrawbox = "withdrawbox"
    var extfund = "extfund"
    var profile = "profile"
    var files = "files"
    var aboutsection = "aboutsection"

    fun pbox(studentId: Int) = "pbox/$studentId"
}