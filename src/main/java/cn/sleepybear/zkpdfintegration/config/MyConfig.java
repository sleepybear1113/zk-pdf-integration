package cn.sleepybear.zkpdfintegration.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/05/01 22:40
 */
@Configuration
@Data
public class MyConfig {
    @Value("${my-config.tmp-dir}")
    private String tmpDir;
    @Value("${my-config.download-dir}")
    private String downloadDir;
    @Value("${app.version}")
    private String appVersion;
}
