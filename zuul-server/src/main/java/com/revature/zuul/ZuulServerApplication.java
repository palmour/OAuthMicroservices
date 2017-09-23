package com.revature.zuul;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CompositeFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableZuulProxy
@SpringBootApplication
@RestController
@EnableOAuth2Client
public class ZuulServerApplication extends WebSecurityConfigurerAdapter {
	
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
	
	@RequestMapping("/user")
	public Principal user(Principal principal) {
		return principal;
	} 

	public static void main(String[] args) {
		SpringApplication.run(ZuulServerApplication.class, args);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http
			.antMatcher("/**")
			.authorizeRequests()
				.antMatchers("/", "/login**", "/api/customer-by-address/webjars/**", "/api/customer-by-address/index.js", "/api/customer-by-address/")
				.permitAll()
			.anyRequest()
				.authenticated()
			.and().logout().logoutSuccessUrl("/").permitAll()
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
	}
	
	private Filter ssoFilter() {
		
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		
		filters.add(ssoFilter(slack(), "/login/slack"));
		filter.setFilters(filters);
		
		return filter;
	}
	
	private Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
		OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		filter.setRestTemplate(template);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(
				client.getResource().getUserInfoUri(), client.getClient().getClientId());
		tokenServices.setRestTemplate(template);
		filter.setTokenServices(tokenServices);
		return filter;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	} 
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Bean
	@ConfigurationProperties("slack")
	public ClientResources slack() {
	  return new ClientResources();
	}
	
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(
			OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}
}
