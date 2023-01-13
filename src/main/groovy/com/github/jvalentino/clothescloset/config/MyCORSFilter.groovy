package com.github.jvalentino.clothescloset.config

import groovy.transform.CompileDynamic
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Yet another hack to get CORS to work with React
 * @author john.valentino
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@CompileDynamic
@SuppressWarnings(['UnnecessaryObjectReferences'])
class MyCORSFilter implements Filter {

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req
        HttpServletResponse response = (HttpServletResponse) res

        response.setHeader('Access-Control-Allow-Origin', request.getHeader('Origin'))
        response.setHeader('Access-Control-Allow-Credentials', 'true')
        response.setHeader('Access-Control-Allow-Methods', 'POST, GET, OPTIONS, DELETE, PUT')
        response.setHeader('Access-Control-Max-Age', '3600')
        response.setHeader('Access-Control-Allow-Headers',
                'Content-Type, Accept, X-Requested-With, x-auth-token, X-Auth-Token')
        response.setHeader('Access-Control-Expose-Headers', 'X-Auth-Token')

        chain.doFilter(req, res)
    }

    @Override
    void init(FilterConfig filterConfig) {
    }

    @Override
    void destroy() {
    }

}
