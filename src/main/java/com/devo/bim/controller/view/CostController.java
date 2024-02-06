package com.devo.bim.controller.view;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.ProcessInfo;
import com.devo.bim.model.entity.Project;
import com.devo.bim.model.vo.SearchDocumentCategoryVO;
import com.devo.bim.model.vo.SearchDocumentVO;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/cost")
@RequiredArgsConstructor
@Secured("ROLE_ADMIN_ESTIMATE")
public class CostController extends AbstractController {
    private final CostService costService;
    private final ProjectService projectService;
    private final WorkService workService;
    private final ProcessService processService;
    private final AlertService alertService;
    private final ProcessCostService processCostService;

    @GetMapping("/indexForExcel")
    public String indexForExcel(Model model) {
        model.addAttribute("costs", costService.findCosts().stream().filter(t -> t.getCostSnapShots().size() > 0).collect(Collectors.toList()));
        model.addAttribute("costSnapShot", costService.findLatestCostSnapshot());

        return "cost/index/pageForExcel";
    }

    @GetMapping("/item")
    public String item(long costId, long costSnapShotId, Model model) {
        model.addAttribute("costs", costService.findCosts().stream().filter(t -> t.getCostSnapShots().size() > 0).collect(Collectors.toList()));
        model.addAttribute("costSnapShot", costService.findCostSnapshot(costId, costSnapShotId));

        return "cost/index/pageForExcel";
    }

    @GetMapping("/index")
    public String index(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) {
        Project project = projectService.findById();

        alertService.setIsReadTrueForProcessItem();

        if (searchProcessItemCostVO.getPageMode() == null || searchProcessItemCostVO.getPageMode().equals("")) {
            searchProcessItemCostVO.setPageMode("tree");
        }

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkVO.setUpId(0);
        List<WorkDTO> workDTOs = workService.findWorkUpDTOs(searchWorkVO);

        ProcessInfo processInfo = processService.getCurrentVersionProcessInfo();
        long currentProcessInfoId = processInfo.getId();

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processInfo);
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("workList", workDTOs);
        model.addAttribute("paidCostSum", processService.getProcessItemPaidCostSum(currentProcessInfoId));
        model.addAttribute("pageMode", searchProcessItemCostVO.getPageMode());

        return "cost/index/page";
    }

    @GetMapping("/indexTable")
    public String indexTable(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) {
        Project project = projectService.findById();

        alertService.setIsReadTrueForProcessItem();

        List<VmProcessItemDTO> VmProcessItemDTOs = processCostService.findProcessItemCostCurrentVersionList(searchProcessItemCostVO);
        if (searchProcessItemCostVO.getPageMode() == null || searchProcessItemCostVO.getPageMode().equals("")) {
            searchProcessItemCostVO.setPageMode("table");
        }
        Integer sumDuration = VmProcessItemDTOs.stream().filter(t -> t.getTaskDepth() == 1).mapToInt(t -> t.getDuration()).sum();
        BigDecimal sumCost = VmProcessItemDTOs.stream().map(t -> t.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkVO.setUpId(searchProcessItemCostVO.getWorkId());
        List<WorkDTO> workDTOs = workService.findWorkUpDTOs(searchWorkVO);

        ProcessInfo processInfo = processService.getCurrentVersionProcessInfo();
        long currentProcessInfoId = processInfo.getId();

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processInfo);
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("workList", workDTOs);
        model.addAttribute("paidCostSum", processService.getProcessItemPaidCostSum(currentProcessInfoId));
        model.addAttribute("pageMode", searchProcessItemCostVO.getPageMode());
        model.addAttribute("processItemList", VmProcessItemDTOs);
        model.addAttribute("sumDuration", sumDuration);
        model.addAttribute("sumCost", sumCost);

        return "cost/index/pageTable";
    }

    @GetMapping("/indexCardBody")
    public String indexCardBody(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) {
        Project project = projectService.findById();

        alertService.setIsReadTrueForProcessItem();

        List<VmProcessItemDTO> VmProcessItemDTOs = processCostService.findProcessItemCostCurrentVersionList(searchProcessItemCostVO);
        if (searchProcessItemCostVO.getPageMode() == null || searchProcessItemCostVO.getPageMode().equals("")) {
            searchProcessItemCostVO.setPageMode("table");
        }
        Integer sumDuration = VmProcessItemDTOs.stream().filter(t -> t.getTaskDepth() == 1).mapToInt(t -> t.getDuration()).sum();
        BigDecimal sumCost = VmProcessItemDTOs.stream().map(t -> t.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchWorkVO.setUpId(searchProcessItemCostVO.getWorkId());
        List<WorkDTO> workDTOs = workService.findWorkUpDTOs(searchWorkVO);

        ProcessInfo processInfo = processService.getCurrentVersionProcessInfo();
        long currentProcessInfoId = processInfo.getId();

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processInfo);
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("workList", workDTOs);
        model.addAttribute("paidCostSum", processService.getProcessItemPaidCostSum(currentProcessInfoId));
        model.addAttribute("pageMode", searchProcessItemCostVO.getPageMode());
        model.addAttribute("processItemList", VmProcessItemDTOs);
        model.addAttribute("sumDuration", sumDuration);
        model.addAttribute("sumCost", sumCost);

        return "cost/index/cardBody";
    }

    @GetMapping("/gisungCostList")
    public String gisungCostList(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) {
        Project project = projectService.findById();

        alertService.setIsReadTrueForProcessItem();

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processService.getCurrentVersionProcessInfo());
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("workList", workDTOs);

        return "gisung/cost/page";
    }

    @GetMapping("/indexForPass")
    public String indexForPass(@ModelAttribute SearchProcessItemCostVO searchProcessItemCostVO, Model model) {
        Project project = projectService.findById();

        alertService.setIsReadTrueForProcessItem();

        model.addAttribute("searchVO", searchProcessItemCostVO);
        model.addAttribute("processInfo", processService.getCurrentVersionProcessInfo());
        model.addAttribute("workNames", workService.findByLanguageCode());
        model.addAttribute("projectStartDate", project.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("projectEndDate", project.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "cost/index/pageForPass";
    }

    @GetMapping("/addListExcelUpload")
    public String addListExcelUpload(Model model) {

        model.addAttribute("costFileExtension", String.join("||", configService.findFileExtension("EXCEL_FILE_EXT", true, userInfo.getProjectId())));
        return "cost/modal/listExcelUpload";
    }
}
