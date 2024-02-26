package com.tanaseb.aggregationservice.config;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import javax.net.ssl.SSLException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@RequiredArgsConstructor
@Data
@Configuration
public class WebClientConfig {

    @Value("${backend.services.root.url}")
    private String rootUrl;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) throws SSLException {
        HttpClient httpClient = getHttpClient();

        return webClientBuilder
                .baseUrl(rootUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private HttpClient getHttpClient() throws SSLException {
        var sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        return HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 20000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(EpollChannelOption.TCP_KEEPIDLE, 300)
                .option(EpollChannelOption.TCP_KEEPINTVL, 60)
                .option(EpollChannelOption.TCP_KEEPCNT, 8)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(20))
                        .addHandlerLast(new WriteTimeoutHandler(20))
                );
    }
}
