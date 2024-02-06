package com.devo.bim.model.entity;

import com.devo.bim.model.embedded.LastModifyEmbedded;
import com.devo.bim.model.embedded.WriteEmbedded;
import com.devo.bim.model.enumulator.CompanyRoleType;
import com.devo.bim.model.vo.CompanyVO;
import com.devo.bim.module.ObjectModelHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Company extends ObjectModelHelper<Company> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    @ManyToOne(fetch = LAZY)
    private CompanyRole companyRole;

    private String name;

    @Enumerated(STRING)
    private CompanyRoleType roleType;

    private String status;
    private String telNo;
    private boolean isDisplay;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="project_id")
    private Project project;

    @Embedded
    private WriteEmbedded writeEmbedded;

    @Embedded
    private LastModifyEmbedded lastModifyEmbedded;

    @ManyToMany(fetch = LAZY)
    @JoinTable(
            name="company_work",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "work_id")
    )
    private List<Work> works = new ArrayList<>();

    public Company(CompanyVO companyVO){
        if(companyVO.getCompanyId() > 0) this.id = companyVO.getCompanyId();
        this.companyRole = new CompanyRole(companyVO.getCompanyRoleId());
        this.name = companyVO.getCompanyName();
        this.telNo = companyVO.getTelNo();

        this.isDisplay = companyVO.isDisplay();
/*
        if(companyVO.isHead()) this.roleType = CompanyRoleType.HEAD;
        else this.roleType = CompanyRoleType.PARTNER;
*/

        if(companyVO.getIsHead() == 0) this.roleType = CompanyRoleType.HEAD;
        else if(companyVO.getIsHead() == 1) this.roleType = CompanyRoleType.LEAD;
        else if(companyVO.getIsHead() == 2) this.roleType = CompanyRoleType.PARTNER;
        else this.roleType = CompanyRoleType.PARTNER;

        if(companyVO.isEnabled()) this.status = "REG";
        else this.status = "DEL";

        this.project = new Project(companyVO.getProjectId());
        this.writeEmbedded = new WriteEmbedded(companyVO.getUserId());
        this.lastModifyEmbedded = new LastModifyEmbedded(companyVO.getUserId());

        if(companyVO.getCompanyWorks().size() > 0) {
            for (Long companyWorkId : companyVO.getCompanyWorks()) {
                setWork(new Work(companyWorkId));
            }
        }
    }

    public void setCompanyAtPutCompany(Company company){
        if(company.getId() != 0) this.id = company.getId();
        this.name = company.getName();
        this.roleType = company.getRoleType();
        this.telNo = company.getTelNo();
        this.isDisplay = company.isDisplay();
        this.status = company.getStatus();

        this.lastModifyEmbedded = new LastModifyEmbedded(company.lastModifyEmbedded.getLastModifier().getId());

        this.companyRole = new CompanyRole(company.getCompanyRole().getId());
        this.project = new Project(company.getProject().getId());

        this.works = new ArrayList<>();

        if(company.getWorks().size() > 0) {
            for (Work work : company.getWorks()) {
                setWork(new Work(work.getId()));
            }
        }
    }

    public void setWork(Work work){
        this.works.add(work);
        work.getCompanies().add(this);
    }

    public Company(long id)
    {
        this.id = id;
    }

    public String getWorksString()
    {
        return works.stream().map(t->t.getId()+"").collect(Collectors.joining(","));
    }

    public String getWorksNames(){
        String worksNames = "";

        for (Work work : this.works.stream().sorted(Comparator.comparing(Work::getId).reversed()).collect(Collectors.toList())) {
            worksNames += work.getWorkName() + ", ";
        }

        return StringUtils.removeEnd(worksNames, ", ");
    }

    public List<Long> getCompanyWorksIds(){
        return this.works.stream().map(o -> o.getId()).collect(Collectors.toList());
    }
}
