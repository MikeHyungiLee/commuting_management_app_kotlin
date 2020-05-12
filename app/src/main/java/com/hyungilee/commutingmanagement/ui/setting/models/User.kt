package com.hyungilee.commutingmanagement.ui.setting.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var employee_id: String? = "",
    var full_name: String? = "",
    var katakana: String? = "",
    var email: String? = "",
    var position: String? = "",
    var department: String? = "",
    var start_time: String? = "",
    var leave_time: String? = "",
    var work_location_longitude: String? = "",
    var work_location_latitude: String? = "",
    var created_at: String? = ""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "employee_id" to employee_id,
            "full_name" to full_name,
            "katakana" to katakana,
            "email" to email,
            "position" to position,
            "department" to department,
            "start_time" to start_time,
            "leave_time" to leave_time,
            "work_location_longitude" to work_location_longitude,
            "work_location_latitude" to work_location_latitude,
            "created_at"  to created_at
        )
    }
}

