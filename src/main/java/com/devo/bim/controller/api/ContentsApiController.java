package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.vo.DocumentCategoryVO;
import com.devo.bim.model.vo.DocumentVO;
import com.devo.bim.service.AccountService;
import com.devo.bim.service.DocumentService;
import com.devo.bim.service.ModelingService;
import com.devo.bim.service.WorkService;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/contentsApi")
@RequiredArgsConstructor
public class ContentsApiController extends AbstractController {
    private final ModelingService modelingService;
    private final AccountService accountService;
    private final DocumentService documentService;
    private final WorkService workService;

    @PostMapping("/postModeling")
    public JsonObject postModeling(@RequestParam(required = true, defaultValue = "0") long workId
            , @RequestParam(required = true, defaultValue = "") String description){

        return modelingService.postModelingInfo(workId, description);
    }

    @PostMapping("/putModelingInfoDelete")
    public JsonObject putModelingDelete(@RequestParam(required = true, defaultValue = "0") long id){
        return modelingService.putModelingInfoDelete(id);
    }

    @GetMapping("/getModeling/{id}")
    public JsonObject getModeling(@PathVariable Long id) {
        return proc.getResult(modelingService.findById(id));
    }

    @DeleteMapping("/mySnapShotFile/{mySnapShotFileId}")
    public JsonObject deleteMySnapShotFile(@PathVariable long mySnapShotFileId, Model model) {
    	return accountService.deleteMySnapShotFile(mySnapShotFileId);
    }

    @PostMapping("/postDocument")
    public JsonObject postDocument(@RequestBody DocumentVO documentVO) {
        return documentService.postDocument(documentVO);
    }

    @DeleteMapping("/deleteDocument")
    public JsonObject deleteDocument(long id) {
        return documentService.deleteDocument(id);
    }

    @DeleteMapping("/deleteSelDocument")
    public JsonObject deleteSelDocument(@RequestBody List<DocumentVO> documentVO) {
        return documentService.deleteSelDocument(documentVO);
    }

    @PutMapping("/putDocument")
    public JsonObject putDocument(@RequestBody DocumentVO documentVO) {
        return documentService.putDocument(documentVO);
    }

    @GetMapping("/getWork/{upId}")
    public JsonObject getWork(@PathVariable int upId) {
        return workService.getWorkAllSecond(upId);
    }

    @PostMapping("/postDocumentCategory")
    public JsonObject postDocumentCategory(@RequestBody DocumentCategoryVO documentCategoryVO) {
        return documentService.postDocumentCategory(documentCategoryVO);
    }

    @DeleteMapping("/deleteDocumentCategory")
    public JsonObject deleteDocumentCategory(long id) {
        return documentService.deleteDocumentCategory(id);
    }

    @PutMapping("/putDocumentCategory")
    public JsonObject putDocumentCategory(@RequestBody DocumentCategoryVO documentCategoryVO) {
        return documentService.putDocumentCategory(documentCategoryVO);
    }

    @DeleteMapping("/deleteDocumentFile")
    public JsonObject deleteDocumentFile(long id) {
        return documentService.deleteDocumentFile(id);
    }

    @PostMapping("/putDocumentCategorySortNoASC")
    public JsonObject putDocumentCategorySortNoASC(@RequestParam(defaultValue = "0") long id) {
        return documentService.putDocumentCategorySortNoASC(id);
    }

    @PostMapping("/putDocumentCategorySortNoDESC")
    public JsonObject putDocumentCategorySortNoDESC(@RequestParam(defaultValue = "0") long id) {
        return documentService.putDocumentCategorySortNoDESC(id);
    }
}
