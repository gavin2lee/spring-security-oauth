package org.springframework.security.oauth.examples.sparklr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    private static final String SPARKLR_RESOURCE_ID = OAuth2ServerConfig.SPARKLR_RESOURCE_ID;
    
    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Value("${tonr.redirect:http://localhost:16001/tonr2/sparklr/redirect}")
    private String tonrRedirectUri;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        // @formatter:off
        clients.inMemory().withClient("tonr") //
                    .resourceIds(SPARKLR_RESOURCE_ID) //
                    .authorizedGrantTypes("authorization_code", "implicit") //
                    .authorities("ROLE_CLIENT") //
                    .scopes("read", "write") //
                    .secret("secret") //
                    .redirectUris("http://localhost:16001/tonr2/sparklr/photos") //
                .and() //
                .withClient("tonr-with-redirect") //
                    .resourceIds(SPARKLR_RESOURCE_ID) //
                    .authorizedGrantTypes("authorization_code", "implicit") //
                    .authorities("ROLE_CLIENT") //
                    .scopes("read", "write") //
                    .secret("secret") //
                    .redirectUris(tonrRedirectUri) //
                .and() //
                .withClient("my-client-with-registered-redirect") //
                    .resourceIds(SPARKLR_RESOURCE_ID) //
                    .authorizedGrantTypes("authorization_code", "client_credentials") //
                    .authorities("ROLE_CLIENT") //
                    .scopes("read", "trust") //
                    .redirectUris("https://anywhere?key=value") //
                .and() //
                .withClient("my-trusted-client") //
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit") //
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT") //
                    .scopes("read", "write", "trust") //
                    .accessTokenValiditySeconds(60) //
                .and() //
                .withClient("my-trusted-client-with-secret") //
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit") //
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT") //
                    .scopes("read", "write", "trust") //
                    .secret("somesecret") //
                .and() //
                .withClient("my-less-trusted-client") //
                    .authorizedGrantTypes("authorization_code", "implicit") //
                    .authorities("ROLE_CLIENT") //
                    .scopes("read", "write", "trust") //
                .and() //
                .withClient("my-less-trusted-autoapprove-client") //
                    .authorizedGrantTypes("implicit") //
                    .authorities("ROLE_CLIENT") //
                    .scopes("read", "write", "trust") //
                    .autoApprove(true); //
        // @formatter:on
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.realm("sparklr2/client");
    }
}
