package cn.sleepybear.zkpdfintegration.logic;

import cn.sleepybear.zkpdfintegration.config.MyConfig;
import cn.sleepybear.zkpdfintegration.dto.PdfResultInfoDto;
import cn.sleepybear.zkpdfintegration.exception.FrontException;
import cn.sleepybear.zkpdfintegration.utils.CommonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/06/07 21:59
 */
@Component
@Slf4j
public class IntegrationPdfLogic {
    private static final String FIRST_PAGE_STR = "第1页";
    private static final String PAGE_TITLE_STR = "毕业生花名册";

    @Resource
    private MyConfig myConfig;

    public static void main(String[] args) {
        String pdfPath = "test/pdf/input/ttt.pdf";
        String outputFolder = "test/pdf/output/test.pdf";
        int numCopiesPerChapter = 3;

        System.out.println("PDF复制和整合完成！");
    }

    public PdfResultInfoDto integrationPdf(String filename, Integer n) {
        if (StringUtils.isBlank(filename)) {
            throw new FrontException("文件名不能为空！");
        }
        if (!filename.endsWith(".pdf")) {
            throw new FrontException("文件格式不正确！");
        }
        String pdfPath = myConfig.getTmpDir() + filename;
        String outputFolder = myConfig.getDownloadDir() + "pdf-output/" + filename;

        File file = new File(pdfPath);
        if (!file.exists()) {
            throw new FrontException("上传的文件不存在！");
        }

        if (n == null || n <= 0) {
            throw new FrontException("复制次数必须大于 0！");
        }

        return integrationPdf(pdfPath, outputFolder, n);
    }

    private PdfResultInfoDto integrationPdf(String pdfPath, String outputPath, int numCopiesPerChapter) {
        List<Integer> list = new ArrayList<>();
        PdfResultInfoDto pdfResultInfoDto = new PdfResultInfoDto();

        // 加载PDF文档
        File file = new File(pdfPath);
        pdfResultInfoDto.setFilenameOriginal(file.getName());
        pdfResultInfoDto.setFilename(file.getName().split(UploadLogic.UPLOAD_FILE_SPLIT)[1]);
        try (PDDocument pdfDocument = Loader.loadPDF(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // 检查 PDF 文档首页是否包含指定标题
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(1);
            String firstPageStr = pdfStripper.getText(pdfDocument);
            if (StringUtils.isBlank(firstPageStr)) {
                throw new FrontException("PDF文档首页为空，无法整合！");
            }
            if (firstPageStr.length() < 50) {
                throw new FrontException("PDF文档首页内容过少，无法整合！");
            }
            if (!StringUtils.contains(firstPageStr.substring(0, 50), PAGE_TITLE_STR)) {
                throw new FrontException("PDF文档首页不包含指定标题，无法整合！");
            }

            String last50Chars = firstPageStr.substring(firstPageStr.length() - 50);
            if (!last50Chars.replace(" ", "").contains(FIRST_PAGE_STR)) {
                throw new FrontException("PDF文档首页不包含页码【第1页】，无法整合！请检查花名册右下角是否包含页码。");
            }
            if (last50Chars.contains(PAGE_TITLE_STR)) {
                throw new FrontException("PDF文档首页尾部有误，无法整合！");
            }

            // 获取PDF总页数
            int numPages = pdfDocument.getNumberOfPages();
            pdfResultInfoDto.setOriginalPageCount(numPages);
            int i = 0;
            while (i < numPages) {
                // 设置当前页为起始页和结束页，只提取该页内容
                int page = i + 1;
                pdfStripper.setStartPage(page);
                pdfStripper.setEndPage(page);
                // 提取并清理当前页文本
                String currPageStr = pdfStripper.getText(pdfDocument).replace(" ", "");
                if (StringUtils.length(currPageStr) < 50) {
                    pdfResultInfoDto.getErrorList().add("第" + page + "页内容过少，无法整合！");
                    i++;
                    continue;
                }

                String first50Chars1 = currPageStr.substring(0, 50);
                String last50Chars1 = currPageStr.substring(currPageStr.length() - 50);

                int startI = i;
                int endI = i;

                if (first50Chars1.contains(PAGE_TITLE_STR) && last50Chars1.contains(FIRST_PAGE_STR) && !last50Chars1.contains(PAGE_TITLE_STR)) {
                    // 当前页包含章节起始标识，开始查找本章节结束页
                    while (true) {
                        i++;
                        if (i >= numPages) {
                            break;
                        }
                        pdfStripper.setStartPage(i + 1);
                        pdfStripper.setEndPage(i + 1);
                        String nextPageStr = pdfStripper.getText(pdfDocument).replace(" ", "");
                        if (nextPageStr.contains(FIRST_PAGE_STR)) {
                            // 找到下一章的起始页，结束查找
                            break;
                        } else {
                            // 更新章节结束页
                            endI = i;
                        }
                    }

                    // 按要求的复制次数添加本章节所有页
                    for (int m = 0; m < numCopiesPerChapter; m++) {
                        for (int j = startI; j <= endI; j++) {
                            list.add(j);
                        }
                    }
                } else {
                    // 当前页不包含章节起始标识，继续下一页
                    i++;
                    pdfResultInfoDto.getErrorList().add("第" + page + "页内容有误，无法整合！");
                }
            }

            // 创建输出PDF文档
            try (PDDocument outputDocument = new PDDocument()) {
                for (int pageIndex : list) {
                    PDPage page = pdfDocument.getPage(pageIndex);
                    // 添加页到输出文档
                    outputDocument.addPage(page);
                }
                // 保存输出文档
                CommonUtils.ensureParentDir(outputPath);
                outputDocument.save(outputPath);
            }

            pdfResultInfoDto.setNewPageCount(list.size());
            pdfResultInfoDto.setOutputPdfPath(outputPath);
        } catch (IOException e) {
            log.error("PDF整合失败！", e);
            throw new FrontException("PDF整合失败！" + e.getMessage());
        } finally {
            // 删除源文件
            if (!file.delete()) {
                log.warn("删除源文件失败，{}", pdfPath);
            }
        }

        return pdfResultInfoDto;
    }
}
