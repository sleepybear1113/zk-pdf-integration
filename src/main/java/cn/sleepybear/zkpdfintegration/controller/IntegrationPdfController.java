package cn.sleepybear.zkpdfintegration.controller;

import cn.sleepybear.zkpdfintegration.config.GlobalConstants;
import cn.sleepybear.zkpdfintegration.dto.PdfResultInfoDto;
import cn.sleepybear.zkpdfintegration.logic.IntegrationPdfLogic;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/06/07 23:46
 */
@RestController
@RequestMapping(value = GlobalConstants.PREFIX)
public class IntegrationPdfController {

    @Resource
    private IntegrationPdfLogic integrationPdfLogic;

    @RequestMapping("/integration/pdf")
    public PdfResultInfoDto integrationPdf(String filename, String picFilename, Integer n, Boolean sort) {
        return integrationPdfLogic.integrationPdf(filename, picFilename, n, Boolean.TRUE.equals(sort));
    }
}
