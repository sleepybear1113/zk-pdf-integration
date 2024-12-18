package cn.sleepybear.zkpdfintegration.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * There is description
 *
 * @author sleepybear
 * @date 2024/12/18 22:27
 */
@Data
public class SubjectInfoDto implements Serializable {
    @Serial
    private static final long serialVersionUID = -2559275756680586981L;

    private Integer id;
    private String subjectName;
    private String subjectCode;
    private String school;
    private Integer index;
    private String pageNum;
    private Integer startIndex;
    private Integer endIndex;

    public SubjectInfoDto() {
        generateId();
    }

    /**
     * 随机数
     */
    public void generateId() {
        this.id = (int) (Math.random() * 100000);
    }

    public String getKey() {
        return this.subjectCode + "_" + this.school + "_" + this.subjectName;
    }

    /**
     * 先比较 subjectCode，再比较 school，最后比较 subjectName
     *
     * @param o 比较对象
     * @return 比较结果
     */
    public int compareTo(SubjectInfoDto o) {
        if (o == null) {
            return 1;
        }
        if (this.subjectCode.equals(o.subjectCode)) {
            if (this.school.equals(o.school)) {
                return this.subjectName.compareTo(o.subjectName);
            }
            return this.school.compareTo(o.school);
        }
        return this.subjectCode.compareTo(o.subjectCode);
    }
}
