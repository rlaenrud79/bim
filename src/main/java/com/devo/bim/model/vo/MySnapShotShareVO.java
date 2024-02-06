 package com.devo.bim.model.vo;

import com.devo.bim.component.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@NoArgsConstructor
public class MySnapShotShareVO {
    private String type;
    private long id;
    private long mySnapShotId;
    private String existSnapShotIds;

    public boolean isCoWork() {
        return "coWork".equals(this.type);
    }

    public boolean isJobSheet() {
        return "jobSheet".equals(this.type);
    }

    public List<Long> targetMySnapShotIds() {
        if (StringUtils.isBlank(this.existSnapShotIds)) {
            return Utils.getLongList("" + mySnapShotId, ",");
        }
        return Utils.getLongList(existSnapShotIds + "," + mySnapShotId, ",");
    }
}
