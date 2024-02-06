package com.devo.bim.service;

import java.io.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.devo.bim.model.entity.*;
import com.devo.bim.repository.spring.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.FileUploadDTO;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.repository.dsl.ModelingDslRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService extends AbstractService {

    private final FileDeleteService fileDeleteService;
    private final ModelingRepository modelingRepository;
    private final ModelingDslRepository modelingDslRepository;
    private final MySnapShotFileRepository mySnapShotFileRepository;
    private final JobSheetFileRepository jobSheetFileRepository;
    private final CoWorkSnapShotFileRepository coWorkSnapShotFileRepository;
    private final IssueReportFileRepository issueReportFileRepository;
    private final CoWorkIssueReportFileRepository coWorkIssueReportFileRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final ProjectImageRepository projectImageRepository;
    private final AccountRepository accountRepository;
    private final BulletinFileRepository bulletinFileRepository;
    private final DocumentFileRepository documentFileRepository;
    private final ChatAPIService chatAPIService;
    private final GisungReportRepository gisungReportRepository;
    private final GisungContractManagerRepository gisungContractManagerRepository;
    private final GisungPaymentFileRepository gisungPaymentFileRepository;

    private final GisungListExcelFileRepository gisungListExcelFileRepository;
    private final ProcessItemCostDetailExcelFileRepository processItemCostDetailExcelFileRepository;
    private final ProcessItemExcelFileRepository processItemExcelFileRepository;
    private final ProcessInfoRepository processInfoRepository;

    @Transactional
    public JsonObject uploadFile(FileUploadDTO fileUploadDTO) {

        if (StringUtils.isEmpty(fileUploadDTO.getFile().getOriginalFilename()))
            return proc.getResult(false, "system.file_upload_service.error_no_exist_file_name");

        fileUploadDTO.setServerBasePath(Utils.getSaveBasePath(winPathUpload, linuxPathUpload, macPathUpload));

        // 버전 적용여부 체크 및 버전 체크인 경우 버전 정보 생성
        setVersionString(fileUploadDTO);

        try {
            // 파일 저장 폴더가 없는경우 생성
            makeUploadFolder(fileUploadDTO.getServerFolderPath());

            // 파일 업로드 작업 수행
            saveFile(fileUploadDTO);
            // 파일 업로드 zip 파일 해제 작업 수행
            //unZip(fileUploadDTO);

            // 파일정보 DB 저장
            saveFileInfoToDB(fileUploadDTO);

            // HoopsConverter 실행
            executeHoopsConverter(fileUploadDTO);

            // 결과 리턴
            return proc.getResult(true, "system.file_upload_service.success_save_file");

        } catch (Exception e) {
            log.error("error", e); // 2) 로그를 작성
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void saveFileInfoToDB(FileUploadDTO fileUploadDTO) {

        FileUploadUIType uiType = fileUploadDTO.getFileUploadUIType();

        if (uiType == FileUploadUIType.MODELING_FILE) {
            saveModelingFile(fileUploadDTO);
            updateSameModelLatest(fileUploadDTO);
        }
        if (uiType == FileUploadUIType.MODELING_SNAP_SHOT_MODEL_FILE) saveSnapShotFile(fileUploadDTO);
        if (uiType == FileUploadUIType.JOB_SHEET_FILE) saveJobSheetFile(fileUploadDTO);
        if (uiType == FileUploadUIType.CO_WORK_SNAP_SHOT_MODEL_FILE) saveCoWorkSnapShotFile(fileUploadDTO);
        if (uiType == FileUploadUIType.MODELING_IFC_FILE) saveModelingIfcFile(fileUploadDTO);
        if (uiType == FileUploadUIType.ISSUE_REPORT) saveIssueReportFile(fileUploadDTO);
        if (uiType == FileUploadUIType.CO_WORK_ISSUE_REPORT) saveCoWorkIssueReportFile(fileUploadDTO);
        if (uiType == FileUploadUIType.NOTIFICATION_FILE) saveNoticeFile(fileUploadDTO);
        if (uiType == FileUploadUIType.PROJECT_IMAGE_FILE) saveProjectImageFile(fileUploadDTO);
        if (uiType == FileUploadUIType.ACCOUNT_PHOTO_FILE) saveAccountPhotoFile(fileUploadDTO);
        if (uiType == FileUploadUIType.BULLETIN_FILE) saveBulletinFile(fileUploadDTO);
        if (uiType == FileUploadUIType.DOCUMENT_FILE) saveDocumentFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_REPORT_SURVEY_FILE) saveGisungReportSurveyFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_REPORT_PART_SURVEY_FILE) saveGisungReportPartSurveyFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_REPORT_AGGREGATE_FILE) saveGisungReportAggregateFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_REPORT_PART_AGGREGATE_FILE) saveGisungReportPartAggregateFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_REPORT_ACCOUNT_FILE) saveGisungReportAccountFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_REPORT_ETC_FILE) saveGisungReportEtcFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_STAMP_FILE) saveGisungStampFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_PAYMENT_FILE) saveGisungPaymentFile(fileUploadDTO);
        if (uiType == FileUploadUIType.GISUNG_LIST_EXCEL_FILE) saveGisungListExcelFile(fileUploadDTO);
        if (uiType == FileUploadUIType.PROCESS_ITEM_COST_DETAIL_EXCEL_FILE) saveProcessItemCostDetailExcelFile(fileUploadDTO);
        if (uiType == FileUploadUIType.PROCESS_ITEM_EXCEL_FILE) saveProcessItemExcelFile(fileUploadDTO);
    }

    // region 파일 정보 각 DB 저장

    private void saveDocumentFile(FileUploadDTO fileUploadDTO) {
        List<DocumentFile> savedDocumentFiles = documentFileRepository.findByDocumentId(fileUploadDTO.getId());
        documentFileRepository.deleteAllInBatch(savedDocumentFiles);

        for (DocumentFile savedDocumentFile : savedDocumentFiles) {
            fileDeleteService.deletePhysicalFile(savedDocumentFile.getFilePath());
        }

        savedDocumentFiles.add(new DocumentFile(fileUploadDTO.getId()
                , fileUploadDTO.getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedDocumentFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()
        ));

        savedDocumentFiles.forEach(t -> {
            documentFileRepository.save(t);
        });
    }

    private void saveAccountPhotoFile(FileUploadDTO fileUploadDTO) {
        Account account = accountRepository
                .findById(fileUploadDTO.getId())
                .map(savedAccount -> {
                    fileDeleteService.deletePhysicalFile(savedAccount.getPhotoPath());
                    savedAccount.putPhotoPath(fileUploadDTO.getClientFileFullPath());
                    return accountRepository.save(savedAccount);
                }).orElseGet(Account::new);

        if (account.getId() != 0) {
            chatAPIService.setUserAvatar(account);
        }
    }

    private void saveNoticeFile(FileUploadDTO fileUploadDTO) {

        List<NoticeFile> savedNoticeFiles = noticeFileRepository.findByNoticeId(fileUploadDTO.getId());

        savedNoticeFiles.add(new NoticeFile(fileUploadDTO.getId()
                , fileUploadDTO.getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedNoticeFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()));

        savedNoticeFiles.forEach(t -> {
            noticeFileRepository.save(t);
        });
    }

    private void saveProjectImageFile(FileUploadDTO fileUploadDTO) {

        List<ProjectImage> savedProjectImages = projectImageRepository.findByProjectId(fileUploadDTO.getId());

        savedProjectImages.add(new ProjectImage(fileUploadDTO.getId()
                , fileUploadDTO.getClientFileFullPath()
                , savedProjectImages.size() + 1
                , userInfo.getId()));

        savedProjectImages.forEach(t -> {
            projectImageRepository.save(t);
        });
    }

    private void saveCoWorkIssueReportFile(FileUploadDTO fileUploadDTO) {
        List<CoWorkIssueReportFile> savedCoWorkIssueReportFiles = coWorkIssueReportFileRepository.findByCoWorkIssueReportId(fileUploadDTO.getId());

        savedCoWorkIssueReportFiles.add(new CoWorkIssueReportFile(fileUploadDTO.getId()
                , fileUploadDTO.getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedCoWorkIssueReportFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())));

        savedCoWorkIssueReportFiles.forEach(t -> {
            coWorkIssueReportFileRepository.save(t);
        });
    }

    private void saveIssueReportFile(FileUploadDTO fileUploadDTO) {
        List<IssueReportFile> savedIssueReportFiles = issueReportFileRepository.findByIssueReportId(fileUploadDTO.getId());

        savedIssueReportFiles.add(new IssueReportFile(fileUploadDTO.getId()
                , fileUploadDTO.getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedIssueReportFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())));

        savedIssueReportFiles.forEach(t -> {
            issueReportFileRepository.save(t);
        });
    }

    private void saveCoWorkSnapShotFile(FileUploadDTO fileUploadDTO) {
        List<CoWorkSnapShotFile> savedCoWorkSnapShotFiles = coWorkSnapShotFileRepository.findByCoWorkSnapShotId(fileUploadDTO.getId());

        savedCoWorkSnapShotFiles.add(new CoWorkSnapShotFile(fileUploadDTO.getId()
                , fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedCoWorkSnapShotFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()));


        savedCoWorkSnapShotFiles.forEach(t -> {
            coWorkSnapShotFileRepository.save(t);
        });
    }

    private void saveFile(FileUploadDTO fileUploadDTO) throws IOException {
        if (fileUploadDTO.getFileUploadUIType() == FileUploadUIType.MODELING_FILE
                || fileUploadDTO.getFileUploadUIType() == FileUploadUIType.MODELING_IFC_FILE){
            fileUploadDTO.setSaveFileName(modelingRepository.findById(fileUploadDTO.getId()).orElseGet(Modeling::new).getWork().getId());
        }
        else fileUploadDTO.setSaveFileName();

        fileUploadDTO.getFile().transferTo(new File(fileUploadDTO.getServerFileFullPath()));
    }

    // region Hoops
    private void executeHoopsConverter(FileUploadDTO fileUploadDTO) {
        if (fileUploadDTO.isExecuteHoopsConverter()
                && fileUploadDTO.getFileUploadUIType() == FileUploadUIType.MODELING_FILE) {
            setConvertStatusToRequest(fileUploadDTO.getId());
        }
    }

    private void setConvertStatusToRequest(long modelingId) {
        Modeling savedModeling = modelingRepository.findById(modelingId).orElseGet(Modeling::new);

        if (savedModeling.getId() == 0) return;
        savedModeling.setFileConvertStatusAtSaveModelingFile();
    }

    // endregion Hoops

    // region 파일 정보 각 DB 저장

    private void saveBulletinFile(FileUploadDTO fileUploadDTO){
        List<BulletinFile> savedBulletinFiles = bulletinFileRepository.findByBulletinId(fileUploadDTO.getId());

        savedBulletinFiles.add(new BulletinFile(fileUploadDTO.getId()
                , fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedBulletinFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()
        ));

        savedBulletinFiles.forEach(t -> {
            bulletinFileRepository.save(t);
        });
    }

    private void saveJobSheetFile(FileUploadDTO fileUploadDTO) {
        List<JobSheetFile> savedJobSheetFiles = jobSheetFileRepository.findByJobSheetId(fileUploadDTO.getId());

        savedJobSheetFiles.add(new JobSheetFile(fileUploadDTO.getId()
                , fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedJobSheetFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()));

        savedJobSheetFiles.forEach(t -> {
            jobSheetFileRepository.save(t);
        });
    }
    // endregion region 파일 정보 각 DB 저장

    // View Point 도면 File DB 저장
    private void saveSnapShotFile(FileUploadDTO fileUploadDTO) {

        List<MySnapShotFile> savedMySnapShotFiles = mySnapShotFileRepository.findByMySnapShotId(fileUploadDTO.getId());

        savedMySnapShotFiles.add(new MySnapShotFile(fileUploadDTO.getId()
                , fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedMySnapShotFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()));

        savedMySnapShotFiles.forEach(t -> {
            mySnapShotFileRepository.save(t);
        });
    }

    // modeling ifc file DB 저장
    private void saveModelingIfcFile(FileUploadDTO fileUploadDTO) {

        Modeling savedModeling = modelingRepository.findById(fileUploadDTO.getId()).orElseGet(Modeling::new);

        savedModeling.setModelingIfcFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        modelingRepository.save(savedModeling);
    }

    // modeling file DB 저장
    private void saveModelingFile(FileUploadDTO fileUploadDTO) {

        Modeling savedModeling = modelingRepository.findById(fileUploadDTO.getId()).orElseGet(Modeling::new);

        savedModeling.setModelingFileInfoAtFileUpload(fileUploadDTO.getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , fileUploadDTO.getVersionString());

        modelingRepository.save(savedModeling);
    }

    // modeling ifc file DB 저장
    private void saveGisungReportSurveyFile(FileUploadDTO fileUploadDTO) {

        GisungReport savedGisungReport = gisungReportRepository.findById(fileUploadDTO.getId()).orElseGet(GisungReport::new);

        savedGisungReport.setGisungSurveyFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        gisungReportRepository.save(savedGisungReport);
    }

    private void saveGisungReportPartSurveyFile(FileUploadDTO fileUploadDTO) {

        GisungReport savedGisungReport = gisungReportRepository.findById(fileUploadDTO.getId()).orElseGet(GisungReport::new);

        savedGisungReport.setGisungPartSurveyFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        gisungReportRepository.save(savedGisungReport);
    }

    private void saveGisungReportAggregateFile(FileUploadDTO fileUploadDTO) {

        GisungReport savedGisungReport = gisungReportRepository.findById(fileUploadDTO.getId()).orElseGet(GisungReport::new);

        savedGisungReport.setGisungAggregateFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        gisungReportRepository.save(savedGisungReport);
    }

    private void saveGisungReportPartAggregateFile(FileUploadDTO fileUploadDTO) {

        GisungReport savedGisungReport = gisungReportRepository.findById(fileUploadDTO.getId()).orElseGet(GisungReport::new);

        savedGisungReport.setGisungPartAggregateFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        gisungReportRepository.save(savedGisungReport);
    }

    private void saveGisungReportAccountFile(FileUploadDTO fileUploadDTO) {

        GisungReport savedGisungReport = gisungReportRepository.findById(fileUploadDTO.getId()).orElseGet(GisungReport::new);

        savedGisungReport.setGisungAccountFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        gisungReportRepository.save(savedGisungReport);
    }

    private void saveGisungReportEtcFile(FileUploadDTO fileUploadDTO) {

        GisungReport savedGisungReport = gisungReportRepository.findById(fileUploadDTO.getId()).orElseGet(GisungReport::new);

        savedGisungReport.setGisungEtcFileInfoAtFileUpload(fileUploadDTO.getFile().getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , new BigDecimal(fileUploadDTO.getFile().getSize()));

        gisungReportRepository.save(savedGisungReport);
    }

    private void saveGisungStampFile(FileUploadDTO fileUploadDTO) {
        System.out.println("fileUploadDTO.getId()--------------------------------" + fileUploadDTO.getId());
        GisungContractManager gisungContractManager = gisungContractManagerRepository
                .findById(fileUploadDTO.getId())
                .map(savedGisungContractManager -> {
                    fileDeleteService.deletePhysicalFile(savedGisungContractManager.getStampPath());
                    savedGisungContractManager.putStampPath(fileUploadDTO.getClientFileFullPath());
                    return gisungContractManagerRepository.save(savedGisungContractManager);
                }).orElseGet(GisungContractManager::new);
    }

    private void saveGisungPaymentFile(FileUploadDTO fileUploadDTO) {
        List<GisungPaymentFile> savedGisungPaymentFiles = gisungPaymentFileRepository.findByGisungPaymentId(fileUploadDTO.getId());
        gisungPaymentFileRepository.deleteAllInBatch(savedGisungPaymentFiles);

        for (GisungPaymentFile savedGisungPaymentFile : savedGisungPaymentFiles) {
            fileDeleteService.deletePhysicalFile(savedGisungPaymentFile.getFilePath());
        }

        savedGisungPaymentFiles.add(new GisungPaymentFile(fileUploadDTO.getId()
                , fileUploadDTO.getOriginalFilename()
                , fileUploadDTO.getClientFileFullPath()
                , savedGisungPaymentFiles.size() + 1
                , new BigDecimal(fileUploadDTO.getFile().getSize())
                , userInfo.getId()
        ));

        savedGisungPaymentFiles.forEach(t -> {
            gisungPaymentFileRepository.save(t);
        });
    }

    private void saveGisungListExcelFile(FileUploadDTO fileUploadDTO) {
        System.out.println("fileUploadDTO.getId()--------------------------------" + fileUploadDTO.getId());
        GisungListExcelFile gisungListExcelFile = gisungListExcelFileRepository
                .findById(fileUploadDTO.getId())
                .map(savedGisungListExcelFile -> {
                    fileDeleteService.deletePhysicalFile(savedGisungListExcelFile.getFilePath());
                    savedGisungListExcelFile.setGisungListExcelFile(fileUploadDTO.getOriginalFilename(), fileUploadDTO.getClientFileFullPath(), 1, new BigDecimal(fileUploadDTO.getFile().getSize()));
                    return gisungListExcelFileRepository.save(savedGisungListExcelFile);
                }).orElseGet(GisungListExcelFile::new);

    }

    private void saveProcessItemCostDetailExcelFile(FileUploadDTO fileUploadDTO) {
        System.out.println("fileUploadDTO.getId()--------------------------------" + fileUploadDTO.getId());
        ProcessItemCostDetailExcelFile processItemCostDetailExcelFile = processItemCostDetailExcelFileRepository
                .findById(fileUploadDTO.getId())
                .map(savedProcessItemCostDetailExcelFile -> {
                    fileDeleteService.deletePhysicalFile(savedProcessItemCostDetailExcelFile.getFilePath());
                    savedProcessItemCostDetailExcelFile.setProcessItemCostDetailExcelFile(fileUploadDTO.getOriginalFilename(), fileUploadDTO.getClientFileFullPath(), 1, new BigDecimal(fileUploadDTO.getFile().getSize()));
                    return processItemCostDetailExcelFileRepository.save(savedProcessItemCostDetailExcelFile);
                }).orElseGet(ProcessItemCostDetailExcelFile::new);

    }

    private void saveProcessItemExcelFile(FileUploadDTO fileUploadDTO) {
        System.out.println("fileUploadDTO.getId()--------------------------------" + fileUploadDTO.getId());
        ProcessItemExcelFile processItemExcelFile = processItemExcelFileRepository
                .findById(fileUploadDTO.getId())
                .map(savedProcessItemExcelFile -> {
                    ProcessInfo newProcessInfo = processInfoRepository.findById(savedProcessItemExcelFile.getProcessInfo().getId()).orElseGet(ProcessInfo::new);
                    if (newProcessInfo.getId() > 0) newProcessInfo.setFileName(fileUploadDTO.getOriginalFilename());

                    fileDeleteService.deletePhysicalFile(savedProcessItemExcelFile.getFilePath());
                    savedProcessItemExcelFile.setProcessItemExcelFile(fileUploadDTO.getOriginalFilename(), fileUploadDTO.getClientFileFullPath(), 1, new BigDecimal(fileUploadDTO.getFile().getSize()));
                    return processItemExcelFileRepository.save(savedProcessItemExcelFile);
                }).orElseGet(ProcessItemExcelFile::new);

    }

    private void updateSameModelLatest(FileUploadDTO fileUploadDTO) {

        Modeling savedModeling = modelingDslRepository.findByNotInIdAndModelNameAndLatest(userInfo.getProjectId()
                , fileUploadDTO.getId()
                , fileUploadDTO.getFile().getOriginalFilename());

        if (savedModeling == null) return;

        savedModeling.setNoLatestAtAddModeling();
        modelingRepository.save(savedModeling);
    }

    // region 업로드 경로
    private void makeUploadFolder(String savePath) {
        if (!new File(savePath).exists()) {
            new File(savePath).mkdirs();
        }
    }

    // endregion 업로드 경로

    // region 버전 정보 관련

    @NotNull
    private void setVersionString(FileUploadDTO fileUploadDTO) {
        String versionString = fileUploadDTO.isMakeVersion() ? getFileVersion(fileUploadDTO.getFileUploadUIType(), fileUploadDTO.getId(), fileUploadDTO.getFile().getOriginalFilename()) : "";
        fileUploadDTO.setVersionString(versionString);
    }

    // 파일 버전 구하기
    private String getFileVersion(FileUploadUIType fileUploadUIType, long modelingId, String fileName) {
        if (fileUploadUIType == FileUploadUIType.MODELING_FILE) return getModelingFileVersion(modelingId, fileName);
        if (fileUploadUIType == FileUploadUIType.MODELING_IFC_FILE) return getModelingIfcFileVersion(modelingId);
        return "";
    }

    // 버전이 필요한 정보마다 가져옴
    private String getModelingFileVersion(long modelingId, String fileName) {
        return makeVersionString(modelingDslRepository.findByFileNameAndFilePathNotExist(userInfo.getProjectId(), modelingId, fileName).size());
    }

    private String getModelingIfcFileVersion(long modelingId) {
        return modelingDslRepository.findByFileNameAndIfcFilePathNotExist(userInfo.getProjectId(), modelingId)
                .stream()
                .sorted(Comparator.comparing(Modeling::getVersion).reversed())
                .collect(Collectors.toList())
                .stream()
                .findFirst()
                .orElseGet(Modeling::new)
                .getVersion();
    }

    // 버전 만들기
    @NotNull
    private String makeVersionString(int versionCnt) {
        int newVersionCnt = versionCnt + 1;
        if (newVersionCnt >= 0 && newVersionCnt < 10) return "v000" + newVersionCnt;
        if (newVersionCnt >= 10 && newVersionCnt < 100) return "v00" + newVersionCnt;
        if (newVersionCnt >= 1000 && newVersionCnt < 10000) return "v0" + newVersionCnt;
        return "v" + newVersionCnt;
    }

    // endregion 버전정보 관리

    //MultipartFile zip 파일 압축 풀기
    private void unZip(FileUploadDTO fileUploadDTO) throws IOException {
        fileUploadDTO.setSaveFileName();
        fileUploadDTO.getFile().transferTo(new File(fileUploadDTO.getServerFileFullPath()));

        String zipFilePath = fileUploadDTO.getServerFileFullPath();
        String unzipFilePath = fileUploadDTO.getServerFileFullPath().replace(".zip", "");

        File zipFile = new File(zipFilePath);
        File unzipFile = new File(unzipFilePath);

        if (!unzipFile.exists()) {
            unzipFile.mkdirs();
        }

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = null;
            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                System.out.println("fileName" + fileName);
                File file = new File(unzipFilePath, fileName);

                if (zipEntry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.createNewFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = zis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                }
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void ExcelToPdfPrinter() {

        // 업로드된 Excel 파일 경로
        //String excelFilePath = "/var/lib/tomcat9/webapps/ROOT/WEB-INF/classes/static/file.xlsx";
        String excelFilePath = "C:\\data\\file_upload\\excel\\file.xlsx";
        // 생성할 PDF 파일 경로
        //String pdfFilePath = "/var/lib/tomcat9/webapps/ROOT/WEB-INF/classes/static/file.pdf";
        String pdfFilePath = "C:\\data\\file_upload\\excel\\file.pdf";
        /**
         Workbook workbook = new Workbook();
         // Open an excel file
         InputStream fileStream = getResourceStream(excelFilePath);
         workbook.open(fileStream);

         // Save to an pdf file
         workbook.save(pdfFilePath);
         **/
        /**
         // Excel 파일 열기
         try (InputStream inp = new FileInputStream(excelFilePath)) {
         Workbook wb = WorkbookFactory.create(inp);
         Sheet sheet = wb.getSheetAt(0);
         int lastRowNum = sheet.getLastRowNum();

         // PDF 생성
         Document document = new Document();
         PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
         document.open();
         PdfContentByte cb = writer.getDirectContent();

         // Excel 파일의 각 셀에서 데이터를 읽어와 PDF에 쓰기
         float x, y;
         for (int i = 0; i <= lastRowNum; i++) {
         Row row = sheet.getRow(i);
         if (row == null) {
         continue;
         }
         int lastCellNum = row.getLastCellNum();
         for (int j = 0; j < lastCellNum; j++) {
         Cell cell = row.getCell(j);
         if (cell == null) {
         continue;
         } else {
         switch (cell.getCellType()) {
         case STRING:
         String stringValue = cell.getStringCellValue();
         // 문자열 값 처리
         break;
         case NUMERIC:
         if (DateUtil.isCellDateFormatted(cell)) {
         Date dateValue = cell.getDateCellValue();
         // 날짜 값 처리
         } else {
         //double numericValue = cell.getNumericCellValue();
         double numericValue = cell.getNumericCellValue();
         String stringValue2 = Double.toString(numericValue);
         System.out.println("Numeric value as string: " + stringValue2);
         // 숫자 값 처리
         }
         break;
         case BOOLEAN:
         boolean booleanValue = cell.getBooleanCellValue();
         // 불리언 값 처리
         break;
         case FORMULA:
         // 수식 결과 값을 가져오는 경우 처리
         switch (cell.getCachedFormulaResultType()) {
         case STRING:
         String formulaStringValue = cell.getStringCellValue();
         // 수식 결과 문자열 값 처리
         break;
         case NUMERIC:
         double formulaNumericValue = cell.getNumericCellValue();
         // 수식 결과 숫자 값 처리
         break;
         case BOOLEAN:
         boolean formulaBooleanValue = cell.getBooleanCellValue();
         // 수식 결과 불리언 값 처리
         break;
         case ERROR:
         byte formulaErrorValue = cell.getErrorCellValue();
         // 수식 결과 에러 값 처리
         break;
         default:
         // 수식 결과 값이 없는 경우 처리
         break;
         }
         break;
         default:
         // 처리할 수 없는 셀 유형인 경우 처리
         break;
         }
         }
         x = (float) j * 50;
         y = (float) (lastRowNum - i) * 50;
         cb.beginText();
         cb.setFontAndSize(BaseFont.createFont(), 12);
         cb.showTextAligned(Element.ALIGN_LEFT, cell.getStringCellValue(), x, y, 0);
         cb.endText();
         }
         }
         document.close();

         // PDF 인쇄
         PDDocument pdf = PDDocument.load(new File(pdfFilePath));
         PrinterJob printerJob = PrinterJob.getPrinterJob();
         PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
         printerJob.setPageable(new PDFPageable(pdf));
         printerJob.setPrintService(printService);
         printerJob.print();
         pdf.close();

         System.out.println("Excel 파일이 성공적으로 PDF로 변환되었고, A4 크기로 인쇄되었습니다.");

         } catch (IOException | DocumentException | PrinterException e) {
         e.printStackTrace();
         }
         **/
    }
}
