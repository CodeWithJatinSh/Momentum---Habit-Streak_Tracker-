package com.momentum.habittracker.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * WebMvc configuration for Single Page Application (SPA) support.
 * Forwards any unmapped browser routes to index.html so React can handle client-side state,
 * while ensuring API endpoints (/api/**) and static assets are resolved properly.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        // Do not intercept API calls; let Spring MVC return 404 if API endpoint doesn't exist
                        if (resourcePath.startsWith("api/") || resourcePath.equals("api")) {
                            return null;
                        }

                        Resource requestedResource = location.createRelative(resourcePath);
                        // Return existing static file (JS, CSS, images, etc.) or fall back to index.html
                        return requestedResource.exists() && requestedResource.isReadable()
                                ? requestedResource
                                : location.createRelative("index.html");
                    }
                });
    }
}
