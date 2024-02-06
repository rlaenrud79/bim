package com.devo.bim.service;

import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.entity.WorkName;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.WorkVO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback
public class WorkServiceTest extends AbstractServiceTest {

    @Autowired
    private WorkService workService;

    @Test
    public void getWorkDtoList_default() {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(1);
        searchWorkVO.setLang("KO");

        List<WorkDTO> result = workService.findWorkDTOs(searchWorkVO);

        Assertions.assertThat(result.get(0).getWorkName()).isEqualTo("배관 공종");
    }

    @Test
    public void getWorkDtoList_sortByName() {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(1);
        searchWorkVO.setLang("KO");
        searchWorkVO.setSortProp("NAME");

        List<WorkDTO> result = workService.findWorkDTOs(searchWorkVO);

        Assertions.assertThat(result.get(0).getWorkName()).isEqualTo("도로 공종");
    }

    @Test
    public void getWorkDtoList_sortByStatus() {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(1);
        searchWorkVO.setLang("KO");
        searchWorkVO.setSortProp("STATUS");

        List<WorkDTO> result = workService.findWorkDTOs(searchWorkVO);

        Assertions.assertThat(result.get(0).getWorkName()).isEqualTo("배수 공종");
    }

    @Test
    public void postWorkAndWorkName_test() {
        int sortNo = 3;

        WorkName workNameEN = new WorkName("EN", "WorkName_EN");
        WorkName workNameKO = new WorkName("KO", "WorkName_KO");
        WorkName workNameZH = new WorkName("ZH", "WorkName_ZH");

        List<WorkName> workNames = new ArrayList<>();
        workNames.add(workNameEN);
        workNames.add(workNameKO);
        workNames.add(workNameZH);

        WorkVO workVO = new WorkVO();
        workVO.setSortNo(sortNo);
        workVO.setWorkNames(workNames);

        workService.postWorkAndWorkName(workVO);

        Work savedWork = workService.findBySortNo(sortNo);
        Assertions.assertThat(savedWork.getName()).isEqualTo(workVO.getWorkName("EN"));

        for (WorkName workName : savedWork.getWorkNames()) {
            if("EN".equals(workName.getLanguageCode())) Assertions.assertThat(workName.getName()).isEqualTo("WorkName_EN");
            if("KO".equals(workName.getLanguageCode())) Assertions.assertThat(workName.getName()).isEqualTo("WorkName_KO");
            if("ZH".equals(workName.getLanguageCode())) Assertions.assertThat(workName.getName()).isEqualTo("WorkName_ZH");
        }
    }

    @Test
    public void putWorkAndWorkName_addOneUpdate_test() {
        long workId = 3L;
        int sortNo = 1;

        WorkName workNameEN = new WorkName("EN", "WorkName_EN_Update");
        WorkName workNameKO = new WorkName("KO", "WorkName_KO_Update");
        WorkName workNameZH = new WorkName("ZH", "WorkName_ZH_Update");

        List<WorkName> workNames = new ArrayList<>();
        workNames.add(workNameEN);
        workNames.add(workNameKO);
        workNames.add(workNameZH);

        WorkVO workVO = new WorkVO();
        workVO.setWorkId(workId);
        workVO.setSortNo(sortNo);
        workVO.setWorkNames(workNames);

        workService.putWorkAndWorkName(workVO);

        Work savedWork1 = workService.findById(workId);
        Assertions.assertThat(savedWork1.getName()).isEqualTo(workVO.getWorkName("EN"));
        Assertions.assertThat(savedWork1.getSortNo()).isEqualTo(sortNo);

        for (WorkName workName : savedWork1.getWorkNames()) {
            if("EN".equals(workName.getLanguageCode())) Assertions.assertThat(workName.getName()).isEqualTo("WorkName_EN_Update");
            if("KO".equals(workName.getLanguageCode())) Assertions.assertThat(workName.getName()).isEqualTo("WorkName_KO_Update");
            if("ZH".equals(workName.getLanguageCode())) Assertions.assertThat(workName.getName()).isEqualTo("WorkName_ZH_Update");
        }


        Work savedWork2 = workService.findBySortNo(2);
        Assertions.assertThat(savedWork2.getName()).isEqualTo("배관 공종");
    }

    @Test
    public void putWorkStatus_DEL_USE_test() {
        long workId = 1;

        workService.putWorkStatus(workId, WorkStatus.DEL);

        Work savedWork1 = workService.findById(workId);
        Assertions.assertThat(savedWork1.getStatus().toString()).isEqualTo("DEL");

        workService.putWorkStatus(workId, WorkStatus.USE);

        Work savedWork2 = workService.findById(workId);
        Assertions.assertThat(savedWork2.getStatus().toString()).isEqualTo("USE");

    }

    @Test
    public void putWorkSortNoASC_test() {
        long workId = 2;

        workService.putWorkSortNoASC(workId);

        Work savedWork = workService.findById(workId);
        Assertions.assertThat(savedWork.getSortNo()).isEqualTo(1);

    }

    @Test
    public void putWorkSortNoDESC_test() {
        long workId = 2;

        workService.putWorkSortNoDESC(workId);

        Work savedWork = workService.findById(workId);
        Assertions.assertThat(savedWork.getSortNo()).isEqualTo(3);

    }
}