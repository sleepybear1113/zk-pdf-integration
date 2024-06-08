package cn.sleepybear.zkpdfintegration.exception;

import cn.sleepybear.zkpdfintegration.advice.ResultCodeConstant;
import lombok.Getter;

import java.io.Serial;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2022/09/18 20:04
 */
@Getter
public class FrontException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -850477151980098414L;

    private Integer code;

    public FrontException() {
    }

    public FrontException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public FrontException(ResultCodeConstant.CodeEnum codeEnum, String message) {
        this(codeEnum.getCode(), message);
    }

    public FrontException(String message) {
        this(ResultCodeConstant.CodeEnum.COMMON_ERROR, message);
    }

}