package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.entity.GisungProcessItemCostDetail;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.ProcessItemCostDslRepository;
import com.devo.bim.repository.dsl.VmProcessItemDTODslRepository;
import com.devo.bim.repository.spring.GisungProcessItemCostDetailRepository;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/gisungApi")
@RequiredArgsConstructor
public class GisungApiController extends AbstractController {
    private final GisungService gisungService;
    private final ExcelFormatterService excelFormatterService;
    private final ProcessCostService processCostService;
    private final GisungContractManagerService gisungContractManagerService;
    private final GisungPaymentService gisungPaymentService;
    private final ProcessItemCostDslRepository processItemCostDslRepository;
    private final GisungProcessItemCostDetailRepository gisungProcessItemCostDetailRepository;
    private final VmProcessItemDTODslRepository vmProcessItemDTODslRepository;

    @PostMapping("/postGisung")
    public JsonObject postGisung(@RequestBody GisungVO gisungVO) {
        return gisungService.postGisung(gisungVO);
    }

    @PostMapping("/putGisung")
    public JsonObject putGisung(@RequestBody GisungVO gisungVO) {
        return gisungService.putGisung(gisungVO);
    }

    @DeleteMapping("/deleteGisung")
    public JsonObject deleteGisung(long id) {
        return gisungService.deleteGisung(id);
    }

    @DeleteMapping("/deleteSelGisung")
    public JsonObject deleteSelGisung(@RequestBody List<GisungVO> gisungVO) {
        return gisungService.deleteSelGisung(gisungVO);
    }

    @PostMapping("/postGisungProcessItem")
    public JsonObject postGisungProcessItem(@RequestBody GisungProcessItemVO gisungProcessItemVO) {
        return gisungService.postGisungProcessItem(gisungProcessItemVO);
    }

    @PutMapping("/putGisungProcessItem")
    public JsonObject putGisungProcessItem(@RequestBody GisungProcessItemVO gisungProcessItemVO) {
        return gisungService.putGisungProcessItem(gisungProcessItemVO);
    }

    @DeleteMapping("/deleteGisungProcessItem")
    public JsonObject deleteGisungProcessItem(long id) {
        return gisungService.deleteGisungProcessItem(id);
    }

    @PostMapping("/postGisungItem")
    public JsonObject postGisungItem(@RequestBody GisungItemVO gisungItemVO) {
        return gisungService.postGisungItem(gisungItemVO);
    }

    @PutMapping("/putGisungItem")
    public JsonObject putGisungItem(@RequestBody GisungItemVO gisungItemVO) {
        return gisungService.putGisungItem(gisungItemVO);
    }

    @GetMapping("/getGisungProcessItemCostDetail/{gisungProcessItemId}")
    JsonObject getGisungProcessItemCostDetail(@PathVariable Long gisungProcessItemId) {
        return proc.getResult(true, gisungService.findGisungProcessItemCostDetail(gisungProcessItemId));
    }

    @PostMapping("/saveGisungProcessItemCostDetail/{gisungProcessItemId}")
    JsonObject saveGisungProcessItemCostDetail(@PathVariable Long gisungProcessItemId, @RequestBody List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs) {
        return gisungService.putGisungProcessItemCostDetails(gisungProcessItemId, gisungProcessItemCostDetailDTOs);
    }

    @PostMapping("/postGisungCover")
    public JsonObject postGisungCover(@RequestBody GisungCoverVO gisungCoverVO) {
        return gisungService.postGisungCover(gisungCoverVO);
    }

    @PutMapping("/putGisungCover")
    public JsonObject putGisungCover(@RequestBody GisungCoverVO gisungCoverVO) {
        return gisungService.putGisungCover(gisungCoverVO);
    }

    @GetMapping("/getGisungCover")
    public JsonObject getGisungCover(long id) {
        return gisungService.getGisungCover(id);
    }

    @DeleteMapping("/deleteGisungCover")
    public JsonObject deleteGisungCover(long id) {
        return gisungService.deleteGisungCover(id);
    }

    @PostMapping("/postGisungTable")
    public JsonObject postGisungTable(@RequestBody GisungTableVO gisungTableVO) {
        return gisungService.postGisungTable(gisungTableVO);
    }

    @PutMapping("/putGisungTable")
    public JsonObject putGisungTable(@RequestBody GisungTableVO gisungTableVO) {
        return gisungService.putGisungTable(gisungTableVO);
    }

    @GetMapping("/getGisungTable")
    public JsonObject getGisungTable(long id) {
        return gisungService.getGisungTable(id);
    }

    @DeleteMapping("/deleteGisungTable")
    public JsonObject deleteGisungTable(long id) {
        return gisungService.deleteGisungTable(id);
    }

    @PostMapping("/putGisungTarget")
    public JsonObject putGisungTarget(@RequestBody GisungVO gisungVO) {
        return gisungService.putGisungTarget(gisungVO);
    }

    @GetMapping("/exportGisungCostTotalXlsx/{id}")
    public void exportGisungCostTotalXlsx(@PathVariable Long id, HttpServletResponse response) throws Exception {
        String fileName = "Cost";
        String sheetName = "Cost";

        List<VmProcessItemDTO> processItemCost = processCostService.listToTreeItems( vmProcessItemDTODslRepository.findGisungProcessItemCostCurrentVersion(userInfo.getProjectId(), new SearchProcessItemCostVO()) );

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_TOTAL, fileName, sheetName, processItemCost, response);

    }

    @GetMapping("/exportGisungCostTotalXlsxPython/{id}")
    public JsonObject exportGisungCostTotalXlsxPython(@PathVariable Long id, HttpServletResponse response) throws Exception {
        String command = "python3 /home/projectworks/python/excel_main.py > /home/projectworks/python/logs/excel_main.log";

        try {
            // Python 파일 경로
            String pythonFile = "./python/excel_main.py";
            //String path = System.getProperty("user.dir");
            //System.out.println(path);

            // Python 인터프리터와 실행할 파일을 명령어로 설정
            //ProcessBuilder pb = new ProcessBuilder("python3", pythonFile);
            ProcessBuilder pb = new ProcessBuilder("python3","/home/projectworks/python/excel_main.py").inheritIO();
            pb.redirectErrorStream(true);

            File workingDir = new File("/home/projectworks");
            //pb.directory(workingDir);

            // 외부 프로세스 실행
            Process process = pb.redirectOutput(new File(workingDir, "python/logs/gisung_excel_output.log")).start();

            System.out.println(".........start   process.........");
            // 외부 프로세스의 출력 결과를 읽기 위한 스트림 설정 (옵션)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("........end   process.......");

            /**
            // 외부 프로세스의 종료를 기다림
            int exitCode = process.waitFor();
            System.out.println("Python process exited with code: " + exitCode);

            if (exitCode == 0) {
                return proc.getResult(true, "system.gisung_service.make_excel_file");
            } else {
                return proc.getResult(false, "system.gisung_service.not_make_excel_file");
            }
             **/
            return proc.getResult(true, "system.gisung_service.make_excel_file");
        //} catch (IOException | InterruptedException e) {
        } catch (IOException e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }

    @GetMapping("/exportGisungCostXlsx/{id}")
    public void exportGisungCostXlsx(@PathVariable Long id, HttpServletResponse response) throws Exception {
        String fileName = "GisungCost";
        String sheetName = "GisungCost";

        List<VmGisungProcessItemDTO> vmGisungProcessItems = gisungService.findVmGisungProcessItemByGisungIdListDTOs(id, "");
        String compareResult = "";
        List<VmGisungProcessItemDTO> savedVmGisungProcessItemLists = new ArrayList<>();
        long parentId = 0;
        if (vmGisungProcessItems.size() > 0) {
            parentId = 0;
            Integer i = 0;

            String prevCate1 = "";
            String prevCate2 = "";
            String prevCate3 = "";
            String prevCate4 = "";
            String prevCate5 = "";
            for (VmGisungProcessItemDTO vmGisungProcessItem : vmGisungProcessItems) {
                if ((vmGisungProcessItem.getCate1() != null && !prevCate1.equals(vmGisungProcessItem.getCate1()))
                         || (vmGisungProcessItem.getCate2() != null && !prevCate2.equals(vmGisungProcessItem.getCate2()))
                         || (vmGisungProcessItem.getCate3() != null && !prevCate3.equals(vmGisungProcessItem.getCate3()))
                         || (vmGisungProcessItem.getCate4() != null && !prevCate4.equals(vmGisungProcessItem.getCate4()))
                         || (vmGisungProcessItem.getCate5() != null && !prevCate5.equals(vmGisungProcessItem.getCate5()))) {
                    VmGisungProcessItemDTO savedVmGisungProcessItem = new VmGisungProcessItemDTO(vmGisungProcessItem.getId()
                            , vmGisungProcessItem.getGisungId()
                            , vmGisungProcessItem.getWorkId()
                            , ""
                            , vmGisungProcessItem.getTaskName()
                            , vmGisungProcessItem.getComplexUnitPrice()
                            , vmGisungProcessItem.getTaskCost()
                            , vmGisungProcessItem.getPaidCost()
                            , vmGisungProcessItem.getPaidProgressRate()
                            , vmGisungProcessItem.getBeforeProgressRate()
                            , vmGisungProcessItem.getAfterProgressRate()
                            , vmGisungProcessItem.getTodayProgressRate()
                            , vmGisungProcessItem.getBeforeProgressAmount()
                            , vmGisungProcessItem.getAfterProgressAmount()
                            , vmGisungProcessItem.getTodayProgressAmount()
                            , vmGisungProcessItem.getCost()
                            , vmGisungProcessItem.getProgressRate()
                            , vmGisungProcessItem.getCompareCost()
                            , vmGisungProcessItem.getCompareProgressRate()
                            , vmGisungProcessItem.getCompareResult()
                            , vmGisungProcessItem.getAddItem()
                            , vmGisungProcessItem.getCate1()
                            , vmGisungProcessItem.getCate2()
                            , vmGisungProcessItem.getCate3()
                            , vmGisungProcessItem.getCate4()
                            , vmGisungProcessItem.getCate5()
                            , vmGisungProcessItem.getCate6()
                            , vmGisungProcessItem.getCate1Name()
                            , vmGisungProcessItem.getCate2Name()
                            , vmGisungProcessItem.getCate3Name()
                            , vmGisungProcessItem.getCate4Name()
                            , vmGisungProcessItem.getCate5Name()
                            , vmGisungProcessItem.getCate6Name());
                    savedVmGisungProcessItemLists.add(savedVmGisungProcessItem);
                    i = i + 1;
                }

                if (vmGisungProcessItem.getCompareResult() != null && vmGisungProcessItem.getCompareResult().equals("AP")) {
                    compareResult = "AP";
                }
                VmGisungProcessItemDTO savedVmGisungProcessItem = new VmGisungProcessItemDTO(vmGisungProcessItem.getId()
                        , vmGisungProcessItem.getGisungId()
                        , vmGisungProcessItem.getWorkId()
                        , vmGisungProcessItem.getPhasingCode()
                        , vmGisungProcessItem.getTaskName()
                        , vmGisungProcessItem.getComplexUnitPrice()
                        , vmGisungProcessItem.getTaskCost()
                        , vmGisungProcessItem.getPaidCost()
                        , vmGisungProcessItem.getPaidProgressRate()
                        , vmGisungProcessItem.getBeforeProgressRate()
                        , vmGisungProcessItem.getAfterProgressRate()
                        , vmGisungProcessItem.getTodayProgressRate()
                        , vmGisungProcessItem.getBeforeProgressAmount()
                        , vmGisungProcessItem.getAfterProgressAmount()
                        , vmGisungProcessItem.getTodayProgressAmount()
                        , vmGisungProcessItem.getCost()
                        , vmGisungProcessItem.getProgressRate()
                        , vmGisungProcessItem.getCompareCost()
                        , vmGisungProcessItem.getCompareProgressRate()
                        , vmGisungProcessItem.getCompareResult()
                        , vmGisungProcessItem.getAddItem()
                        , vmGisungProcessItem.getCate1()
                        , vmGisungProcessItem.getCate2()
                        , vmGisungProcessItem.getCate3()
                        , vmGisungProcessItem.getCate4()
                        , vmGisungProcessItem.getCate5()
                        , vmGisungProcessItem.getCate6()
                        , vmGisungProcessItem.getCate1Name()
                        , vmGisungProcessItem.getCate2Name()
                        , vmGisungProcessItem.getCate3Name()
                        , vmGisungProcessItem.getCate4Name()
                        , vmGisungProcessItem.getCate5Name()
                        , vmGisungProcessItem.getCate6Name());
                savedVmGisungProcessItemLists.add(savedVmGisungProcessItem);
                prevCate1 = vmGisungProcessItem.getCate1();
                prevCate2 = vmGisungProcessItem.getCate2();
                prevCate3 = vmGisungProcessItem.getCate3();
                prevCate4 = vmGisungProcessItem.getCate4();
                prevCate5 = vmGisungProcessItem.getCate5();
            }
        }

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST, fileName, sheetName, savedVmGisungProcessItemLists, response);
    }

    @GetMapping("/exportGisungGcodeXlsx/{id}")
    public void exportGisungGcodeXlsx(@PathVariable Long id, HttpServletResponse response) throws Exception {
        String fileName = "GisungGcode";
        String sheetName = "GisungGcode";

        List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetails = gisungService.findAllGisungIdByGisungProcessItemCostDestinct(id);
        List<GisungProcessItemCostDetailDTO> savedGisungProcessItemCostDetailss = new ArrayList<>();
        if (gisungProcessItemCostDetails.size() > 0) {
            for (GisungProcessItemCostDetailDTO gisungProcessItemCostDetail : gisungProcessItemCostDetails) {
                GisungProcessItemCostDetailDTO savedGisungProcessItem = new GisungProcessItemCostDetailDTO(gisungProcessItemCostDetail.getCode()
                        , gisungService.getGisungProcessItemCostDetailByCountSum(id, gisungProcessItemCostDetail.getCode())
                        , gisungService.getGisungProcessItemCostDetailByCostSum(id, gisungProcessItemCostDetail.getCode()));
                savedGisungProcessItemCostDetailss.add(savedGisungProcessItem);
            }
        }

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.GISUNG_PROCESS_ITEM_COST_DETAIL_GCODE, fileName, sheetName, savedGisungProcessItemCostDetailss, response);
    }

    @PostMapping("/postGisungContractManager")
    public JsonObject postGisungContractManager(@RequestBody GisungContractManagerVO gisungContractManagerVO) {
        return gisungContractManagerService.procGisungContractManager(gisungContractManagerVO);
    }

    @PutMapping("/putGisungContractManager")
    public JsonObject putGisungContractManager(@RequestBody GisungContractManagerVO gisungContractManagerVO) {
        return gisungContractManagerService.putGisungContractManager(gisungContractManagerVO);
    }

    @DeleteMapping("/deleteGisungContractManager")
    public JsonObject deleteGisungContractManager(long id) {
        return gisungContractManagerService.deleteGisungContractManager(id);
    }

    @PostMapping("/postGisungPayment")
    public JsonObject postGisungPayment(@RequestBody GisungPaymentVO gisungPaymentVO) {
        return gisungPaymentService.postGisungPayment(gisungPaymentVO);
    }

    @DeleteMapping("/deleteGisungPayment")
    public JsonObject deleteGisungPayment(long id) {
        return gisungPaymentService.deleteGisungPayment(id);
    }

    @PutMapping("/putGisungPayment")
    public JsonObject putGisungPayment(@RequestBody GisungPaymentVO gisungPaymentVO) {
        return gisungPaymentService.putGisungPayment(gisungPaymentVO);
    }

    @GetMapping("/getGisungContractManagers")
    JsonObject getGisungContractManagers() {
        // 기성 계약자 정보 관리 리스트
        return gisungContractManagerService.findGisungContractManagerDTOs();
    }

    @PostMapping("/postGisungListExcel")
    public JsonObject postGisungListExcel(@RequestBody GisungListExcelFileVO gisungListExcelFileVO) {
        return gisungService.postGisungListExcelFile(gisungListExcelFileVO);
    }

    @PostMapping("/getGisungProcessItemCostCodeAP")
    JsonObject getGisungProcessItemCostCodeAP(@RequestBody GisungVO gisungVO) {
        return gisungService.findAllGisungIdByGisungProcessItemCostCompareResult(gisungVO.getId());
    }

    @PostMapping("/getProcessItemCostSearchCode")
    JsonObject getProcessItemCostSearchCode(@RequestBody SearchProcessItemCostVO searchProcessItemCostVO) {
        List<Long> processItemIds = new ArrayList<>();
        if (searchProcessItemCostVO.getCode() != null && !searchProcessItemCostVO.getCode().equals("")) {
            List<GisungProcessItemCostDetail> gisungProcessItemCostDetails = gisungProcessItemCostDetailRepository.findByGisungIdAndCode(searchProcessItemCostVO.getId(), searchProcessItemCostVO.getCode());
            for (GisungProcessItemCostDetail gisungProcessItemCostDetail : gisungProcessItemCostDetails) {
                processItemIds.add(gisungProcessItemCostDetail.getProcessItem().getId());
            }
        }

        JsonObject result = new JsonObject();
        if (searchProcessItemCostVO.getIsMinCost()) {
            if (processItemIds.size() > 0) {
                result = processCostService.findProcessItemCostSearchCodeJson(searchProcessItemCostVO, processItemIds);
            } else {
                result = processCostService.findProcessItemSearch(searchProcessItemCostVO);
            }
        } else {
            result = processCostService.findProcessItemCostSearchCodeJson(searchProcessItemCostVO, processItemIds);
        }
        return result;
    }

    @PostMapping("/saveGisungProcessItemCostDetailProgressCount/{gisungId}")
    JsonObject saveGisungProcessItemCostDetailProgressCount(@PathVariable Long gisungId, @RequestBody List<GisungProcessItemCostDetailDTO> gisungProcessItemCostDetailDTOs) {
        return gisungService.putGisungProcessItemCostDetailCodes(gisungId, gisungProcessItemCostDetailDTOs);
    }

    @GetMapping("/exportGisungComparePython/{id}")
    public JsonObject exportGisungComparePython(@PathVariable Long id, HttpServletResponse response) throws Exception {
        String command = "python3 /home/projectworks/python/gisung_compare_main.py " + id + " > /home/projectworks/python/logs/gisung_compare_main.log";

        try {
            // Python 파일 경로
            String pythonFile = "./python/gisung_compare_main.py";
            //String path = System.getProperty("user.dir");
            //System.out.println(path);

            // Python 인터프리터와 실행할 파일을 명령어로 설정
            //ProcessBuilder pb = new ProcessBuilder("python3", pythonFile);
            ProcessBuilder pb = new ProcessBuilder("python3","/home/projectworks/python/gisung_compare_main.py", id.toString()).inheritIO();
            //ProcessBuilder pb = new ProcessBuilder("python3", "/home/projectworks/python/gisung_compare_main.py", id.toString());
            pb.redirectErrorStream(true);

            File workingDir = new File("/home/projectworks");
            //pb.directory(workingDir);

            // 외부 프로세스 실행
            Process process = pb.redirectOutput(new File(workingDir, "python/logs/compare_output.log")).start();

            System.out.println(".........start   process.........");
            // 외부 프로세스의 출력 결과를 읽기 위한 스트림 설정 (옵션)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("........end   process.......");

            /**
             // 외부 프로세스의 종료를 기다림
             int exitCode = process.waitFor();
             System.out.println("Python process exited with code: " + exitCode);

             if (exitCode == 0) {
             return proc.getResult(true, "system.gisung_service.make_excel_file");
             } else {
             return proc.getResult(false, "system.gisung_service.not_make_excel_file");
             }
             **/
            return proc.getResult(true, "system.gisung_service.make_gisung_compare");
            //} catch (IOException | InterruptedException e) {
        } catch (IOException e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
