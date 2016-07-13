package com.giu.service

import com.turner.domain.Enterprise
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

/**
 * Created by mbritez on 5/11/15.
 */
class JdbcEnterpriseServiceTests extends Specification {

    JdbcEnterpriseService service
    JdbcTemplate mockedTemplate

    private static final String USERNAME = "aUsername"

    def "test get enterprises"(){
        setup:
        List<Enterprise> enterprises = []
        mockedTemplate = Mock() {
            query(_,_,_) >> enterprises
        }
        service = new JdbcEnterpriseService(mockedTemplate);

        when:
        List<Enterprise> result = service.getEnterprises(USERNAME)

        then:
        result != null
        result == enterprises
    }

}
