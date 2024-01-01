package com.mallang.auth.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.mallang.auth.presentation.support.AuthArgumentResolver;
import com.mallang.auth.presentation.support.AuthInterceptor;
import com.mallang.auth.presentation.support.ExtractAuthenticationInterceptor;
import com.mallang.auth.presentation.support.OptionalAuthArgumentResolver;
import com.mallang.common.presentation.UriAndMethodAndParamCondition;
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
    private final OptionalAuthArgumentResolver optionalAuthArgumentResolver;

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
                UriAndMethodAndParamCondition.builder()
                        .uriPatterns(Set.of("/comments/**"))
                        .httpMethods(Set.of(POST, PUT, DELETE))
                        .params(Map.of("unauthenticated", "true"))
                        .build(),
                UriAndMethodAndParamCondition.builder()
                        .uriPatterns(Set.of("/members", "/members/login", "/infra/aws/s3/presigned-url"))
                        .httpMethods(Set.of(POST))
                        .build(),
                UriAndMethodAndParamCondition.builder()
                        .uriPatterns(Set.of(
                                "/members/*",
                                "/blogs",
                                "/posts/**",
                                "/categories",
                                "/comments",
                                "/blog-subscribes/*",
                                "/post-stars",
                                "/star-groups",
                                "/abouts",
                                "/statistics/**",
                                "/favicon.ico"
                        ))
                        .httpMethods(Set.of(GET))
                        .build()
        );
        return authInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
        resolvers.add(optionalAuthArgumentResolver);
    }
}
