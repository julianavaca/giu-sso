package com.giu.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Created by mbritez on 11/09/15.
 */
public class GiuAuthority implements GrantedAuthority {

    private SimpleGrantedAuthority authority;
    private String ROLE_FORMAT = "ROLE_%s-%s";

    public GiuAuthority(String role) {
        this.authority = new SimpleGrantedAuthority(role);
    }

    @Override
    public String getAuthority() {
        return String.format(ROLE_FORMAT,authority.getAuthority());
    }

}
