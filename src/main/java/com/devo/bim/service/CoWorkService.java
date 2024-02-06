package com.devo.bim.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.devo.bim.component.Utils;
import com.devo.bim.model.dto.CoWorkDTO;
import com.devo.bim.model.dto.PageDTO;
import com.devo.bim.model.entity.Chatting;
import com.devo.bim.model.entity.CoWork;
import com.devo.bim.model.entity.CoWorkIssue;
import com.devo.bim.model.entity.CoWorkIssueJoiner;
import com.devo.bim.model.entity.CoWorkIssueReport;
import com.devo.bim.model.entity.CoWorkJoiner;
import com.devo.bim.model.entity.CoWorkModeling;
import com.devo.bim.model.entity.CoWorkSnapShot;
import com.devo.bim.model.entity.CoWorkSnapShotFile;
import com.devo.bim.model.entity.MySnapShot;
import com.devo.bim.model.entity.MySnapShotFile;
import com.devo.bim.model.enumulator.AlertType;
import com.devo.bim.model.enumulator.FileUploadUIType;
import com.devo.bim.model.vo.CoWorkIssueReportVO;
import com.devo.bim.model.vo.CoWorkIssueVO;
import com.devo.bim.model.vo.CoWorkVO;
import com.devo.bim.model.vo.MySnapShotShareVO;
import com.devo.bim.model.vo.SearchCoWorkVO;
import com.devo.bim.repository.dsl.CoWorkDslRepository;
import com.devo.bim.repository.spring.ChattingRepository;
import com.devo.bim.repository.spring.CoWorkIssueJoinerRepository;
import com.devo.bim.repository.spring.CoWorkIssueReportRepository;
import com.devo.bim.repository.spring.CoWorkIssueRepository;
import com.devo.bim.repository.spring.CoWorkJoinerRepository;
import com.devo.bim.repository.spring.CoWorkModelingRepository;
import com.devo.bim.repository.spring.CoWorkRepository;
import com.devo.bim.repository.spring.CoWorkSnapShotFileRepository;
import com.devo.bim.repository.spring.CoWorkSnapShotRepository;
import com.devo.bim.repository.spring.MySnapShotRepository;
import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoWorkService extends AbstractService {
    private final EntityManager em;
    private final CoWorkRepository coWorkRepository;
    private final CoWorkIssueRepository coWorkIssueRepository;
    private final CoWorkModelingRepository coWorkModelingRepository;
    private final CoWorkJoinerRepository coWorkJoinerRepository;
    private final CoWorkSnapShotRepository coWorkSnapShotRepository;
    private final CoWorkSnapShotFileRepository coWorkSnapShotFileRepository;
    private final MySnapShotRepository mySnapShotRepository;
    private final CoWorkDslRepository coWorkDslRepository;
    private final CoWorkIssueReportRepository coWorkIssueReportRepository;
    private final CoWorkIssueJoinerRepository coWorkIssueJoinerRepository;
    private final FileDeleteService fileDeleteService;
    private final AlertService alertService;
    private final ChatAPIService chatAPIService;
    private final ChattingRepository chattingRepository;

    public PageDTO<CoWorkDTO> findCoWorkBySearchCondition(SearchCoWorkVO searchCoWorkVO, Pageable pageable) {
        return coWorkDslRepository.findCoWorks(searchCoWorkVO, userInfo.getProjectId(), userInfo.getId(), pageable);
    }

    @Transactional
    public CoWork postNewCoWork(String modelIds, long accountViewPointId) {
        CoWork newCoWork = coWorkRepository.save(new CoWork(userInfo));

        for (long modelId : Utils.getLongList(modelIds, ","))
            coWorkModelingRepository.save(new CoWorkModeling(modelId, newCoWork.getId(), userInfo.getId()));

        coWorkJoinerRepository.save(new CoWorkJoiner(newCoWork.getId(), userInfo.getId()));

        //SnapShot 개발
        MySnapShot mySnapShot = mySnapShotRepository.findByIdAndOwnerId(accountViewPointId,userInfo.getId());
        CoWorkSnapShot coWorkSnapShot = coWorkSnapShotRepository.save(new CoWorkSnapShot(newCoWork,mySnapShot));
        
        newCoWork.setCoWorkSnapShot(coWorkSnapShot);
        
        mySnapShot.getMySnapShotFiles()
        		.stream()
        		.map(savedMySnapShtoFile ->coWorkSnapShotFileRepository.save(new CoWorkSnapShotFile(coWorkSnapShot, savedMySnapShtoFile)));
        return newCoWork;
    }
    
    @Transactional
    public CoWork postNewCoWork(String modelIds) {
        CoWork newCoWork = coWorkRepository.save(new CoWork(userInfo));

        for (long modelId : Utils.getLongList(modelIds, ","))
            coWorkModelingRepository.save(new CoWorkModeling(modelId, newCoWork.getId(), userInfo.getId()));

        coWorkJoinerRepository.save(new CoWorkJoiner(newCoWork.getId(), userInfo.getId()));

        return newCoWork;
    }

    public CoWork findByCoWorkId(long coWorkId) {
        return coWorkRepository.findByIdAndProjectId(coWorkId, userInfo.getProjectId()).orElseGet(CoWork::new);
    }

    public CoWork findByCoWorkIssueId(long coWorkIssueId) {
        return coWorkIssueRepository
                .findCoWorkById(coWorkIssueId)
                .map(savedCoWorkIssue -> savedCoWorkIssue.getCoWork())
                .orElseGet(CoWork::new);
    }

    private String getSaveFileName(String originalFileName) {
        return Utils.getFileNameWithOutExt(originalFileName) + "_" + Utils.getSaveFileNameDate() + "." + Utils.getFileExtName(originalFileName);
    }

    private boolean isJoiner(CoWork coWork) {
        return coWork
                .getCoWorkJoiners()
                .stream()
                .map(m -> m.getJoiner().getId() == userInfo.getId())
                .count() > 0;
    }

    @Transactional
    public JsonObject postSnapShotWithShare(MySnapShotShareVO mySnapShotShareVO) {
        CoWork coWork = coWorkRepository.findById(mySnapShotShareVO.getId()).orElseGet(CoWork::new);
        if (coWork.getId() == 0) return proc.getResult(false, "system.co_work_service.not_exist_co_work");
        if (!isJoiner(coWork)) return proc.getResult(false, "system.co_work_service.joiner_is_not_in_this_co_work");

        long newSavedSnapShotId = 0L;

        try {
            List<MySnapShot> sharedMySnapShot = mySnapShotRepository.findByIdInAndOwnerId(mySnapShotShareVO.targetMySnapShotIds(), userInfo.getId());
            
            for (MySnapShot mySnapShot : sharedMySnapShot) {
            	CoWorkSnapShot savedCoWorkSnapShot = coWorkSnapShotRepository.save(new CoWorkSnapShot(coWork, mySnapShot));
            	newSavedSnapShotId = savedCoWorkSnapShot.getId();
            	
            	for (long modelId : Utils.getLongList(mySnapShot.getViewModelId(), ",")) {
            		CoWorkModeling coWorkModeling = coWorkModelingRepository.findByModelingIdAndCoWorkId(modelId, mySnapShotShareVO.getId()).stream().findFirst().orElseGet(CoWorkModeling::new);
            		if(coWorkModeling.getId() == 0) coWorkModelingRepository.save(new CoWorkModeling(modelId, mySnapShotShareVO.getId(), userInfo.getId()));
            	}
            	
            	for (MySnapShotFile mySnapShotFile : mySnapShot.getMySnapShotFiles()) {
            		String newFilePathSavedDB = "";

                    // 저장된 파일 패스 조회 (파일명 포함)
                    File oldFile = new File(Utils.getPhysicalFilePath(winPathUpload, linuxPathUpload, macPathUpload, mySnapShotFile.getFilePath()));

                    // 파일 존재시 복사
                    if (oldFile.exists()) {

                        // 파일 저장 Base 폴더 지정
                        String saveBasePath = Utils.getSaveBasePath(winPathUpload, linuxPathUpload, macPathUpload);

                        // 새로운 파일이 저장될 실제 경로
                        String newFilePath = userInfo.getProjectId() + "/" + FileUploadUIType.CO_WORK_VIEW_POINT_MODEL_FILE.name().toLowerCase(Locale.ROOT);

                        // 새로운 파일명
                        String newFileName = getSaveFileName(mySnapShotFile.getOriginFileName());

                        // 실제 DB에 저장되는 파일 경로
                        newFilePathSavedDB = "/" + Utils.getBaseFolderName(saveBasePath) + "/" + newFilePath + "/" + newFileName;

                        // 파일 복사
                        FileCopyUtils.copy(oldFile, new File(saveBasePath + "/" + newFilePath + "/" + newFileName));
                    }
                    
                    // 파일 경로 세팅
                    CoWorkSnapShotFile newCoWorkSnapShotFile = new CoWorkSnapShotFile(savedCoWorkSnapShot.getId()
                            , mySnapShotFile.getOriginFileName()
                            , newFilePathSavedDB
                            , mySnapShotFile.getSortNo()
                            , mySnapShotFile.getSize()
                            , userInfo.getId());

                    // 도면 파일 copy 및 경로 조정 필요
                    coWorkSnapShotFileRepository.save(newCoWorkSnapShotFile);
            	}                    
            }
        } catch (Exception e) {
            log.error("error", e); // 2) 로그를 작성
            return proc.getMessageResult(false, e.getMessage());
        }

        return proc.getResult(true, newSavedSnapShotId, "system.co_work_service.success_sharing_my_snap_shot");
        ///
    }

    public CoWorkModeling findModeling(long modelingId) {
        return coWorkModelingRepository.findById(modelingId).orElseGet(CoWorkModeling::new);
    }

    public List<CoWorkModeling> findModelingByCoWorkId(long coWorkId) {
        return coWorkRepository.findByIdAndProjectId(coWorkId, userInfo.getProjectId()).orElseGet(CoWork::new).getCoWorkModelings();
    }

    public CoWorkSnapShot findSnapShot(long coWorkId) {
    	return coWorkSnapShotRepository.findCoWorkSnapShotFileById(coWorkId).orElseGet(CoWorkSnapShot::new);
    }
    
    @Transactional
    public CoWork deleteSnapShotId(long snapShotId) {
    	CoWorkSnapShot coWorkSnapShot = coWorkSnapShotRepository
    			.findCoWorkSnapShotFileById(snapShotId)
    			.orElseGet(CoWorkSnapShot::new);
    	
    	// view point 파일 삭제
    	coWorkSnapShot.getCoWorkSnapShotFiles().forEach(t -> {
    		fileDeleteService.deletePhysicalFile(t.getFilePath());
    	});
    	
    	coWorkSnapShot.delete();
    	return coWorkSnapShot.getCoWork();
    }

    @Transactional
    public CoWork deleteSnapShot(long snapShotId) {
        CoWorkSnapShot coWorkSnapShot = coWorkSnapShotRepository.findCoWorkSnapShotFileById(snapShotId).orElseGet(CoWorkSnapShot::new);
        coWorkSnapShot.delete();
        return coWorkSnapShot.getCoWork();
    }

    @Transactional
    public CoWorkSnapShot deleteSnapShotFile(long coWorkSnapShotFileId) {
    	CoWorkSnapShotFile coWorkSnapShotFile = coWorkSnapShotFileRepository.findCoWorkSnapShotById(coWorkSnapShotFileId);

    	try {
    		fileDeleteService.deletePhysicalFile(coWorkSnapShotFile.getFilePath());
    		coWorkSnapShotFileRepository.delete(coWorkSnapShotFile);
    	} catch (Exception e) {
    		log.error("error", e); // 2) 로그를 작성
    	}
    	return coWorkSnapShotFile.getCoWorkSnapShot();
    }

    @Transactional
    public CoWorkIssue saveCoWorkIssue(CoWorkIssueVO coWorkIssueVO) {
        CoWorkIssue coWorkIssue =
                coWorkIssueRepository
                        .findById(coWorkIssueVO.getId())
                        .map(savedCoWorkIssue -> {
                            putCoWorkIssueJoiner(coWorkIssueVO, savedCoWorkIssue);
                            return savedCoWorkIssue.updateData(coWorkIssueVO, userInfo.getId());
                        })
                        .orElseGet(() -> {
                            CoWorkIssue newCoWorkIssue = coWorkIssueRepository.save(new CoWorkIssue(coWorkIssueVO, userInfo.getId()));
                            postCoWorkIssueJoiner(coWorkIssueVO, newCoWorkIssue);
                            return newCoWorkIssue;
                        });

        em.flush();
        em.clear();

        return coWorkIssueRepository.findCoWorkById(coWorkIssue.getId()).orElseGet(CoWorkIssue::new);
    }

    private void postCoWorkIssueJoiner(CoWorkIssueVO coWorkIssueVO, CoWorkIssue newCoWorkIssue) {
        for (String joinerId : coWorkIssueVO.getJoinerIds().split(",")) {
            long accountId = Long.parseLong(joinerId);
            coWorkIssueJoinerRepository.save(new CoWorkIssueJoiner(newCoWorkIssue, accountId));
            String title = proc.translate("system.co_work_service.co_work_issue_invite", new String[]{coWorkIssueVO.getTitle()});
            alertService.saveAlert(true, newCoWorkIssue.getId(), accountId, title, AlertType.CO_WORK_ISSUE);
        }
    }

    private void putCoWorkIssueJoiner(CoWorkIssueVO coWorkIssueVO, CoWorkIssue savedCoWorkIssue) {
        for (String joinerId : coWorkIssueVO.getJoinerIds().split(",")) {
            long accountId = Long.parseLong(joinerId);

            // 기 협업 이슈 참여자 체크 및 신규 등록(알림포함)
            savedCoWorkIssue.getCoWorkIssueJoiners()
                    .stream()
                    .filter(t -> t.getJoiner().getId() == accountId)
                    .findFirst()
                    .map(t -> t.setTarget(true))  // 기 대상자 처리
                    .orElseGet(() -> {
                                CoWorkIssueJoiner newCoWorkIssueJoiner = coWorkIssueJoinerRepository.save(new CoWorkIssueJoiner(savedCoWorkIssue, accountId));
                                String title = proc.translate("system.co_work_service.co_work_issue_invite", new String[]{savedCoWorkIssue.getTitle()});
                                alertService.saveAlert(true, savedCoWorkIssue.getId(), accountId, title, AlertType.CO_WORK_ISSUE);
                                return newCoWorkIssueJoiner;
                            }
                    );

            // 협업 이슈 참여자에서 제외 된 대상 삭제(협업 이슢 생성자 삭제 대상에서 제외)
            savedCoWorkIssue
                    .getCoWorkIssueJoiners()
                    .stream()
                    .filter(t -> !t.isTarget() && t.getJoiner().getId() != savedCoWorkIssue.getWriteEmbedded().getWriter().getId())
                    .collect(Collectors.toList())
                    .forEach(t -> {
                        alertService.setDisabledAlert(userInfo.getProjectId(), t.getId(), t.getJoiner().getId(), AlertType.CO_WORK_ISSUE);
                        coWorkIssueJoinerRepository.delete(t);
                    });
        }
    }

    public CoWorkIssue findByCoWorKIssueId(long coWorkIssueId) {
        return coWorkIssueRepository.findCoWorkById(coWorkIssueId).orElseGet(CoWorkIssue::new);
    }

    @Transactional
    public CoWork deleteCoWorkIssue(long coWorkIssueId) {
        CoWorkIssue coWorkIssue = coWorkIssueRepository.findCoWorkById(coWorkIssueId).orElseGet(CoWorkIssue::new);
        coWorkIssueRepository.delete(coWorkIssue);

        em.flush();
        em.clear();

        return findByCoWorkId(coWorkIssue.getCoWork().getId());
    }

    @Transactional
    public JsonObject postCoWorkIssueReport(CoWorkIssueReportVO coWorkIssueReportVO) {

        CoWorkIssue savedCoWorkIssue = coWorkIssueRepository.findCoWorkById(coWorkIssueReportVO.getCoWorkIssueId()).orElseGet(CoWorkIssue::new);

        if (savedCoWorkIssue.getCoWork().getId() == 0) return proc.getResult(false, "system.co_work_service.not_exist_co_work");
        if (!isJoiner(savedCoWorkIssue.getCoWork())) return proc.getResult(false, "system.co_work_service.joiner_is_not_in_this_co_work");

        try {
            return proc.getResult(true, coWorkIssueReportRepository.save(new CoWorkIssueReport(coWorkIssueReportVO, userInfo.getId())).getId());
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putCoWork(CoWorkVO coWorkVO) {

        return coWorkRepository
                .findByIdAndProjectId(coWorkVO.getId(), userInfo.getProjectId())
                .map(savedCoWork -> {
                    putCoWorkJoiner(coWorkVO, savedCoWork);
                    savedCoWork.putCoWork(coWorkVO, userInfo.getId());

                    return proc.getMessageResult(true, coWorkVO.getSubject());
                }).orElseGet(() ->
                        proc.getMessageResult(false, coWorkVO.getSubject())
                );
    }

    private void putCoWorkJoiner(CoWorkVO coWorkVO, CoWork savedCoWork) {
        Chatting savedChatting = chattingRepository.findByCoWorkId(savedCoWork.getId()).orElseGet(Chatting::new);

        // 기 참여자 체크 및 신규 참여자 등록(알림 포함)
        for (String joinerId : coWorkVO.getJoinerIds().split(",")) {
            long accountId = Long.parseLong(joinerId);
            savedCoWork
                    .getCoWorkJoiners()
                    .stream()
                    .filter(t -> t.getJoiner().getId() == accountId)
                    .findFirst()
                    .map(t -> t.setTarget(true))  // 기 대상자 처리
                    .orElseGet(() -> {
                                CoWorkJoiner newCoWorkJoiner = coWorkJoinerRepository.save(new CoWorkJoiner(savedCoWork, accountId));
                                String title = proc.translate("system.co_work_service.co_work_invite", new String[]{savedCoWork.getSubject()});
                                alertService.saveAlert(true, savedCoWork.getId(), accountId, title, AlertType.CO_WORK);
                                return newCoWorkJoiner;
                            }
                    );
        }

        // 채팅 참여 추가는 목록을 대상으로 한 번만 수행
        if (coWorkVO.getJoinerIds().length() > 1) {
            chatAPIService.postChatJoinerAtPutWork(
                    savedChatting,
                    Arrays.stream(coWorkVO.getJoinerIds().split(",")).map(Long::parseLong).collect(Collectors.toList())
            );
        }


        // 참여자에서 제외 된 대상 삭제(협업 생성자 삭제 대상에서 제외)
        savedCoWork
                .getCoWorkJoiners()
                .stream()
                .filter(t -> !t.isTarget() && t.getJoiner().getId() != savedCoWork.getWriteEmbedded().getWriter().getId())
                .collect(Collectors.toList())
                .forEach(t -> {
                    alertService.setDisabledAlert(userInfo.getProjectId(), t.getCoWork().getId(), t.getJoiner().getId(), AlertType.CO_WORK);
                    coWorkJoinerRepository.delete(t);
                    chatAPIService.deleteChatJoinerAtPutWork(savedChatting, t.getJoiner().getId());
                });
    }

    @Transactional
    public JsonObject deleteCoWork(long coWorkId) {
        return coWorkRepository
                .findByIdAndProjectId(coWorkId, userInfo.getProjectId())
                .map(savedCoWork -> {
                            if (savedCoWork.getWriteEmbedded().getWriter().getId() == userInfo.getId()) {
                                coWorkRepository.delete(savedCoWork);
                                return proc.getResult(true, "system.co_work_service.co_work_delete_success");
                            }
                            return proc.getResult(false, "system.co_work_service.co_work_delete_no_role");
                        }
                ).orElseGet(() -> proc.getResult(false, "system.co_work_service.co_work_not_exist_this_project"));
    }

    @Transactional
    public JsonObject completeCoWork(long coWorkId) {
        CoWork savedCoWork = coWorkRepository.findByIdAndProjectId(coWorkId, userInfo.getProjectId()).orElseGet(CoWork::new);

        if (savedCoWork.getId() == 0) {
            proc.getResult(false, "system.co_work_service.co_work_not_exist_this_project");
        }
        if (!chatAPIService.setReadOnlyChatting(savedCoWork.getChatting().getTeamName())) {
            proc.getResult(false, "system.co_work_service.failed_set_read_only_chatting");
        }

        savedCoWork.completeStatus();
        return proc.getResult(true, "system.co_work_service.co_work_status_complete_update");
    }
}
