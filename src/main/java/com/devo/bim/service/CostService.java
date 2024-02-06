package com.devo.bim.service;

import com.devo.bim.model.dto.CostSnapShotDTO;
import com.devo.bim.model.entity.Cost;
import com.devo.bim.model.entity.CostSnapShot;
import com.devo.bim.model.vo.CostSnapShotVO;
import com.devo.bim.repository.spring.CostRepository;
import com.devo.bim.repository.spring.CostSnapShotRepository;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CostService extends AbstractService {
    private final CostRepository costRepository;
    private final CostSnapShotRepository costSnapShotRepository;

    public CostSnapShot findLatestCostSnapshot() {
        return costSnapShotRepository
                .getLatestCostSnapshot(userInfo.getProjectId())
                .orElseGet(() -> new CostSnapShot(new Cost(), "", "", userInfo.getId()));
    }

    public JsonObject getCostSnapshot(CostSnapShotVO costSnapShotVO) {
        return getCostSnapshot(costSnapShotVO.getCostSnapShotId(), costSnapShotVO.getCostId());
    }

    public JsonObject getCostSnapshot(long costId, long costSnapShotId) {
        return proc.getResult(
                true
                , findCostSnapshot(costId, costSnapShotId)
        );
    }

    public CostSnapShotDTO findCostSnapshot(long costId, long costSnapShotId) {
        return new CostSnapShotDTO(costSnapShotRepository
                .findByIdAndCostId(costSnapShotId, costId)
                .map(savedCostSnapShot -> {
                    if (savedCostSnapShot.getCost().getProjectId() == userInfo.getProjectId()) return savedCostSnapShot;
                    return new CostSnapShot();
                })
                .orElseGet(CostSnapShot::new)
        );
    }

    @Transactional
    public JsonObject postCost(String name) {
        Cost newCost = costRepository.save(new Cost(userInfo.getProjectId(), name, userInfo.getId()));

        return proc.getResult(true, "system.cost_service.cost_register", new CostSnapShotDTO(newCost));
    }

    @Transactional
    public JsonObject postSnapShotCost(CostSnapShotVO costSnapShotVO) {
        Cost cost = costRepository
                .findByIdAndProjectId(costSnapShotVO.getCostId(), userInfo.getProjectId())
                .orElseGet(Cost::new);

        CostSnapShot newCostSnapShot = costSnapShotRepository.save(new CostSnapShot(cost, costSnapShotVO.getDescription(), costSnapShotVO.getData(), userInfo.getId()));

        return proc.getResult(true, "system.cost_service.cost_snapshot_success", new CostSnapShotDTO(newCostSnapShot));
    }

    @Transactional
    public JsonObject saveCostSnapShot(CostSnapShotVO costSnapShotVO) {
        CostSnapShot costSnapshot = costSnapShotRepository
                .findByIdAndCostId(costSnapShotVO.getCostSnapShotId(), costSnapShotVO.getCostId())
                .map(savedCostSnapShot -> {
                    if (savedCostSnapShot.getCost().getProjectId() == userInfo.getProjectId()) {
                        savedCostSnapShot.updateCostSnapShotData(costSnapShotVO.getData(), userInfo.getId());
                        return costSnapShotRepository.save(savedCostSnapShot);
                    }

                    return new CostSnapShot();
                })
                .orElseGet(CostSnapShot::new);

        if (costSnapshot.getId() == 0) return proc.getResult(false, "system.cost_service.cost_save_fail", new CostSnapShotDTO(costSnapshot));
        return proc.getResult(true, "system.cost_service.cost_save_success", new CostSnapShotDTO(costSnapshot));
    }

    public List<Cost> findCosts() {
        return costRepository.findByProjectId(userInfo.getProjectId());
    }
}
