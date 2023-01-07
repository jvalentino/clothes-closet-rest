package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SettingsController {

    @Autowired
    SettingsRepository settingsRepository

    @GetMapping("/settings")
    List<Settings> all() {
        return settingsRepository.retrieveAll()
    }
}
