package com.themodernizers.misc.upload;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.ServletContext;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer implements WebSocketConfigurer {
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // registry.addHandler(new TestSocketHandler(), "/binary");
        registry.addHandler(new UploadWSHandler(), "/binary");

    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(50 * 1024 * 1024);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

    }
}
