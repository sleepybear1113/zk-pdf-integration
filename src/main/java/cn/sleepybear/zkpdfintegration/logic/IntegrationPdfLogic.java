package cn.sleepybear.zkpdfintegration.logic;

import cn.sleepybear.zkpdfintegration.config.MyConfig;
import cn.sleepybear.zkpdfintegration.dto.PdfResultInfoDto;
import cn.sleepybear.zkpdfintegration.dto.SubjectInfoDto;
import cn.sleepybear.zkpdfintegration.exception.FrontException;
import cn.sleepybear.zkpdfintegration.utils.CommonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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
    private static final String PAGE_TITLE_STR = "毕业生花名册";
    private static final String PAGE_TITLE_STR2 = "花名册";

    @Resource
    private MyConfig myConfig;

    public static void main(String[] args) {
        String pdfPath = "test/pdf/input/毕业生花名册_@@_4.pdf";
        String outputFolder = "test/pdf/output/test.pdf";
        String picPath = "test/pdf/input/111.png";
        int numCopiesPerChapter = 3;

        integrationPdf(new File(pdfPath), new File(picPath), outputFolder, numCopiesPerChapter, true);
        System.out.println("PDF复制和整合完成！");
    }

    public PdfResultInfoDto integrationPdf(String filename, String picFilename, Integer n, boolean sort) {
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

        File picFile = null;
        if (StringUtils.isNotBlank(picFilename)) {
            picFile = new File(myConfig.getTmpDir() + picFilename);
            if (!picFile.exists()) {
                picFile = null;
            }
        }

        PdfResultInfoDto pdfResultInfoDto = integrationPdf(file, picFile, outputFolder, n, sort);
        // 删除源文件
        if (!file.delete()) {
            log.warn("删除源文件失败，{}", pdfPath);
        }
        return pdfResultInfoDto;
    }

    private static PdfResultInfoDto integrationPdf(File file, File picFile, String outputPath, int numCopiesPerChapter, boolean sort) {
        List<Integer> list = new ArrayList<>();
        PdfResultInfoDto pdfResultInfoDto = new PdfResultInfoDto();

        // 加载PDF文档
        pdfResultInfoDto.setFilenameOriginal(file.getName());
        pdfResultInfoDto.setFilename(file.getName().split(UploadLogic.UPLOAD_FILE_SPLIT)[1]);
        try (PDDocument pdfDocument = Loader.loadPDF(file)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();

            List<SubjectInfoDto> subjectInfoDtoList = new ArrayList<>();
            // 获取 PDF 总页数
            int numPages = pdfDocument.getNumberOfPages();
            pdfResultInfoDto.setOriginalPageCount(numPages);
            for (int i = 0; i < numPages; i++) {
                // 设置当前页为起始页和结束页，只提取该页内容
                int page = i + 1;
                pdfStripper.setStartPage(page);
                pdfStripper.setEndPage(page);
                // 提取并清理当前页文本，将多个空格或者空白替换为空白
                String currPageStr = pdfStripper.getText(pdfDocument).trim().replaceAll(" ", "");

                // 检测当前页内容是否符合要求：不为空，包含指定标题和页码
                if (currPageStr.isBlank()) {
                    pdfResultInfoDto.getErrorList().add("第%d页内容为空，无法整合！请检查PDF文件！".formatted(page));
                    continue;
                }
                String[] lines = currPageStr.split("\n");
                if (lines.length < 3) {
                    pdfResultInfoDto.getErrorList().add("第%d页内容有误，无法整合！请检查PDF文件！内容过少".formatted(page));
                    continue;
                }
                if (!lines[0].contains(PAGE_TITLE_STR) && !lines[0].contains(PAGE_TITLE_STR2)) {
                    pdfResultInfoDto.getErrorList().add("第%d页内容有误，无法整合！请检查PDF文件！首行不包含“%s”字样！".formatted(page, PAGE_TITLE_STR));
                    continue;
                }

                SubjectInfoDto subjectInfoDto = new SubjectInfoDto();

                // 第二行，提取专业名称，spilt by 20xx年
                String line2 = lines[1];
                String zy = "专业：";
                if (!line2.contains(zy)) {
                    pdfResultInfoDto.getErrorList().add("第%d页内容有误，无法整合！请检查PDF文件！第二行不包含“%s”字样！".formatted(page, zy));
                    continue;
                }
                String zyCodeName = line2.substring(zy.length()).split("20\\d{2}年")[0].trim();
                String[] split = zyCodeName.split("-");
                subjectInfoDto.setSubjectCode(split[0]);
                subjectInfoDto.setSubjectName(split[1]);

                // 最后一行，提取页码，第n页
                String last3Line = lines[lines.length - 3];
                if (!last3Line.contains("第") && !last3Line.contains("页")) {
                    pdfResultInfoDto.getErrorList().add("第%d页内容有误，无法整合！请检查PDF文件！末行不包含“第xx页”字样！".formatted(page));
                    continue;
                }
                String[] split1 = last3Line.split("第");
                String pageNum = split1[1].split("页")[0];
                subjectInfoDto.setPageNum(pageNum);
                subjectInfoDto.setIndex(i);

                // 倒数第二行，提取学校名称
                String lineLast2 = lines[lines.length - 2].trim();
                if (!lineLast2.contains("市教育考试机构盖章") && !lineLast2.contains("盖章省自考委盖章")) {
                    pdfResultInfoDto.getErrorList().add("第%d页内容有误，无法整合！请检查PDF文件！倒数第二行不包含“高校名称”字样！".formatted(page));
                    continue;
                }
                String school = lineLast2.replace("市教育考试机构盖章", "").replace("盖章省自考委盖章", "");
                subjectInfoDto.setSchool(school);

                subjectInfoDtoList.add(subjectInfoDto);
            }

            List<SubjectInfoDto> resList = new ArrayList<>();
            SubjectInfoDto pre = null;
            for (SubjectInfoDto subjectInfoDto : subjectInfoDtoList) {
                if (pre == null || !pre.getKey().equals(subjectInfoDto.getKey())) {
                    pre = subjectInfoDto;
                    pre.setStartIndex(subjectInfoDto.getIndex());
                    pre.setEndIndex(subjectInfoDto.getIndex());
                    resList.add(subjectInfoDto);
                } else {
                    pre.setEndIndex(subjectInfoDto.getIndex());
                }
            }

            if (sort) {
                resList.sort(SubjectInfoDto::compareTo);
            }

            for (SubjectInfoDto subjectInfoDto : resList) {
                for (int m = 0; m < numCopiesPerChapter; m++) {
                    for (int j = subjectInfoDto.getStartIndex(); j <= subjectInfoDto.getEndIndex(); j++) {
                        list.add(j);
                    }
                }
            }
            pdfResultInfoDto.setNewPageCount(list.size());
            if (list.isEmpty()) {
                pdfResultInfoDto.getErrorList().add("无法整合PDF文件！");
                return pdfResultInfoDto;
            }

            // 创建输出PDF文档
            try (PDDocument outputDocument = new PDDocument()) {

                for (int pageIndex : list) {
                    PDPage page = pdfDocument.getPage(pageIndex);

                    if (picFile != null) {
                        PDImageXObject pdImage = PDImageXObject.createFromFile(picFile.getAbsolutePath(), outputDocument);
                        // 图片大小 (根据需要调整)
                        float imageWidth = pdImage.getWidth();
                        float imageHeight = pdImage.getHeight();
                        // 创建一个内容流，追加内容
                        PDPageContentStream contentStream = new PDPageContentStream(outputDocument, page, PDPageContentStream.AppendMode.APPEND, true);
                        float xPosition = 428f;
                        float yPosition = 3430f;
                        // 在指定位置插入图片
                        contentStream.drawImage(pdImage, xPosition, yPosition, imageWidth, -imageHeight);
                        // 关闭内容流
                        contentStream.close();
                    }

                    // 添加页到输出文档
                    outputDocument.addPage(page);
                }
                // 保存输出文档
                CommonUtils.ensureParentDir(outputPath);
                outputDocument.save(outputPath);
            }

            pdfResultInfoDto.setOutputPdfPath(outputPath);
        } catch (IOException e) {
            log.error("PDF整合失败！", e);
            throw new FrontException("PDF整合失败！" + e.getMessage());
        }

        return pdfResultInfoDto;
    }
}
