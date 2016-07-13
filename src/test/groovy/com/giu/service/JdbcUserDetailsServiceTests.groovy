package com.giu.service

import com.turner.domain.AlephUser
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

/**
 * Created by mbritez on 5/11/15.
 */
class JdbcUserDetailsServiceTests extends Specification {

    JdbcUserDetailsService service
    JdbcTemplate mockedTemplate;

    private static final String USERNAME = "aUsername"

    def "test map user from context"(){
        setup:
        AlephUser mockedResult = Mock()
        mockedTemplate = Mock() {
            queryForObject(_,_,_) >> mockedResult
        }
        service = new JdbcUserDetailsService(mockedTemplate);
        DirContextOperations mockedContext = Mock()
        List<GrantedAuthority> mockedAuthorities = Mock()

        when:
        UserDetails result = service.mapUserFromContext(
                mockedContext,
                USERNAME,
                mockedAuthorities)

        then:
        result != null
        result == mockedResult
    }

    def "test map user from context with empty result"(){
        setup:
        EmptyResultDataAccessException ex = Mock()

        mockedTemplate = Mock() {
            queryForObject(_,_,_) >> {throw ex}
        }
        service = new JdbcUserDetailsService(mockedTemplate);
        DirContextOperations mockedContext = Mock()
        List<GrantedAuthority> mockedAuthorities = Mock()

        when:
        UserDetails result = service.mapUserFromContext(
                mockedContext,
                USERNAME,
                mockedAuthorities)

        then:
        result == null
    }

    def "test map user to context"() {
        setup:
        mockedTemplate = Mock()
        service = new JdbcUserDetailsService(mockedTemplate);
        DirContextAdapter mockedContext = Mock()
        UserDetails mockedUser = Mock()

        when:
        service.mapUserToContext(
                mockedUser,
                mockedContext)

        then:
        true

    }

}
