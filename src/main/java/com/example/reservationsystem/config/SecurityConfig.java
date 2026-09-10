package com.example.reservationsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

// Spring Securityの設定を行うクラス
@Configuration
public class SecurityConfig {

    // URLごとのアクセス権限を設定する
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // お客様用の予約登録画面・予約処理・エラー画面は誰でもアクセス可能
                        .requestMatchers(
                                "/reservations/new",
                                "/reservations",
                                "/error",
                                "/css/**"
                        ).permitAll()

                        // 管理者用画面はADMIN権限を持つユーザーだけアクセス可能
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")

                        // その他のURLはログイン済みユーザーのみアクセス可能
                        .anyRequest()
                        .authenticated()
                )

                // Spring Security標準のログイン画面を使用する
                .formLogin(form -> form
                        .permitAll()
                )

                // ログアウト機能を有効にする
                .logout(logout -> logout
                        .permitAll()
                );

        return http.build();
    }

    // パスワードを安全に扱うためのEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // 開発用の管理者ユーザーを作成する
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        var admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}