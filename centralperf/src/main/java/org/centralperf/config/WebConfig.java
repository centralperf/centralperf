package org.centralperf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(xsltViewResolver());
    }

    /**
     * Enable XSL View to render jMeter JMX XML Files
     */
    public XsltViewResolver xsltViewResolver() {
        XsltViewResolver viewResolver = new XsltViewResolver();
        viewResolver.setPrefix("classpath:/views/xsl/");
        viewResolver.setViewNames("*.xsl");
        viewResolver.setCacheTemplates(true);;
        return viewResolver;
    }
}
