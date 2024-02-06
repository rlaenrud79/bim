package com.devo.bim.service;

import java.io.File;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.Notice;
import com.devo.bim.model.entity.NoticeFile;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.repository.spring.NoticeFileRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileDeleteService extends AbstractService {

    private final NoticeFileRepository noticeFileRepository;

    public void deletePhysicalFile(String filePath) {
        if(StringUtils.isEmpty(filePath)) return;
        String deletePath = Utils.getPhysicalFilePath(winPathUpload, linuxPathUpload, macPathUpload, filePath);
        File file = new File(deletePath);
        if (file.exists()) file.delete();
    }

    @Transactional
    public JsonObject deleteFileAndDB(FileUploadUIType fileUploadUIType, long fileId) {

        try {
            if(fileUploadUIType == FileUploadUIType.NOTIFICATION_FILE) return deleteNoticeFile(fileId);

            return proc.getResult(true, "system.file_delete_service.success_save_file");
        } catch (Exception e) {
            log.error("error", e); // 2) 로그를 작성
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private  JsonObject deleteNoticeFile(long fileId){
        // 1. 파일 정보 조회
        NoticeFile savedNoticeFile = noticeFileRepository.findById(fileId).orElseGet(NoticeFile::new);

        // 2. 파일 정보 존재 체크
        if(savedNoticeFile.getId() == 0)
            return proc.getResult(false, "system.file_delete_service.error_no_file_info");

        // 3. 파일 삭제 권한 체크 (공지 삭제 권한과 동일)
        if(!haveRightForDelete(savedNoticeFile.getNotice()))
            return proc.getResult(false, "system.file_delete_service.error_no_right_file_delete");

        // 3. 파일 삭제
        deletePhysicalFile(savedNoticeFile.getFilePath());

        // 4. DB 정보 삭제
        noticeFileRepository.delete(savedNoticeFile);

        // 결과 리턴
        return proc.getResult(true, "system.file_delete_service.success_save_file");
    }

    private boolean haveRightForDelete(Notice notice){
        if(userInfo.isRoleAdminProject()) return true;
        if(notice.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        return false;
    }
}
