package com.giu.service;

import com.giu.converter.MenuConverter;
import com.giu.domain.ServiceComposite;
import com.turner.sp.acq.pl.MnuMenuJwfPL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by biandra on 15/09/15.
 */
@Service
public class JdbcServicesService {

    private MnuMenuJwfPL repository;
    private MenuConverter menuConverter;

    @Autowired
    public JdbcServicesService(JdbcTemplate jdbcTemplate, MenuConverter menuConverter){
        this.repository = getRepository(jdbcTemplate);
        this.menuConverter = menuConverter;
    }

    public List<ServiceComposite> get(String userName, Long enterpriseId) {
        return menuConverter.convert(repository.getmenuaux(userName, enterpriseId));
    }

    protected MnuMenuJwfPL getRepository(JdbcTemplate template){
        return new MnuMenuJwfPL(template);
    }

}
