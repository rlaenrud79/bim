package com.devo.bim.controller.modal;

import com.devo.bim.model.dto.VmProcessItemDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.SearchProcessItemCostVO;
import com.devo.bim.repository.dsl.VmProcessItemDTODslRepository;
import com.devo.bim.repository.spring.ProcessInfoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.service.JobSheetGrantorService;
import com.devo.bim.service.JobSheetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/projectModal")
@RequiredArgsConstructor
public class ProjectModalController extends AbstractController {

    private final JobSheetService jobSheetService;
    private final JobSheetGrantorService jobSheetGrantorService;

    private final ProcessInfoRepository processInfoRepository;
    private final VmProcessItemDTODslRepository vmProcessItemDTODslRepository;

    @GetMapping("/jobSheetReferenceList")
    public String jobSheetReferenceList(long jobSheetId, Model model) {
        JobSheet jobSheet = jobSheetService.findJobSheetReferencesById(jobSheetId);
        model.addAttribute("references", jobSheet.getJobSheetReferences());

        return "project/modal/jobSheetReferenceList";
    }

    @GetMapping("/denyJobSheet")
    public String denyJobSheet(long id, Model model) {
        model.addAttribute("jobSheetGrantor", jobSheetGrantorService.findById(id));
        return "project/modal/denyJobSheet";
    }

    @GetMapping("/approveJobSheet")
    public String approveJobSheet(long id, Model model) {
        model.addAttribute("jobSheetGrantor", jobSheetGrantorService.findById(id));
        return "project/modal/approveJobSheet";
    }

    @GetMapping("/selectWorkerList")
    public String selectWorkerList(@RequestParam(required = true, defaultValue = "") String formElementIdForModal,
                                   long id, Model model) {

        long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();
        List<ProcessWorker> processWorkerList = jobSheetService.findByProjectIdAndProcessIdAndWorkerList(processId);

        //model.addAttribute("jobSheetGrantor", jobSheetGrantorService.findById(id));
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("currentProcessItemIndex", id);
        model.addAttribute("processWorkerList", processWorkerList);
        return "project/modal/selectWorkerList";
    }

    @GetMapping("/selectDeviceList")
    public String selectDeviceList(@RequestParam(required = true, defaultValue = "") String formElementIdForModal,
                                   long id, Model model) {
        //model.addAttribute("jobSheetGrantor", jobSheetGrantorService.findById(id));
        long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();
        List<ProcessDevice> processDeviceList = jobSheetService.findByProjectIdAndProcessIdAndDeviceList(processId);

        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("currentProcessItemIndex", id);
        model.addAttribute("processDeviceList", processDeviceList);
        return "project/modal/selectDeviceList";
    }

    @GetMapping("/jobSheetProcessItemWorkerTable")
    public String jobSheetProcessItemWorkerTable(@RequestParam(required = true, defaultValue = "") String formElementIdForModal,
                                   long jobSheetId, Model model) {

        JobSheetProcessItem jobSheetProcessItem = jobSheetService.findJobSheetProcessItemById(jobSheetId);

        //model.addAttribute("jobSheetGrantor", jobSheetGrantorService.findById(id));
        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("currentProcessItemIndex", jobSheetId);
        model.addAttribute("jobSheetProcessItem", jobSheetProcessItem);
        return "project/modal/jobSheetProcessItemWorkerTable";
    }

    @GetMapping("/jobSheetProcessItemDeviceTable")
    public String jobSheetProcessItemDeviceTable(@RequestParam(required = true, defaultValue = "") String formElementIdForModal,
                                   long jobSheetId, Model model) {
        JobSheetProcessItem jobSheetProcessItem = jobSheetService.findJobSheetProcessItemById(jobSheetId);

        model.addAttribute("formElementIdForModal", formElementIdForModal);
        model.addAttribute("currentProcessItemIndex", jobSheetId);
        model.addAttribute("jobSheetProcessItem", jobSheetProcessItem);
        return "project/modal/jobSheetProcessItemDeviceTable";
    }

    @GetMapping("/processItemSearchList")
    public String processItemSearchList(@RequestParam(required = true, defaultValue = "") String taskName,
                                        Model model) {
        SearchProcessItemCostVO searchProcessItemCostVO = new SearchProcessItemCostVO();
        searchProcessItemCostVO.setTaskName(taskName);
        List<VmProcessItemDTO> vmProcessItemDTOS = vmProcessItemDTODslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO);
        String prevCate1 = "";
        long cateCnt = 0;
        List<VmProcessItemDTO> savedVmProcessItemDTO = new ArrayList<>();
        for (VmProcessItemDTO vmProcessItemDTO : vmProcessItemDTOS) {
            if (!prevCate1.equals(vmProcessItemDTO.getCate1())) {
                cateCnt = vmProcessItemDTOS.stream().filter(t -> t.getCate1().equals(vmProcessItemDTO.getCate1())).count();
                SearchProcessItemCostVO savedSearchProcessItemCostVO = new SearchProcessItemCostVO();
                savedSearchProcessItemCostVO.setCate1(vmProcessItemDTO.getCate1());
                VmProcessItemDTO savedVmProcessItemDTO1 = vmProcessItemDTODslRepository.findProcessItemByCate(userInfo.getProjectId(), savedSearchProcessItemCostVO);
                savedVmProcessItemDTO1.setFirstCount(BigDecimal.valueOf(cateCnt));
                savedVmProcessItemDTO.add(savedVmProcessItemDTO1);
                prevCate1 = vmProcessItemDTO.getCate1();
                System.out.println("cate1 : " + vmProcessItemDTO.getCate1() + ", cate1name : " + vmProcessItemDTO.getCate1Name() + ", prevCate1 : " + prevCate1);
            }
            savedVmProcessItemDTO.add(vmProcessItemDTO);
        }

        model.addAttribute("taskName", searchProcessItemCostVO.getTaskName());
        model.addAttribute("totalCnt", vmProcessItemDTOS.size());
        model.addAttribute("vmProcessItemDTOS", savedVmProcessItemDTO);
        model.addAttribute("processItemJson", proc.getResult(vmProcessItemDTOS));
        return "project/modal/processItemSearchList";
    }
}
