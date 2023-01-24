package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.util.BaseIntg
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc

class AppointmentControllerIntgTest extends BaseIntg {

    @Autowired
    MockMvc mockMvc

    def "test schedule"() {
        when:
        true

        then:
        true
    }

}
