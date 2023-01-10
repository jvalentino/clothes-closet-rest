package com.github.jvalentino.clothescloset

import com.github.jvalentino.clothescloset.entity.SpringSession
import com.github.jvalentino.clothescloset.repo.SpringSessionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@Service
@Configurable
class SecurityFilter extends GenericFilterBean {

    @Autowired
    SpringSessionRepository springSessionRepository

    @Override
    void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        println 'SecurityFilter'
        println springSessionRepository.toString()
        if (springSessionRepository == null) {
            println 'springSessionRepository is null'
           chain.doFilter(request, response)

           return
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request
        String token = httpRequest.getHeader('x-auth-token')

        String pathInfo = httpRequest.getRequestURI()

        //println webSecurityCustomizer.properties.toString()

        if (pathInfo == '/oauth') {
            println 'path info is oath'
            chain.doFilter(request, response)
            return
        }

        List<SpringSession> results = springSessionRepository.selectBySessionId(token)

        if (results.size() == 0) {
            throw new ServletException("No session ID for ${token}")
        }

        SpringSession session = results.first()

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(session.principalName, token)
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(authReq);


        chain.doFilter(request, response);

    }
}
