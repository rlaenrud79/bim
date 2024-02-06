package com.devo.bim.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ProcessItemDTO {
    List<TaskDTO> tasks = new ArrayList();
    List<LinkDTO> links = new ArrayList();
}
