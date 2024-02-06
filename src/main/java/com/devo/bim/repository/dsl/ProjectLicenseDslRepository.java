package com.devo.bim.repository.dsl;

import com.devo.bim.model.entity.ProjectLicense;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.devo.bim.model.entity.QProjectLicense.projectLicense;

@Repository
@RequiredArgsConstructor

public class ProjectLicenseDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<ProjectLicense> findMacAddressInProjectLicenseList(String userMacAddress) {
        return queryFactory
                .select(projectLicense)
                .from(projectLicense)
                .where(projectLicense.macAddress.eq(userMacAddress))
                .fetch();
    }
}
