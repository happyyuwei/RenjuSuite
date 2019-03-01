/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venus.Renju.web;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
/**
 *
 * @author happy
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{
    /**
     * 
     * @param registry
     */
     @Override    
     public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //static page resouce
        registry.addResourceHandler("/game/**").addResourceLocations("file:./page/"); 
     }
}
