package com.github.jvalentino.clothescloset.config

import com.github.jvalentino.clothescloset.entity.SpringSession
import com.github.jvalentino.clothescloset.repo.SpringSessionRepository
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * A mess of a hack in an attempt to use a session token after JWT
 * @wuthor john.valentino
 */
@Service
@Configurable
@CompileDynamic
@SuppressWarnings(['UnnecessaryGetter', 'UnnecessarySetter', 'DuplicateStringLiteral'])
@Slf4j
class SecurityFilter extends GenericFilterBean {

    @Autowired
    SpringSessionRepository springSessionRepository

    @Override
    void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (springSessionRepository == null) {
            log.info("No springSessionRepository instance: ${request}")
            chain.doFilter(request, response)
            return
        }

        // pull the token out of the header
        HttpServletRequest httpRequest = (HttpServletRequest) request
        String token = httpRequest.getHeader('x-auth-token')
        if (token == null) {
            token = httpRequest.getHeader('X-Auth-Token')
        }

        // react likes to strip off headers no matter what I do,
        // so we will look for it as a parameter
        if (token == null) {
            token = httpRequest.getParameter('x-auth-token')
        }

        String pathInfo = httpRequest.getRequestURI()

        // if this is insecure just ignore it
        if (SecurityConfig.INSECURES.contains(pathInfo)) {
            log.info("No security on ${pathInfo}: ${request}")
            chain.doFilter(request, response)
            return
        }

        // otherwise attempt to pull the session
        List<SpringSession> results = springSessionRepository.selectBySessionId(token)

        // if we can't find it, throw an error
        if (results.size() == 0) {
            log.error("No session ID for ${token} on ${pathInfo}: ${request}")
            throw new ServletException("No session ID for ${token} on ${pathInfo}")
        }

        SpringSession session = results.first()

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(session.principalName, token)
        SecurityContext sc = SecurityContextHolder.getContext()
        sc.setAuthentication(authReq)

        //session.setMaxInactiveInterval(86400)

        log.info("Authenticated: ${request}")

        chain.doFilter(request, response)
    }

}
