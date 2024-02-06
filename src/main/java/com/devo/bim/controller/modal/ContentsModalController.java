package com.devo.bim.controller.modal;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.DocumentCategoryDTO;
import com.devo.bim.model.dto.WorkDTO;
import com.devo.bim.model.entity.Document;
import com.devo.bim.model.entity.DocumentCategory;
import com.devo.bim.model.entity.Work;
import com.devo.bim.model.enumulator.WorkStatus;
import com.devo.bim.model.vo.SearchDocumentCategoryVO;
import com.devo.bim.model.vo.SearchWorkVO;
import com.devo.bim.service.ConfigService;
import com.devo.bim.service.DocumentService;
import com.devo.bim.service.WorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/contentsModal")
@RequiredArgsConstructor
public class ContentsModalController extends AbstractController {
    private final WorkService workService;
    private final DocumentService documentService;
    private final ConfigService configService;

    @GetMapping("/addModeling")
    public String addModeling(Model model) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("modelingFileExtension", String.join("||", configService.findFileExtension("MODELING_FILE_EXT", true, userInfo.getProjectId())));
        model.addAttribute("ifcFileExtension", String.join("||", configService.findFileExtension("IFC_FILE_EXT", true, userInfo.getProjectId())));
        return "contents/modal/addModeling";
    }

    @GetMapping("/addModelFile/{mySnapShotId}")
    public String addModelFile(@PathVariable Long mySnapShotId, Model model) {

        model.addAttribute("mySnapShotId", mySnapShotId);
        model.addAttribute("mySnapShotFileExtension", String.join("||", configService.findFileExtension("VIEW_POINT_FILE_EXT", true, userInfo.getProjectId())));
        return "contents/modal/addModelFile";
    }

    @GetMapping("/modelFileViewer")
    public String modelFileViewer(Model model) {

        model.addAttribute("locale", LocaleContextHolder.getLocale());
        return "contentsModal/modelFileViewer";
    }

    @GetMapping("/addDocument")
    public String addDocument(Model model) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        // documentCategory
        SearchDocumentCategoryVO searchDocumentCategoryVO = new SearchDocumentCategoryVO();
        searchDocumentCategoryVO.setProjectId(userInfo.getProjectId());
        List<DocumentCategoryDTO> documentCategoryDTOs = documentService.findDocumentCategoryDTOs(searchDocumentCategoryVO);

        model.addAttribute("workDTOs" , workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("documentCategoryDTOs" , documentCategoryDTOs);
        model.addAttribute("documentFileExtension", String.join("||", configService.findFileExtension("DOCUMENT_FILE_EXT", true, userInfo.getProjectId())));
        return "contents/modal/addDocument";
    }

    @GetMapping("/addDocumentCategory")
    public String addDocumentCategory(Model model) {

        DocumentCategory documentCategory = new DocumentCategory();
        model.addAttribute("documentCategory", documentCategory);
        model.addAttribute("ptype", "write");
        return "contents/modal/addDocumentCategory";
    }

    @GetMapping("/updateDocumentCategory")
    public String updateDocumentCategory(@RequestParam(required = false, defaultValue = "0") long mDocumentCategoryId, Model model) {

        DocumentCategory documentCategory = documentService.findDocumentCategoryById(mDocumentCategoryId);

        model.addAttribute("ptype", "edit");
        model.addAttribute("documentCategory", documentCategory);

        return "contents/modal/addDocumentCategory";
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    @GetMapping("/updateDocument")
    public String updateDocument(long id, Model model) {
        SearchWorkVO searchWorkVO = new SearchWorkVO();
        setWorkDTOSearchCondition(searchWorkVO);
        List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
        String jsonString = Utils.getJsonListString(workDTOs);

        // documentCategory
        SearchDocumentCategoryVO searchDocumentCategoryVO = new SearchDocumentCategoryVO();
        searchDocumentCategoryVO.setProjectId(userInfo.getProjectId());
        List<DocumentCategoryDTO> documentCategoryDTOs = documentService.findDocumentCategoryDTOs(searchDocumentCategoryVO);

        Document document = documentService.findByIdAndProjectId(id, userInfo.getProjectId());

        model.addAttribute("workDTOs", workDTOs);
        model.addAttribute("workJson" , jsonString);
        model.addAttribute("documentCategoryDTOs", documentCategoryDTOs);
        model.addAttribute("document", document);
        model.addAttribute("documentFileExtension", String.join("||", configService.findFileExtension("DOCUMENT_FILE_EXT", true, userInfo.getProjectId())));
        return "contents/modal/updateDocument";
    }
}
