package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungExcelFile extends ObjectModelHelper<GisungExcelFile> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String originFileName;
    private String filePath;

    private BigDecimal size;

    public GisungExcelFile(String originFileName, String filePath, BigDecimal size) {
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.size = size;
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }
}
