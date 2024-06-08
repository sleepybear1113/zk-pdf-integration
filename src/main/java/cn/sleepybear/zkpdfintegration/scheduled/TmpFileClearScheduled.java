package cn.sleepybear.zkpdfintegration.scheduled;

import cn.sleepybear.zkpdfintegration.config.MyConfig;
import cn.sleepybear.zkpdfintegration.utils.CommonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/06/08 21:52
 */
@Component
@Slf4j
public class TmpFileClearScheduled {
    @Resource
    private MyConfig myConfig;

    /**
     * 每小时清理一次临时文件 tmp
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void clearTmpFile() {
        String tmpDir = myConfig.getTmpDir();
        log.info("清理临时文件夹：{}", tmpDir);
        CommonUtils.deleteInnerFilesAndDir(tmpDir);
    }
}
