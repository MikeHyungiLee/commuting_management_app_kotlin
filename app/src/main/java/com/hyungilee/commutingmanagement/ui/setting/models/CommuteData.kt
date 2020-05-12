package com.hyungilee.commutingmanagement.ui.setting.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CommuteData(
    var email: String? = "",
    var num_of_paid_vacation_days: Int? = 0,
    var num_of_working_days: Int? = 0,
    var actual_working_days: Int? = 0,
    var actual_working_hours: Int? = 0,
    var num_of_absence_days: Int? = 0,
    var num_of_lateness_days: Int? = 0,
    var num_of_actual_lateness_hours: Int? = 0,
    var num_of_used_paid_vacation_days: Int? = 0,
    var num_of_early_leaving_days: Int? = 0,
    var num_of_early_leaving_hours: Int? = 0,
    var num_of_ordinary_overtime_hours: Int? = 0,
    var num_of_ordinary_midnight_overtime_hours: Int? = 0,
    var num_of_legal_holiday_working_days: Int? = 0,
    var num_of_legal_holiday_working_hours: Int? = 0,
    var num_of_legal_holiday_overtime_hours: Int? = 0,
    var num_of_legal_holiday_midnight_overtime_hours: Int? = 0,
    var created_at: String? = ""
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "num_of_paid_vacation_days" to num_of_paid_vacation_days,
            "num_of_working_days" to num_of_working_days,
            "actual_working_days" to actual_working_days,
            "actual_working_hours" to actual_working_hours,
            "num_of_absence_days" to num_of_absence_days,
            "num_of_lateness_days" to num_of_lateness_days,
            "num_of_actual_lateness_hours" to  num_of_actual_lateness_hours,
            "num_of_used_paid_vacation_days" to num_of_used_paid_vacation_days,
            "num_of_early_leaving_days" to num_of_early_leaving_days,
            "num_of_early_leaving_hours" to num_of_early_leaving_hours,
            "num_of_ordinary_overtime_hours" to num_of_ordinary_overtime_hours,
            "num_of_ordinary_midnight_overtime_hours" to num_of_ordinary_midnight_overtime_hours,
            "num_of_legal_holiday_working_days" to num_of_legal_holiday_working_days,
            "num_of_legal_holiday_working_hours" to num_of_legal_holiday_working_hours,
            "num_of_legal_holiday_overtime_hours" to num_of_legal_holiday_overtime_hours,
            "num_of_legal_holiday_midnight_overtime_hours" to num_of_legal_holiday_midnight_overtime_hours,
            "created_at" to created_at
        )
    }
}

