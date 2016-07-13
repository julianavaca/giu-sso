package com.turner.service;

import com.turner.domain.Enterprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by biandra on 16/09/15.
 */
@Service
public class JdbcEnterpriseService {

    private static final String ENTERPRISES_QUERY = "SELECT DISTINCT e.EMP_CODIGO, e.EMP_NOMBRE FROM M000_FUNCION_USUARIOS fu INNER JOIN CCP_EMPRESAS e ON fu.EMP_CODIGO = e.EMP_CODIGO WHERE FU.USR_NOMBRE = ?";
    public static final String EMP_CODIGO_COLUMN = "EMP_CODIGO";
    public static final String EMP_NOMBRE_COLUMN = "EMP_NOMBRE";
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcEnterpriseService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Enterprise> getEnterprises(String userName){
        return jdbcTemplate.query(ENTERPRISES_QUERY,
                new String[]{userName.toUpperCase()},
                (rs, rowNum) -> new Enterprise(
                        rs.getLong(EMP_CODIGO_COLUMN),
                        rs.getString(EMP_NOMBRE_COLUMN)
                ));
    }
}
