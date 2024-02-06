package com.devo.bim.controller.modal;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.ProcessItemCategoryDslRepository;
import com.devo.bim.repository.dsl.ProcessItemCostDslRepository;
import com.devo.bim.repository.spring.ProcessItemRepository;
import com.devo.bim.service.ProcessCostService;
import com.devo.bim.service.ProcessService;
import com.devo.bim.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/processModal")
@RequiredArgsConstructor
public class ProcessModalController extends AbstractController {

    private final ProcessService processService;
    private final ProcessCostService processCostService;
    private final ProcessItemCategoryDslRepository processItemCategoryDslRepository;
    private final ProcessItemCostDslRepository processItemCostDslRepository;
    private final ProcessItemRepository processItemRepository;
    private final WorkService workService;

    @GetMapping("/uploadProcessFile")
    public String uploadProcessFile(Model model){
        return "process/modal/processFileUpload";
    }

    @GetMapping("/startValidate")
    public String startValidate(Model model){
        return "process/modal/startValidate";
    }

    @GetMapping("/processingCodeValidation")
    public String processingCodeValidation(Model model){
        return "process/modal/processingCodeValidation";
    }

    @GetMapping({"/resultCodeValidation", "/resultCodeValidationCardBody"})
    public String resultCodeValidation(@ModelAttribute SearchCodeValidationVO searchCodeValidationVO
            , @PageableDefault(size = defaultPageSize_codeValidationResultList) Pageable pageable
            , HttpServletRequest request
            , Model model){

        setProcessItemSearchCondition(searchCodeValidationVO);

        PageDTO<ProcessItem> processItems = processService.findCurrentVersionTaskPageByProjectId(searchCodeValidationVO, pageable);

        setPagingConfig(model, processItems);

        model.addAttribute("successProcessItemCnt", processItems.getSuccessCount());
        model.addAttribute("failProcessItemCnt", processItems.getFailCount());
        model.addAttribute("noneProcessItemCnt", processItems.getNoneCount());
        model.addAttribute("searchCodeValidationVO", searchCodeValidationVO);

        if ("resultCodeValidation".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "process/modal/resultCodeValidation";;
        return "process/modal/resultCodeValidationCardBody";
    }

    private void setProcessItemSearchCondition(SearchCodeValidationVO searchCodeValidationVO) {
        searchCodeValidationVO.setProjectId(userInfo.getProjectId());

    }

    @GetMapping("/getSaveVersionList")
    public String getSaveVersionList(Model model){
        model.addAttribute("processInfoDTOs", processService.findProcessInfoDTOs());
        return "process/modal/saveVersionList";
    }

    @GetMapping("/costDetail/{processItemId}")
    public String costDetail(@PathVariable Long processItemId, Model model) {

        if(!haveRightCostDetailButton()) {
            throw new ForbiddenException(proc.translate("system.process_modal_controller.do_not_have_right"));
        }

        model.addAttribute("processItem", processCostService.findProcessItemById(processItemId));
        return "process/modal/costDetail";
    }

    private boolean haveRightCostDetailButton() {
        if(userInfo.isRoleAdminProject()) return true;
        if(userInfo.isRoleAdminProcess() && userInfo.isRoleAdminEstimate()) return true;
        return false;
    }

    /**
    @GetMapping("/searchProcessHtml")
    public String searchProcessHtml(Model model, SearchProcessItemCostVO searchProcessItemCostVO){
        List<ProcessItemCostDTO> processItemCost = processItemCostDslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), searchProcessItemCostVO);
        String html = "";
        if (processItemCost.size() > 0) {
            String depth1 = "";
            String depth2 = "";
            String depth3 = "";
            String depth4 = "";
            String depth5 = "";
            String depth6 = "";
            String depth7 = "";
            int i = 0;
            for (ProcessItemCostDTO processItemCostDTO : processItemCost) {
                String itemCheckName = processItemCostDTO.getCate1() + "" + processItemCostDTO.getCate2() + "" + processItemCostDTO.getCate3() + "" + processItemCostDTO.getCate4() + "" + processItemCostDTO.getCate5();
                String itemCheck = "itemCheck"+i;
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate2().equals("000")) {   //1depth
                    if (!depth2.equals("") && !depth2.equals("000")) {
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li>";
                    } else {
                        html += "<section class=\"depth0\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(1, itemCheck, itemCheckName, processItemCostDTO);
                }
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate3() != null && !processItemCostDTO.getCate2().equals("000") && processItemCostDTO.getCate3().equals("000")) {   //2depth
                    if (!depth3.equals("000")) {
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li>";
                    }
                    if (depth2.equals("") || depth2.equals("000")) {
                        html += "<section class=\"depth1\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(2, itemCheck, itemCheckName, processItemCostDTO);
                }
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate3() != null && processItemCostDTO.getCate4() != null && !processItemCostDTO.getCate2().equals("000") && !processItemCostDTO.getCate3().equals("000") && processItemCostDTO.getCate4().equals("000")) {   //3depth
                    if (!depth4.equals("000")) {
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li>";
                    }
                    if (depth3.equals("") || depth3.equals("000")) {
                        html += "<section class=\"depth2\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(3, itemCheck, itemCheckName, processItemCostDTO);
                }
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate3() != null && processItemCostDTO.getCate4() != null
                        && processItemCostDTO.getCate5() != null && !processItemCostDTO.getCate2().equals("000") && !processItemCostDTO.getCate3().equals("000")
                        && !processItemCostDTO.getCate4().equals("000") && processItemCostDTO.getCate5().equals("000")) {   //4depth
                    if (!depth5.equals("000")) {
                        html += "</li></ul></section>";
                        html += "</li></ul></section>";
                        html += "</li>";
                    }
                    if (depth4.equals("") || depth4.equals("000")) {
                        html += "<section class=\"depth3\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(4, itemCheck, itemCheckName, processItemCostDTO);
                }
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate3() != null && processItemCostDTO.getCate4() != null
                        && processItemCostDTO.getCate5() != null && processItemCostDTO.getCate6() != null && !processItemCostDTO.getCate2().equals("000") && !processItemCostDTO.getCate3().equals("000")
                        && !processItemCostDTO.getCate4().equals("000") && !processItemCostDTO.getCate5().equals("000") && processItemCostDTO.getCate6().equals("00000")) {   //5depth
                    if (!depth6.equals("00000")) {
                        html += "</li></ul></section>";
                        html += "</li>";
                    }
                    if (depth5.equals("") || depth5.equals("000")) {
                        html += "<section class=\"depth4\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(5, itemCheck, itemCheckName, processItemCostDTO);
                }
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate3() != null && processItemCostDTO.getCate4() != null
                        && processItemCostDTO.getCate5() != null && processItemCostDTO.getCate6() != null && processItemCostDTO.getCate7() != null && !processItemCostDTO.getCate2().equals("000") && !processItemCostDTO.getCate3().equals("000")
                        && !processItemCostDTO.getCate4().equals("000") && !processItemCostDTO.getCate5().equals("000") && !processItemCostDTO.getCate6().equals("00000") && processItemCostDTO.getCate7().equals("00000")) {   //6depth
                    if (!depth7.equals("00000")) {
                        html += "</li></ul></section>";
                        html += "</li>";
                    }
                    if (depth6.equals("") || depth6.equals("00000")) {
                        html += "<section class=\"depth5\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(6, itemCheck, itemCheckName, processItemCostDTO);
                }
                if (processItemCostDTO.getCate2() != null && processItemCostDTO.getCate3() != null && processItemCostDTO.getCate4() != null
                        && processItemCostDTO.getCate5() != null && processItemCostDTO.getCate6() != null && processItemCostDTO.getCate7() != null && !processItemCostDTO.getCate2().equals("000") && !processItemCostDTO.getCate3().equals("000")
                        && !processItemCostDTO.getCate4().equals("000") && !processItemCostDTO.getCate5().equals("000") && !processItemCostDTO.getCate6().equals("00000") && !processItemCostDTO.getCate7().equals("00000")) {   //7depth
                    if (depth7.equals("") || depth7.equals("00000")) {
                        html += "<section class=\"depth6\">";
                        html += "<ul>";
                    }
                    html += getProgressItemDiv(7, itemCheck, itemCheckName, processItemCostDTO);
                }
                depth1 = processItemCostDTO.getCate1();
                depth2 = processItemCostDTO.getCate2();
                depth3 = processItemCostDTO.getCate3();
                depth4 = processItemCostDTO.getCate4();
                depth5 = processItemCostDTO.getCate5();
                depth6 = processItemCostDTO.getCate6();
                depth7 = processItemCostDTO.getCate7();
                i = i + 1;
            }
            html += "</li></ul></section>";
            html += "</li></ul></section>";
            html += "</li></ul></section>";
            html += "</li></ul></section>";
            html += "</li></ul></section>";
            html += "</li></ul></section>";
        }

        model.addAttribute("processCategoryContent", html);
        return "process/modal/searchProcessHtml";
    }
    **/
    public String getProgressItemDiv(int depthNo, String itemCheck, String itemCheckName, ProcessItemCostDTO processItemCostDTO) {
        String html = "";

        html += "<li><div class=\"menu-top\">";
        html += "<button class=\"menu-btn\"></button>";
        html += "<div class=\"menu-tit\">";
        html += "<span>"+processItemCostDTO.getTaskName()+"</span>";
        html += "<span class=\"code\">("+processItemCostDTO.getPhasingCode()+"</span>";
        html += "<span class=\"rate\">30%</span>";
        html += "</div>";
        html += "<div class=\"check-set\">";
        if (depthNo <= 3) {
            html += "<input type=\"checkbox\" id=\""+itemCheck+"\">";
            html += "<label for=\""+itemCheck+"\"></label>";
        } else {
            if (processItemCostDTO.getGanttTaskType() != null && processItemCostDTO.getGanttTaskType().equals("TASK")) {
                html += "<input type=\"checkbox\" id=\""+itemCheck+"\" class=\"itemCheck \""+itemCheckName+"\" name=\"item_no[]\" data-no=\"" + processItemCostDTO.getProcessItemId()+"\" value=\"" + processItemCostDTO.getProcessItemId()+"\">";
                html += "<label for=\""+itemCheck+"\"></label>";
            } else {
                html += "<input type=\"checkbox\" id=\""+itemCheck+"\" class=\"itemCheck\" data-name=\""+itemCheckName+"\">";
                html += "<label for=\""+itemCheck+"\"></label>";
            }
        }
        html += "</div>";
        html += "</div>";
        return html;
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    /**
     * 공정 등록
     * @param model
     * @return
     */
    @GetMapping("/addProcessItem")
    public String addProcessItem(@ModelAttribute SearchProcessItemCategoryVO searchProcessItemCategoryVO, Model model) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        searchProcessItemCategoryVO.setUpCode("00000");
        searchProcessItemCategoryVO.setCateNo(1);
        List<ProcessItemCategoryDTO> cate1List = processItemCategoryDslRepository.selectCateList(searchProcessItemCategoryVO);

        model.addAttribute("ptype", "write");
        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("cate1List" , cate1List);
        model.addAttribute("searchProcessCategory" , searchProcessItemCategoryVO);
        return "process/modal/addProcessItem";
    }

    /**
     * 공정 등록 폼
     * @param model
     * @return
     */
    @GetMapping("/addProcessItemDetail")
    public String addProcessItemDetail(@ModelAttribute SearchProcessItemCategoryVO searchProcessItemCategoryVO, Model model) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);
        ProcessItem processItem = new ProcessItem();
        String ptype = "write";

        if (searchProcessItemCategoryVO.getProcessItemId() > 0) {
            processItem = processItemRepository.findById(searchProcessItemCategoryVO.getProcessItemId()).orElseGet(ProcessItem::new);
            ptype = "edit";
            searchProcessItemCategoryVO.setCate1(processItem.getCate1());
            searchProcessItemCategoryVO.setCate2(processItem.getCate2());
            searchProcessItemCategoryVO.setCate3(processItem.getCate3());
            searchProcessItemCategoryVO.setCate4(processItem.getCate4());
            searchProcessItemCategoryVO.setCate5(processItem.getCate5());
            searchProcessItemCategoryVO.setCate6(processItem.getCate6());
        }
        model.addAttribute("ptype", ptype);
        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("searchProcessCategory" , searchProcessItemCategoryVO);
        model.addAttribute("processItem" , processItem);
        return "process/modal/addProcessItemDetail";
    }

    @GetMapping("/addProcessItemExcelUpload")
    public String addProcessItemExcelUpload(Model model) {

        model.addAttribute("processFileExtension", String.join("||", configService.findFileExtension("EXCEL_FILE_EXT", true, userInfo.getProjectId())));
        return "process/modal/processExcelUpload";
    }
}
