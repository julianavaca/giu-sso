package com.giu.controller

import com.turner.domain.AlephUser
import com.turner.domain.Enterprise
import com.turner.domain.ServiceComposite
import com.turner.service.JdbcEnterpriseService
import com.turner.service.JdbcServicesService
import org.springframework.security.authentication.DisabledException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model
import org.springframework.web.servlet.ModelAndView
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import java.security.Principal

/**
 * Created by mbritez on 5/11/15.
 */
class UserControllerTests extends Specification {

    UserController controller;
    JdbcEnterpriseService mockedEnterpriseService;
    JdbcServicesService mockedServicesService;

    def "get user"() {
        setup:
        mockedEnterpriseService = Mock()
        mockedServicesService = Mock()
        Principal mockedUser = Mock()
        controller = new UserController(mockedServicesService,mockedEnterpriseService)

        when:
        Principal result = controller.user(mockedUser)

        then:
        result != null
        result == mockedUser
    }

    def "get login"(){
        setup:
        mockedEnterpriseService = Mock()
        mockedServicesService = Mock()
        Boolean mockedError = false;
        Model mockedModel = Mock() {
            asMap() >> ["error":false, blocked: false]
        }

        DisabledException ex = Mock()

        HttpSession mockedSession = Mock(){
            getAttribute(_) >> ex
        }

        HttpServletRequest mockedRequest = Mock() {
            getSession() >> mockedSession
        }

        controller = new UserController(mockedServicesService,mockedEnterpriseService)

        when:
        ModelAndView result = controller.login(
                mockedError,
                mockedModel,
                mockedRequest)

        then:
        result != null
        result.getModel().get("blocked") == false
        result.getViewName() == "login"
    }

    def "get login enabled"(){
        setup:
        mockedEnterpriseService = Mock()
        mockedServicesService = Mock()
        Boolean mockedError = false;
        Model mockedModel = Mock() {
            asMap() >> ["error":false, blocked: true]
        }

        HttpSession mockedSession = Mock(){
            getAttribute(_) >> null
        }

        HttpServletRequest mockedRequest = Mock() {
            getSession() >> mockedSession
        }

        controller = new UserController(mockedServicesService,mockedEnterpriseService)

        when:
        ModelAndView result = controller.login(
                mockedError,
                mockedModel,
                mockedRequest)

        then:
        result != null
        result.getModel() == mockedModel.asMap()
        result.getModel().get("blocked") == true
        result.getViewName() == "login"
    }

    def "get services"() {
        setup:
        AlephUser alephUser = Mock(){
            geDbUser() >> "aUsername"
        }
        Authentication mockedAuthentication = Mock() {
            getPrincipal() >> alephUser;
        }
        SecurityContextHolder.getContext().setAuthentication(mockedAuthentication)

        mockedEnterpriseService = Mock()
        Enterprise mockedEnterprise = Mock()
        List<Enterprise> mockedResult = [mockedEnterprise]
        mockedServicesService = Mock() {
            get("aUsername",_) >> mockedResult
        }
        controller = new UserController(mockedServicesService,mockedEnterpriseService)

        when:
        List<ServiceComposite> result = controller.getServices()

        then:
        result != null
        result == mockedResult
    }

    def "get enterprises"() {
        setup:
        AlephUser alephUser = Mock(){
            geDbUser() >> "aUsername"
        }
        Authentication mockedAuthentication = Mock() {
            getPrincipal() >> alephUser;
        }
        SecurityContextHolder.getContext().setAuthentication(mockedAuthentication)

        Enterprise mockedEnterprise = Mock()
        List<Enterprise> mockedResult = [mockedEnterprise]

        mockedEnterpriseService = Mock() {
            getEnterprises("aUsername") >> mockedResult
        }

        mockedServicesService = Mock()

        controller = new UserController(mockedServicesService,mockedEnterpriseService)

        when:
        List<Enterprise> result = controller.getEnterprises()

        then:
        result != null
        result == mockedResult
    }

}
