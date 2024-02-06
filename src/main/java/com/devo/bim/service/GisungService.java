package com.devo.bim.service;

import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.*;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.*;
import com.devo.bim.repository.spring.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GisungService extends AbstractService {
    private final GisungRepository gisungRepository;
    private final GisungItemRepository gisungItemRepository;

    private final GisungProcessItemRepository gisungProcessItemRepository;
    private final VmGisungProcessItemDTODslRepository vmGisungProcessItemDTODslRepository;
    private final GisungProcessItemCostDetailRepository gisungProcessItemCostDetailRepository;
    private final GisungProcessItemCostDetailDTODslRepository gisungProcessItemCostDetailDTODslRepository;
    private final GisungItemDetailRepository gisungItemDetailRepository;
    private final GisungDTODslRepository gisungDTODslRepository;
    private final GisungReportRepository gisungReportRepository;
    private final VmGisungProcessItemRepository vmGisungProcessItemRepository;

    private final JobSheetRepository jobSheetRepository;
    private final JobSheetProcessItemRepository jobSheetProcessItemRepository;
    private final JobSheetProcessItemDTODslRepository jobSheetProcessItemDTODslRepository;
    private final ProcessInfoRepository processInfoRepository;
    private final ProcessItemRepository processItemRepository;
    private final ProcessItemDslRepository processItemDslRepository;
    private final ProcessItemCostDetailRepository processItemCostDetailRepository;
    private final ProcessItemCostDslRepository processItemCostDslRepository;
    private final ProcessItemCostPayRepository processItemCostPayRepository;
    private final GisungWorkCostRepository gisungWorkCostRepository;
    private final VmGisungWorkCostDTODslRepository vmGisungWorkCostDTODslRepository;
    private final GisungContractorRepository gisungContractorRepository;
    private final WorkService workService;
    private final ProcessCostService processCostService;
    private final GisungCoverRepository gisungCoverRepository;
    private final GisungTableRepository gisungTableRepository;
    private final GisungListExcelFileRepository gisungListExcelFileRepository;
    private final GisungProcessItemCostTargetRepository gisungProcessItemCostTargetRepository;
    private final GisungProcessItemTargetRepository gisungProcessItemTargetRepository;

    public PageDTO<GisungDTO> findGisungDTOs(SearchGisungVO searchGisungVO, Pageable pageable) {
        return gisungDTODslRepository.findGisungDTOs(searchGisungVO, pageable);
    }

    public List<VmJobSheetProcessItemCostDTO> findJobSheetProcessItemListIdsByReportDate(long processId, String startDate, String endDate) {
        return jobSheetProcessItemDTODslRepository.findJobSheetProcessItemListIdsByReportDate(userInfo.getProjectId(), processId, startDate, endDate);
    }

    /**
     * 기성 등록
     * @param gisungVO
     * @return
     */
    @Transactional
    public JsonObject postGisung(GisungVO gisungVO) {
        try {
            long processId = processInfoRepository.findByProjectIdAndIsCurrentVersion( userInfo.getProjectId(), true ).stream().findFirst().get().getId();

            Gisung gisung = new Gisung();
            gisung.setGisungAtAddGisung(gisungVO, userInfo);
            gisungRepository.save(gisung);

            List<VmJobSheetProcessItemCostDTO> vmJobSheetProcessItemCostDTOs = findJobSheetProcessItemListIdsByReportDate(processId, gisung.getJobSheetStartDate(), gisung.getJobSheetEndDate());
            for (VmJobSheetProcessItemCostDTO vmJobSheetProcessItemCostDTO : vmJobSheetProcessItemCostDTOs) {
                GisungProcessItem gisungProcessItem = new GisungProcessItem();
                ProcessItem processItem = processItemRepository.findById(vmJobSheetProcessItemCostDTO.getProcessItemId()).orElseGet(ProcessItem::new);
                vmJobSheetProcessItemCostDTO.setProgressRate(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)));
                vmJobSheetProcessItemCostDTO.setCost(new BigDecimal(vmJobSheetProcessItemCostDTO.getTodayProgressAmount()));
                BigDecimal firstCount = new BigDecimal(BigInteger.ZERO);
                BigDecimal costDetailFirstCount = new BigDecimal(BigInteger.ZERO);
                if (processItem.getComplexUnitPrice() != null && processItem.getCost() != null && processItem.getFirstCount() != null
                        && processItem.getComplexUnitPrice().compareTo(processItem.getCost()) < 0) {
                    vmJobSheetProcessItemCostDTO.setComplexUnitPrice(processItem.getComplexUnitPrice().multiply(processItem.getFirstCount()));
                    firstCount = processItem.getFirstCount();
                    costDetailFirstCount = processItemCostDetailRepository.findProcessItemCostDetailByProcessItemIdByIsFirst(vmJobSheetProcessItemCostDTO.getProcessItemId()).orElse(BigDecimal.ZERO);
                }
                gisungProcessItem.setGisungProcessItemCostAtAddGisung(userInfo, gisung, vmJobSheetProcessItemCostDTO, processItem);
                gisungProcessItemRepository.save(gisungProcessItem);

                List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findProcessItemCostDetailByProcessItemIdListDTOs(vmJobSheetProcessItemCostDTO.getProcessItemId());
                for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                    GisungProcessItemCostDetail gisungProcessItemCostDetail = new GisungProcessItemCostDetail();
                    GisungProcessItemCostDetailDTO prevGisungProcessItemCostDetailDTO = gisungProcessItemCostDetailDTODslRepository.getGisungProcessItemCostDetailAtLatest(userInfo.getProjectId(), gisung.getId(), vmJobSheetProcessItemCostDTO.getProcessItemId(), gisungProcessItemCostDetailDTO.getCode());
                    BigDecimal paidProgressCount = new BigDecimal(BigInteger.ZERO);
                    BigDecimal paidCost = new BigDecimal(BigInteger.ZERO);
                    if (prevGisungProcessItemCostDetailDTO != null && prevGisungProcessItemCostDetailDTO.getId() > 0) {
                        paidProgressCount = prevGisungProcessItemCostDetailDTO.getProgressCount().add(prevGisungProcessItemCostDetailDTO.getPaidProgressCount());
                        paidCost = prevGisungProcessItemCostDetailDTO.getProgressCost().add(prevGisungProcessItemCostDetailDTO.getPaidCost());
                    }
                    gisungProcessItemCostDetailDTO.setJobSheetProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                    gisungProcessItemCostDetailDTO.setJobSheetProgressAmount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()).longValue());
                    gisungProcessItemCostDetailDTO.setPaidProgressCount(paidProgressCount);
                    gisungProcessItemCostDetailDTO.setPaidCost(paidCost);
                    gisungProcessItemCostDetailDTO.setProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                    gisungProcessItemCostDetailDTO.setProgressCost(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()));

                    if (gisungProcessItemCostDetailDTO.getCode() != null && !gisungProcessItemCostDetailDTO.getCode().equals("")) {
                        if (firstCount.compareTo(BigDecimal.ZERO) > 0) {
                            gisungProcessItemCostDetailDTO.setCount(gisungProcessItemCostDetailDTO.getCount().divide(costDetailFirstCount, 4, RoundingMode.HALF_UP).multiply(firstCount));
                            gisungProcessItemCostDetailDTO.setCost(gisungProcessItemCostDetailDTO.getCost().divide(costDetailFirstCount, 4, RoundingMode.HALF_UP).multiply(firstCount));
                            gisungProcessItemCostDetailDTO.setJobSheetProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                            gisungProcessItemCostDetailDTO.setJobSheetProgressAmount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()).longValue());
                            gisungProcessItemCostDetailDTO.setPaidProgressCount(paidProgressCount);
                            gisungProcessItemCostDetailDTO.setPaidCost(paidCost);
                            gisungProcessItemCostDetailDTO.setProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                            gisungProcessItemCostDetailDTO.setProgressCost(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()));
                        }
                    }
                    gisungProcessItemCostDetail.setGisungProcessItemCostDetailAtAddGisung(userInfo, gisung, gisungProcessItem, gisungProcessItemCostDetailDTO, processItem);
                    gisungProcessItemCostDetailRepository.save(gisungProcessItemCostDetail);
                }
            }

            return proc.getResult(true, gisung.getId(), "system.gisung_service.post_gisung");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    /**
     * 기성 수정
     * @param gisungVO
     * @return
     */
    @Transactional
    public JsonObject putGisung(GisungVO gisungVO) {
        Gisung savedGisung = gisungRepository.findById(gisungVO.getId()).orElseGet(Gisung::new);
        if (savedGisung.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung");
        }

        try {
            savedGisung.setGisungAtUpdateGisung(gisungVO, userInfo);
            return proc.getResult(true, gisungVO.getId(), "system.gisung_service.put_gisung");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    /**
     * 기성 삭제
     * @param id
     * @return
     */
    @Transactional
    public JsonObject deleteGisung(long id) {
        Gisung savedGisung = gisungRepository.findById(id).orElseGet(Gisung::new);
        if (savedGisung == null || savedGisung.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung");
        }

        try {
            // 기성 공정 상세제품 정보 테이블
            List<GisungProcessItemCostDetail> gisungProcessItemCostDetails = gisungProcessItemCostDetailRepository.findGisungProcessItemCostDetailByGisungId(id);
            //System.out.println("gisungProcessItemCostDetails : " + gisungProcessItemCostDetails.size());
            gisungProcessItemCostDetailRepository.deleteAllByIdInQuery(id);
            //if (gisungProcessItemCostDetails != null && gisungProcessItemCostDetails.size() > 0) {
            //    gisungProcessItemCostDetailRepository.deleteAllByIdInQuery(id);
            //}

            // 기성 공정정보 테이블
            List<GisungProcessItem> gisungProcessItems = gisungProcessItemRepository.findGisungProcessItemByGisungId(id);
            if (gisungProcessItems != null && gisungProcessItems.size() > 0) {
                for (GisungProcessItem gisungProcessItem : gisungProcessItems) {
                    if (gisungProcessItem.getProcessItem() != null && gisungProcessItem.getProcessItem().getId() > 0) {
                        ProcessItemCostPay processItemCostPay = processItemCostPayRepository.getProcessItemCostPayByMaxId(gisungProcessItem.getProcessItem().getId()).orElse(new ProcessItemCostPay());

                        // 기성 공정 삭제
                        gisungProcessItemRepository.deleteById(gisungProcessItem.getId());

                        // 기성 목표치 삭제
                        gisungProcessItemCostTargetRepository.deleteAllByIdInQuery(id);
                        gisungProcessItemTargetRepository.deleteAllByIdInQuery(id);

                        // 이전 기성 정보의 공정별 최종 기성 정보 가져오기
                        BigDecimal paidCost = BigDecimal.ZERO;
                        BigDecimal paidProgressRate = BigDecimal.ZERO;

                        if (gisungProcessItem.getPhasingCode() != null && !gisungProcessItem.getPhasingCode().equals("")) {
                            GisungProcessItem newGisungProcessItem = gisungProcessItemRepository.getGisungProcessItemPrevPaidCostInfo(gisungProcessItem.getPhasingCode(), gisungProcessItem.getProcessItem().getId()).orElse(new GisungProcessItem());
                            if (newGisungProcessItem != null) {
                                paidCost = newGisungProcessItem.getPaidCost();
                                paidProgressRate = newGisungProcessItem.getPaidProgressRate();
                            }
                        }

                        // 공정 정보 가져오기
                        ProcessItem savedProcessItem = processItemRepository.findById(gisungProcessItem.getProcessItem().getId()).orElseGet(ProcessItem::new);

                        // 내역관리 기성 금액 업데이트
                        if (processItemCostPay != null && processItemCostPay.getId() > 0) { // 최종 저장인 경우
                            BigDecimal prevPaidCost = processItemCostPay.getCost();
                            BigDecimal prevPaidProgressRate = processItemCostPay.getProgressRate();

                            // 기성 내역 리스트 삭제
                            processItemCostPayRepository.deleteById(processItemCostPay.getId());

                            BigDecimal sumCost = BigDecimal.ZERO;
                            BigDecimal sumProgressRate = BigDecimal.ZERO;

                            //sumCost = gisungProcessItemRepository.getGisungProcessItemSumCost(gisungProcessItem.getPhasingCode(), gisungProcessItem.getProcessItem().getId()).orElse(BigDecimal.ZERO);
                            //sumProgressRate = gisungProcessItemRepository.getGisungProcessItemSumProgressRate(gisungProcessItem.getPhasingCode(), gisungProcessItem.getProcessItem().getId()).orElse(BigDecimal.ZERO);
                            if (paidCost.compareTo(BigDecimal.ZERO) > 0) {
                                savedProcessItem.updateCost(paidCost, paidProgressRate, paidCost.subtract(prevPaidCost), paidProgressRate.subtract(prevPaidProgressRate), userInfo.getId());
                            } else {
                                savedProcessItem.updateCost(paidCost, paidProgressRate, BigDecimal.ZERO, BigDecimal.ZERO, userInfo.getId());
                            }

                        } else {
                            savedProcessItem.updateCost(paidCost, paidProgressRate, BigDecimal.ZERO, BigDecimal.ZERO, userInfo.getId());
                        }
                    }
                }
                gisungProcessItemRepository.deleteAllByIdInQuery(id);
            }
            // 기성 목표수량 정보 삭제
            gisungProcessItemCostTargetRepository.deleteAllByIdInQuery(id);
            // 기성 리스트 엑셀 업로드 삭제
            gisungListExcelFileRepository.deleteAllByIdInQuery(id);
            // 기성 집계표 엑셀 업로드 삭제
            gisungItemDetailRepository.deleteAllByIdInQuery(id);
            // 기성 집계표 엑셀 업로드 정보 삭제
            gisungItemRepository.deleteAllByIdInQuery(id);
            // 기성 공종 가격 테이블 삭제
            gisungWorkCostRepository.deleteAllByIdInQuery(id);
            // 기성 계약자 테이블 삭제
            gisungContractorRepository.deleteAllByIdInQuery(id);
            // 기성 표지 테이블 삭제
            gisungCoverRepository.deleteAllByIdInQuery(id);
            // 기성 문서 테이블 삭제
            gisungReportRepository.deleteAllByIdInQuery(id);
            // 기성 목록 테이블 삭제
            gisungTableRepository.deleteAllByIdInQuery(id);
            // 기성 작업일지 공정추가 삭제
            jobSheetProcessItemRepository.deleteAllByGisungIdInQuery(id);
            /**
            List<GisungItemDetail> gisungItemDetails = gisungItemDetailRepository.findGisungItemDetailByGisungId(id);
            if (gisungItemDetails != null && gisungItemDetails.size() > 0) {
                gisungItemDetailRepository.deleteAllByIdInQuery(id);
            }
            // 기성 집계표 엑셀 업로드 정보
            List<GisungItem> gisungItems = gisungItemRepository.findGisungItemByGisungId(id);
            if (gisungItems != null && gisungItems.size() > 0) {
                gisungItemRepository.deleteAllByIdInQuery(id);
            }
            // 기성 공종 가격 테이블
            List<GisungWorkCost> gisungWorkCosts = gisungWorkCostRepository.findGisungWorkCostByGisungId(id);
            if (gisungWorkCosts != null && gisungWorkCosts.size() > 0) {
                gisungWorkCostRepository.deleteAllByIdInQuery(id);
            }
            // 기성 계약자 테이블
            List<GisungContractor> gisungContractors = gisungContractorRepository.findGisungContractorByGisungId(id);
            if (gisungContractors != null && gisungContractors.size() > 0) {
                gisungContractorRepository.deleteAllByIdInQuery(id);
            }
            // 기성 표지 테이블
            List<GisungCover> gisungCovers = gisungCoverRepository.findGisungCoverByGisungId(id);
            if (gisungCovers != null && gisungCovers.size() > 0) {
                gisungCoverRepository.deleteAllByIdInQuery(id);
            }
            // 기성 문서 테이블
            List<GisungReport> gisungReports = gisungReportRepository.findGisungReportByGisungId(id);
            if (gisungReports != null && gisungReports.size() > 0) {
                gisungReportRepository.deleteAllByIdInQuery(id);
            }
            // 기성 목록 테이블
            List<GisungTable> gisungTables = gisungTableRepository.findGisungTableByGisungId(id);
            if (gisungTables != null && gisungTables.size() > 0) {
                gisungTableRepository.deleteAllByIdInQuery(id);
            }
            **/
            gisungRepository.delete(savedGisung);
            System.out.println("savedGisung.getId()22 : " + savedGisung.getId());
            return proc.getResult(true, "system.gisung_service.delete_gisung");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    /**
     * 기성 선택 삭제
     * @param gisungVO
     * @return
     */
    @Transactional
    public JsonObject deleteSelGisung(List<GisungVO> gisungVO) {
        try {
            for (int i = 0; i < gisungVO.size(); i++) {
                Gisung deleteGisung = gisungRepository.findById(gisungVO.get(i).getId()).orElseGet(Gisung::new);
                if (deleteGisung.getId() == 0) {
                    return proc.getResult(false, "system.gisung_service.not_exist_gisung");
                }

                // 기성 공정 상세제품 정보 테이블
                List<GisungProcessItemCostDetail> gisungProcessItemCostDetails = gisungProcessItemCostDetailRepository.findGisungProcessItemCostDetailByGisungId(deleteGisung.getId());
                gisungProcessItemCostDetailRepository.deleteAllByIdInQuery(deleteGisung.getId());

                // 기성 공정정보 테이블
                List<GisungProcessItem> gisungProcessItems = gisungProcessItemRepository.findGisungProcessItemByGisungId(deleteGisung.getId());
                if (gisungProcessItems != null && gisungProcessItems.size() > 0) {
                    for (GisungProcessItem gisungProcessItem : gisungProcessItems) {
                        if (gisungProcessItem.getProcessItem() != null && gisungProcessItem.getProcessItem().getId() > 0) {
                            ProcessItemCostPay processItemCostPay = processItemCostPayRepository.getProcessItemCostPayByMaxId(gisungProcessItem.getProcessItem().getId()).orElse(new ProcessItemCostPay());

                            // 기성 공정 삭제
                            gisungProcessItemRepository.deleteById(gisungProcessItem.getId());

                            // 기성 목표치 삭제
                            gisungProcessItemCostTargetRepository.deleteAllByIdInQuery(deleteGisung.getId());
                            gisungProcessItemTargetRepository.deleteAllByIdInQuery(deleteGisung.getId());

                            // 이전 기성 정보의 공정별 최종 기성 정보 가져오기
                            BigDecimal paidCost = BigDecimal.ZERO;
                            BigDecimal paidProgressRate = BigDecimal.ZERO;

                            if (gisungProcessItem.getPhasingCode() != null && !gisungProcessItem.getPhasingCode().equals("")) {
                                GisungProcessItem newGisungProcessItem = gisungProcessItemRepository.getGisungProcessItemPrevPaidCostInfo(gisungProcessItem.getPhasingCode(), gisungProcessItem.getProcessItem().getId()).orElse(new GisungProcessItem());
                                if (newGisungProcessItem != null) {
                                    paidCost = newGisungProcessItem.getPaidCost();
                                    paidProgressRate = newGisungProcessItem.getPaidProgressRate();
                                }
                            }

                            // 공정 정보 가져오기
                            ProcessItem savedProcessItem = processItemRepository.findById(gisungProcessItem.getProcessItem().getId()).orElseGet(ProcessItem::new);

                            // 내역관리 기성 금액 업데이트
                            if (processItemCostPay != null && processItemCostPay.getId() > 0) { // 최종 저장인 경우
                                BigDecimal prevPaidCost = processItemCostPay.getCost();
                                BigDecimal prevPaidProgressRate = processItemCostPay.getProgressRate();

                                // 기성 내역 리스트 삭제
                                processItemCostPayRepository.deleteById(processItemCostPay.getId());

                                BigDecimal sumCost = BigDecimal.ZERO;
                                BigDecimal sumProgressRate = BigDecimal.ZERO;

                                //sumCost = gisungProcessItemRepository.getGisungProcessItemSumCost(gisungProcessItem.getPhasingCode(), gisungProcessItem.getProcessItem().getId()).orElse(BigDecimal.ZERO);
                                //sumProgressRate = gisungProcessItemRepository.getGisungProcessItemSumProgressRate(gisungProcessItem.getPhasingCode(), gisungProcessItem.getProcessItem().getId()).orElse(BigDecimal.ZERO);
                                if (paidCost.compareTo(BigDecimal.ZERO) > 0) {
                                    savedProcessItem.updateCost(paidCost, paidProgressRate, paidCost.subtract(prevPaidCost), paidProgressRate.subtract(prevPaidProgressRate), userInfo.getId());
                                } else {
                                    savedProcessItem.updateCost(paidCost, paidProgressRate, BigDecimal.ZERO, BigDecimal.ZERO, userInfo.getId());
                                }

                            } else {
                                savedProcessItem.updateCost(paidCost, paidProgressRate, BigDecimal.ZERO, BigDecimal.ZERO, userInfo.getId());
                            }
                        }
                    }
                    gisungProcessItemRepository.deleteAllByIdInQuery(deleteGisung.getId());
                }

                // 기성 목표수량 정보 삭제
                gisungProcessItemCostTargetRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 리스트 엑셀 업로드 삭제
                gisungListExcelFileRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 집계표 엑셀 업로드 삭제
                gisungItemDetailRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 집계표 엑셀 업로드 정보 삭제
                gisungItemRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 공종 가격 테이블 삭제
                gisungWorkCostRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 계약자 테이블 삭제
                gisungContractorRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 표지 테이블 삭제
                gisungCoverRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 문서 테이블 삭제
                gisungReportRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 목록 테이블 삭제
                gisungTableRepository.deleteAllByIdInQuery(deleteGisung.getId());
                // 기성 작업일지 공정추가 삭제
                jobSheetProcessItemRepository.deleteAllByGisungIdInQuery(deleteGisung.getId());

                gisungRepository.delete(deleteGisung);
                System.out.println("savedGisung.getId()22 : " + deleteGisung.getId());
            }

            return proc.getResult(true, "system.gisung_service.delete_gisung");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public VmJobSheetProcessItemCostDTO findJobSheetProcessItemListIdByPhasingCode(String phasingCode, long processItemId) {
        return jobSheetProcessItemDTODslRepository.findJobSheetProcessItemListIdByPhasingCode(userInfo.getProjectId(), phasingCode, processItemId);
    }

    /**
     * 기성 공정추가
     * @param gisungProcessItemVO
     * @return
     */
    @Transactional
    public JsonObject postGisungProcessItem(GisungProcessItemVO gisungProcessItemVO) {
        try {
            Gisung gisung = gisungRepository.getById(gisungProcessItemVO.getGisungId());
            for (GisungProcessItem gisungProcessItem : gisungProcessItemVO.getData()) {
                GisungProcessItem newGisungProcessItem = gisungProcessItemRepository.findByGisungIdAndPhasingCode(gisungProcessItemVO.getGisungId(), gisungProcessItem.getPhasingCode()).orElseGet(GisungProcessItem::new);
                System.out.println(gisungProcessItemVO.getGisungId() + " : " + gisungProcessItem.getPhasingCode() + "-------------------------------------3");
                if (newGisungProcessItem == null || newGisungProcessItem.getId() == null) {
                    System.out.println(gisungProcessItemVO.getGisungId() + " : " + gisungProcessItem.getPhasingCode() + "-------------------------------------4");
                    GisungProcessItem savedGisungProcessItem = new GisungProcessItem();
                    // 작업일지 진행한 공정인지 체크
                    System.out.println(gisungProcessItemVO.getGisungId() + " : " + gisungProcessItem.getId() + "-------------------------------------6");
                    VmJobSheetProcessItemCostDTO vmJobSheetProcessItemCostDTO = findJobSheetProcessItemListIdByPhasingCode(gisungProcessItem.getPhasingCode(), gisungProcessItem.getId());
                    System.out.println(gisungProcessItemVO.getGisungId() + " : " + gisungProcessItem.getPhasingCode() + "-------------------------------------5");
                    //JobSheetProcessItem newJobSheetProcessItem = jobSheetProcessItemRepository.findJobSheetProcessItemById(gisungProcessItem.getId()).orElseGet(JobSheetProcessItem::new);
                    if (vmJobSheetProcessItemCostDTO != null && vmJobSheetProcessItemCostDTO.getId() > 0) {   // 작업일지 진행한 공정
                        ProcessItem processItem = processItemRepository.findById(vmJobSheetProcessItemCostDTO.getProcessItemId()).orElseGet(ProcessItem::new);
                        System.out.println(processItem.getPhasingCode() + " : " + processItem.getTaskFullPath() + "-------------------------------------2");
                        BigDecimal firstCount = new BigDecimal(BigInteger.ZERO);
                        BigDecimal costDetailFirstCount = new BigDecimal(BigInteger.ZERO);
                        if (processItem.getComplexUnitPrice() != null && processItem.getCost() != null && processItem.getFirstCount() != null
                                && processItem.getComplexUnitPrice().compareTo(processItem.getCost()) < 0) {
                            vmJobSheetProcessItemCostDTO.setComplexUnitPrice(processItem.getComplexUnitPrice().multiply(processItem.getFirstCount()));
                            firstCount = processItem.getFirstCount();
                            costDetailFirstCount = processItemCostDetailRepository.findProcessItemCostDetailByProcessItemIdByIsFirst(vmJobSheetProcessItemCostDTO.getProcessItemId()).orElse(BigDecimal.ZERO);
                        }
                        newGisungProcessItem.setGisungProcessItemCostAtAddGisung(userInfo, gisung, vmJobSheetProcessItemCostDTO, processItem);
                        gisungProcessItemRepository.save(newGisungProcessItem);

                        // 실적 진행률
                        vmJobSheetProcessItemCostDTO.setTodayProgressRate(vmJobSheetProcessItemCostDTO.getAfterProgressRate());
                        // 실적 금액
                        vmJobSheetProcessItemCostDTO.setTodayProgressAmount(vmJobSheetProcessItemCostDTO.getAfterProgressAmount());
                        // 금회 기성 진행률
                        vmJobSheetProcessItemCostDTO.setProgressRate(vmJobSheetProcessItemCostDTO.getAfterProgressRate().multiply(new BigDecimal(0.01)));
                        // 금회 기성 금액
                        vmJobSheetProcessItemCostDTO.setCost(new BigDecimal(vmJobSheetProcessItemCostDTO.getAfterProgressAmount()));
                        vmJobSheetProcessItemCostDTO.setAddItem("Y");
                        savedGisungProcessItem.setGisungProcessItemCostAtAddGisung(userInfo, gisung, vmJobSheetProcessItemCostDTO, processItem);
                        gisungProcessItemRepository.save(savedGisungProcessItem);

                        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findProcessItemCostDetailByProcessItemIdListDTOs(vmJobSheetProcessItemCostDTO.getProcessItemId());
                        for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                            GisungProcessItemCostDetail gisungProcessItemCostDetail = new GisungProcessItemCostDetail();
                            GisungProcessItemCostDetailDTO prevGisungProcessItemCostDetailDTO = gisungProcessItemCostDetailDTODslRepository.getGisungProcessItemCostDetailAtLatest(userInfo.getProjectId(), gisungProcessItemVO.getGisungId(), vmJobSheetProcessItemCostDTO.getProcessItemId(), gisungProcessItemCostDetailDTO.getCode());
                            BigDecimal paidProgressCount = new BigDecimal(BigInteger.ZERO);
                            BigDecimal paidCost = new BigDecimal(BigInteger.ZERO);
                            if (prevGisungProcessItemCostDetailDTO != null && prevGisungProcessItemCostDetailDTO.getId() > 0) {
                                // 전회 기성 수량
                                paidProgressCount = prevGisungProcessItemCostDetailDTO.getProgressCount().add(prevGisungProcessItemCostDetailDTO.getPaidProgressCount());
                                // 전회 기성 금액
                                paidCost = prevGisungProcessItemCostDetailDTO.getProgressCost().add(prevGisungProcessItemCostDetailDTO.getPaidCost());
                            }
                            // 실적 수량
                            BigDecimal jobSheetProgressCount = vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
                            // 실적 금액
                            BigDecimal jobSheetProgressAmount = vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice());

                            gisungProcessItemCostDetailDTO.setJobSheetProgressAmount(jobSheetProgressAmount.longValue());
                            // 전회 기성 수량
                            gisungProcessItemCostDetailDTO.setPaidProgressCount(paidProgressCount);
                            // 전회 기성 금액
                            gisungProcessItemCostDetailDTO.setPaidCost(paidCost);
                            // 금회 기성 수량
                            gisungProcessItemCostDetailDTO.setProgressCount(jobSheetProgressCount);
                            // 금회 기성 금액
                            gisungProcessItemCostDetailDTO.setProgressCost(jobSheetProgressAmount);

                            if (gisungProcessItemCostDetailDTO.getCode() != null && !gisungProcessItemCostDetailDTO.getCode().equals("")) {
                                if (firstCount.compareTo(BigDecimal.ZERO) > 0) {
                                    gisungProcessItemCostDetailDTO.setCount(gisungProcessItemCostDetailDTO.getCount().divide(costDetailFirstCount, 4, RoundingMode.HALF_UP).multiply(firstCount));
                                    gisungProcessItemCostDetailDTO.setCost(gisungProcessItemCostDetailDTO.getCost().divide(costDetailFirstCount, 4, RoundingMode.HALF_UP).multiply(firstCount));
                                    gisungProcessItemCostDetailDTO.setJobSheetProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                                    gisungProcessItemCostDetailDTO.setJobSheetProgressAmount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()).longValue());
                                    gisungProcessItemCostDetailDTO.setPaidProgressCount(paidProgressCount);
                                    gisungProcessItemCostDetailDTO.setPaidCost(paidCost);
                                    gisungProcessItemCostDetailDTO.setProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                                    gisungProcessItemCostDetailDTO.setProgressCost(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()));
                                }
                            }

                            gisungProcessItemCostDetail.setGisungProcessItemCostDetailAtAddGisung(userInfo, gisung, savedGisungProcessItem, gisungProcessItemCostDetailDTO, processItem);
                            gisungProcessItemCostDetailRepository.save(gisungProcessItemCostDetail);
                        }
                    } else { // 작업일지 진행하지 않은 공정
                        ProcessItem processItem = processItemRepository.findById(gisungProcessItem.getId()).orElseGet(ProcessItem::new);
                        System.out.println(gisung.getJobSheetStartDate() + " : " + gisung.getJobSheetEndDate() + "-------------------------------------");
                        BigDecimal todayProgressRate = BigDecimal.ZERO;
                        if (processItem != null && processItem.getId() > 0) {
                            JobSheet savedJobSheet = jobSheetRepository.findJobSheetAndGisungReportDate(userInfo.getProjectId(), gisung.getJobSheetStartDate(), gisung.getJobSheetEndDate()).orElse(new JobSheet());
                            // 기성 선택일자의 마지막 보고일자 공사일지에 공정추가
                            JobSheetProcessItem jobSheetProcessItem = new JobSheetProcessItem();
                            List<Object> worker = new ArrayList<>();
                            List<Object> device = new ArrayList<>();
                            jobSheetProcessItem.setJobSheetProcessItemAtAddJobSheet(
                                    savedJobSheet, processItem,
                                    processItem.getPhasingCode(),
                                    processItem.getTaskFullPath(),
                                    processItem.getProgressRate(),
                                    processItem.getProgressRate(),
                                    todayProgressRate,
                                    processItem.getProgressAmount(),
                                    processItem.getProgressAmount(),
                                    0,
                                    userInfo.getId(),
                                    worker,
                                    device,
                                    processItem.getExchangeIds(),
                                    0, "",
                                    gisungProcessItemVO.getGisungId()
                            );
                            jobSheetProcessItemRepository.save(jobSheetProcessItem);
                        }
                        vmJobSheetProcessItemCostDTO = new VmJobSheetProcessItemCostDTO(processItem.getPhasingCode(), processItem.getTaskName(),
                                processItem.getTaskFullPath(), processItem.getRowNum(), processItem.getParentRowNum(), todayProgressRate.longValue(), todayProgressRate.longValue(), todayProgressRate.longValue(),
                                todayProgressRate, todayProgressRate, todayProgressRate, processItem.getComplexUnitPrice(), processItem.getCost(), processItem.getPaidCost(),
                                processItem.getPaidProgressRate(), processItem.getParentId(), "", processItem.getWork().getId(), processItem.getId());
                        vmJobSheetProcessItemCostDTO.setAddItem("Y");
                        BigDecimal firstCount = new BigDecimal(BigInteger.ZERO);
                        BigDecimal costDetailFirstCount = new BigDecimal(BigInteger.ZERO);
                        if (processItem.getComplexUnitPrice() != null && processItem.getCost() != null && processItem.getFirstCount() != null
                                && processItem.getComplexUnitPrice().compareTo(processItem.getCost()) < 0) {
                            vmJobSheetProcessItemCostDTO.setComplexUnitPrice(processItem.getComplexUnitPrice().multiply(processItem.getFirstCount()));
                            firstCount = processItem.getFirstCount();
                            costDetailFirstCount = processItemCostDetailRepository.findProcessItemCostDetailByProcessItemIdByIsFirst(processItem.getId()).orElse(BigDecimal.ZERO);
                        }
                        savedGisungProcessItem.setGisungProcessItemCostAtAddGisung(userInfo, gisung, vmJobSheetProcessItemCostDTO, processItem);
                        gisungProcessItemRepository.save(savedGisungProcessItem);

                        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findProcessItemCostDetailByProcessItemIdListDTOs(vmJobSheetProcessItemCostDTO.getProcessItemId());
                        for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                            GisungProcessItemCostDetail gisungProcessItemCostDetail = new GisungProcessItemCostDetail();
                            GisungProcessItemCostDetailDTO prevGisungProcessItemCostDetailDTO = gisungProcessItemCostDetailDTODslRepository.getGisungProcessItemCostDetailAtLatest(userInfo.getProjectId(), gisungProcessItemVO.getGisungId(), vmJobSheetProcessItemCostDTO.getProcessItemId(), gisungProcessItemCostDetailDTO.getCode());
                            BigDecimal paidProgressCount = new BigDecimal(BigInteger.ZERO);
                            BigDecimal paidCost = new BigDecimal(BigInteger.ZERO);
                            if (prevGisungProcessItemCostDetailDTO != null && prevGisungProcessItemCostDetailDTO.getId() > 0) {
                                paidProgressCount = prevGisungProcessItemCostDetailDTO.getProgressCount().add(prevGisungProcessItemCostDetailDTO.getPaidProgressCount());
                                paidCost = prevGisungProcessItemCostDetailDTO.getProgressCost().add(prevGisungProcessItemCostDetailDTO.getPaidCost());
                            }
                            gisungProcessItemCostDetailDTO.setJobSheetProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                            gisungProcessItemCostDetailDTO.setJobSheetProgressAmount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()).longValue());
                            gisungProcessItemCostDetailDTO.setPaidProgressCount(paidProgressCount);
                            gisungProcessItemCostDetailDTO.setPaidCost(paidCost);
                            gisungProcessItemCostDetailDTO.setProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                            gisungProcessItemCostDetailDTO.setProgressCost(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()));

                            if (gisungProcessItemCostDetailDTO.getCode() != null && !gisungProcessItemCostDetailDTO.getCode().equals("")) {
                                if (firstCount.compareTo(BigDecimal.ZERO) > 0) {
                                    gisungProcessItemCostDetailDTO.setCount(gisungProcessItemCostDetailDTO.getCount().divide(costDetailFirstCount, 4, RoundingMode.HALF_UP).multiply(firstCount));
                                    gisungProcessItemCostDetailDTO.setCost(gisungProcessItemCostDetailDTO.getCost().divide(costDetailFirstCount, 4, RoundingMode.HALF_UP).multiply(firstCount));
                                    gisungProcessItemCostDetailDTO.setJobSheetProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                                    gisungProcessItemCostDetailDTO.setJobSheetProgressAmount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()).longValue());
                                    gisungProcessItemCostDetailDTO.setPaidProgressCount(paidProgressCount);
                                    gisungProcessItemCostDetailDTO.setPaidCost(paidCost);
                                    gisungProcessItemCostDetailDTO.setProgressCount(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(gisungProcessItemCostDetailDTO.getCount()).divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
                                    gisungProcessItemCostDetailDTO.setProgressCost(vmJobSheetProcessItemCostDTO.getTodayProgressRate().multiply(new BigDecimal(0.01)).multiply(gisungProcessItemCostDetailDTO.getCount()).multiply(gisungProcessItemCostDetailDTO.getUnitPrice()));
                                }
                            }

                            gisungProcessItemCostDetail.setGisungProcessItemCostDetailAtAddGisung(userInfo, gisung, savedGisungProcessItem, gisungProcessItemCostDetailDTO, processItem);
                            gisungProcessItemCostDetailRepository.save(gisungProcessItemCostDetail);
                        }
                    }
                }
            }

            return proc.getResult(true, gisung.getId(), "system.gisung_service.post_gisung_process_item");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    /**
     * 기성 공정 등록(임시저장, 최종저장)
     * @param gisungProcessItemVO
     * @return
     */
    @Transactional
    public JsonObject putGisungProcessItem(GisungProcessItemVO gisungProcessItemVO) {

        Gisung savedGisung = gisungRepository.findById(gisungProcessItemVO.getGisungId()).orElseGet(Gisung::new);
        if (savedGisung.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung");
        }

        // 이전 기성내역 조회
        Gisung prevGisung = gisungDTODslRepository.getGisungAtId(userInfo.getProjectId(), savedGisung.getId());

        try {
            BigDecimal sumPaidCost = BigDecimal.ZERO;
            BigDecimal sumPaidProgressRate = BigDecimal.ZERO;
            for (GisungProcessItem gisungProcessItem : gisungProcessItemVO.getData()) {
                GisungProcessItem savedGisungProcessItem = gisungProcessItemRepository.getById(gisungProcessItem.getId());

                if (gisungProcessItemVO.getStatus() != null && gisungProcessItemVO.getStatus().equals("COMPLETE")) {   // 최종저장
                    long processItemId = savedGisungProcessItem.getProcessItem().getId();

                    ProcessItem savedProcessItem = processItemRepository.findById(processItemId).orElseGet(ProcessItem::new);
                    if (savedProcessItem.getProcessInfo().getProjectId() != userInfo.getProjectId())
                        return proc.getResult(false, "system.gisung_service.project_is_not_in_task");

                    List<ProcessItemCostPayDTO> savedProcessItemCostPayDTOs = processItemCostDslRepository.findProcessItemCostPay(userInfo.getProjectId(), processItemId);
                    Integer costNo = savedProcessItemCostPayDTOs.size() + 1;
                    ProcessItemCostPay newProcessItemCostPay = new ProcessItemCostPay(savedProcessItem, costNo, gisungProcessItem, userInfo.getId());

                    // 신규 기성내역 저장 시 누적 진행률이 100%를 오버하는지 체크
                    // progressRate 진행률 합계
                    BigDecimal savedSumProgressRate = savedProcessItemCostPayDTOs.stream().map(t -> t.getProgressRate()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    System.out.println(processItemId + " : " + savedSumProgressRate + " : " + newProcessItemCostPay.getProgressRate() + " : " + savedSumProgressRate.add(newProcessItemCostPay.getProgressRate()) + "-------------------------------------");
                    System.out.println(savedSumProgressRate.add(newProcessItemCostPay.getProgressRate()).compareTo(new BigDecimal("100")) + "-------------------------------------");
                    if (savedSumProgressRate.add(newProcessItemCostPay.getProgressRate()).compareTo(new BigDecimal("100")) > 0)
                        return proc.getResult(false, "system.gisung_service.task_progress_rate_over_100");

                    // 신규 기성내역 저장 시 누적 기성금이 총 공사비를 오버하는지 체크
                    // cost 기성금 합계
                    BigDecimal savedSumCost = savedProcessItemCostPayDTOs.stream().map(t -> new BigDecimal(t.getCost())).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal taskCost = savedProcessItem.getCost();       // 총 공사비
                    BigDecimal thisCost = newProcessItemCostPay.getCost();  // 신규 기성금

                    System.out.println(taskCost + " : " + savedSumCost + " : " + thisCost + " : " + savedSumCost.add(thisCost) + "-------------------------------------");
                    if (taskCost.compareTo(savedSumCost.add(thisCost)) < 0)
                        return proc.getResult(false, "system.gisung_service.task_cost_over");
                }
            }
            for (GisungProcessItem gisungProcessItem : gisungProcessItemVO.getData()) {
                GisungProcessItem savedGisungProcessItem = gisungProcessItemRepository.getById(gisungProcessItem.getId());
                savedGisungProcessItem.setGisungProcessItemCostAtUpdateGisung(gisungProcessItem.getCost(), gisungProcessItem.getProgressRate(), "N");

                sumPaidCost = sumPaidCost.add(gisungProcessItem.getCost());
                sumPaidProgressRate = sumPaidProgressRate.add(gisungProcessItem.getProgressRate());

                if (gisungProcessItemVO.getStatus() != null && gisungProcessItemVO.getStatus().equals("COMPLETE")) {   // 최종저장
                    long processItemId = savedGisungProcessItem.getProcessItem().getId();

                    // 작업 최근 기성내역 조회
                    //ProcessItemCostPay latestProcessItemCostPay = processItemCostDslRepository.getProcessItemCostPayAtLatest(userInfo.getProjectId(), processItemId);

                    ProcessItem savedProcessItem = processItemRepository.findById(processItemId).orElseGet(ProcessItem::new);

                    List<ProcessItemCostPayDTO> savedProcessItemCostPayDTOs = processItemCostDslRepository.findProcessItemCostPay(userInfo.getProjectId(), processItemId);
                    Integer costNo = savedProcessItemCostPayDTOs.size() + 1;

                    ProcessItemCostPay newProcessItemCostPay = new ProcessItemCostPay(savedProcessItem, costNo, gisungProcessItem, userInfo.getId());

                    // 기성 내역 등록
                    processItemCostPayRepository.save(newProcessItemCostPay);

                    // 내역관리 기성 금액 업데이트
                    savedProcessItem.updateCost(newProcessItemCostPay.getSumCost(), newProcessItemCostPay.getSumProgressRate(), savedProcessItem.getPaidCost(), savedProcessItem.getPaidProgressRate(), userInfo.getId());

                    // 신규 등록 된 기성 내역 목록에 추가
                    ProcessItemCostPayDTO newProcessItemCostPayDTO = new ProcessItemCostPayDTO(newProcessItemCostPay);
                    newProcessItemCostPayDTO.setEdit(true);
                    savedProcessItemCostPayDTOs.add(0, newProcessItemCostPayDTO);
                }
            }


            if (gisungProcessItemVO.getStatus() != null && gisungProcessItemVO.getStatus().equals("COMPLETE")) {   // 최종저장
                GisungVO savedGisungVO = new GisungVO();
                savedGisungVO.setStatus(gisungProcessItemVO.getStatus());
                savedGisungVO.setSumPaidCost(sumPaidCost);                      // 금회기성금액
                savedGisungVO.setSumPaidProgressRate(sumPaidProgressRate);      // 금회기성률
                savedGisung.setGisungAtGisungStatusComplete(savedGisungVO);

                // 1. 프로젝트에서 사용하는 공종 조회
                // work
                SearchWorkVO searchWorkVO = new SearchWorkVO();
                setWorkDTOSearchCondition(searchWorkVO);
                List<WorkDTO> workDTOs = workService.findWorkDTOs(searchWorkVO);
                long workUpIdCount = workDTOs.stream().filter(m -> m.getUpId() > 0).count();
                List<VmGisungProcessItemDTO> newGisungProcessItemDTOs = vmGisungProcessItemDTODslRepository.findByGisungIdAndWorkIdDTOs(savedGisung.getId(), 0, 0);
                for (WorkDTO workDTO : workDTOs) {
                    Work work = workService.findById(workDTO.getId());
                    // 2. 공종별로 기성금액 조회
                    // 신규 기성
                    BigDecimal savedPaidCost = BigDecimal.ZERO;
                    if (workUpIdCount > 0 && workDTO.getUpId() == 0) {  // 공종 2뎁스이고 1뎁스일때
                        savedPaidCost = newGisungProcessItemDTOs.stream().filter(t -> t.getWorkUpId() == workDTO.getId()).map(t -> t.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    } else {
                        savedPaidCost = newGisungProcessItemDTOs.stream().filter(t -> t.getWorkId() == workDTO.getId()).map(t -> t.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    }
                    System.out.println("workUpIdCount : " + workUpIdCount + " workDTO.getId() : " + workDTO.getId() + " workDTO.getUpId() : " + workDTO.getUpId() + " savedPaidCost : " + savedPaidCost + "-------------------------------------");
                    // 이전 기성
                    //List<GisungProcessItem> prevGisungProcessItems = gisungProcessItemRepository.findByGisungIdAndWorkId(prevGisung.getId(), workDTO.getId());

                    BigDecimal savedPrevPaidCost = new BigDecimal(BigInteger.ZERO);
                    BigDecimal savedTotalPaidCost = new BigDecimal(BigInteger.ZERO);

                    GisungWorkCost gisungWorkCost = gisungWorkCostRepository.findByWorkIdAndGisungId(workDTO.getId(), savedGisung.getId()).orElseGet(GisungWorkCost::new);
                    GisungWorkCost prevGisungWorkCost = new GisungWorkCost();
                    if (prevGisung != null && prevGisung.getId() > 0) {
                        prevGisungWorkCost = gisungWorkCostRepository.findByWorkIdAndGisungId(workDTO.getId(), prevGisung.getId()).orElseGet(GisungWorkCost::new);
                    }

                    if (gisungWorkCost != null && gisungWorkCost.getId() > 0) {
                        savedPrevPaidCost = gisungWorkCost.getPrevPaidCost();
                        savedTotalPaidCost = savedPaidCost.add(savedPrevPaidCost);
                        GisungWorkCostDTO gisungWorkCostDTO = new GisungWorkCostDTO(gisungWorkCost.getId(), savedGisung.getId(), savedGisung.getGisungNo(), workDTO.getId(), userInfo.getProjectId(), savedGisung.getYear()
                                , savedPaidCost, savedPrevPaidCost, savedTotalPaidCost);
                        gisungWorkCost.setGisungWorkCostAtUpdateGisung(userInfo, savedGisung, gisungWorkCostDTO);
                    } else {
                        // 전회기성금액(전회의 전회까지 + 금회)
                        if (prevGisung != null && prevGisung.getId() > 0 && prevGisungWorkCost != null && prevGisungWorkCost.getId() > 0) {
                            savedPrevPaidCost = prevGisungWorkCost.getTotalPaidCost();
                        }
                        savedTotalPaidCost = savedPaidCost.add(savedPrevPaidCost);   // 금회기성금액 + 전회기성금액(전회의 전회까지 + 금회)
                        GisungWorkCostDTO gisungWorkCostDTO = new GisungWorkCostDTO(0, savedGisung.getId(), savedGisung.getGisungNo(), workDTO.getId(), userInfo.getProjectId(), savedGisung.getYear()
                                , savedPaidCost, savedPrevPaidCost, savedTotalPaidCost);
                        gisungWorkCost.setGisungWorkCostAtAddGisung(userInfo, work, savedGisung, gisungWorkCostDTO);
                        gisungWorkCostRepository.save(gisungWorkCost);
                    }
                }
                return proc.getResult(true, "system.gisung_service.post_gisung_process_item_complete");
            } else {
                return proc.getResult(true, "system.gisung_service.post_gisung_process_item_writing");
            }


        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    private void setWorkDTOSearchCondition(SearchWorkVO searchWorkVO) {
        searchWorkVO.setProjectId(userInfo.getProjectId());
        searchWorkVO.setLang(LocaleContextHolder.getLocale().getLanguage().toUpperCase());
    }

    /**
     * 기성 복합단가 수정
     * @param gisungProcessItemId
     * @param gisungProcessItemCostDetailDTOs
     * @return
     */
    public JsonObject putGisungProcessItemCostDetails(Long gisungProcessItemId, List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs) {
        VmGisungProcessItem vmGisungProcessItem = vmGisungProcessItemRepository.findById(gisungProcessItemId).orElseGet(VmGisungProcessItem::new);

        try {
            if(vmGisungProcessItem.getId() == 0)
                return proc.getResult(false,"system.process_cost_service.task_is_not");

            if(vmGisungProcessItem.getGisung().getProjectId() != userInfo.getProjectId())
                return proc.getResult(false,"system.process_cost_service.project_is_not_in_task");

            BigDecimal sumCost = BigDecimal.ZERO;
            BigDecimal sumProgressRate = BigDecimal.ZERO;
            for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                sumCost = sumCost.add(gisungProcessItemCostDetailDTO.getProgressCost());
                sumProgressRate = sumProgressRate.add(gisungProcessItemCostDetailDTO.getProgressCount());
                //if (gisungProcessItemCostDetailDTO.getProgressCost() != null && gisungProcessItemCostDetailDTO.getProgressCost().compareTo(BigDecimal.ZERO) > 0) {
                    GisungProcessItemCostDetail savedGisungProcessItemCostDetail = gisungProcessItemCostDetailRepository.findById(gisungProcessItemCostDetailDTO.getId()).orElseGet(GisungProcessItemCostDetail::new);
                    savedGisungProcessItemCostDetail.setGisungProcessItemCostDetailAtUpdateGisung(gisungProcessItemCostDetailDTO.getProgressCount(), gisungProcessItemCostDetailDTO.getProgressCost());
                //}
            }
            sumProgressRate = sumCost.divide(vmGisungProcessItem.getTaskCost(), 4, RoundingMode.HALF_UP);
            //sumProgressRate = sumProgressRate.multiply(new BigDecimal("0.01"));
            GisungProcessItem gisungProcessItem = new GisungProcessItem();
            gisungProcessItem.setGisungProcessItemCostAtUpdateGisung(sumCost, sumProgressRate, "Y");

            return proc.getResult(true, "system.gisung_service.post_gisung_process_item_cost_detail");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteGisungProcessItem(long id) {
        GisungProcessItem savedGisungProcessItem = gisungProcessItemRepository.findById(id).orElseGet(GisungProcessItem::new);
        if (savedGisungProcessItem.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung_process_item");
        }

        try {
            // 기성 공정 상세제품 정보 테이블
            List<GisungProcessItemCostDetail> gisungProcessItemCostDetails = gisungProcessItemCostDetailRepository.findGisungProcessItemCostDetailByGisungProcessItemId(savedGisungProcessItem.getId());
            if (gisungProcessItemCostDetails.size() > 0) {
                gisungProcessItemCostDetailRepository.deleteAllInBatch(gisungProcessItemCostDetails);
            }
            // 기성 공정정보 테이블
            gisungProcessItemRepository.delete(savedGisungProcessItem);

            // 작업일지 기성으로 추가된 공정 삭제
            jobSheetProcessItemRepository.deleteAllByGisungIdAndProcessItemIdInQuery(savedGisungProcessItem.getGisung().getId(), savedGisungProcessItem.getProcessItem().getId());

            return proc.getResult(true, "system.gisung_service.delete_gisung_process_item");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject postGisungItem(GisungItemVO gisungItemVO) {
        try {
            long id = 0;

            Gisung gisung = gisungRepository.findById(gisungItemVO.getGisungId()).orElseGet(Gisung::new);

            //GisungItem
            GisungItem savedGisungItem = new GisungItem();
            savedGisungItem.setGisungItemAtAddGisungItem(userInfo, gisung, gisungItemVO);
            gisungItemRepository.save(savedGisungItem);
            id = savedGisungItem.getId();

            if (gisungItemVO.getDocumentNo() != null) {
                if (gisungItemVO.getDocumentNo() == 1 || gisungItemVO.getDocumentNo() == 3) {
                    // 준공검사조서 문서 저장
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setStaff(gisungContractor.getStaff());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                } else if (gisungItemVO.getDocumentNo() == 2 || gisungItemVO.getDocumentNo() == 4) {
                    // 준공계 문서 저장
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setCompany2(gisungContractor.getCompany2());
                            savedGisungContractorVO.setName2(gisungContractor.getName2());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor2(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }

                } else if (gisungItemVO.getDocumentNo() == 5 || gisungItemVO.getDocumentNo() == 6) {
                    // 집계표 문서 저장
                    //GisungItemDetail
                    List<GisungItemDetail> gisungItemDetails = gisungItemDetailRepository.findGisungItemDetailByGisungIdAndGisungItemId(gisungItemVO.getGisungId(), gisungItemVO.getId());
                    if (gisungItemDetails.size() > 0) {
                        gisungItemDetailRepository.deleteAllInBatch(gisungItemDetails);
                    }

                    long contractAmount = 0;
                    BigDecimal itemPaidCost = new BigDecimal("0");
                    BigDecimal sumItemPaidCost = new BigDecimal("0");
                    BigDecimal itemPaidProgressRate = new BigDecimal("0");
                    for (GisungItemDetail gisungItemDetail : gisungItemVO.getData()) {
                        GisungItemDetailVO savedGisungItemDetailVO = new GisungItemDetailVO();
                        GisungItemDetail savedGisungItemDetail = new GisungItemDetail();
                        savedGisungItemDetailVO.setGisungItemId(id);
                        savedGisungItemDetailVO.setGisungId(gisungItemVO.getGisungId());
                        savedGisungItemDetailVO.setItemDetail01(gisungItemDetail.getItemDetail01());
                        savedGisungItemDetailVO.setItemDetail02(gisungItemDetail.getItemDetail02());
                        savedGisungItemDetailVO.setItemDetail03(gisungItemDetail.getItemDetail03());
                        savedGisungItemDetailVO.setItemDetail04(gisungItemDetail.getItemDetail04());
                        savedGisungItemDetailVO.setItemDetail05(gisungItemDetail.getItemDetail05());
                        savedGisungItemDetailVO.setItemDetail06(gisungItemDetail.getItemDetail06());
                        savedGisungItemDetailVO.setNetCheck(gisungItemDetail.getNetCheck());
                        savedGisungItemDetailVO.setGtype(gisungItemDetail.getGtype());
                        savedGisungItemDetail.setGisungItemDetail(userInfo, savedGisungItem, savedGisungItemDetailVO);
                        gisungItemDetailRepository.save(savedGisungItemDetail);

                        if (gisungItemDetail.getGtype() != null && gisungItemDetail.getGtype().equals("E")) {   // 가. 도  급  액  계
                            contractAmount = Long.parseLong(gisungItemDetail.getItemDetail02());    // 도급액
                            itemPaidCost = new BigDecimal(gisungItemDetail.getItemDetail04());      // 금회
                            sumItemPaidCost = new BigDecimal(gisungItemDetail.getItemDetail05());   // 누계
                            itemPaidProgressRate = new BigDecimal(gisungItemDetail.getItemDetail05()).divide(new BigDecimal(gisungItemDetail.getItemDetail02()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));  // 기성율
                        }
                    }

                    if (gisungItemVO.getDocumentNo() == 6) {    // 기성부분집계표(연차)
                        Gisung savedGisung = gisungRepository.findById(gisungItemVO.getGisungId()).orElseGet(Gisung::new);
                        GisungVO savedGisungVO = new GisungVO();
                        savedGisungVO.setContractAmount(contractAmount);
                        savedGisungVO.setItemPaidCost(itemPaidCost);
                        savedGisungVO.setSumItemPaidCost(sumItemPaidCost);
                        savedGisungVO.setItemPaidProgressRate(itemPaidProgressRate);
                        savedGisung.setGisungAtUpdateGisungItem(savedGisungVO);
                    }
                }
            }

            return proc.getResult(true, gisung.getId(), "system.gisung_service.post_gisung");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putGisungItem(GisungItemVO gisungItemVO) {
        try {
            long id = gisungItemVO.getId();

            Gisung gisung = gisungRepository.findById(gisungItemVO.getGisungId()).orElseGet(Gisung::new);

            GisungItem savedGisungItem = gisungItemRepository.findById(gisungItemVO.getId()).orElseGet(GisungItem::new);
            if (savedGisungItem.getId() == 0) {
                return proc.getResult(false, "system.gisung_service.not_exist_gisung");
            }

            //GisungItem
            savedGisungItem.setGisungItemAtUpdateGisungItem(userInfo, gisungItemVO);
            //gisungItemRepository.save(savedGisungItem);

            if (gisungItemVO.getDocumentNo() != null) {
                if (gisungItemVO.getDocumentNo() == 1) {
                    List<GisungContractor> gisungContractors = gisungContractorRepository.findGisungContractorByGisungIdAndDocumentNo(gisungItemVO.getGisungId(), gisungItemVO.getDocumentNo());
                    if (gisungContractors.size() > 0) {
                        gisungContractorRepository.deleteAllInBatch(gisungContractors);
                    }

                    // 준공검사조서 문서 저장
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setStampPath(gisungContractor.getStampPath());
                            savedGisungContractorVO.setCompany2(gisungContractor.getCompany2());
                            savedGisungContractorVO.setName2(gisungContractor.getName2());
                            savedGisungContractorVO.setStampPath2(gisungContractor.getStampPath2());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor2(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                    /**
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setStaff(gisungContractor.getStaff());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setStampPath(gisungContractor.getStampPath());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                     **/
                } else if (gisungItemVO.getDocumentNo() == 2) {
                    List<GisungContractor> gisungContractors = gisungContractorRepository.findGisungContractorByGisungIdAndDocumentNo(gisungItemVO.getGisungId(), gisungItemVO.getDocumentNo());
                    if (gisungContractors.size() > 0) {
                        gisungContractorRepository.deleteAllInBatch(gisungContractors);
                    }

                    // 준공계 문서 저장
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setStampPath(gisungContractor.getStampPath());
                            savedGisungContractorVO.setCompany2(gisungContractor.getCompany2());
                            savedGisungContractorVO.setName2(gisungContractor.getName2());
                            savedGisungContractorVO.setStampPath2(gisungContractor.getStampPath2());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor2(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                } else if (gisungItemVO.getDocumentNo() == 3) {
                    List<GisungContractor> gisungContractors = gisungContractorRepository.findGisungContractorByGisungIdAndDocumentNo(gisungItemVO.getGisungId(), gisungItemVO.getDocumentNo());
                    if (gisungContractors.size() > 0) {
                        gisungContractorRepository.deleteAllInBatch(gisungContractors);
                    }

                    // 기성부분검사조서 문서 저장
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setStampPath(gisungContractor.getStampPath());
                            savedGisungContractorVO.setCompany2(gisungContractor.getCompany2());
                            savedGisungContractorVO.setName2(gisungContractor.getName2());
                            savedGisungContractorVO.setStampPath2(gisungContractor.getStampPath2());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor2(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                    /**
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setStaff(gisungContractor.getStaff());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setStampPath(gisungContractor.getStampPath());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                     **/
                } else if (gisungItemVO.getDocumentNo() == 4) { // 기성부분 검사원
                    List<GisungContractor> gisungContractors = gisungContractorRepository.findGisungContractorByGisungIdAndDocumentNo(gisungItemVO.getGisungId(), gisungItemVO.getDocumentNo());
                    if (gisungContractors.size() > 0) {
                        gisungContractorRepository.deleteAllInBatch(gisungContractors);
                    }

                    // 준공계 문서 저장
                    for (GisungContractor gisungContractor : gisungItemVO.getContractorData()) {
                        GisungContractor savedGisungContractor = new GisungContractor();
                        GisungContractorVO savedGisungContractorVO = new GisungContractorVO();
                        if (gisungContractor.getCompany() != null && !gisungContractor.getCompany().equals("")) {
                            savedGisungContractorVO.setGisungId(id);
                            savedGisungContractorVO.setCompany(gisungContractor.getCompany());
                            savedGisungContractorVO.setName(gisungContractor.getName());
                            savedGisungContractorVO.setStampPath(gisungContractor.getStampPath());
                            savedGisungContractorVO.setCompany2(gisungContractor.getCompany2());
                            savedGisungContractorVO.setName2(gisungContractor.getName2());
                            savedGisungContractorVO.setStampPath2(gisungContractor.getStampPath2());
                            savedGisungContractorVO.setDocumentNo(gisungItemVO.getDocumentNo());
                            savedGisungContractor.setGisungContractor2(userInfo, gisung, savedGisungContractorVO);
                            gisungContractorRepository.save(savedGisungContractor);
                        }
                    }
                } else if (gisungItemVO.getDocumentNo() == 5 || gisungItemVO.getDocumentNo() == 6) {
                    // 집계표 문서 저장
                    //GisungItemDetail
                    List<GisungItemDetail> gisungItemDetails = gisungItemDetailRepository.findGisungItemDetailByGisungIdAndGisungItemId(gisungItemVO.getGisungId(), gisungItemVO.getId());
                    if (gisungItemDetails.size() > 0) {
                        gisungItemDetailRepository.deleteAllInBatch(gisungItemDetails);
                    }

                    long contractAmount = 0;
                    BigDecimal itemPaidCost = new BigDecimal("0");
                    BigDecimal sumItemPaidCost = new BigDecimal("0");
                    BigDecimal itemPaidProgressRate = new BigDecimal("0");
                    for (GisungItemDetail gisungItemDetail : gisungItemVO.getData()) {
                        GisungItemDetailVO savedGisungItemDetailVO = new GisungItemDetailVO();
                        GisungItemDetail savedGisungItemDetail = new GisungItemDetail();
                        savedGisungItemDetailVO.setGisungItemId(id);
                        savedGisungItemDetailVO.setGisungId(gisungItemVO.getGisungId());
                        savedGisungItemDetailVO.setItemDetail01(gisungItemDetail.getItemDetail01());
                        savedGisungItemDetailVO.setItemDetail02(gisungItemDetail.getItemDetail02());
                        savedGisungItemDetailVO.setItemDetail03(gisungItemDetail.getItemDetail03());
                        savedGisungItemDetailVO.setItemDetail04(gisungItemDetail.getItemDetail04());
                        savedGisungItemDetailVO.setItemDetail05(gisungItemDetail.getItemDetail05());
                        savedGisungItemDetailVO.setItemDetail06(gisungItemDetail.getItemDetail06());
                        savedGisungItemDetailVO.setNetCheck(gisungItemDetail.getNetCheck());
                        savedGisungItemDetailVO.setGtype(gisungItemDetail.getGtype());
                        savedGisungItemDetail.setGisungItemDetail(userInfo, savedGisungItem, savedGisungItemDetailVO);
                        gisungItemDetailRepository.save(savedGisungItemDetail);

                        if (gisungItemDetail.getGtype() != null && gisungItemDetail.getGtype().equals("E")) {   // 가. 도  급  액  계
                            contractAmount = Long.parseLong(gisungItemDetail.getItemDetail02());    // 도급액
                            itemPaidCost = new BigDecimal(gisungItemDetail.getItemDetail04());      // 금회
                            sumItemPaidCost = new BigDecimal(gisungItemDetail.getItemDetail05());   // 누계
                            itemPaidProgressRate = new BigDecimal(gisungItemDetail.getItemDetail05()).divide(new BigDecimal(gisungItemDetail.getItemDetail02()), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));  // 기성율
                        }
                    }

                    if (gisungItemVO.getDocumentNo() == 6) {    // 기성부분집계표(연차)
                        Gisung savedGisung = gisungRepository.findById(gisungItemVO.getGisungId()).orElseGet(Gisung::new);
                        GisungVO savedGisungVO = new GisungVO();
                        savedGisungVO.setContractAmount(contractAmount);
                        savedGisungVO.setItemPaidCost(itemPaidCost);
                        savedGisungVO.setSumItemPaidCost(sumItemPaidCost);
                        savedGisungVO.setItemPaidProgressRate(itemPaidProgressRate);
                        savedGisung.setGisungAtUpdateGisungItem(savedGisungVO);
                    }
                }
            }


            return proc.getResult(true, id, "system.gisung_service.put_gisung");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject postGisungCover(GisungCoverVO gisungCoverVO) {
        try {
            long id = 0;

            Gisung gisung = gisungRepository.findById(gisungCoverVO.getGisungId()).orElseGet(Gisung::new);

            //GisungCover
            GisungCover savedGisungCover = new GisungCover();
            savedGisungCover.setGisungCoverAtAddGisungCover(userInfo, gisung, gisungCoverVO);
            gisungCoverRepository.save(savedGisungCover);

            return proc.getResult(true, gisung.getId(), "system.gisung_service.post_gisung_cover");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putGisungCover(GisungCoverVO gisungCoverVO) {
        try {
            long id = gisungCoverVO.getId();

            Gisung gisung = gisungRepository.findById(gisungCoverVO.getGisungId()).orElseGet(Gisung::new);

            GisungCover savedGisungCover = gisungCoverRepository.findById(gisungCoverVO.getId()).orElseGet(GisungCover::new);
            if (savedGisungCover.getId() == 0) {
                return proc.getResult(false, "system.gisung_service.not_exist_gisung_cover");
            }

            //GisungCover
            savedGisungCover.setGisungCoverAtUpdateGisungCover(userInfo, gisungCoverVO);

            return proc.getResult(true, id, "system.gisung_service.put_gisung_cover");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteGisungCover(long id) {
        GisungCover savedGisungCover = gisungCoverRepository.findById(id).orElseGet(GisungCover::new);
        if (savedGisungCover.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung_cover");
        }

        try {
            // 기성 표지 테이블
            gisungCoverRepository.delete(savedGisungCover);

            return proc.getResult(true, "system.gisung_service.delete_gisung_cover");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject postGisungTable(GisungTableVO gisungTableVO) {
        try {
            long id = 0;

            Gisung gisung = gisungRepository.findById(gisungTableVO.getGisungId()).orElseGet(Gisung::new);

            //GisungCover
            GisungTable savedGisungTable = new GisungTable();
            savedGisungTable.setGisungTableAtAddGisungTable(userInfo, gisung, gisungTableVO);
            gisungTableRepository.save(savedGisungTable);

            return proc.getResult(true, gisung.getId(), "system.gisung_service.post_gisung_table");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject putGisungTable(GisungTableVO gisungTableVO) {
        try {
            long id = gisungTableVO.getId();

            Gisung gisung = gisungRepository.findById(gisungTableVO.getGisungId()).orElseGet(Gisung::new);

            GisungTable savedGisungTable = gisungTableRepository.findById(gisungTableVO.getId()).orElseGet(GisungTable::new);
            if (savedGisungTable.getId() == 0) {
                return proc.getResult(false, "system.gisung_service.not_exist_gisung_table");
            }

            //GisungCover
            savedGisungTable.setGisungTableAtUpdateGisungTable(gisungTableVO);

            return proc.getResult(true, id, "system.gisung_service.put_gisung_table");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @Transactional
    public JsonObject deleteGisungTable(long id) {
        GisungTable savedGisungTable = gisungTableRepository.findById(id).orElseGet(GisungTable::new);
        if (savedGisungTable.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung_table");
        }

        try {
            // 기성 목록 테이블
            gisungTableRepository.delete(savedGisungTable);

            return proc.getResult(true, "system.gisung_service.delete_gisung_table");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public List<GisungProcessItemCostDetailDTO> getGisungProcessItemCostDetail(Long gisungId) {
        List<VmGisungProcessItem> vmGisungProcessItems = vmGisungProcessItemRepository.findByGisungId(gisungId);
        List<GisungProcessItemCostDetailDTO> savedProcessItemCostDetailLists = new ArrayList<>();

        if (vmGisungProcessItems.size() > 0) {
            for (VmGisungProcessItem vmGisungProcessItem : vmGisungProcessItems) {
                List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findGisungProcessItemCostDetail(vmGisungProcessItem.getId());
                if (gisungProcessItemCostDetailDTOs.size() > 0) {
                    for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                        BigDecimal count = gisungProcessItemCostDetailDTO.getCount();
                        BigDecimal cost = gisungProcessItemCostDetailDTO.getCost();
                        BigDecimal unitPrice = gisungProcessItemCostDetailDTO.getUnitPrice();
                        BigDecimal progressCount = gisungProcessItemCostDetailDTO.getProgressCount();
                        BigDecimal progressCost = gisungProcessItemCostDetailDTO.getProgressCost();
                        progressCost = progressCount.multiply(unitPrice);
                        BigDecimal paidProgressCount = gisungProcessItemCostDetailDTO.getPaidProgressCount();
                        BigDecimal paidCost = gisungProcessItemCostDetailDTO.getPaidCost();
                        BigDecimal sumProgressCount = paidProgressCount.add(progressCount);
                        BigDecimal sumProgressCost = paidCost.add(progressCost);
                        BigDecimal remindProgressCount = count.subtract(sumProgressCount);
                        BigDecimal remindProgressCost = cost.subtract(sumProgressCost);

                        GisungProcessItemCostDetailDTO savedGisungProcessItemCostDetailDTO = new GisungProcessItemCostDetailDTO(vmGisungProcessItem.getTaskName()
                                , vmGisungProcessItem.getPhasingCode()
                                , gisungProcessItemCostDetailDTO.getId()
                                , gisungProcessItemCostDetailDTO.getCode()
                                , gisungProcessItemCostDetailDTO.getName()
                                , count
                                , gisungProcessItemCostDetailDTO.getUnit()
                                , gisungProcessItemCostDetailDTO.getUnitPrice()
                                , cost
                                , gisungProcessItemCostDetailDTO.isFirst()
                                , gisungProcessItemCostDetailDTO.getJobSheetProgressCount()
                                , gisungProcessItemCostDetailDTO.getJobSheetProgressAmount()
                                , paidProgressCount
                                , paidCost
                                , progressCount
                                , progressCost
                                , sumProgressCount
                                , sumProgressCost
                                , remindProgressCount
                                , remindProgressCost);
                        savedProcessItemCostDetailLists.add(savedGisungProcessItemCostDetailDTO);
                    }
                }
            }
        }
        return savedProcessItemCostDetailLists;
    }

    public Gisung findById(long id) {
        return gisungRepository.findById(id).orElseGet(Gisung::new);
    }

    public GisungItem findByGisungIdAndDocumentNo(long id, Integer no) {
        return gisungItemRepository.findByGisungIdAndDocumentNo(id, no).orElseGet(GisungItem::new);
    }

    public List<VmGisungProcessItemDTO> findVmGisungProcessItemByGisungIdListDTOs(long gisungId, String searchCompareResult) {
        return vmGisungProcessItemDTODslRepository.findVmGisungProcessItemByGisungIdListDTOs(userInfo.getProjectId(), gisungId, searchCompareResult);
    }

    public List<GisungItemDetail> findGisungItemDetailByGisungItemId(long gisungItemId) {
        return gisungItemDetailRepository.findGisungItemDetailByGisungItemId(gisungItemId);
    }

    public List<GisungItemDetail> getGisungItemDetailList01(long gisungItemId) {
        return gisungItemDetailRepository.getGisungItemDetailList01(gisungItemId);
    }

    public List<GisungItemDetail> getGisungItemDetailGisungIdGtypeList(long gisungId, String gtype) {
        return gisungItemDetailRepository.getGisungItemDetailGisungIdGtypeList(gisungId, gtype);
    }

    public List<String> getGisungItemDetailGisungIdGtypeListData(long gisungId, String gtype, Integer documentNo) {
        return gisungItemDetailRepository.getGisungItemDetailGisungIdGtypeListData(gisungId, gtype, documentNo);
    }

    public List<GisungItemDetail> getGisungItemDetailList02(long gisungItemId) {
        return gisungItemDetailRepository.getGisungItemDetailList02(gisungItemId);
    }

    public Integer getGisungItemDetailRowSpanCnt(long gisungItemId) {
        return gisungItemDetailRepository.getGisungItemDetailRowSpanCnt(gisungItemId).orElse(0);
    }

    public Integer getGisungMaxGisungNo() {
        return gisungRepository.getGisungMaxGisungNo(userInfo.getProjectId()).orElse(1);
    }

    public List<VmGisungWorkCostDTO> findGisungWorkCostByGisungIdListDTOs(long gisungId, String year, SearchWorkVO searchWorkVO) {
        return vmGisungWorkCostDTODslRepository.findVmGisungWorkCostByGisungIdListDTOs(userInfo.getProjectId(), gisungId, year, searchWorkVO);
    }

    public Long getGisungPrevId(long id) {
        return gisungItemDetailRepository.getGisungPrevId(id).orElse(0L);
    }

    public Integer getGisungYearCount(String year, long id) {
        return gisungItemDetailRepository.getGisungYearCount(year, id).orElse(0);
    }

    public List<GisungProcessItemCostDetailDTO> findGisungProcessItemCostDetail(Long gisungProcessItemId) {
        return gisungProcessItemCostDetailDTODslRepository.findGisungProcessItemCostDetail(gisungProcessItemId);
    }

    public GisungProcessItem findGisungProcessItemById(long id) {
        return gisungProcessItemRepository.findById(id).orElseGet(GisungProcessItem::new);
    }

    public List<GisungContractor> findGisungContractorByGisungIdAndDocumentNo(long gisungId, Integer documentNo) {
        return gisungContractorRepository.findGisungContractorByGisungIdAndDocumentNo(gisungId, documentNo);
    }

    public List<GisungCover> findGisungCoverByGisungId(long gisungId) {
        return gisungCoverRepository.findGisungCoverByGisungId(gisungId);
    }

    public JsonObject getGisungCover(long id) {
        return proc.getResult(true, findGisungCoverById(id));
    }

    public GisungCoverDTO findGisungCoverById(long id) {
        return new GisungCoverDTO(gisungCoverRepository
                .findById(id).orElseGet(GisungCover::new)
        );
    }

    public List<GisungTable> findGisungTableByGisungId(long gisungId) {
        return gisungTableRepository.findGisungTableByGisungId(gisungId);
    }

    public JsonObject getGisungTable(long id) {
        return proc.getResult(true, findGisungTableById(id));
    }

    public GisungTableDTO findGisungTableById(long id) {
        return new GisungTableDTO(gisungTableRepository
                .findById(id).orElseGet(GisungTable::new)
        );
    }

    @Transactional
    public JsonObject procGisungItemCostExcel(List<JsonObject> gisungItemCostJsonObject){
        long gisungId = 0;
        try{
            int objectSize = gisungItemCostJsonObject.size();
            if (objectSize > 0) {
                gisungId = gisungItemCostJsonObject.get((objectSize - 1)).get("gisungNo").getAsLong();
            }
            Gisung gisung = gisungRepository.findById(gisungId).orElseGet(Gisung::new);

            if (gisungItemCostJsonObject.size() == 1){ // 입력하는 excel 데이터의 길이가 1개라면
                return proc.getMessageResult(false, "저장 실패");
            }
            else{
                // List<JsonObject>에서 첫 번째 JsonObject 객체 가져오기
                JsonObject jsonObject01 = gisungItemCostJsonObject.get(0);
                // 첫 번째 JsonObject 객체에서 모든 key-value 쌍 출력
                int m = 0;
                String title = "";
                String item01 = "";
                String item02 = "";
                for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject01.entrySet()) {
                    String key = entry.getKey();
                    com.google.gson.JsonElement value = entry.getValue();
                    if (m == 1) {
                        title = key;
                        item01 = jsonObject01.get(key).toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_3")) {
                        item02 = value.toString().replaceAll("\"", "");
                    }
                    m++;
                }

                JsonObject jsonObject02 = gisungItemCostJsonObject.get(1);
                m = 0;
                String item03 = "";
                String item04 = "";
                for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject02.entrySet()) {
                    String key = entry.getKey();
                    com.google.gson.JsonElement value = entry.getValue();
                    if (m == 1) {
                        item03 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_3")) {
                        item04 = value.toString().replaceAll("\"", "");
                    }
                    //System.out.println(key + ": " + value);
                    m++;
                }

                JsonObject jsonObject03 = gisungItemCostJsonObject.get(2);
                m = 0;
                String item05 = "";
                String item06 = "";
                for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject03.entrySet()) {
                    String key = entry.getKey();
                    com.google.gson.JsonElement value = entry.getValue();
                    if (m == 1) {
                        item05 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_3")) {
                        item06 = value.toString().replaceAll("\"", "");
                    }
                    //System.out.println(key + ": " + value);
                    m++;
                }

                JsonObject jsonObject04 = gisungItemCostJsonObject.get(112);
                m = 0;
                String item07 = "";
                String item08 = "";
                String item09 = "";
                String item10 = "";
                String item11 = "";
                String item12 = "";
                for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObject04.entrySet()) {
                    String key = entry.getKey();
                    com.google.gson.JsonElement value = entry.getValue();
                    if (key != null && key.equals(title)) {
                        item07 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_1")) {
                        item08 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_2")) {
                        item09 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_3")) {
                        item10 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_4")) {
                        item11 = value.toString().replaceAll("\"", "");
                    }
                    if (key != null && key.equals("__EMPTY_5")) {
                        item12 = value.toString().replaceAll("\"", "");
                    }
                    m++;
                }

                //GisungItem
                GisungItem gisungItem = gisungItemRepository.findByGisungIdAndDocumentNo(gisung.getId(), 3).orElseGet(GisungItem::new);
                GisungItemVO gisungItemVO = new GisungItemVO();
                gisungItemVO.setGisungId(gisung.getId());
                gisungItemVO.setTitle(title);
                gisungItemVO.setItem01(item01);
                gisungItemVO.setItem02(item02);
                gisungItemVO.setItem03(item03);
                gisungItemVO.setItem04(item04);
                gisungItemVO.setItem05(item05);
                gisungItemVO.setItem06(item06);
                gisungItemVO.setItem07(item07);
                gisungItemVO.setItem08(item08);
                gisungItemVO.setItem09(item09);
                gisungItemVO.setItem10(item10);
                gisungItemVO.setItem11(item11);
                gisungItemVO.setItem12(item12);
                if (gisungItem != null && gisungItem.getId() > 0) {
                    gisungItem.setGisungItemAtUpdateGisungItem(userInfo, gisungItemVO);
                    gisungItemRepository.save(gisungItem);
                } else {
                    gisungItem.setGisungItemAtAddGisungItem(userInfo, gisung, gisungItemVO);
                    gisungItemRepository.save(gisungItem);
                }

                //GisungItemDetail
                List<GisungItemDetail> gisungItemDetails = gisungItemDetailRepository.findGisungItemDetailByGisungItemId(gisungItem.getId());
                if (gisungItemDetails.size() > 0) {
                    gisungItemDetailRepository.deleteAllInBatch(gisungItemDetails);
                }

                boolean insertChk = true;
                for(int i=6 ; i < objectSize ; i++) {
                    JsonObject jsonObjectDetail = gisungItemCostJsonObject.get(i);
                    GisungItemDetail gisungItemDetail = new GisungItemDetail();
                    GisungItemDetailVO gisungItemDetailVO = new GisungItemDetailVO();
                    m = 0;
                    boolean addItemDetail = false;
                    for (Map.Entry<String, com.google.gson.JsonElement> entry : jsonObjectDetail.entrySet()) {
                        String key = entry.getKey();
                        com.google.gson.JsonElement value = entry.getValue();
                        if (key != null) {
                            if (key.equals(title)) {
                                gisungItemDetailVO.setItemDetail01(value.toString().replaceAll("\"", ""));
                                if (value != null && value.toString().replaceAll("\"", "").equals("노선")) {
                                    insertChk = false;
                                }
                            }
                            switch (key) {
                                case "__EMPTY":
                                    gisungItemDetailVO.setItemDetail01(value.toString().replaceAll("\"", ""));
                                    addItemDetail = true;
                                    break;
                                case "__EMPTY_2":
                                    gisungItemDetailVO.setItemDetail02(value.toString().replaceAll("\"", ""));
                                    addItemDetail = true;
                                    break;
                                case "__EMPTY_3":
                                    gisungItemDetailVO.setItemDetail03(value.toString().replaceAll("\"", ""));
                                    addItemDetail = true;
                                    break;
                                case "__EMPTY_4":
                                    gisungItemDetailVO.setItemDetail04(value.toString().replaceAll("\"", ""));
                                    addItemDetail = true;
                                    break;
                                case "__EMPTY_5":
                                    gisungItemDetailVO.setItemDetail05(value.toString().replaceAll("\"", ""));
                                    addItemDetail = true;
                                    break;
                                case "__EMPTY_6":
                                    gisungItemDetailVO.setItemDetail06(value.toString().replaceAll("\"", ""));
                                    addItemDetail = true;
                                    break;
                            }
                        }

                        m++;
                    }
                    if (addItemDetail && insertChk) {
                        gisungItemDetail.setGisungItemDetail(userInfo, gisungItem, gisungItemDetailVO);
                        gisungItemDetailRepository.save(gisungItemDetail);
                    }
                }

                for(int i=0 ; i < objectSize ; i++) {
                    /**
                    //아무것도 입력이 안된 라인인지 확인
                    if(gisungItemCostJsonObject.get(i).get("rowNum") != null && !gisungItemCostJsonObject.get(i).get("rowNum").toString().equals("")){
                        System.out.println(gisungItemCostJsonObject.get(i) + "------" + currentPhasingCode);
                        if(processItemCostDetailJsonObject.size() == 1){
                            if(onlyOneIsFirst(processItemCostDetail)){
                                insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                                return proc.getMessageResult(true, "저장 완료");
                            }
                            else{
                                return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                            }
                        }
                        else{
                            processItemCostDetailJsonObject.remove(0);
                            continue;
                        }
                    }
                    // 같은 공정들인지 확인
                    else if( currentPhasingCode.equals(processItemCostDetailJsonObject.get(0).get("공정코드").toString()) && currentTaskName.equals(processItemCostDetailJsonObject.get(0).get("공정명").toString()) ){
                        processItemCostDetail.add(processItemCostDetailJsonObject.get(0));
                    }
                    // 다른 공정이 발생한 경우(지금까지의 공정을 DB에 저장)
                    else if( !currentPhasingCode.equals(processItemCostDetailJsonObject.get(0).get("공정코드").toString()) || !currentTaskName.equals(processItemCostDetailJsonObject.get(0).get("공정명").toString()) ){
                        if(onlyOneIsFirst(processItemCostDetail)){
                            insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                            processItemCostDetail = new ArrayList<>();
                            processItemCostDetail.add(processItemCostDetailJsonObject.get(0));
                            currentTaskName = processItemCostDetailJsonObject.get(0).get("공정명") == null ? "" : processItemCostDetailJsonObject.get(0).get("공정명").toString();
                            currentPhasingCode = processItemCostDetailJsonObject.get(0).get("공정코드") == null ? "" : processItemCostDetailJsonObject.get(0).get("공정코드").toString();
                        }
                        else{
                            return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                        }
                    }
                     **/
                }
            }
            /**
            if (gisungItemCostJsonObject.size() == 1){ // 입력하는 excel 데이터의 길이가 1개라면
                if(gisungItemCostJsonObject.get(0).get("공정명") != null && gisungItemCostJsonObject.get(0).get("공정코드") != null){
                    insertProcessItemCostDetailExcel(processItemCostDetailJsonObject, processId);
                    return proc.getMessageResult(true, "저장 완료");
                }
                else{
                    return proc.getMessageResult(false, "저장 실패");
                }
            }
            else{
                String currentTaskName = processItemCostDetailJsonObject.get(0).get("공정명").toString();
                String currentPhasingCode = processItemCostDetailJsonObject.get(0).get("공정코드").toString();
                int objectSize = processItemCostDetailJsonObject.size();
                List<JsonObject> processItemCostDetail = new ArrayList<>();
                for(int i=0 ; i < objectSize ; i++){
                    //아무것도 입력이 안된 라인인지 확인
                    if(processItemCostDetailJsonObject.get(0).get("공정명") == null || processItemCostDetailJsonObject.get(0).get("공정코드") == null){
                        if(processItemCostDetailJsonObject.size() == 1){
                            if(onlyOneIsFirst(processItemCostDetail)){
                                insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                                return proc.getMessageResult(true, "저장 완료");
                            }
                            else{
                                return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                            }
                        }
                        else{
                            processItemCostDetailJsonObject.remove(0);
                            continue;
                        }
                    }
                    // 같은 공정들인지 확인
                    else if( currentPhasingCode.equals(processItemCostDetailJsonObject.get(0).get("공정코드").toString()) && currentTaskName.equals(processItemCostDetailJsonObject.get(0).get("공정명").toString()) ){
                        processItemCostDetail.add(processItemCostDetailJsonObject.get(0));
                    }
                    // 다른 공정이 발생한 경우(지금까지의 공정을 DB에 저장)
                    else if( !currentPhasingCode.equals(processItemCostDetailJsonObject.get(0).get("공정코드").toString()) || !currentTaskName.equals(processItemCostDetailJsonObject.get(0).get("공정명").toString()) ){
                        if(onlyOneIsFirst(processItemCostDetail)){
                            insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                            processItemCostDetail = new ArrayList<>();
                            processItemCostDetail.add(processItemCostDetailJsonObject.get(0));
                            currentTaskName = processItemCostDetailJsonObject.get(0).get("공정명") == null ? "" : processItemCostDetailJsonObject.get(0).get("공정명").toString();
                            currentPhasingCode = processItemCostDetailJsonObject.get(0).get("공정코드") == null ? "" : processItemCostDetailJsonObject.get(0).get("공정코드").toString();
                        }
                        else{
                            return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                        }
                    }

                    if (processItemCostDetailJsonObject.size() == 1){
                        if(onlyOneIsFirst(processItemCostDetail)){
                            insertProcessItemCostDetailExcel(processItemCostDetail, processId);
                            return proc.getMessageResult(true, "저장 완료");
                        }
                        else{
                            return proc.getMessageResult(false, "저장 실패 : 같은 공정에 대표가 2개 이상 체크가 되어 있습니다.");
                        }
                    }
                    else{
                        processItemCostDetailJsonObject.remove(0);
                    }
                }
                return proc.getMessageResult(false, "저장 실패");
            }
             **/
            return proc.getMessageResult(false, "저장 실패");
        } catch(Exception e){
            System.out.println(e);
            return proc.getMessageResult(false, "저장 실패 : " + e);
        }
    }
    /**
    private boolean insertGisungItemCostDetailExcel(List<JsonObject> gisungItemCostDetail, Long processId){
        String taskName = gisungItemCostDetail.get(0).get("공정명") == null ? "" : gisungItemCostDetail.get(0).get("공정명").toString().replaceAll("^\"|\"$", "").trim();
        String phasingCode = gisungItemCostDetail.get(0).get("공정코드") == null ? "" : gisungItemCostDetail.get(0).get("공정코드").toString().replaceAll("^\"|\"$", "").trim();
        List<Long> processItemId = processItemRepository.findByProcessIdAndTaskNameAndPhasingCode(processId, taskName, phasingCode);
        String code = "";
        String name = "";
        BigDecimal count = new BigDecimal("0.00");
        String unit = "";
        BigDecimal unitPrice = new BigDecimal("0.00");;
        BigDecimal cost = new BigDecimal("0.00");;
        boolean isFirst = false;

        long userId = userInfo.getId();
        LocalDateTime writeDate =  LocalDateTime.now();

        if(processItemId.size() == 0){
            return false;
        }

        //기존에 저장 되어 있던 값들 삭제
        List<ProcessItemCostDetail> haveCostDetail = processItemCostDetailRepository.findByProcessItemId(processItemId.get(0));
        if(haveCostDetail.size() > 0){
            processItemCostDetailRepository.deleteByProcessItemId(processItemId.get(0));
        }

        for(int i=0 ; i < processItemCostDetail.size() ; i++){
            code = processItemCostDetail.get(i).get("코드") == null ? "" : processItemCostDetail.get(i).get("코드").toString().replaceAll("^\"|\"$", "").trim();
            name = processItemCostDetail.get(i).get("명") == null ? "" : processItemCostDetail.get(i).get("명").toString().replaceAll("^\"|\"$", "").trim();
            count = new BigDecimal( processItemCostDetail.get(i).get("값") == null ? "0.00" : processItemCostDetail.get(i).get("값").toString().replaceAll(",", "").replaceAll("^\"|\"$", "").trim() );
            unit = processItemCostDetail.get(i).get("단위") == null ? "" : processItemCostDetail.get(i).get("단위").toString().replaceAll("^\"|\"$", "").trim();
            unitPrice = new BigDecimal( processItemCostDetail.get(i).get("단가") == null ? "0.00" : processItemCostDetail.get(i).get("단가").toString().replaceAll(",", "").replaceAll("^\"|\"$", "").trim() );

            String isCheck = processItemCostDetail.get(i).get("대표") == null ? "" : processItemCostDetail.get(i).get("대표").toString().replaceAll("^\"|\"$", "").trim();
            if(isCheck.equals("o") || isCheck.equals("O") || isCheck.equals("v") || isCheck.equals("V") || isCheck.equals("0") ){
                isFirst = true;
            }
            else{
                isFirst = false;
            }

            cost = count.multiply(unitPrice).setScale(0, RoundingMode.DOWN);
            processItemCostDetailRepository.insertProcessItemCostDetailExcel(processItemId.get(0), code, name, count, unit, unitPrice, cost, isFirst, userId, writeDate, userId, writeDate);
        }

        //process_item 테이블도 업데이트 해야 화면에 정상적으로 출력 됨
        List<ProcessItemCostDetailDTO> processItemCostDetailDTOs = findProcessItemCostDetail(processItemId.get(0), "R");
        if(updateProcessItemComplexUnitPrice(processItemCostDetailDTOs)){
            return true;
        }
        else{
            return false;
        }
    }
     **/

    public List<JobSheetProcessItemDTO> getModelExchangeIdsByJobSheetProcessItem(String startDate, String endDate) {
        List<JobSheetProcessItemDTO> jobSheetProcessItemDTOs = null;
        try {
            jobSheetProcessItemDTOs =  jobSheetProcessItemDTODslRepository.getModelExchangeIdsByJobSheetProcessItem(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobSheetProcessItemDTOs;
    }

    public BigDecimal getPrevPaidCost(long projectId, String year, long workId, long gisungId) {
        BigDecimal prevPaidCost = new BigDecimal(BigInteger.ZERO);
        try {
            prevPaidCost = gisungWorkCostRepository.getPrevPaidCost(projectId, year, workId, gisungId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prevPaidCost;
    }

    public List<GisungProcessItemCostDetailDTO> findAllGisungIdByGisungProcessItemCostDestinct(Long gisungId) {
        return gisungProcessItemCostDetailDTODslRepository.findAllGisungIdByGisungProcessItemCostDestinct(gisungId);
    }

    public BigDecimal getGisungProcessItemCostDetailByCountSum(Long gisungId, String code) {
        return gisungProcessItemCostDetailRepository.getGisungProcessItemCostDetailByCountSum(gisungId, code).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getGisungProcessItemCostDetailByCostSum(Long gisungId, String code) {
        return gisungProcessItemCostDetailRepository.getGisungProcessItemCostDetailByCostSum(gisungId, code).orElse(BigDecimal.ZERO);
    }

    public JsonObject postGisungListExcelFile(GisungListExcelFileVO gisungListExcelFileVO) {
        try {
            GisungListExcelFile gisungListExcelFile = new GisungListExcelFile();
            gisungListExcelFile.setGisungListExcelFileAtAddGisungListExcelFile(gisungListExcelFileVO, userInfo);
            gisungListExcelFileRepository.save(gisungListExcelFile);

            return proc.getResult(true, gisungListExcelFile.getId(), "system.gisung_payment_service.post_gisung_list_excel_file");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public GisungListExcelFile findGisungListExcelFileByGisungId(long gisungId) {
        return gisungListExcelFileRepository.findGisungListExcelFileByGisungId(gisungId).orElseGet(GisungListExcelFile::new);
    }

    public List<ProcessItemCostDetailDTO> findCostDetailByCode(long processId, String code, long gisungId){
        List<ProcessItemCostDetailDTO> processItemCostDetail = processItemCostDslRepository.findCostDetailByCode(processId, code, gisungId);

        return processItemCostDetail;
    }

    /**
     * 기성 검증/비교 적용
     * @param gisungVO
     * @return
     */
    @Transactional
    public JsonObject putGisungTarget(GisungVO gisungVO) {
        Gisung savedGisung = gisungRepository.findById(gisungVO.getId()).orElseGet(Gisung::new);
        if (savedGisung.getId() == 0) {
            return proc.getResult(false, "system.gisung_service.not_exist_gisung");
        }

        try {
            // 기성 공정 검증/비교 결과 값 업데이트
            for (int i = 0; i < gisungVO.getGisungProcessItemIds().size(); i++) {
                GisungProcessItem savedGisungProcessItem = gisungProcessItemRepository.getById(gisungVO.getGisungProcessItemIds().get(i));

                // progressRate 진행률 합계 비교
                BigDecimal progressRate = savedGisungProcessItem.getCompareProgressRate();
                BigDecimal progressRateSum = progressRate.add(savedGisungProcessItem.getPaidProgressRate());
                if (progressRateSum.compareTo(new BigDecimal("100")) > 0)
                    return proc.getResult(false, "system.gisung_service.task_progress_rate_over_100");

                List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findGisungProcessItemCostDetail(savedGisungProcessItem.getId());
                if (gisungProcessItemCostDetailDTOs != null && gisungProcessItemCostDetailDTOs.size() > 0) {
                    for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetail : gisungProcessItemCostDetailDTOs) {
                        // progressCount 복합단가 수량 합계 비교
                        BigDecimal progressCount = gisungProcessItemCostDetail.getCompareProgressCount();
                        BigDecimal progressCountSum = progressCount.add(gisungProcessItemCostDetail.getPaidProgressCount());
                        System.out.println("gisungProcessItemCostDetail.getCode() : " + gisungProcessItemCostDetail.getCode() + " / savedGisungProcessItem.getId() : " + savedGisungProcessItem.getId() + " / progressCountSum : " + progressCountSum + " / gisungProcessItemCostDetail.getCount() : " + gisungProcessItemCostDetail.getCount() + "------------------");
                        if (progressCountSum.compareTo(gisungProcessItemCostDetail.getCount()) > 0)
                            return proc.getResult(false, "system.gisung_service.task_progress_count_over_100");
                    }
                }
            }

            // 기성 공정 검증/비교 결과 값 업데이트
            for (int i = 0; i < gisungVO.getGisungProcessItemIds().size(); i++) {
                GisungProcessItem savedGisungProcessItem = gisungProcessItemRepository.getById(gisungVO.getGisungProcessItemIds().get(i));
                savedGisungProcessItem.setGisungProcessItemCostAtUpdateGisung(savedGisungProcessItem.getCompareCost(), savedGisungProcessItem.getCompareProgressRate(), savedGisungProcessItem.getAddItem());

                // progressCount 복합단가 수량 값 업데이트
                List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs = gisungProcessItemCostDetailDTODslRepository.findGisungProcessItemCostDetail(savedGisungProcessItem.getId());
                if (gisungProcessItemCostDetailDTOs != null && gisungProcessItemCostDetailDTOs.size() > 0) {
                    for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                        // progressCount 복합단가 수량 합계 비교
                        GisungProcessItemCostDetail gisungProcessItemCostDetail = gisungProcessItemCostDetailRepository.findById(gisungProcessItemCostDetailDTO.getId()).orElseGet(GisungProcessItemCostDetail::new);
                        gisungProcessItemCostDetail.setGisungProcessItemCostDetailAtUpdateGisung(gisungProcessItemCostDetailDTO.getCompareProgressCount(), gisungProcessItemCostDetailDTO.getCompareProgressCost());
                    }
                }
            }

            return proc.getResult(true, gisungVO.getId(), "system.gisung_service.put_gisung");

        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public JsonObject findAllGisungIdByGisungProcessItemCostCompareResult(long gisungId) {
        return proc.getResult(true, gisungProcessItemCostDetailDTODslRepository.findAllGisungIdByGisungProcessItemCostCompareResult(gisungId));
    }

    public List<GisungProcessItemCostDetail> findGisungProcessItemDetailByGisungProcessItemId(long gisungProcessItemId, long gisungId){
        List<GisungProcessItemCostDetail> gisungProcessItemCostDetail = gisungProcessItemCostDetailRepository.findByGisungIdAndGisungProcessItemId(gisungId, gisungProcessItemId);

        return gisungProcessItemCostDetail;
    }

    /**
     * 기성 복합단가 코드별 수량 수정
     * @param gisungId
     * @param gisungProcessItemCostDetailDTOs
     * @return
     */
    public JsonObject putGisungProcessItemCostDetailCodes(Long gisungId, List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs) {
        List<ProcessItemCostDetailDTO> savedProcessItemCostDetailLists = new ArrayList<>();
        String code = "";

        try {
            for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetailDTO : gisungProcessItemCostDetailDTOs) {
                code = gisungProcessItemCostDetailDTO.getCode();
                System.out.println("code : " + code + "-------------------------------------------");
                GisungProcessItemCostDetail savedGisungProcessItemCostDetail = gisungProcessItemCostDetailRepository.findByGisungIdAndProcessItemIdAndCode(gisungId, gisungProcessItemCostDetailDTO.getId(), code).orElseGet(GisungProcessItemCostDetail::new);
                long gisungProcessItemId = savedGisungProcessItemCostDetail.getGisungProcessItem().getId();
                savedGisungProcessItemCostDetail.setGisungProcessItemCostDetailAtUpdateGisung(gisungProcessItemCostDetailDTO.getProgressCount(), gisungProcessItemCostDetailDTO.getProgressCost());

                BigDecimal sumCost = BigDecimal.ZERO;
                BigDecimal sumProgressRate = BigDecimal.ZERO;
                GisungProcessItem gisungProcessItem = gisungProcessItemRepository.findById(gisungProcessItemId).orElseGet(GisungProcessItem::new);
                List<GisungProcessItemCostDetail> gisungProcessItemCostDetails = findGisungProcessItemDetailByGisungProcessItemId(gisungProcessItemId, gisungId);
                for (GisungProcessItemCostDetail gisungProcessItemCostDetail : gisungProcessItemCostDetails) {
                    sumCost = sumCost.add(gisungProcessItemCostDetail.getProgressCost());
                    sumProgressRate = sumProgressRate.add(gisungProcessItemCostDetail.getProgressCount());
                }

                sumProgressRate = sumCost.divide(gisungProcessItem.getTaskCost(), 4, RoundingMode.HALF_UP);
                //sumProgressRate = sumProgressRate.multiply(new BigDecimal("0.01"));
                gisungProcessItem.setGisungProcessItemCostAtUpdateGisungCost(sumCost, sumProgressRate);
            }

            return proc.getResult(true, "system.gisung_service.post_gisung_process_item_cost_detail");
        } catch (Exception e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    public long getGisungProcessItemSumYearMonthCost(String year, String month) {
        return gisungProcessItemRepository.getGisungProcessItemSumYearMonthCost(year, month).orElse(0L);
    }
}
