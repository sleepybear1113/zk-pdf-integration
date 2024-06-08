package cn.sleepybear.zkpdfintegration.interceptor;

import cn.sleepybear.zkpdfintegration.config.GlobalConstants;
import cn.sleepybear.zkpdfintegration.config.MyConfig;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/06/08 23:07
 */
@Component
@Slf4j
public class StaticFileInterceptor implements HandlerInterceptor {

    @Resource
    private MyConfig myConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        String requestUri = request.getRequestURI();
        log.info(requestUri);
        String path = requestUri.substring(GlobalConstants.PREFIX.length() + 1);
        // path 需要解码 url decode
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File file = new File(path);
        if (!file.exists()) {
            response.setCharacterEncoding("UTF-8");
            String msg = "所访问的[%s]文件不存在！可能是因为临时文件已经被系统删除！".formatted(file.getName());
            // 输出错误信息，纯文本
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
            try {
                response.getWriter().write(msg);
            } catch (Exception e) {
                log.error("输出错误信息失败！", e);
            }

            return false;
        }
        return true;
    }
}
