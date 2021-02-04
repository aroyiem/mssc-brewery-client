package com.roy.msscbreweryclient.web.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NIORestTemplateCustomizer implements RestTemplateCustomizer {

    private final Integer maxtTotalConnections;
    private final Integer defaultMaxTotalConnections;
    private final Integer connectionRequestTimeOut;
    private final Integer socketTimeout;

    public NIORestTemplateCustomizer(@Value("${roy.maxtotalconnections}") Integer maxtTotalConnections,
                                     @Value("${roy.defaultmaxtotalconnections}") Integer defaultMaxTotalConnections,
                                     @Value("${roy.connectionrequesttimeout}") Integer connectionRequestTimeOut,
                                     @Value("${roy.sockettimeout}") Integer socketTimeout) {
        this.maxtTotalConnections = maxtTotalConnections;
        this.defaultMaxTotalConnections = defaultMaxTotalConnections;
        this.connectionRequestTimeOut = connectionRequestTimeOut;
        this.socketTimeout = socketTimeout;
    }

    public ClientHttpRequestFactory clientHttpRequestFactory() throws IOReactorException {

        final DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(IOReactorConfig.custom()
                .setConnectTimeout(connectionRequestTimeOut)
                .setIoThreadCount(4)
                .setSoTimeout(socketTimeout)
                .build());

        final PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
        connectionManager.setDefaultMaxPerRoute(defaultMaxTotalConnections);
        connectionManager.setMaxTotal(maxtTotalConnections);

        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        return new HttpComponentsAsyncClientHttpRequestFactory(httpAsyncClient);
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        try {
            restTemplate.setRequestFactory(this.clientHttpRequestFactory());
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }
}
