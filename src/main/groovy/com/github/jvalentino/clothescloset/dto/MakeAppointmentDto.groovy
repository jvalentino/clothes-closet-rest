package com.github.jvalentino.clothescloset.dto

import com.github.jvalentino.clothescloset.entity.Guardian
import com.github.jvalentino.clothescloset.entity.Student

class MakeAppointmentDto {

    String datetime

    Guardian guardian

    List<Student> students
}
