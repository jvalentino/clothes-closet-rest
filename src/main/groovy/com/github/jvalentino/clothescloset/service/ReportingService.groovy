package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.ReportingDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * General service used for reporting
 * @author john.valentino
 */
@CompileDynamic
@Service
@SuppressWarnings(['NestedForLoop', 'UnnecessaryObjectReferences'])
class ReportingService {

    @Autowired
    AppointmentRepository appointmentRepository

    ReportingDto generateReport(Date startDate, Date endDate, String timeZone) {
        ReportingDto result = new ReportingDto()
        result.with {
            start = DateUtil.dateToFriendlyMonthDayYear(startDate, timeZone)
            end = DateUtil.dateToFriendlyMonthDayYear(endDate, timeZone)
        }

        List<Appointment> appointments = appointmentRepository.findWithVisits(startDate, endDate)
        for (Appointment appointment : appointments) {
            result.appointents++

            for (Visit visit : appointment.visits) {
                if (visit.student != null) {
                    result.students++
                } else {
                    result.persons++
                }

                result.totalPeople++

                result.socks += visit.socks
                result.underwear += visit.underwear
                result.shoes += visit.shoes
                result.coats += visit.coats
                result.backpacks += visit.backpacks
                result.misc += visit.misc

                result.total += visit.socks + visit.underwear + visit.shoes +
                        visit.coats + visit.backpacks + visit.misc
            }
        }

        result
    }

}
