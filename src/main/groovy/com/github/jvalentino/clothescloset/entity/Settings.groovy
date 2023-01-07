package com.github.jvalentino.clothescloset.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "settings")
class Settings {

    @Id @GeneratedValue
    Long id
    String gender
    Integer quantity
    String label

}
