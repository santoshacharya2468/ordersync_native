package com.orderpush.app.core.extension

import android.os.Build
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter


fun Instant.format(pattern:String):String{

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.toLocalDateTime(TimeZone.currentSystemDefault())
                .toJavaLocalDateTime()
                .format(DateTimeFormatter.ofPattern(pattern))
    } else {
        return this.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
    }

}