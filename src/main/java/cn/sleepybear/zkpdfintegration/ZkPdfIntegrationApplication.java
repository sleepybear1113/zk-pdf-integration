package cn.sleepybear.zkpdfintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author xjx
 */
@EnableScheduling
@SpringBootApplication
public class ZkPdfIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZkPdfIntegrationApplication.class, args);
    }

}
