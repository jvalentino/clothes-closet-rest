package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.dto.SettingsDto
import com.github.jvalentino.clothescloset.dto.UploadAcceptedDto
import com.github.jvalentino.clothescloset.entity.AcceptedId
import com.github.jvalentino.clothescloset.entity.School
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.repo.AcceptedIdRepository
import com.github.jvalentino.clothescloset.repo.SchoolRepository
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import com.opencsv.bean.CsvToBeanBuilder
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

/**
 * Lists the general settings
 * @author john.valentino
 */
@CompileDynamic
@RestController
class SettingsController {

    @Autowired
    SettingsRepository settingsRepository

    @Autowired
    AcceptedIdRepository acceptedIdRepository

    @Autowired
    SchoolRepository schoolRepository

    @GetMapping('/settings')
    SettingsDto all() {
        SettingsDto result = new SettingsDto()

        result.settings = settingsRepository.retrieveAll()

        result
    }

    @PostMapping('/settings')
    ResultDto saveSetting(@Valid @RequestBody Settings setting) {
        settingsRepository.save(setting)
        new ResultDto()
    }

    @PostMapping('/settings/delete')
    ResultDto deleteSetting(@RequestParam Long id) {
        settingsRepository.deleteById(id)
        new ResultDto()
    }

    @PostMapping('/settings/upload/accepted')
    ResultDto uploadAccepted(@Valid @RequestBody UploadAcceptedDto payload) {
        String csv = new String(payload.payloadBase64.decodeBase64())

        // convert CSV to entity
        List<AcceptedId> records = new CsvToBeanBuilder(new StringReader(csv))
                .withType(AcceptedId)
                .build()
                .parse()

        // remove the first record because it is the title row
        records.remove(0)

        for (AcceptedId entity : records) {
            entity.studentId = entity.studentId.trim()
            entity.school = entity.school.trim()
            entity.grade = entity.grade.trim()
        }

        // delete all current records
        acceptedIdRepository.deleteAll()

        // add new records for everything given
        acceptedIdRepository.saveAll(records)

        // pull out all the schools
        List<String> schools = acceptedIdRepository.findSchools()

        // create records for all the schools
        List<School> schoolRecords = []
        for (String school : schools) {
            schoolRecords.add(new School(label:school))
        }

        schoolRepository.saveAll(schoolRecords)

        new ResultDto()
    }

}
