package com.devo.bim.model.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ProcessCategoryId implements Serializable {
    private String cate1;
    private String cate2;
    private String cate3;
    private String cate4;
    private String cate5;
    private String cate6;
    private String cate7;

    // 생성자
    public ProcessCategoryId() {
        // 기본 생성자
    }

    public ProcessCategoryId(String cate1, String cate2, String cate3, String cate4, String cate5, String cate6, String cate7) {
        this.cate1 = cate1;
        this.cate2 = cate2;
        this.cate3 = cate3;
        this.cate4 = cate4;
        this.cate5 = cate5;
        this.cate6 = cate6;
        this.cate7 = cate7;
    }

    // Getter와 Setter
    public String getCate1() {
        return cate1;
    }

    public void setCate1(String cate1) {
        this.cate1 = cate1;
    }

    public String getCate2() {
        return cate2;
    }

    public void setCate2(String cate2) {
        this.cate2 = cate2;
    }

    public String getCate3() {
        return cate3;
    }

    public void setCate3(String cate3) {
        this.cate3 = cate3;
    }

    public String getCate4() {
        return cate4;
    }

    public void setCate4(String cate4) {
        this.cate4 = cate4;
    }

    public String getCate5() {
        return cate5;
    }

    public void setCate5(String cate5) {
        this.cate5 = cate5;
    }

    public String getCate6() {
        return cate6;
    }

    public void setCate6(String cate6) {
        this.cate6 = cate6;
    }

    public String getCate7() {
        return cate7;
    }

    public void setCate7(String cate7) {
        this.cate7 = cate7;
    }
}
