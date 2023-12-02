package com.mallang.auth.infrastructure.oauth;

import com.mallang.auth.infrastructure.oauth.github.client.GithubApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpInterfaceConfig {

    private final RestClient restClient;

    public HttpInterfaceConfig(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    @Bean
    public GithubApiClient googleApiClient() {
        return createHttpInterface(GithubApiClient.class);
    }

    private <T> T createHttpInterface(Class<T> clazz) {
        HttpServiceProxyFactory build = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient)).build();
        return build.createClient(clazz);
    }
}
