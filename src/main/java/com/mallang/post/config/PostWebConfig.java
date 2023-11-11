package com.mallang.post.config;

import com.mallang.post.presentation.support.ExtractPostPasswordInterceptor;
import com.mallang.post.presentation.support.OptionalPostPasswordArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class PostWebConfig implements WebMvcConfigurer {

    private final ExtractPostPasswordInterceptor postPasswordInterceptor;
    private final OptionalPostPasswordArgumentResolver optionalPostPasswordArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(postPasswordInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(optionalPostPasswordArgumentResolver);
    }
}
