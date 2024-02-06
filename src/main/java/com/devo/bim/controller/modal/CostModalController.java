package com.devo.bim.controller.modal;

import com.devo.bim.component.Utils;
import com.devo.bim.controller.view.AbstractController;
import com.devo.bim.model.dto.ProcessItemCostDTO;
import com.devo.bim.model.dto.ProcessItemCostPayDTO;
import com.devo.bim.model.entity.*;
import com.devo.bim.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/costModal")
@RequiredArgsConstructor
public class CostModalController extends AbstractController {
    private final ProcessCostService processCostService;

    @GetMapping("/costDetail/{processItemId}")
    public String costDetail(@PathVariable Long processItemId, Model model) {
        ProcessItem processItem = processCostService.findProcessItemById(processItemId);
        long jobSheetProcessItemCount = processCostService.getJobSheetProcessItemCount(processItem.getPhasingCode(), processItemId);
        model.addAttribute("isBookmark", processItem.isBookmark());
        model.addAttribute("isPaidCost", processItem.getPaidCost().compareTo(Utils.getDefaultDecimal())>0);
        model.addAttribute("savedComplexUnitPrice", processItem.getComplexUnitPrice());
        model.addAttribute("jobSheetProcessItemCount", jobSheetProcessItemCount);
        model.addAttribute("costDetailList", processCostService.findProcessItemCostDetail(processItemId, "R"));
        model.addAttribute("bookmarkList", processCostService.findProcessItemCostDetailBookmark());
        model.addAttribute("processItemId", processItemId);
        return "cost/modal/costDetail";
    }

    @GetMapping("/gisungCostDetail/{processItemId}")
    public String gisungCostDetail(@PathVariable Long processItemId, Model model) {
        ProcessItem processItem = processCostService.findProcessItemById(processItemId);
        model.addAttribute("isBookmark", processItem.isBookmark());
        model.addAttribute("isPaidCost", processItem.getPaidCost().compareTo(Utils.getDefaultDecimal())>0);
        model.addAttribute("savedComplexUnitPrice", processItem.getComplexUnitPrice());
        return "cost/modal/gisungCostDetail";
    }

    @GetMapping("/paidCost/{processItemId}")
    public String paidCost(@PathVariable Long processItemId, Model model) {

        ProcessItemCostPay processItemCostPay = processCostService.getProcessItemCostPayAtLatest(processItemId);
        List<ProcessItemCostPayDTO> processItemCostPayDTOs = processCostService.findProcessItemCostPayLatestEditable(processItemId);

        model.addAttribute("processItem", new ProcessItemCostDTO(processItemCostPay.getProcessItem()));
        model.addAttribute("processItemCostPayAtCurrent", processItemCostPay.getPredictedCostPayAtCurrent());
        model.addAttribute("processItemCostPayList", processItemCostPayDTOs);
        return "cost/modal/paidCost";
    }

    @GetMapping("/gisungPaidCost/{processItemId}")
    public String gisungPaidCost(@PathVariable Long processItemId, Model model) {

        ProcessItemCostPay processItemCostPay = processCostService.getProcessItemCostPayAtLatest(processItemId);

        model.addAttribute("processItem", new ProcessItemCostDTO(processItemCostPay.getProcessItem()));
        model.addAttribute("processItemCostPayAtCurrent", processItemCostPay.getPredictedCostPayAtCurrent());
        return "cost/modal/gisungPaidCost";
    }

    @GetMapping("/paidCostAll/{processInfoId}")
    public String paidCostAll(@PathVariable Long processInfoId, Model model) {

        model.addAttribute("paidCostAll", processCostService.getProcessItemCostPayAtLatestCostNo(processInfoId));
        return "cost/modal/paidCostAll";
    }

    @GetMapping("/processItemCost/{processItemId}")
    public String processItemCost(@PathVariable Long processItemId, Model model) {
        ProcessItem processItem = processCostService.findProcessItemById(processItemId);
        model.addAttribute("processItem", processItem);
        return "cost/modal/processItemCost";
    }

    @GetMapping("/getProcessItemCostDetail/{processItemId}/{rowState}")
    public String getProcessItemCostDetail(@PathVariable Long processItemId, @PathVariable String rowState, Model model) {
        model.addAttribute("costDetailList", processCostService.findProcessItemCostDetail(processItemId, rowState));
        return "cost/modal/processItemCostDetail";
    }
}
