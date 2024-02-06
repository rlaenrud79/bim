package com.devo.bim.service;

import com.devo.bim.component.UserInfo;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.DocumentCategoryDTODslRepository;
import com.devo.bim.repository.dsl.DocumentDTODslRepository;
import com.devo.bim.repository.spring.DocumentCategoryRepository;
import com.devo.bim.repository.spring.DocumentFileRepository;
import com.devo.bim.repository.spring.DocumentRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService extends AbstractService {
    private final DocumentRepository documentRepository;
    private final DocumentDTODslRepository documentDTODslRepository;
    private final DocumentCategoryDTODslRepository documentCategoryDTODslRepository;
    private final DocumentFileRepository documentFileRepository;
    private final FileDeleteService fileDeleteService;
    private final DocumentCategoryRepository documentCategoryRepository;

    public PageDTO<DocumentDTO> findDocumentDTOs(SearchDocumentVO searchDocumentVO, Pageable pageable) {
        return documentDTODslRepository.findDocumentDTOs(searchDocumentVO, pageable);
    }

    @Transactional
    public JsonObject postDocument(DocumentVO documentVO) {
        try {
            Document document = new Document();
            document.setDocumentAtAddDocument(documentVO, userInfo);
            documentRepository.save(document);

            return proc.getResult(true, document.getId(), "system.document_service.post_document");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteDocument(long id) {
        Document savedDocument = documentRepository.findById(id).orElseGet(Document::new);
        if (savedDocument.getId() == 0) {
            return proc.getResult(false, "system.document_service.not_exist_document");
        }

        DocumentFile savedDocumentFile = savedDocument.getDocumentFiles().stream().findFirst().orElseGet(DocumentFile::new);
        //if (savedDocumentFile.getId() == 0) {
        //    return proc.getResult(false, "system.document_service.not_exist_document_file");
        //}

        try {
            documentFileRepository.delete(savedDocumentFile);
            documentRepository.delete(savedDocument);
            fileDeleteService.deletePhysicalFile(savedDocumentFile.getFilePath());

            return proc.getResult(true, "system.document_service.delete_document");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteSelDocument(List<DocumentVO> documentVO) {
        try {
            for (int i = 0; i < documentVO.size(); i++) {
                Document deleteDocument = documentRepository.findById(documentVO.get(i).getId()).orElseGet(Document::new);
                if (deleteDocument.getId() == 0) {
                    return proc.getResult(false, "system.document_service.not_exist_document");
                }

                DocumentFile savedDocumentFile = deleteDocument.getDocumentFiles().stream().findFirst().orElseGet(DocumentFile::new);

                documentFileRepository.delete(savedDocumentFile);
                documentRepository.delete(deleteDocument);
                fileDeleteService.deletePhysicalFile(savedDocumentFile.getFilePath());
            }

            return proc.getResult(true, "system.document_service.delete_document");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public Document findByIdAndProjectId(long id, long projectId) {
        return documentRepository.findByIdAndProjectId(id, projectId).orElseGet(Document::new);
    }

    @Transactional
    public JsonObject putDocument(DocumentVO documentVO) {
        Document savedDocument = documentRepository.findById(documentVO.getId()).orElseGet(Document::new);
        if (savedDocument.getId() == 0) {
            return proc.getResult(false, "system.document_service.not_exist_document");
        }

        try {
            savedDocument.setDocumentAtUpdateDocument(documentVO, userInfo);
            return proc.getResult(true, "system.document_service.put_document");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<DocumentCategoryDTO> findDocumentCategorys(SearchDocumentCategoryVO searchDocumentCategoryVO) {
        return documentCategoryDTODslRepository.findDocumentCategoryDTOs(searchDocumentCategoryVO);
    }

    public List<DocumentCategoryDTO> findDocumentCategoryDTOs(SearchDocumentCategoryVO searchDocumentCategoryVO) {
        return documentCategoryDTODslRepository.findDocumentCategorySelectDTOs(searchDocumentCategoryVO);
    }

    public DocumentCategory findDocumentCategoryById(long id) {
        return documentCategoryRepository.findById(id).orElseGet(DocumentCategory::new);
    }

    @Transactional
    public JsonObject postDocumentCategory(DocumentCategoryVO documentCategoryVO) {
        try {
            int sortNo = documentCategoryVO.getSortNo();
            // 신규 DocumentCategory의 sortNo 값이 기존 목록의 sortNo 사이로 추가되는 경우 기존 목록의 sortNo 하나씩 증가
            documentCategoryRepository.addOneGOESortNo(sortNo, userInfo.getProjectId());

            DocumentCategory documentCategory = new DocumentCategory();
            documentCategory.setDocumentCategoryAtAddDocumentCategory(documentCategoryVO, sortNo, userInfo);
            documentCategoryRepository.save(documentCategory);

            return proc.getResult(true, documentCategory.getId(), "system.document_category_service.post_document_category");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteDocumentCategory(long id) {
        DocumentCategory savedDocumentCategory = documentCategoryRepository.findById(id).orElseGet(DocumentCategory::new);
        if (savedDocumentCategory.getId() == 0) {
            return proc.getResult(false, "system.document_category_service.not_exist_document_category");
        }

        try {
            documentCategoryRepository.delete(savedDocumentCategory);

            return proc.getResult(true, "system.document_category_service.delete_document_category");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteDocumentFile(long id) {
        DocumentFile savedDocumentFile = documentFileRepository.findById(id).orElseGet(DocumentFile::new);

        if(savedDocumentFile.getId() == 0) return proc.getResult(false, "system.document_service.not_exist_job_sheet_file");

        try{
            fileDeleteService.deletePhysicalFile(savedDocumentFile.getFilePath());
            documentFileRepository.delete(savedDocumentFile);

            return proc.getResult(true, "system.document_service.delete_document_file");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putDocumentCategory(DocumentCategoryVO documentCategoryVO) {
        DocumentCategory savedDocumentCategory = documentCategoryRepository.findById(documentCategoryVO.getId()).orElseGet(DocumentCategory::new);
        if (savedDocumentCategory.getId() == 0) {
            return proc.getResult(false, "system.document_category_service.post_document_category");
        }

        try {
            savedDocumentCategory.setDocumentCategoryAtUpdateDocumentCategory(documentCategoryVO, userInfo);
            return proc.getResult(true, "system.document_category_service.put_document_category");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putDocumentCategorySortNoDESC(long id) {
        long projectId = userInfo.getProjectId();

        // DocumentCategory 조회
        DocumentCategory savedDocumentCategory = documentCategoryRepository.findByIdAndProjectId(id, projectId).orElseGet(DocumentCategory::new);
        if (savedDocumentCategory.getId() == 0) return proc.getResult(false, "system.document_category_service.not_exist_document_category");

        // 조회한 DocumentCategory 의 다음 SortNo 에 해당하는 Work 조회
        DocumentCategory savedDocumentCategoryNext = documentCategoryDTODslRepository.findNextDocumentCategoryBySortNo(projectId, savedDocumentCategory.getSortNo());

        try {
            // swap sortNo
            int tmpSortNo = savedDocumentCategory.getSortNo();
            savedDocumentCategory.setSortNoAtUpdateSortNo(savedDocumentCategoryNext.getSortNo());
            savedDocumentCategoryNext.setSortNoAtUpdateSortNo(tmpSortNo);
            return proc.getResult(true, "system.document_category_service.put_document_category_sort_no_asc");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putDocumentCategorySortNoASC(long workId) {
        long projectId = userInfo.getProjectId();

        // DocumentCategory 조회
        DocumentCategory savedDocumentCategory = documentCategoryRepository.findByIdAndProjectId(workId, projectId).orElseGet(DocumentCategory::new);
        if (savedDocumentCategory.getId() == 0) return proc.getResult(false, "system.document_category_service.not_exist_document_category");

        // 조회한 DocumentCategory 의 이전 SortNo 에 해당하는 DocumentCategory 조회
        DocumentCategory savedDocumentCategoryPrevious = documentCategoryDTODslRepository.findNextDocumentCategoryBySortNo(projectId, savedDocumentCategory.getSortNo());

        try {
            // swap sortNo
            int tmpSortNo = savedDocumentCategory.getSortNo();
            savedDocumentCategory.setSortNoAtUpdateSortNo(savedDocumentCategoryPrevious.getSortNo());
            savedDocumentCategoryPrevious.setSortNoAtUpdateSortNo(tmpSortNo);
            return proc.getResult(true, "system.document_category_service.put_document_category_sort_no_asc");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
