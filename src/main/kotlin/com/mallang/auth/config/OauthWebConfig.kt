package com.mallang.auth.config

import com.mallang.auth.presentation.support.OauthServerTypeConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class OauthWebConfig : WebMvcConfigurer {
    
    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(OauthServerTypeConverter())
    }
}
