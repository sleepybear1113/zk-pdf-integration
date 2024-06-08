package cn.sleepybear.zkpdfintegration.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/06/08 00:01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdfResultInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 6019851347065787054L;

    private String filename;
    private String filenameOriginal;
    private Integer originalPageCount;
    private Integer newPageCount;

    private String outputPdfPath;
}
