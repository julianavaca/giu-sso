package com.giu

import org.springframework.security.core.Authentication
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by mbritez on 31/07/15.
 */
class CustomLogoutSuccessHandlerTests extends Specification{

    CustomLogoutSuccessHandler handler = new CustomLogoutSuccessHandler()

    def "logout success"() {
        setup:
        def mockedRequest = Mock(HttpServletRequest){
            getParameter("redirect_uri") >> "/aRedirectURI"
        }
        def mockedResponse = Mock(HttpServletResponse)
        def mockedAuthentication = Mock(Authentication)

        when:
        handler.onLogoutSuccess(mockedRequest, mockedResponse, mockedAuthentication)

        then:
        handler.alwaysUseDefaultTargetUrl == true
        handler.defaultTargetUrl == "/aRedirectURI"
    }
}
