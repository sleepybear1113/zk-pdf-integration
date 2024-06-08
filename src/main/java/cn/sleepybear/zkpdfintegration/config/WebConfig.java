package cn.sleepybear.zkpdfintegration.config;

import cn.sleepybear.zkpdfintegration.interceptor.StaticFileInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/06/08 01:40
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private MyConfig myConfig;
    @Resource
    private StaticFileInterceptor staticFileInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(staticFileInterceptor).addPathPatterns(GlobalConstants.PREFIX + "/" + myConfig.getDownloadDir() + "**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(GlobalConstants.PREFIX + "/" + myConfig.getDownloadDir() + "**")
                .addResourceLocations("file:" + myConfig.getDownloadDir());
    }
}