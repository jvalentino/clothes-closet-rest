package com.github.jvalentino.clothescloset.rest

import com.github.jvalentino.clothescloset.dto.ResultDto
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest

import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException

/**
 * Because we have validation, like every service should, we have to provide
 * explicit failure handling
 * @author john.valentino
 */
@Component
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@CompileDynamic
@SuppressWarnings(['DuplicateMapLiteral', 'UnnecessaryGetter'])
class ExceptionController {

    @ExceptionHandler(ConstraintViolationException)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ResultDto handleConstraintViolationException(ConstraintViolationException e) {
        ResultDto result = new ResultDto(success:false)
        for (ConstraintViolation v : e.constraintViolations) {
            result.messages.add(v.message)
        }

        result
    }

    @ExceptionHandler(MethodArgumentNotValidException)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ResultDto handleBindException(MethodArgumentNotValidException e, WebRequest request) {
        ResultDto result = new ResultDto(success:false)
        log.warn(request.toString() + " had ${e.fieldErrors.size()} field errors")
        for (FieldError v : e.fieldErrors) {
            result.messages.add(v.getDefaultMessage())
        }

        result
    }

}
