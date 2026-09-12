package in.rahul.learning.security.config;


import in.rahul.learning.security.filter.JwtAuthorizationFilter;
import in.rahul.learning.security.jwt.JwtAccessDeniedHandler;
import in.rahul.learning.security.jwt.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    protected static final String[] PUBLIC_URLS = {

            // login
            "/dev/api/v1/users/login/**",
            "/api/v1/users/login/**",
//            "/**",

            //signip
            "/dev/api/v1/users/create/**",
            "/api/v1/users/create/**",

            // swagger ui
            "/swagger-ui.html",
            "/swagger-ui/**",

            // openapi docs
            "/v3/api-docs/**",
            "/dev/v3/api-docs/**",

            // optional (sometimes needed)
            "/webjars/**"
    };

    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                //.csrf(csrf -> csrf.disable()) // CSRF attacks mainly exploit browser cookies that are automatically sent with requests.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        //.requestMatchers("/api/v1/users").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ).addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    /* Disable default user completely. If you add below bean the spring security password will hide in the console
    *   Or else you can add UserDetail Service
    *       @Bean
            public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
            }
    * */

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("No default authentication");
        };
    }

   /* @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "https://yourfrontend.com"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }*/

/*

  ✅ You need loadUserByUsername() if you're using Spring Security authentication
  <p>
 authenticationManager.authenticate(
 new UsernamePasswordAuthenticationToken(
 request.getEmail(),
 request.getPassword()
 )
 );

 @Bean public DaoAuthenticationProvider authenticationProvider() {
 DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

 daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
 daoAuthenticationProvider.setUserDetailsService(userDetailsService);

 return daoAuthenticationProvider;
 }

 @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
 return authenticationConfiguration.getAuthenticationManager();
 }
 */
  /*  @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Collections.singletonList("*"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(false);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                // This allow us to expose the headers
                config.setExposedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Authorization","x-xsrf-token","Access-Control-Allow-Headers","Origin","Accept","X-Requested-With","Content-Type","Access-Control-Request-Method","Access-Control-Request-Headers","Access-Control-Expose-Headers"));

                return config;
            }
        });
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and().authorizeHttpRequests().requestMatchers(PUBLIC_URLS).permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }*/
}
