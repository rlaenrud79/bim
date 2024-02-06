package com.devo.bim.repository.spring;


import com.devo.bim.model.entity.Alert;
import com.devo.bim.model.enumulator.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByProjectIdAndRefId(long projectId, long refId);

    List<Alert> findByProjectIdAndRefIdAndReceiverId(long projectId, long refId, long receiverId);

    List<Alert> findByProjectIdAndRefIdAndType(long projectId, long refId, AlertType alertType);

    List<Alert> findByProjectIdAndRefIdAndReceiverIdAndType(long projectId, long refId, long receiverId, AlertType alertType);

    Optional<Alert> findByIdAndProjectIdAndReceiverId(long alertId, long projectId, long receiverId);
}
