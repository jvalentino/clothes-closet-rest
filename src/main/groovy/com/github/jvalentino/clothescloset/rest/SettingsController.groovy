package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
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

    @GetMapping('/settings')
    List<Settings> all() {
        settingsRepository.retrieveAll()
    }

    @PostMapping('/settings')
    ResultDto saveSetting(@Valid @RequestBody Settings setting) {
        settingsRepository.save(setting)
        new ResultDto()
    }

    @DeleteMapping('/settings')
    ResultDto deleteSetting(@RequestParam Long id) {
        settingsRepository.deleteById(id)
        new ResultDto()
    }

}
