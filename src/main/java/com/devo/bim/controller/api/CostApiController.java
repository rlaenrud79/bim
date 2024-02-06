package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.*;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.vo.*;
import com.devo.bim.repository.dsl.ProcessItemCostDslRepository;
import com.devo.bim.repository.dsl.VmProcessItemDTODslRepository;
import com.devo.bim.service.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/costApi")
@RequiredArgsConstructor
public class CostApiController extends AbstractController {
    private final CostService costService;
    private final ProcessCostService processCostService;
    private final ProcessService processService;
    private final ProcessItemCostDslRepository processItemCostDslRepository;
    private final VmProcessItemDTODslRepository vmProcessItemDTODslRepository;
    private final ExcelFormatterService excelFormatterService;

    private final GisungService gisungService;

    @PostMapping("/postCost")
    JsonObject postCost(@RequestBody CostVO costVO) {
        return costService.postCost(costVO.getName());
    }

    @PostMapping("/saveCostSnapShot")
    JsonObject saveCostSnapShot(@RequestBody CostSnapShotVO costSnapShotVO) {
        return costService.saveCostSnapShot(costSnapShotVO);
    }

    @PostMapping("/postSnapShotCost")
    JsonObject postSnapShotCost(@RequestBody CostSnapShotVO costSnapShotVO) {
        return costService.postSnapShotCost(costSnapShotVO);
    }

    @GetMapping("/getSnapShotCost")
    JsonObject getSnapShotCost(long costId, long costSnapShotId) {
        return costService.getCostSnapshot(costId, costSnapShotId);
    }

    @PostMapping("/getProcessItemCost")
    JsonObject getProcessItemCost(@RequestBody SearchProcessItemCostVO searchProcessItemCostVO) {
        //SearchProcessItemCostVO searchProcessItemCostVO = new SearchProcessItemCostVO();
        return processCostService.findProcessItemCostCurrentVersion(searchProcessItemCostVO);
    }

    @PostMapping("/getSubWorkList")
    JsonObject getSubWorkList(@RequestBody SearchProcessItemCostVO searchProcessItemCostVO) {
        return processCostService.findSubWorkList(searchProcessItemCostVO);
    }

    @PostMapping("/getProcessItemCostWithChildren")
    JsonObject getProcessItemCostWithChildren(@RequestBody SearchProcessItemCostVO searchProcessItemCostVO) {
        return processCostService.findProcessItemCostWithChildrenCurrentVersion(searchProcessItemCostVO);
    }

    @PostMapping("/getProcessItemsCost")
    List<JsonObject> getProcessItemCostsByPhasingCodes(@RequestBody JsonObject phasingCodes) {
        return processCostService.findProcessItemsCostCurrentVersionByPhasingCodes(phasingCodes.get("phasingCodes"));
    }

    @PostMapping("/getProcessItemCostForPass")
    JsonObject getProcessItemCostForPass(@RequestBody SearchProcessItemCostVO searchProcessItemCostVO) {
    	return processCostService.findProcessItemCostForPass(searchProcessItemCostVO);
    }

    @GetMapping("/getProcessItemCostDetail/{processItemId}/{rowState}")
    JsonObject getProcessItemCostDetail(@PathVariable Long processItemId, @PathVariable String rowState) {
        return proc.getResult(true, processCostService.findProcessItemCostDetail(processItemId, rowState));
    }

    @PostMapping("/saveProcessItemCostDetail/{processItemId}")
    JsonObject saveProcessItemCostDetail(@PathVariable Long processItemId, @RequestBody List<ProcessItemCostDetailDTO> processItemCostDetailVOs) {
        return processCostService.procProcessItemDetails(processItemId, processItemCostDetailVOs, "save");
    }

    @PostMapping("/saveProcessItemCostDetailByExcel")
    JsonObject saveProcessItemCostDetailByExcel(@RequestBody List<JsonObject> processItemCostDetailJsonObject) {
        return processCostService.procProcessItemDetailsExcel(processItemCostDetailJsonObject);
    }

    @GetMapping("/exportCostXlsx")
    public void exportCostXlsx(HttpServletResponse response) throws Exception {
        String fileName = "Cost";
        String sheetName = "Cost";

        List<VmProcessItemDTO> processItemCost = processCostService.listToTreeItems( vmProcessItemDTODslRepository.findProcessItemCostCurrentVersion(userInfo.getProjectId(), new SearchProcessItemCostVO()) );

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.PROCESS_ITEM_COST, fileName, sheetName, processItemCost, response);
    }

    @PostMapping("/deleteProcessItemCostDetail/{processItemId}")
    JsonObject deleteProcessItemCostDetail(@PathVariable Long processItemId,@RequestBody List<ProcessItemCostDetailDTO> processItemCostDetailVOs) {
        return processCostService.procProcessItemDetails(processItemId, processItemCostDetailVOs,  "delete");
    }

    @PostMapping("/setProcessItemBookmark/{processItemId}/{isBookmark}")
    JsonObject setProcessItemBookmark(@PathVariable Long processItemId, @PathVariable boolean isBookmark) {
        return processCostService.setProcessItemBookmark(processItemId, isBookmark);
    }

    @GetMapping("/getProcessItemCostDetailBookmark")
    JsonObject getProcessItemCostDetailBookmark() {
        return proc.getResult(true, processCostService.findProcessItemCostDetailBookmark());
    }

    @GetMapping("/getProcessItemCostPay/{processItemId}")
    JsonObject getProcessItemCostPay(@PathVariable Long processItemId) {
        return proc.getResult(true, processCostService.findProcessItemCostPayLatestEditable(processItemId));
    }

    @PostMapping("/postPaidCost/{processItemId}")
    JsonObject postPaidCost(@PathVariable Long processItemId, @RequestBody ProcessItemCostPayDTO processItemCostPayDTO) {
        return processCostService.postPaidCost(processItemId,processItemCostPayDTO);
    }

    @PutMapping("/putPaidCost/{processItemCostPayId}")
    JsonObject putPaidCost(@PathVariable Long processItemCostPayId, @RequestBody ProcessItemCostPayDTO processItemCostPayDTO) {
        return processCostService.putPaidCost(processItemCostPayId,processItemCostPayDTO);
    }

    @PostMapping("/processItemCostPayAll/{processInfoId}")
    JsonObject postProcessItemCostPayAll(@PathVariable Long processInfoId, @RequestBody PaidCostAllDTO paidCostAllDTO) {
        return processCostService.postProcessItemCostPayAll(processInfoId, paidCostAllDTO);
    }

    @PutMapping("/putFormulaEvaluation")
    JsonObject putFormulaEvaluation(@RequestBody ProcessItemCostDTO processItemCostDTO) {
        return processCostService.putFormulaEvaluation(processItemCostDTO, userInfo.getProjectId());
    }

    @PutMapping("/putProcessItemForCost/{colId}")
    JsonObject putProcessItemForCost(@PathVariable String colId, @RequestBody ProcessItemCostDTO processItemCostDTO) {
        return processCostService.putProcessItemForCost(colId,processItemCostDTO);
    }

    @GetMapping("/getProcessItemWorkerDeviceDetail/{processItemId}")
    JsonObject getProcessItemWorkerDeviceDetail(@PathVariable Long processItemId) {
        return proc.getResult(true, processService.getProcessItemWorkerDeviceDetail(processItemId));
    }

    @PostMapping("/saveGisungItemCostByExcel")
    JsonObject saveGisungItemCostByExcel(@RequestBody List<JsonObject> gisungItemCostJsonObject) {
        return gisungService.procGisungItemCostExcel(gisungItemCostJsonObject);
    }
    @PostMapping("/postProcessItemCostDetailExcelFileExcel")
    public JsonObject postProcessItemCostDetailExcelFileExcel() {
        return processCostService.postProcessItemCostDetailExcelFile();
    }

    @GetMapping("/exportProcessItemCostDetailPython")
    public JsonObject exportGisungComparePython(HttpServletResponse response) throws Exception {
        String command = "python3 /home/projectworks/python/cost_detail_main.py > /home/projectworks/python/logs/cost_detail_main.log";

        try {
            // Python 파일 경로
            String pythonFile = "./python/cost_detail_main.py";
            //String path = System.getProperty("user.dir");
            //System.out.println(path);

            // Python 인터프리터와 실행할 파일을 명령어로 설정
            //ProcessBuilder pb = new ProcessBuilder("python3", pythonFile);
            ProcessBuilder pb = new ProcessBuilder("python3","/home/projectworks/python/cost_detail_main.py");
            pb.redirectErrorStream(true);

            File workingDir = new File("/home/projectworks");
            //pb.directory(workingDir);

            // 외부 프로세스 실행
            Process process = pb.redirectOutput(new File(workingDir, "python/logs/cost_detail_output.log")).start();

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
            return proc.getResult(true, "system.process_cost_service.make_process_item_cost_detail_excel_file");
            //} catch (IOException | InterruptedException e) {
        } catch (IOException e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}