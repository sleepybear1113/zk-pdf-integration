package cn.sleepybear.zkpdfintegration.controller;

import cn.sleepybear.zkpdfintegration.advice.ResultCode;
import cn.sleepybear.zkpdfintegration.config.GlobalConstants;
import cn.sleepybear.zkpdfintegration.config.MyConfig;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/05/03 08:27
 */
@RestController
@RequestMapping(value = GlobalConstants.PREFIX)
public class SystemController {

    @Resource
    private MyConfig myConfig;

    @RequestMapping("/system/getVersion")
    public ResultCode<String> getVersion() {
        return ResultCode.buildResult(myConfig.getAppVersion());
    }
}
