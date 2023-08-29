package com.mallang.auth.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.mallang.auth.presentation.AuthArgumentResolver;
import com.mallang.auth.presentation.AuthInterceptor;
import com.mallang.auth.presentation.AuthInterceptor.UriAndMethodCondition;
import com.mallang.auth.presentation.ExtractAuthenticationInterceptor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class AuthConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final ExtractAuthenticationInterceptor extractAuthenticationInterceptor;
    private final AuthArgumentResolver authArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(extractAuthenticationInterceptor)
                .addPathPatterns("/**")
                .order(0);
        registry.addInterceptor(setUpAuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/oauth/**")
                .order(1);
    }

    private AuthInterceptor setUpAuthInterceptor() {
        authInterceptor.setNoAuthRequiredConditions(
                UriAndMethodCondition.builder()
                        .uriPatterns(Set.of("/posts/**"))
                        .httpMethods(Set.of(GET))
                        .build(),
                UriAndMethodCondition.builder()
                        .uriPatterns(Set.of("/comments/**"))
                        .httpMethods(Set.of(POST, PUT, DELETE))
                        .params(Map.of("unauthenticated", "true"))
                        .build()
        );
        return authInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
