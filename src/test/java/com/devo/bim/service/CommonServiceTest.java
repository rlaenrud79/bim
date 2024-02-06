package com.devo.bim.service;


import com.devo.bim.component.Utils;
import com.devo.bim.model.entity.ConfigCustom;
import com.devo.bim.model.enumulator.FileDownloadUIType;
import com.devo.bim.model.enumulator.FileUploadUIType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class CommonServiceTest extends AbstractServiceTest {

    @Test
    public void string_to_enum_test() {
        Assertions.assertThat( FileDownloadUIType.valueOf("MODELING_FILE")).isEqualTo(FileDownloadUIType.MODELING_FILE);
    }

    @Test
    public void string_to_enum_not_arg_default_value_test() {

        Assertions.assertThat(Utils.getFileDownloadUITypeEnum("AAAA")).isEqualTo(FileUploadUIType.NONE);
    }

    @Test
    public void list_long_type_to_array_string(){
        List<Long> longs = new ArrayList<>();
        longs.add(6l);
        longs.add(7l);
        longs.add(8l);
        longs.add(9l);
        longs.add(10l);

        System.out.println(longs.toString());
//        Assertions.assertThat(longs.toString()).isEqualTo("6,7,8,9,10");
    }
}