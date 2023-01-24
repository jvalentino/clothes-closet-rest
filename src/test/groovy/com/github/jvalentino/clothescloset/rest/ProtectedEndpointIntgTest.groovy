package com.github.jvalentino.clothescloset.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jvalentino.clothescloset.ClothesclosetApplication
import com.github.jvalentino.clothescloset.dto.ResultDto
import com.github.jvalentino.clothescloset.entity.Settings
import com.github.jvalentino.clothescloset.repo.SettingsRepository
import com.github.jvalentino.clothescloset.util.BaseIntg
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.transaction.annotation.Transactional

import static org.hamcrest.Matchers.containsString
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import org.springframework.test.web.servlet.MvcResult

class ProtectedEndpointIntgTest extends BaseIntg {
    @Autowired
    MockMvc mvc

    @Autowired
    SettingsRepository settingsRepository

    def "test persistence"() {
        when:
        Settings entity = new Settings()
        entity.with {
            gender = "Male"
            quantity = 1
            label = "alpha"
        }
        settingsRepository.save(entity)

        then:
        true
    }

    def "test independent transactions"() {
        when:
        true

        then:
        Assert.assertEquals(0, settingsRepository.retrieveAll().size())
    }


    def "test hitting protected endpoints"() {
        given:
        String sessionId = this.makeSession()

        when:
        MvcResult response = mvc.perform(
                get("/protected").header('x-auth-token', sessionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        String json = response.getResponse().getContentAsString()
        ResultDto result = new ObjectMapper().readValue(json, ResultDto)

        then:
        result.success
    }
}
