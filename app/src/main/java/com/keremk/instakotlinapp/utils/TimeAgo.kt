package com.keremk.instakotlinapp.utils

class TimeAgo {



    companion object {

        fun getTimeAgo(time: Long): String? {
            val SECOND_MILLIS = 1000
            val MINUTE_MILLIS = 60 * SECOND_MILLIS
            val HOUR_MILLIS = 60 * MINUTE_MILLIS
            val DAY_MILLIS = 24 * HOUR_MILLIS
            var time = time
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now: Long = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }

            // TODO: localize
            val diff: Long = now - time

            return if (diff < MINUTE_MILLIS) {
                "Az önce"
            } else if (diff < 2 * MINUTE_MILLIS) {
                "Bir dakika önce"
            } else if (diff < 50 * MINUTE_MILLIS) {
                (diff / MINUTE_MILLIS).toString() + " minutes ago"
            } else if (diff < 90 * MINUTE_MILLIS) {
                "Bir saat önce"
            } else if (diff < 24 * HOUR_MILLIS) {
                (diff / HOUR_MILLIS).toString() + " saat Önce"
            } else if (diff < 48 * HOUR_MILLIS) {
                "Dün"
            } else {
                (diff / DAY_MILLIS).toString() + " gün önce"
            }
        }
        fun getTimeAgoForComments(time: Long): String? {
            val SECOND_MILLIS = 1000
            val MINUTE_MILLIS = 60 * SECOND_MILLIS
            val HOUR_MILLIS = 60 * MINUTE_MILLIS
            val DAY_MILLIS = 24 * HOUR_MILLIS
            var time = time
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now: Long = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }

            // TODO: localize
            val diff: Long = now - time

            return if (diff < MINUTE_MILLIS) {
                "Az önce"
            } else if (diff < 2 * MINUTE_MILLIS) {
                "1d"
            } else if (diff < 50 * MINUTE_MILLIS) {
                (diff / MINUTE_MILLIS).toString() + "d"
            } else if (diff < 90 * MINUTE_MILLIS) {
                "1s"
            } else if (diff < 24 * HOUR_MILLIS) {
                (diff / HOUR_MILLIS).toString() + "s"
            } else if (diff < 48 * HOUR_MILLIS) {
                "Dün"
            } else {
                (diff / DAY_MILLIS).toString() + "g"
            }
        }
    }
}