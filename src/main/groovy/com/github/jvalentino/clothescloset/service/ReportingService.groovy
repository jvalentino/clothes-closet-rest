package com.github.jvalentino.clothescloset.service

import com.github.jvalentino.clothescloset.dto.NameValuePairDto
import com.github.jvalentino.clothescloset.dto.ReportingDto
import com.github.jvalentino.clothescloset.entity.Appointment
import com.github.jvalentino.clothescloset.entity.Visit
import com.github.jvalentino.clothescloset.repo.AppointmentRepository
import com.github.jvalentino.clothescloset.repo.StudentRepository
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

    @Autowired
    StudentRepository studentRepository

    List<NameValuePairDto> generateShoeReport(Date startDate, Date endDate) {
        List<Object[]> responses = studentRepository.reportOnShoeSizes(startDate, endDate)
        List<NameValuePairDto> results = []

        for (Object[] object : responses) {
            NameValuePairDto dto = new NameValuePairDto(name:object[0], value:object[1])
            results.add(dto)
        }

        results
    }

    List<NameValuePairDto> generateUnderwearReport(Date startDate, Date endDate) {
        List<Object[]> responses = studentRepository.reportOnUnderwearSizes(startDate, endDate)
        List<NameValuePairDto> results = []

        for (Object[] object : responses) {
            NameValuePairDto dto = new NameValuePairDto(name:object[0], value:object[1])
            results.add(dto)
        }

        results
    }

    ReportingDto generateReport(Date startDate, Date endDate, String timeZone) {
        ReportingDto result = new ReportingDto()
        result.with {
            start = DateUtil.dateToFriendlyMonthDayYear(startDate, timeZone)
            end = DateUtil.dateToFriendlyMonthDayYear(endDate, timeZone)
        }

        List<Appointment> appointments = appointmentRepository.findWithVisits(startDate, endDate)
        for (Appointment appointment : appointments) {
            result.appointments++

            for (Visit visit : appointment.visits) {
                if (visit.student != null) {
                    result.students++
                } else {
                    result.persons++
                }

                result.totalPeople++

                if (visit.socks != null) {
                    result.socks += visit.socks
                    result.total += visit.socks
                }

                if (visit.underwear != null) {
                    result.underwear += visit.underwear
                    result.total += visit.underwear
                }

                if (visit.shoes != null) {
                    result.shoes += visit.shoes
                    result.total += visit.shoes
                }

                if (visit.coats != null) {
                    result.coats += visit.coats
                    result.total += visit.coats
                }

                if (visit.backpacks != null) {
                    result.backpacks += visit.backpacks
                    result.total += visit.backpacks
                }

                if (visit.misc != null) {
                    result.misc += visit.misc
                    result.total += visit.misc
                }
            }
        }

        result
    }

}
