package com.giu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;

/**
 * Created by mbritez on 20/11/15.
 */
@Configuration
public class SuccessLoginEvent implements ApplicationListener<AbstractAuthenticationEvent> {

    @Value("${ldap.mocked}")
    private Boolean isMocked;

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if(!isMocked) {
            if(event.getAuthentication().isAuthenticated()) {
//                if (!((AlephUser) event.getAuthentication().getPrincipal()).isEnabled()) {
//                    throw new DisabledException("Account disabled");
//                }
            }
        }

    }
}
