package com.giu.service

import com.turner.converter.MenuConverter
import com.turner.domain.ServiceComposite
import com.turner.sp.acq.domain.MnuMenuJwfVO
import com.turner.sp.acq.pl.MnuMenuJwfPL
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

/**
 * Created by mbritez on 5/11/15.
 */
class JdbcServicesServiceTests extends Specification {

    JdbcServicesService service
    MnuMenuJwfPL mockedRepository;

    private static final String USERNAME = "aUsername"
    private static final Long ENTERPRISE_ID = 1L

    def "test get services"(){
        setup:
        JdbcTemplate mockedTemplate = Mock()

        MnuMenuJwfVO menu = Mock()
        List<MnuMenuJwfVO> storedMenu = [menu]

        ServiceComposite serviceComposite = Mock()
        List<ServiceComposite> convertedResult = [serviceComposite]

        MenuConverter mockedConverter = Mock() {
            convert(_) >> convertedResult
        }

        mockedRepository = Mock() {
            getmenuaux(USERNAME, ENTERPRISE_ID) >> storedMenu
        }

        service = new JdbcServicesService(mockedTemplate, mockedConverter);

        when:
        List<ServiceComposite> result = service.get(USERNAME, ENTERPRISE_ID)

        then:
        result != null
        result == convertedResult
    }

    class CustomJdbcServicesService extends JdbcServicesService {

        CustomJdbcServicesService(JdbcTemplate jdbcTemplate, MenuConverter menuConverter) {
            super(jdbcTemplate, menuConverter)
        }

        @Override
        protected MnuMenuJwfPL getRepository(JdbcTemplate template) {
            return mockedRepository;
        }
    }

}
