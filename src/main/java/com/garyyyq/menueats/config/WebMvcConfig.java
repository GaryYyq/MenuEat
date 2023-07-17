package com.garyyyq.menueats.config;

import com.garyyyq.menueats.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * Set static resource path
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Start to set static resource path");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }


    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Start to set custom Jackson converter");

        // Create a custom Jackson converter
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // Set the custom Jackson converter, convert Java object to JSON
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // Set the custom Jackson converter to the first position
        converters.add(0, messageConverter);
        super.extendMessageConverters(converters);
    }
}
