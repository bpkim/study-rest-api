package com.bpkim.studyrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
            // WebMvc는 웹에 대한 테스트 이므로 repository는 따로 빈 등록을 해줘야 한다.
            EventRepository eventRepository;


    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                    .name("spring")
                    .description("REST API Development with Spring")
                    .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                    .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                    .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                    .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("강남역 D2 스타텁 팩토리")
                    .build();
        event.setId(10);

        // eventRepository.save가 호출될ㄸ대 사용한 event가 있으면 전달한 event 값을 리턴해라
        // MOC에서 Repository 사용하는 방법
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event))
                    )
                .andDo(print()) // 요청과 응답을 콘솔에서 확인 가능
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())// id가 있는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        ;

    }


    @Test
    public void createEvent2() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true) //  잘못된 값 넣어서 테스트
                .offline(false) //  잘못된 값 넣어서 테스트
                .build();

        // eventRepository.save가 호출될ㄸ대 사용한 event가 있으면 전달한 event 값을 리턴해라
        // MOC에서 Repository 사용하는 방법
        Mockito.when(eventRepository.save(event)).thenReturn(event);
        //>> 그러나 여기서 null pointer exception 발생
        // 그 이유는 ! 전달한 event가 save할때 들어간 event와 다르기 때문에
        //   dto로 전달 되었고 컨트롤러에서 새로 생성된 event 로 save가 되었고, 전달된 seve와 다르기 때문이다.
        // 그래서 null 이 리턴이 되었다.
        // 목킹을 하지 않겟다
        // 슬라이싱 테스트가 아니라 Spring boot test 로 해야한다.

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print()) // 요청과 응답을 콘솔에서 확인 가능
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())// id가 있는지 확인
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))

        ;

    }

}
