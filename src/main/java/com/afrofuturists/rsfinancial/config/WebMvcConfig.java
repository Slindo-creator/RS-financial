package com.afrofuturists.rsfinancial.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Maps /uploads/** to wherever FileStorageService actually writes files,
 * so a ClaimDocument's servedUrl (e.g. /uploads/claims/{id}/photo.jpg)
 * resolves to a real, downloadable file instead of a 404. This path is
 * NOT added to SecurityConfig's permitAll list, so it still requires the
 * same Bearer token as every other endpoint - appropriate for now since
 * claim photos/IDs are sensitive, but worth knowing that means a plain
 * browser <img src="..."> tag won't work unauthenticated; the frontend
 * will need to fetch the bytes with an Authorization header attached
 * (e.g. via fetch() + a blob URL) rather than a bare <img> tag.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebMvcConfig(@Value("${app.file-storage.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
