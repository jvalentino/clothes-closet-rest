package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

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

}
