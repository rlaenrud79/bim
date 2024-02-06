package com.devo.bim.model.entity;

import com.devo.bim.component.UserInfo;
import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.vo.GisungListExcelFileVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class GisungListExcelFile extends ObjectModelHelper<Gisung> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_id")
    private Gisung gisung;

    private String originFileName;
    private String filePath;
    private int sortNo;

    private BigDecimal size;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }

    public void setGisungListExcelFileAtAddGisungListExcelFile(GisungListExcelFileVO gisungListExcelFileVO, UserInfo userInfo) {
        this.gisung = new Gisung(gisungListExcelFileVO.getGisungId());
        this.writeEmbedded = new WriteEmbedded(userInfo.getId());
    }

    public void setGisungListExcelFile(String originFileName, String filePath, int sortNo, BigDecimal size) {
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
    }
}