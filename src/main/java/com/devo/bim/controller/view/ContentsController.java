package com.devo.bim.controller.view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.SearchDocumentCategoryVO;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.devo.bim.component.Utils;
import com.devo.bim.exception.NotFoundException;
import com.devo.bim.model.vo.SearchDocumentVO;
import com.devo.bim.model.vo.SearchModelingVO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.service.AccountService;
import com.devo.bim.service.DocumentService;
import com.devo.bim.service.JobSheetSnapShotService;
import com.devo.bim.service.ModelingService;
import com.devo.bim.service.ProcessCostService;
import com.devo.bim.service.WorkService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/contents")
@RequiredArgsConstructor
public class ContentsController extends AbstractController {

    private final ModelingService modelingService;
    private final WorkService workService;
    private final AccountService accountService;
    private final JobSheetSnapShotService jobSheetSnapShotService;
    private final DocumentService documentService;
    private final ProcessCostService processCostService;

    @GetMapping({"/modelingList", "/modelingListCardBody"})
    public String modelingList(@ModelAttribute SearchModelingVO searchModelingVO
            , HttpServletRequest request
            , Model model) {

        initSearchCondition(searchModelingVO);
        getModelingList(searchModelingVO, model);

        if ("modelingList".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "contents/modelingList/page";
        return "contents/modelingList/cardBody";
    }

    private void initSearchCondition(@ModelAttribute SearchModelingVO searchModelingVO) {
        searchModelingVO.setProjectId(userInfo.getProjectId());
        searchModelingVO.setWorkId(setUserDefaultWorkId(searchModelingVO));
    }

    private long setUserDefaultWorkId(SearchModelingVO searchModelingVO) {
        if (searchModelingVO.getWorkId() < 0) {
            return accountService.findLoginAccount().getWorks()
                    .stream()
                    .sorted(Comparator.comparingInt(Work::getSortNo))
                    .findFirst()
                    .orElseGet(Work::new)
                    .getId();
        }
        return searchModelingVO.getWorkId();
    }

    private void getModelingList(SearchModelingVO searchModelingVO, Model model) {
        List<ModelingDTO> modelingDTOs = modelingService.findModelingDTOsBySearchCondition(searchModelingVO);

        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        model.addAttribute("searchModelingVO", searchModelingVO);
        model.addAttribute("modelingDTOs", modelingDTOs);
        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
    }

    @GetMapping("/documentList")
    public String documentList(@ModelAttribute SearchDocumentVO searchDocumentVO
            , @PageableDefault(size = defaultPageSize_documentList) Pageable pageable
            , Model model) {
        // work
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        // documentCategory
        SearchDocumentCategoryVO searchDocumentCategoryVO = new SearchDocumentCategoryVO();
        searchDocumentCategoryVO.setProjectId(userInfo.getProjectId());
        List<DocumentCategoryDTO> documentCategoryDTOs = documentService.findDocumentCategoryDTOs(searchDocumentCategoryVO);

        // document
        searchDocumentVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchDocumentVO.setProjectId(userInfo.getProjectId());
        PageDTO<DocumentDTO> pageDocumentDTOs = documentService.findDocumentDTOs(searchDocumentVO, pageable);

        setPagingConfig(model, pageDocumentDTOs);

        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("documentCategoryDTOs", documentCategoryDTOs);
        model.addAttribute("searchDocumentVO", searchDocumentVO);
        model.addAttribute("list", pageDocumentDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "contents/documentList/page";
    }

    @GetMapping("/documentListCardBody")
    public String documentListCardBody(@ModelAttribute SearchDocumentVO searchDocumentVO
            , @PageableDefault(size = defaultPageSize_documentList) Pageable pageable
            , Model model) {

        // documentCategory
        SearchDocumentCategoryVO searchDocumentCategoryVO = new SearchDocumentCategoryVO();
        searchDocumentCategoryVO.setProjectId(userInfo.getProjectId());
        List<DocumentCategoryDTO> documentCategoryDTOs = documentService.findDocumentCategoryDTOs(searchDocumentCategoryVO);

        // document
        searchDocumentVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchDocumentVO.setProjectId(userInfo.getProjectId());
        PageDTO<DocumentDTO> pageDocumentDTOs = documentService.findDocumentDTOs(searchDocumentVO, pageable);

        setPagingConfig(model, pageDocumentDTOs);

        model.addAttribute("documentCategoryDTOs", documentCategoryDTOs);
        model.addAttribute("searchDocumentVO", searchDocumentVO);
        model.addAttribute("list", pageDocumentDTOs.getPageList());
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());

        return "contents/documentList/cardBody";
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    @GetMapping("/modelingViewByJobSheetViewPoint/{jobSheetViewPointId}")
    public String modelingViewByJobSheetViewPointId(@PathVariable long jobSheetViewPointId, Model model) throws Exception {
    	JobSheetSnapShot jobSheetSnapShot = jobSheetSnapShotService.findById(jobSheetViewPointId);
        if (jobSheetSnapShot.getId() == 0) new NotFoundException(proc.translate("system.contents_controller.not_found_view_point"));

        String htmlPath = getModelingViewPage(jobSheetSnapShot.getViewModelId(), "bimModelArea", jobSheetSnapShot.getViewPointJson(), model);

    	model.addAttribute("mySnapShot", jobSheetSnapShot);
    	model.addAttribute("mySnapShotFiles", null);
        return htmlPath;
    }

    // region contents_modeling_viewer
    @NotNull
    private String getModelingViewPage(String modelIds, String tabType, String selectedViewPoint, Model model) throws Exception {
        if (modelIds.isBlank()) new NotFoundException(proc.translate("system.contents_controller.not_found_model"));
        List<Modeling> modelings = modelingService.findByModelIds(modelIds);
        ModelingViewDTO modeling = modelings.stream().findFirst().map(m -> new ModelingViewDTO(m)).orElseGet(ModelingViewDTO::new);

        Account account = accountService.findLoginAccount();

        model.addAttribute("modelings", modelings);
        model.addAttribute("modeling", modeling);
        //mySnapShot 개발
        List<MySnapShot> mySnapShots = account.getMySnapShotsContainWithRenderedModelByIdAsc(modelIds);
        model.addAttribute("mySnapShots", mySnapShots);
        model.addAttribute("mySnapShot", mySnapShots.stream().findFirst().orElseGet(MySnapShot::new));
        model.addAttribute("mySnapShotFiles", mySnapShots.stream().findFirst().map(m -> m.getMySnapShotFiles()).orElseGet(() -> new ArrayList<>()));

        model.addAttribute("tabType", tabType);
        model.addAttribute("selectedMySnapShot", selectedViewPoint);
        model.addAttribute("modelIds", modelIds);
        model.addAttribute("viewerType",configService.findConfig("SYSTEM","BIM_RENDERING_MODEL").getCustomValue());

        return "contents/modelingView/page";
    }

    @GetMapping("/modelingViewBy/{mySnopShotId}")
    public String modelingViewBy(@PathVariable long mySnopShotId, Model model) {
        Account account = accountService.findLoginAccount();
        List<MySnapShot> mySnapShots = account.getMySnapShots();
        MySnapShot mySnapShot = mySnapShots
                .stream()
                .filter(m -> m.getId() == mySnopShotId)
                .findFirst()
                .orElseGet(MySnapShot::new);

        if (mySnapShot.getId() == 0) new NotFoundException(proc.translate("system.contents_controller.not_found_snap_shot"));

        List<Modeling> modelings = modelingService.findByModelIds(mySnapShot.getViewModelId());
        ModelingViewDTO modeling = modelings.stream().findFirst().map(m -> new ModelingViewDTO(m)).orElseGet(ModelingViewDTO::new);

        List<MySnapShot> snapShots = account.getMySnapShotsContainWithRenderedModelByIdAsc(mySnapShot.getViewModelId());

        model.addAttribute("modelings", modelings);
        model.addAttribute("modeling", modeling);
        model.addAttribute("mySnapShots", snapShots);
        model.addAttribute("mySnapShot", mySnapShot);
        model.addAttribute("mySnapShotFiles", mySnapShot.getMySnapShotFiles());
        model.addAttribute("selectedMySnapShot", mySnapShot.getViewPointJson());

        model.addAttribute("tabType", "mySnapShotArea");

        model.addAttribute("modelIds", mySnapShot.getViewModelId());

        return "contents/modelingView/page";
    }

    @GetMapping("/modelingViewByProcessItemId/{processItemId}")
    public String modelingViewByProcessItemId(@PathVariable long processItemId, Model model) {
    	Account account = accountService.findLoginAccount();
        ModelingViewerDTO modelingViewerDTO = modelingService.findModelingDTOs();
        String modelIds = modelingViewerDTO.getModelIds();
        List<Modeling> modelings = modelingService.findByModelIds(modelIds);
        ModelingViewDTO modeling = modelings.stream().findFirst().map(m -> new ModelingViewDTO(m)).orElseGet(ModelingViewDTO::new);

    	ProcessItem processItem =  processCostService.findProcessItemById(processItemId);
    	model.addAttribute("ExchangeIds", processItem.getExchangeIds());

    	model.addAttribute("modelings", modelings);
        model.addAttribute("modeling", modeling);
        model.addAttribute("modelIds", modelIds);

        List<MySnapShot> mySnapShots = account.getMySnapShotsContainWithRenderedModelByIdAsc(modelIds);
        model.addAttribute("mySnapShots", mySnapShots);
        model.addAttribute("mySnapShot", mySnapShots.stream().findFirst().orElseGet(MySnapShot::new));
        model.addAttribute("mySnapShotFiles", mySnapShots.stream().findFirst().map(m -> m.getMySnapShotFiles()).orElseGet(() -> new ArrayList<>()));
    	model.addAttribute("latestModel", "Y");
    	model.addAttribute("ExchangeId", processItem.getExchangeIds());

    	return "contents/modelingView/page";
    }

    @GetMapping("/modelingView")
    public String modelingView(@RequestParam(required = true, defaultValue = "") String modelIds
            , @RequestParam(required = false, defaultValue = "") String tabType
            , @RequestParam(required = false, defaultValue = "") String selectedViewPoint
            , Model model) throws Exception {
        return getModelingViewPage(modelIds, tabType, selectedViewPoint, model);
    }

    @GetMapping("/bimModelArea/{modelingId}")
    public String bimModelArea(@RequestParam(required = true, defaultValue = "") String modelIds
            , @PathVariable long modelingId
            , Model model) {
        List<Modeling> modelings = modelingService.findByModelIds(modelIds);
        ModelingViewDTO modeling = modelings
                .stream()
                .filter(m -> m.getId() == modelingId)
                .findFirst()
                .map(n -> new ModelingViewDTO(n))
                .orElseGet(ModelingViewDTO::new);

        model.addAttribute("modelings", modelings);
        model.addAttribute("modeling", modeling);

        return "contents/modelingView/bimModelArea";
    }

    @GetMapping("/bimModelDetail/{modelingId}")
    public String bimModelDetail(@PathVariable long modelingId
            , Model model) {
        model.addAttribute("modeling", modelingService.findById(modelingId));
        return "contents/modelingView/bimModelDetail";
    }

    @GetMapping("/mySnapShotArea/{mySnapShotId}")
    public String mySnapShotArea(@PathVariable long mySnapShotId, String modelIds, Model model) {

    	Account account = accountService.findLoginMySnapShot();
        List<MySnapShot> mySnapShots = account.getMySnapShotsContainWithRenderedModelByIdAsc(modelIds);

        MySnapShot mySnapShot = mySnapShots
                .stream()
                .filter(m -> m.getId() == mySnapShotId)
                .findFirst()
                .orElseGet(MySnapShot::new);
        List<Modeling> modelings = modelingService.findByModelIds(modelIds);
        model.addAttribute("modelings", modelings);
        model.addAttribute("mySnapShots", mySnapShots);
        model.addAttribute("mySnapShot", mySnapShot);
        model.addAttribute("mySnapShotFiles", mySnapShot.getMySnapShotFiles());

        return "contents/modelingView/mySnapShotArea";
    }

    @GetMapping("/mySnapShotFileList/{mySnapShotId}")
    public String mySnapShotFileList(@PathVariable long mySnapShotId
            , Model model) {

        MySnapShot mySnapShot = accountService.findSnapShotId(mySnapShotId);

        model.addAttribute("mySnapShot", mySnapShot);
        model.addAttribute("mySnapShotFiles", mySnapShot.getMySnapShotFiles());

        return "contents/modelingView/mySnapShotFileList";
    }

    @NotNull
    private String getMySnapShotAreaPage(Model model) {
        model.addAttribute("mySnapShots", accountService.findMySnapShots());
        return "contents/modelingView/mySnapShotArea";
    }

    @DeleteMapping("/mySnapShot/{mySnapShotsId}")
    public String deleteMySnapShot(@PathVariable long mySnapShotsId, Model model) {
        accountService.deleteMySnapShot(mySnapShotsId);
        return getMySnapShotAreaPage(model);
    }

    @GetMapping({"/documentCategory", "/documentCategoryCardBody"})
    public String documentCategoryList(SearchDocumentCategoryVO searchDocumentCategoryVO
            , HttpServletRequest request
            , Model model) {

        searchDocumentCategoryVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
        searchDocumentCategoryVO.setProjectId(userInfo.getProjectId());
        List<DocumentCategoryDTO> pageDocumentCategoryDTOs = documentService.findDocumentCategorys(searchDocumentCategoryVO);

        model.addAttribute("searchDocumentCategoryVO", searchDocumentCategoryVO);
        model.addAttribute("list", pageDocumentCategoryDTOs);
        model.addAttribute("userInfoId", userInfo.getId());
        model.addAttribute("isRoleAdminProject", userInfo.isRoleAdminProject());
        model.addAttribute("isRoleUserNormal", userInfo.isRoleUserNormal());
        model.addAttribute("minSortNo", getDocumentCategoryListMinSortNo(pageDocumentCategoryDTOs));
        model.addAttribute("maxSortNo", getDocumentCategoryListMaxSortNo(pageDocumentCategoryDTOs));

        if ("documentCategory".equalsIgnoreCase(Utils.getRequestMappingValue(request.getRequestURI()))) return "contents/documentCategory/page";
        return "contents/documentCategory/cardBody";
    }

    private int getDocumentCategoryListMinSortNo(List<DocumentCategoryDTO> documentCategoryDTOs) {
        return documentCategoryDTOs.stream()
                .min(Comparator.comparing(DocumentCategoryDTO::getSortNo))
                .orElseGet(() -> new DocumentCategoryDTO(1))
                .getSortNo();
    }

    private int getDocumentCategoryListMaxSortNo(List<DocumentCategoryDTO> documentCategoryDTOs) {
        return documentCategoryDTOs.stream()
                .max(Comparator.comparing(DocumentCategoryDTO::getSortNo))
                .orElseGet(() -> new DocumentCategoryDTO(1))
                .getSortNo();
    }

    // endregion
}