package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.Settings
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for Settings
 * @author john.valentino
 */
interface SettingsRepository extends JpaRepository<Settings, Long> {

    @Query('select distinct settings from Settings settings order by settings.gender, settings.label')
    List<Settings> retrieveAll()

}
