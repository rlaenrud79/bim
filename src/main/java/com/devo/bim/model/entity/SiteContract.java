package com.devo.bim.model.entity;

import com.devo.bim.model.dto.SiteContractDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NamedNativeQuery(
    name = "find_site_contract",
    query = " " +
        " select " +
        " (row_number() over()) as id," +
        " :projectId projectId," +
        " v1 siteStatus," +
        " sum( case when outtbl.v2 ~ '^([0-9]+[.]?[0-9]*|[.][0-9]+)$' then cast(outtbl.v2 as bigint) else 0 end ) siteSum" +
        " from(" +
        "	select" +
        "		tbl1.modeling_assembly_id, tbl1.attribute_value v1, tbl2.attribute_value v2 " +
        "	from (" +
        "		select attr.modeling_assembly_id, attr.attribute_name, attr.attribute_value  from modeling m inner join modeling_assembly assem " +
        "		on m.id = assem.modeling_id and m.latest = true and m.project_id = :projectId" +
        "		inner join modeling_attribute attr " +
        "		on assem.id = attr.modeling_assembly_id and attr.attribute_name = 'Data/계약완료'" +
        "		and attr.attribute_value in( '보상완료', '수용재결', '미보상')" + // 용지도 계약현황 "계약완료" > "보상완료"로 변경
        "	) tbl1" +
        "	inner join" +
        "	(" +
        "		select attr.modeling_assembly_id, attr.attribute_name, attr.attribute_value  from modeling m inner join modeling_assembly assem " +
        "		on m.id = assem.modeling_id and m.latest = true and m.project_id = :projectId" +
        "		inner join modeling_attribute attr " +
        "		on assem.id = attr.modeling_assembly_id and attr.attribute_name = 'Data/감정평가금액' and attr.attribute_value <> '-'" +
        "	) tbl2" +
        "	on tbl1.modeling_assembly_id = tbl2.modeling_assembly_id" +
        " )  outtbl " +
        " group by outtbl.v1",
        resultSetMapping = "site_contract_dto"
)
@SqlResultSetMapping(
    name = "site_contract_dto",
        classes = @ConstructorResult(
                targetClass = SiteContract.class,
                columns = {
                        @ColumnResult(name = "id", type= Long.class),
                        @ColumnResult(name = "projectId", type= Long.class),
                        @ColumnResult(name = "siteStatus", type= String.class),
                        @ColumnResult(name = "siteSum", type = String.class)
                }
        )
)
@NoArgsConstructor
@Getter
@Setter
public class SiteContract implements Serializable {
    private static final long serialVesionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private String siteStatus;
    private String siteSum;

    public SiteContract(Long id, Long projectId, String siteStatus, String siteSum){
        this.id = id;
        this.projectId = projectId;
        this.siteStatus = siteStatus;
        this.siteSum = siteSum;
    }
}
