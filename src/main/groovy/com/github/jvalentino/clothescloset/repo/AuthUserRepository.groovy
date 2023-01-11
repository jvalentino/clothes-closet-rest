package com.github.jvalentino.clothescloset.repo

import com.github.jvalentino.clothescloset.entity.AuthUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * DAO for AuthUser
 * @author john.valentino
 */
interface AuthUserRepository extends JpaRepository<AuthUser, String> {

    @Query('select distinct user from AuthUser user where user.email=?1')
    List<AuthUser> find(String email)

}
