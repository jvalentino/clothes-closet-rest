package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.DashboardLandingDto
import com.github.jvalentino.clothescloset.service.ReportingService
import com.github.jvalentino.clothescloset.util.DateUtil
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Used for content involving the admin dashboard
 * @author john.valentino
 */
@CompileDynamic
@RestController
@Validated
@SuppressWarnings(['NoJavaUtilDate'])
class DashboardController {

    @Autowired
    ReportingService reportingService

    @GetMapping('/dashboard')
    DashboardLandingDto landing(@RequestParam(required = false, defaultValue = 'America/Chicago')
                                String timeZone) {
        Date timestamp = new Date()
        Date semesterStart = DateUtil.findSemesterStart(timestamp, timeZone)
        Date semesterEnd = DateUtil.findSemesterEnd(timestamp, timeZone)

        DashboardLandingDto result = new DashboardLandingDto(timeZone:timeZone)
        result.with {
            currentDate = timestamp
            currentDateString = DateUtil.dateToFriendlyMonthDayYear(currentDate, timeZone)
            semesterStartDate = semesterStart
            semesterStartDateString = DateUtil.dateToFriendlyMonthDayYear(semesterStart, timeZone)
            semesterEndDate = semesterEnd
            semesterEndDateString = DateUtil.dateToFriendlyMonthDayYear(semesterEnd, timeZone)
            report = reportingService.generateReport(semesterStart, semesterEnd, timeZone)
        }

        result
    }

}
