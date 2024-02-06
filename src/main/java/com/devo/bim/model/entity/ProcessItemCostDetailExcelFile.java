package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class ProcessItemCostDetailExcelFile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String originFileName;
    private String filePath;
    private int sortNo;

    private BigDecimal size;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }

    public void setProcessItemCostDetailExcelFileAtAddProcessItemCostDetailExcelFile(UserInfo userInfo) {
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setProcessItemCostDetailExcelFile(String originFileName, String filePath, int sortNo, BigDecimal size) {
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
    }
}
