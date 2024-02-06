package com.devo.bim.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobSheetGrantorVO {

    public long id = 0L;
    public long jobSheetId = 0L;
    public String description = "";
}
