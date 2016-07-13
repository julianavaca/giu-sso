package com.giu;

import com.giu.domain.GiuAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Obtains a list of granted authorities for an Ldap user.
 * Created by biandra on 13/08/15.
 */
@Component
public class JdbcAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private static final String USERNAME_QUERY = "SELECT RED.USR_NOMBRE FROM M000_RED_USERS RED WHERE RED.USR_NOMBRE_RED = ?";
    private static final String FXN_BY_USER_QUERY = "SELECT FN.FXN_CODIGO, FN.EMP_CODIGO FROM M000_FUNCION_USUARIOS FN, M000_USUARIOS US  WHERE US.USR_NOMBRE = ? AND US.USR_NOMBRE = FN.USR_NOMBRE AND US.USR_ESTADO <> 2 ORDER BY EMP_CODIGO ASC";
    public static final String FXN_CODIGO_COLUMN = "FXN_CODIGO";
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAuthoritiesPopulator(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Get the granted authorities for the given user.
     * @param userData the context object which was returned by the LDAP authenticator.
     * @param username that consulting the roles
     * @return {@link List}&lt;{@link GrantedAuthority}&gt;}
     */
    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) throws AuthenticationException {
        try {
            return getGrantedAuthoritiesByUser(getUsername(username));
        } catch (SQLException e) {
            return Collections.emptySet();
        }
    }

    private List<GrantedAuthority> getGrantedAuthoritiesByUser(String username) throws SQLException {
        List<GrantedAuthority> result = jdbcTemplate.query(FXN_BY_USER_QUERY,
                new String[]{username.toUpperCase()},
                (rs, rowNum) -> new GiuAuthority(
                        rs.getString(FXN_CODIGO_COLUMN)));
        return result;
    }

    private String getUsername(String ldapUsername){
        try {
            return jdbcTemplate.queryForObject(USERNAME_QUERY,new String[]{ldapUsername.toUpperCase()}, String.class);
        } catch (EmptyResultDataAccessException e){
            throw new AuthenticationCredentialsNotFoundException("Nonexistent account");
        }

    }
}