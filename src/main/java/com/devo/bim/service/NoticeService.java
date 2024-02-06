package com.devo.bim.service;

import com.devo.bim.exception.ForbiddenException;
import com.devo.bim.exception.NotFoundException;
import com.devo.bim.model.dto.NoticeDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Account;
import com.devo.bim.model.entity.Notice;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.vo.SearchNoticeVO;
import com.devo.bim.repository.dsl.AccountDslRepository;
import com.devo.bim.repository.dsl.NoticeDTODslRepository;
import com.devo.bim.repository.spring.NoticeRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService extends AbstractService {

    private final NoticeDTODslRepository noticeDTODslRepository;
    private final NoticeRepository noticeRepository;
    private final AlertService alertService;
    private final AccountDslRepository accountDslRepository;
    private final AccountService accountService;


    public Notice findById(long noticeId){
        Notice savedNotice = noticeRepository.findById(noticeId).orElseGet(Notice::new);
        if(savedNotice.getId() == 0) new NotFoundException(proc.translate("system.common.exception_not_found"));
        if(!haveRightForView(savedNotice)) throw new ForbiddenException(proc.translate("system.common.exception_forbidden"));
        return savedNotice;
    }

    public boolean haveRightForView(Notice notice){
        if(userInfo.isRoleAdminProject()) return true;
        if(notice.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        if(accountService.haveWorks(notice.getWorks())) return true;
        return false;
    }

    public PageDTO<NoticeDTO> findNoticeDTOsBySearchCondition(SearchNoticeVO searchNoticeVO, Pageable pageable){
        return noticeDTODslRepository.findNoticeDTOs(searchNoticeVO, userInfo.getId(), userInfo.isRoleAdminProject(), pageable);
    }

    @Transactional
    public JsonObject postNotice(Notice notice){
        // 1 공지 등록 권한 체크
        if(!haveRightForAdd()) return proc.getResult(false, "system.notice_service.add_notice.no_have_right_add");

        try {
            // 2. Notice 저장
            Notice savedNotice = noticeRepository.save(notice);

            // 3. alert 대상 조회
            List<Account> workUsers = accountDslRepository.findByWorkIds(userInfo.getProjectId(), notice.getWorks());

            // 4. alert 저장
            saveAlert(savedNotice, workUsers, "INSERT");

            return proc.getResult(true, savedNotice.getId());
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private boolean haveRightForAdd(){
        if(userInfo.isRoleAdminProject() || userInfo.isRoleAdminWork()) return true;
        return false;
    }

    private void saveAlert(Notice notice, List<Account> workUsers, String actionType) {
        workUsers.forEach( t -> {
            alertService.insertAlertForNotice(userInfo.getProjectId(), t.getId(), notice, actionType);
        });
    }

    public Notice setViewCountPlusOne(Notice notice){
        notice.setViewCountPlusOne();
        return noticeRepository.save(notice);
    }

    @Transactional
    public JsonObject putNotice(Notice notice){
        // 1. 공지 조회
        Notice savedNotice = noticeRepository.findByIdAndProjectId(notice.getId(), notice.getProjectId()).orElseGet(Notice::new);
        if(savedNotice.getId() == 0 ) return proc.getResult(false, "system.notice_service.update_notice.no_notice");

        // 2. 공지 수정 권한 체크
        if(!haveRightForUpdate(savedNotice)) return proc.getResult(false, "system.notice_service.update_notice.no_have_right_update");

        try {
            // 3. notice data 변환
            savedNotice.setNoticeAtPutNotice(notice);

            // 4. 기존 alert 모두 disabled 처리
            setDisabledPreviousAlert(savedNotice);

            if(savedNotice.isEnabled()) {

                // 5. alert 대상 조회
                List<Account> workUsers = accountDslRepository.findByWorkIds(userInfo.getProjectId(), savedNotice.getWorks());

                // 6. alert 저장
                saveAlert(savedNotice, workUsers, "UPDATE");
            }
            
            return proc.getResult(true, savedNotice.getId());
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private boolean haveRightForUpdate(Notice notice){
        if(userInfo.isRoleAdminProject()) return true;
        if(notice.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        return false;
    }

    private void setDisabledPreviousAlert(Notice notice) {
        alertService.setDisabledPreviousAlert(userInfo.getProjectId(), notice.getId(), AlertType.NOTICE);
    }

    @Transactional
    public JsonObject deleteNotice(long noticeId){
        // 1. 공지 존재여부
        Notice savedNotice = noticeRepository.findByIdAndProjectId(noticeId, userInfo.getProjectId()).orElseGet(Notice::new);
        if(savedNotice.getId() == 0 ) return proc.getResult(false, "system.notice_service.delete_notice.no_notice");

        // 2. 공지 삭제 권한 체크
        if(!haveRightForDelete(savedNotice)) return proc.getResult(false, "system.notice_service.delete_notice.no_have_right_delete");

        try {
            // 2. 공지 삭제 처리 (상태 변경)
            savedNotice.setDisEnabledAtDeleteNotice();
            noticeRepository.save(savedNotice);

            // 3. alert disalbed 처리
            setDisabledAlert(savedNotice);

            return proc.getResult(true, "system.notice_service.delete_notice.success_notice_delete");
        }
        catch (Exception e){
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private boolean haveRightForDelete(Notice notice){
        if(userInfo.isRoleAdminProject()) return true;
        if(notice.getWriteEmbedded().getWriter().getId() == userInfo.getId()) return true;
        return false;
    }

    private void setDisabledAlert(Notice notice) {
        alertService.setDisabledAlert(userInfo.getProjectId(), notice.getId(), AlertType.NOTICE);
    }
}
