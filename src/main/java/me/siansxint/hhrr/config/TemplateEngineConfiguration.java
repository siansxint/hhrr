package me.siansxint.hhrr.config;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies.GroupingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
public class TemplateEngineConfiguration {

    @Autowired
    public void configureTemplateEngine(TemplateEngine engine) {
        engine.addDialect(new LayoutDialect(new GroupingStrategy()));
        engine.addDialect(new SpringSecurityDialect());
    }
}