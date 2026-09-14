package br.com.fatecads.fatecads.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        private final RoleBasedAuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler;

        public SecurityConfig(RoleBasedAuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler) {
                this.roleBasedAuthenticationSuccessHandler = roleBasedAuthenticationSuccessHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
    "/",
    "/login",
    "/fatecads",
    "/css/**",
    "/images/**",
    "/usuarios/criar",
    "/usuarios/salvar",
    "/recuperacao/**")
                                                .permitAll()
                                                .requestMatchers("/home", "/alunos/**", "/cursos/**", "/professores/**",
                                                                "/disciplinas/**", "/usuarios/listar", "/usuarios/editar/**",
                                                                "/usuarios/excluir/**", "/produtos/listar", "/produtos/criar",
                                                                "/produtos/salvar", "/produtos/editar/**", "/produtos/excluir/**",
                                                                "/pedidos/criar")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.POST, "/pedidos")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/loja", "/compras/**", "/produtos/imagem/**")
                                                .hasAnyRole("ADMIN", "USER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler(roleBasedAuthenticationSuccessHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }

}
