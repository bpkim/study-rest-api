package com.bpkim.studyrestapi.index;


import com.bpkim.studyrestapi.common.RestDocsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc // SpringBootTest 에서 moc 쓰기
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) // 레스트 에이피아이 이쁘게 만들기 config 적용
@ActiveProfiles("test")
public class IndexControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void index() throws Exception{
        this.mockMvc.perform(get("/api/"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("_links.events").exists())


        ;
    }
}
