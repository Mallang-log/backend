package com.mallang.auth.infrastructure.oauth

import com.mallang.auth.infrastructure.oauth.github.client.GithubApiClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class HttpInterfaceConfig {

    @Bean
    fun googleApiClient(): GithubApiClient {
        return createHttpInterface(GithubApiClient::class.java)
    }

    private fun <T> createHttpInterface(clazz: Class<T>): T {
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(RestClient.create()))
                .build()
                .createClient(clazz)
    }
}
