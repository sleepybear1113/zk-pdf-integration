package cn.sleepybear.zkpdfintegration.controller2;

import cn.sleepybear.zkpdfintegration.config.GlobalConstants;
import cn.sleepybear.zkpdfintegration.config.MyConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/05/04 22:56
 */
@RestController
@RequestMapping(value = GlobalConstants.PREFIX)
@Slf4j
public class DownloadController {

    @Resource
    private MyConfig myConfig;

    /**
     * 下载文件
     *
     * @param filename filename
     */
    @GetMapping("/downloadFile/{f}")
    public ResponseEntity<byte[]> downloadFile(HttpServletResponse response, String filename, @PathVariable String f) {
        response.setCharacterEncoding("UTF-8");

        // 判断 filename 是否为空
        if (StringUtils.isEmpty(filename)) {
            // 如果 filename 为空，则返回空的字节数组
            return generateErrorResponse("文件名不能为空！");
        }

        String baseDownloadDir = myConfig.getDownloadDir();
        if (!filename.startsWith(baseDownloadDir)) {
            return generateErrorResponse("文件路径不正确！");
        }

        // 获取文件字节
        File file = new File(filename);
        if (!file.exists()) {
            return generateErrorResponse("文件不存在！");
        }

        byte[] fileBytes;
        String contentType;
        Path path = Paths.get(filename);
        try {
            fileBytes = Files.readAllBytes(path);
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            log.warn("读取文件失败", e);
            return generateErrorResponse("读取文件失败！");
        }
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(URLEncoder.encode(file.getName(), StandardCharsets.UTF_8))
                .build();

        // 构建返回的文件响应
        MediaType mediaType = MediaType.parseMediaType(contentType);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(file.length())
                .body(fileBytes);
    }

    private ResponseEntity<byte[]> generateErrorResponse(String errorMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(errorMessage.getBytes(StandardCharsets.UTF_8));
    }
}
