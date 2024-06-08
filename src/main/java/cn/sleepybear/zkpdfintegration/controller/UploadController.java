package cn.sleepybear.zkpdfintegration.controller;

import cn.sleepybear.zkpdfintegration.config.GlobalConstants;
import cn.sleepybear.zkpdfintegration.logic.UploadLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/05/02 22:58
 */
@RestController
@RequestMapping(value = GlobalConstants.PREFIX)
public class UploadController {

    @Resource
    private UploadLogic uploadLogic;

    @RequestMapping("/upload/file")
    public List<String> upload(MultipartFile[] files) {
        return uploadLogic.upload(files);
    }
}
