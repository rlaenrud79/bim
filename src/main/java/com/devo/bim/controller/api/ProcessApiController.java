package com.devo.bim.controller.api;

import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.ProcessDTO;
import com.devo.bim.model.entity.ProcessItem;
import com.devo.bim.model.enumulator.ExcelDownloadCaseType;
import com.devo.bim.model.vo.*;
import com.devo.bim.service.ExcelFormatterService;
import com.devo.bim.service.ProcessService;
import com.devo.bim.service.ProjectService;
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
@RequestMapping("/processApi")
@RequiredArgsConstructor
public class ProcessApiController extends AbstractController {

    private final ProjectService projectService;
    private final ProcessService processService;
    private final ExcelFormatterService excelFormatterService;

    @GetMapping("/ganttDataInit")
    public JsonObject ganttDataInit () {
        return processService.getGanttDataInit();
    }

    @GetMapping("/ganttData")
    public JsonObject getGanttData (long processId) {
        return processService.getGanttData(processId);
    }

    @PostMapping("/ganttData")
    public JsonObject postGanttData (@RequestBody TaskPostPutVO taskPostPutVO) {
        return processService.postGanttData(taskPostPutVO);
    }

    @PutMapping("/ganttData")
    public JsonObject putGanttData (@RequestBody TaskPostPutVO taskPutVO) {
        return processService.putGanttData(taskPutVO);
    }

    @PutMapping("/ganttDataAfterAutoSchedule")
    public JsonObject putGanttDataAfterAutoSchedule (@RequestBody TaskVO taskVO) {
        return processService.putGanttDataAfterAutoSchedule(taskVO.getProcessItems());
    }

    @DeleteMapping("/ganttData")
    public JsonObject deleteGanttData (@RequestBody TaskPostPutVO taskPostPutVO) {
        return processService.deleteGanttData(taskPostPutVO.getEntity(), taskPostPutVO.getId());
    }

    /**
     * 공정파일 업로드
     * @param uploadFileName
     * @return
     */
    @PostMapping("/postProcessFileProcessInfo")
    public JsonObject postProcessFileProcessInfo(String uploadFileName){
        return processService.postProcessInfo(uploadFileName);
    }

    /**
     * 공정파일 processItems 업로드
     * @param taskVO
     * @return
     */
    @PostMapping("/postProcessFileProcessItems")
    public JsonObject postProcessFileProcessItems(@RequestBody TaskVO taskVO){
        return processService.postProcessItems(taskVO.getNewProcessInfoId(), taskVO.getProcessItems());
    }

    @PutMapping("/putIsCurrentVersion")
    public JsonObject putIsCurrentVersion(long newProcessInfoId, boolean isAddNewFile){
        return processService.putIsCurrentVersion(newProcessInfoId, isAddNewFile);
    }

    @PutMapping("/putProcessItemParentId")
    public JsonObject putProcessItemParentId(long newProcessInfoId){
        return processService.updateProcessItemParentId(newProcessInfoId);
    }

    @PutMapping("/putProcessItemLinkPredecessorId")
    public JsonObject putProcessItemLinkPredecessorId(long newProcessInfoId){
        return processService.updateProcessItemLinkPredecessorId(newProcessInfoId);
    }

    @PutMapping("/putProcessItems")
    public JsonObject putProcessItems(@RequestBody TaskVO taskVO){
        return processService.updateProcessItems(taskVO);
    }

    @GetMapping("/getUseWorkAll")
    public JsonObject getUseWorkAll(){
        return processService.getUseWorkAll();
    }

    /**
     * 엑셀 다운로드
     * @param response
     * @throws Exception
     */
    @GetMapping("/exportProcessExcel")
    public void exportProcessExcel(HttpServletResponse response) throws Exception {

        String fileName = "process";
        String sheetName = projectService.findById().getName();

        List<ProcessDTO> processDTOs = processService.findProcessDTOs();

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.PROCESS, fileName, sheetName, processDTOs, response);
    }

    /**
     * 새버전생성
     * @param taskVO
     * @return
     */
    @PostMapping("/newVersion")
    public JsonObject newVersion(@RequestBody TaskVO taskVO){
        return processService.postNewVersion(taskVO.getDescription());
    }

    /**
     * 코드 검증
     * @return
     */
    @PutMapping("/putCodeValidate")
    public JsonObject putCodeValidate(){
        return processService.validateCode(userInfo.getProjectId());
    }

    @GetMapping("/getCodeValidateStatus")
    public JsonObject getCodeValidateStatus(){
        return processService.getCodeValidateStatusCurrentProcessInfo();
    }

    @GetMapping("/resultCodeValidationExcel")
    public void resultCodeValidationExcel(@ModelAttribute SearchCodeValidationVO searchCodeValidationVO
            , HttpServletResponse response) throws Exception {

        String fileName = "CodeValidationResult";
        String sheetName = "Result";

        searchCodeValidationVO.setProjectId(userInfo.getProjectId());

        List<ProcessItem> processItems = processService.findCurrentVersionTaskListByProjectId(searchCodeValidationVO);

        excelFormatterService.ProcMakeAndDownLoadExcelFile(ExcelDownloadCaseType.PHASING_CODE_VALIDATION_RESULT, fileName, sheetName, processItems, response);
    }

    @GetMapping("/exchangeIds/{phasingCode}")
    public JsonObject exchangeIds (@PathVariable String phasingCode) {
        return processService.findTaskExchangeIdsByPhasingCode(phasingCode);
    }

    @PostMapping("/getProcessCategory")
    JsonObject getProcessCategory(@RequestBody SearchProcessItemCategoryVO searchProcessItemCategoryVO) {
        return processService.findProcessCategory(searchProcessItemCategoryVO);
    }

    @PostMapping("/postProcessItem")
    public JsonObject postProcessItem(@RequestBody ProcessItemVO processItemVO) {
        return processService.postProcessItem(processItemVO);
    }

    @PutMapping("/putProcessItem")
    public JsonObject putProcessItem(@RequestBody ProcessItemVO processItemVO) {
        return processService.putProcessItem(processItemVO);
    }

    @DeleteMapping("/deleteProcessItem")
    public JsonObject deleteProcessItem (long cateNo, String code, long id) {
        return processService.deleteProcessItem(cateNo, code, id);
    }

    @PutMapping("/putProcessItemDisplay")
    public JsonObject putProcessItemDisplay(@RequestBody ProcessItemVO processItemVO) {
        return processService.putProcessItemDisplay(processItemVO);
    }

    @PostMapping("/postProcessItemExcelFileExcel")
    public JsonObject postProcessItemExcelFileExcel() {
        return processService.postProcessItemExcelFile();
    }

    @GetMapping("/exportProcessItemPython")
    public JsonObject exportProcessItemPython(HttpServletResponse response) throws Exception {
        String command = "python3 /home/projectworks/python/process_main.py > /home/projectworks/python/logs/process_main.log";

        try {
            // Python 파일 경로
            String pythonFile = "./python/process_main.py";
            //String path = System.getProperty("user.dir");
            //System.out.println(path);

            // Python 인터프리터와 실행할 파일을 명령어로 설정
            //ProcessBuilder pb = new ProcessBuilder("python3", pythonFile);
            ProcessBuilder pb = new ProcessBuilder("python3","/home/projectworks/python/process_main.py");
            pb.redirectErrorStream(true);

            File workingDir = new File("/home/projectworks");
            //pb.directory(workingDir);

            // 외부 프로세스 실행
            Process process = pb.redirectOutput(new File(workingDir, "python/logs/process_output.log")).start();

            System.out.println(".........process_main start   process.........");
            // 외부 프로세스의 출력 결과를 읽기 위한 스트림 설정 (옵션)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("........process_main end   process.......");

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
            return proc.getResult(true, "system.process_service.make_process_item_excel_file");
            //} catch (IOException | InterruptedException e) {
        } catch (IOException e) {
            return proc.getMessageResult(false, e.getMessage());
        }
    }
}
