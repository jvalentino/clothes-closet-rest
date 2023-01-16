package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.PhoneType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for Phone TYpe
 * @author john.valentino
 */
interface PhoneTypeRepository extends JpaRepository<PhoneType, String> {

    @Query('select distinct phoneType from PhoneType phoneType order by phoneType.orderPosition')
    List<PhoneType> retrieveAll()

}
