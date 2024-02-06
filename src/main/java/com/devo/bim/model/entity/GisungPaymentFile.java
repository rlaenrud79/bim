package com.devo.bim.model.entity;

import com.devo.bim.component.Utils;
import com.devo.bim.model.embedded.WriteEmbedded;
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
public class GisungPaymentFile extends ObjectModelHelper<GisungPaymentFile> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "gisung_payment_id")
    private GisungPayment gisungPayment;

    private String originFileName;
    private String filePath;
    private int sortNo;

    private BigDecimal size;

    @Embedded
    private WriteEmbedded writeEmbedded;

    public GisungPaymentFile(long gisungPaymentId, String originFileName, String filePath, int sortNo, BigDecimal size, long writerId) {
        this.gisungPayment = new GisungPayment(gisungPaymentId);
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.sortNo = sortNo;
        this.size = size;
        this.writeEmbedded = new WriteEmbedded(writerId);
    }

    public String getOriginFileNameExt() {
        return Utils.getFileExtName(originFileName);
    }
}
