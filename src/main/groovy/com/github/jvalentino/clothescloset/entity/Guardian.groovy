package com.github.jvalentino.clothescloset.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "guardian")
class Guardian {

    @Id
    String email

    @Column(name = "first_name")
    @NotBlank(message = "firstName cannot be blank")
    String firstName

    @Column(name = "last_name")
    String lastName

    @Column(name = "phone_number")
    String phoneNumber

    @Column(name = "phone_type_label")
    String phoneTypeLabel

}
