package com.giu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Collections;

@SpringBootApplication
@SessionAttributes("authorizationRequest")
@EnableResourceServer
@ImportResource(value = "classpath:aop-config.xml")
public class Application extends ResourceServerConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()

            .addFilterBefore(new CORSFilter(), AbstractPreAuthenticatedProcessingFilter.class)
            .addFilterAfter(CORSFilter.class)
            .csrf().disable()

            .authorizeRequests().anyRequest().authenticated()
            .and()
            .anonymous().authorities("ROLE_ANONYMOUS");
    }

    @Value("${giu.oauth.uri}")
    private String oauthHost;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        OAuth2AuthenticationEntryPoint ep = new OAuth2AuthenticationEntryPoint();
        ep.setExceptionRenderer(new CustomDefaultOAuth2ExceptionRenderer(oauthHost));
        resources.authenticationEntryPoint(ep);
    }


    @Configuration
    @Order(-10)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.addFilterAfter(new CORSFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()

                    .csrf().disable()

                    .requestMatchers()
                    .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "/logout")
                    .and()

                    .authorizeRequests()
                    .antMatchers("/manage/**").permitAll()
                    .and()

                    .formLogin()
                    .loginPage("/login").failureUrl("/login?error=true").permitAll()
                    .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .logoutSuccessHandler(new CustomLogoutSuccessHandler()).permitAll()
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated();

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.parentAuthenticationManager(authenticationManager);
        }
        @Override
        public void configure(WebSecurity webSecurity) throws Exception {
            // TODO sacar si es prod
            webSecurity
                    .ignoring()
                    .antMatchers("/config*/**");

        }
    }

    @Configuration
    @EnableAuthorizationServer
    protected static class OAuth2AuthorizationConfig extends
            AuthorizationServerConfigurerAdapter {

        @Value("${giu.oauth.redis.enabled}")
        private Boolean redisEnabled;

        @Value("${giu.oauth.redis.host}")
        private String redisHost;

        @Value("${giu.oauth.redis.port}")
        private int redisPort;


        @Bean
        JedisConnectionFactory jedisConnectionFactory() {
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName(redisHost);
            factory.setPort(redisPort);
            factory.setUsePool(true);
            return factory;
        }

        @Autowired
        Environment environment;

        @Bean
        public TokenStore tokenStore() {
            TokenStore result;
            if (redisEnabled){
                result = new RedisTokenStore(jedisConnectionFactory());
            } else {
                result = new InMemoryTokenStore();
            }

            return result;
        }

        @Autowired
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("finance")
                    .secret("finance").scopes("home")
                    .autoApprove(true)
                    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token", "password")
                    .and()
                    .withClient("branding")
                    .secret("branding").scopes("home")
                    .autoApprove(true)
                    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token", "password")
                    .and()
                    .withClient("media")
                    .secret("media").scopes("home") //TODO: cambiar scope
                    .autoApprove(true)
                    .authorizedGrantTypes("authorization_code", "implicit", "refresh_token", "password")
                    .and()
                    .withClient("adSales")
                    .secret("adSales").scopes("home")
                    .autoApprove(true)
                    .authorizedGrantTypes("authorization_code","implicit","refresh_token","password");
        }

        @Bean
        @Primary
        public DefaultTokenServices tokenServices() {
            DefaultTokenServices tokenServices = new DefaultTokenServices();
            tokenServices.setSupportRefreshToken(true);
            tokenServices.setTokenStore(tokenStore());
            return tokenServices;
        }


        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {

            endpoints.tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);

        }

        @Value("${aleph.oauth.uri}")
        private String oauthHost;

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer)
                throws Exception {
            OAuth2AuthenticationEntryPoint ep = new OAuth2AuthenticationEntryPoint();
            ep.setExceptionRenderer(new CustomDefaultOAuth2ExceptionRenderer(oauthHost));
            oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess(
                    "isAuthenticated()")
                    .authenticationEntryPoint(ep).allowFormAuthenticationForClients();
        }

    }

    @Configuration
    @EnableWebSecurity
    @Order(201)
    protected static class AuthServerConfig extends WebSecurityConfigurerAdapter {

        @Value("${ldap.url}")
        private String ldapUrl;
        @Value("${ldap.host}")
        private String ldapHost;
        @Value("${ldap.mocked}")
        private Boolean isMocked;

        @Autowired
        private JdbcUserDetailsService jdbcUserDetailsService;

        @Autowired
        private JdbcAuthoritiesPopulator jdbcAuthoritiesPopulator;

        @Autowired
        public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
            if(!isMocked) {
                auth
                        .ldapAuthentication()
                        .contextSource()
                        .url(ldapUrl)
                        .managerDn("cn=AlephWebServices,cn=Users,dc=turner,dc=com")
                        .managerPassword("TTurner2012**")
                        .and()
                        .userDnPatterns("cn=Users,dc=turner,dc=com")
                        .userSearchBase("cn=Users,dc=turner,dc=com")
                        .userSearchFilter("(&(sAMAccountName={0})(objectClass=top)(objectClass=person)(objectClass=organizationalPerson)(objectClass=user))")
                        .groupSearchBase("cn=Users,dc=turner,dc=com")
                        .ldapAuthoritiesPopulator(jdbcAuthoritiesPopulator)
                        .userDetailsContextMapper(jdbcUserDetailsService);
            } else {
                auth
                        .authenticationProvider(new AuthenticationProvider() {
                            @Override
                            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                                return new UsernamePasswordAuthenticationToken(
                                        jdbcUserDetailsService.mapUserFromContext(
                                                null,
                                                authentication.getPrincipal().toString(),
                                                Collections.emptyList()),
                                        authentication.getCredentials(),
                                        jdbcAuthoritiesPopulator.getGrantedAuthorities(null, authentication.getPrincipal().toString()));
                            }

                            @Override
                            public boolean supports(Class<?> authentication) {
                                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
                            }
                        })
                        .userDetailsService(new UserDetailsService() {
                            @Override
                            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                                return jdbcUserDetailsService.mapUserFromContext(null, username, Collections.emptyList());
                            }
                        });


                auth
                        .authenticationProvider(new AuthenticationProvider() {
                            @Override
                            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                                return new UsernamePasswordAuthenticationToken(
                                        jdbcUserDetailsService.mapUserFromContext(
                                                null,
                                                authentication.getPrincipal().toString(),
                                                Collections.emptyList()),
                                        authentication.getCredentials(),
                                        jdbcAuthoritiesPopulator.getGrantedAuthorities(null, authentication.getPrincipal().toString()));
                            }

                            @Override
                            public boolean supports(Class<?> authentication) {
                                return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
                            }
                        })
                        .userDetailsService(new UserDetailsService() {
                            @Override
                            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                                return jdbcUserDetailsService.mapUserFromContext(null, username, Collections.emptyList());
                            }
                        });
            }
        }
    }

    @Configuration
    @EnableRedisHttpSession
    @Profile({ "prod", "trng", "preprod" })
    protected static class HttpSessionConfig {

        @Value("${aleph.oauth.redis.host}")
        private String redisHost;

        @Value("${aleph.oauth.redis.port}")
        private int redisPort;

        @Bean
        JedisConnectionFactory jedisConnectionFactory() {
            JedisConnectionFactory factory = new JedisConnectionFactory();
            factory.setHostName(redisHost);
            factory.setPort(redisPort);
            factory.setUsePool(true);
            return factory;
        }

    }

}