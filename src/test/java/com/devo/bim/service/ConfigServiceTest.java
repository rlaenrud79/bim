package com.devo.bim.service;


import com.devo.bim.model.entity.ConfigCustom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
//@Rollback(value = false)
class ConfigServiceTest extends AbstractServiceTest {

    @Autowired private ConfigService configService;

    @Test
    public void findConfig_BaseConfig_getValue() throws Exception {

        ConfigCustom configCustom = configService.findConfig("SYSTEM","IMAGE_FILE_EXT");
        Assertions.assertThat(configCustom.getCustomValue()).isEqualTo("*.png, *.jpg, *.gif, *.bmp, *.jpeg, *.svg");
        System.out.println(configCustom.getSource());
    }

    @Test
    public void findConfig_CustomConfig_getValue() throws Exception {

        ConfigCustom configCustom = configService.findConfig("SCHEDULE","ANNIVERSARY");
        Assertions.assertThat(configCustom.getCustomValue()).isEqualTo("true");
        System.out.println(configCustom.getSource());
    }

    @Test
    public void findConfig_groupCode_not_exist_exception() {

        try {
            configService.findConfig("SCHEDULE1","ANNIVERSARY");
        } catch (Exception e)
        {
            Assertions.assertThat(e.getMessage()).isEqualTo("SCHEDULE1: 존재하지 않는 설정 그룹명 입니다.");
        }
    }

    @Test
    public void findConfig_configCode_not_exist_exception() {

        try {
            configService.findConfig("SCHEDULE","ANNIVERSARY1");
        } catch (Exception e)
        {
            Assertions.assertThat(e.getMessage()).isEqualTo("ANNIVERSARY1: 존재하지 않는 설정 코드 입니다.");
        }
    }
}