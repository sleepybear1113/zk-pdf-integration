package cn.sleepybear.zkpdfintegration.logic;

import cn.sleepybear.zkpdfintegration.config.MyConfig;
import cn.sleepybear.zkpdfintegration.utils.CommonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/05/02 22:59
 */
@Component
@Slf4j
public class UploadLogic {
    public static final String UPLOAD_FILE_SPLIT = "_@@_";

    @Resource
    private MyConfig myConfig;

    public List<String> upload(MultipartFile[] files) {
        List<String> filenames = new ArrayList<>();
        if (files == null) {
            return filenames;
        }

        for (MultipartFile multipartFile : files) {
            String originalFilename = multipartFile.getOriginalFilename();
            String filename = CommonUtils.generateRandomString(8) + UPLOAD_FILE_SPLIT + originalFilename;
            String path = myConfig.getTmpDir() + filename;
            CommonUtils.ensureParentDir(path);

            // file 写入本地 path
            try {
                File file = new File(path);
                if (file.exists()) {
                    if (multipartFile.getSize() == file.length()) {
                        filenames.add(filename);
                        log.info("文件已存在：{}", filename);
                        continue;
                    }
                }
                Files.write(file.toPath(), multipartFile.getBytes());
                log.info("文件写入成功：{}", filename);
                filenames.add(filename);
            } catch (IOException e) {
                log.warn("文件写入失败", e);
            }
        }

        return filenames;
    }
}
