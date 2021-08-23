package com.bpkim.studyrestapi.events;

import com.bpkim.studyrestapi.common.RestDocsConfiguration;
import com.bpkim.studyrestapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc // SpringBootTest 에서 moc 쓰기
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) // 레스트 에이피아이 이쁘게 만들기 config 적용
@ActiveProfiles("test")
public class EventControllerTests2 {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

//    @MockBean
    // WebMvc는 웹에 대한 테스트 이므로 repository는 따로 빈 등록을 해줘야 한다.
//            EventRepository eventRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Test
    @TestDescription("이벤트 정상 생성 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
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

        // eventRepository.save가 호출될ㄸ대 사용한 event가 있으면 전달한 event 값을 리턴해라
        // MOC에서 Repository 사용하는 방법

//        Mockito.when(eventRepository.save(event)).thenReturn(event);
        //>> 그러나 여기서 null pointer exception 발생
        // 그 이유는 ! 전달한 event가 save할때 들어간 event와 다르기 때문에
        //   dto로 전달 되었고 컨트롤러에서 새로 생성된 event 로 save가 되었고, 전달된 seve와 다르기 때문이다.
        // 그래서 null 이 리턴이 되었다.
        // 목킹을 하지 않겟다
        // 슬라이싱 테스트가 아니라 Spring boot test 로 해야한다. 

        ResultActions resultActions = mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
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
                .andExpect(jsonPath("offline").value(true))
//                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event"    // 문서의 이름

                        , links(linkWithRel("self").description("link to self")     // 링크와 관련된 snipet 추가 > 링크와 관련된 문서도 생성됨
                                , linkWithRel("query-events").description("link to query events")
                                , linkWithRel("update-event").description("link to update an existing event")
                                , linkWithRel("profile").description("link to update an existing event")
                        )
                        , requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")

                        )
                        , requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of enrollment new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of enrollment new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment")
                        )
                        , responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("response location"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        )
//                                , relaxedResponseFields(
                        , responseFields(
                                //relaxed ~ 프리픽스 : 일부만 문서화 하겠다.
                                //  문서 일부분만 테스트 할 수 있다.
                                //  정확한 문서를 생성하지 못한다.
                                fieldWithPath("id").description("Identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of enrollment new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of enrollment new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status of new event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.update-event.href").description("link to update"),
                                fieldWithPath("_links.query-events.href").description("link to query exists")
                                , fieldWithPath("_links.profile.href").description("profile")

                        )
                        )
                );

    }

    /**
     * 입력값 이외에 에러 발생 테스트
     *

     프로퍼티에 설정 하기
     #json > object 변환 : deserialization
     #object > json 변환 : serialization

     # 알려지지 않은 것이 있으면 실패 > badRequest 발생 400 에러
     spring.jackson.deserialization.fail-on-unknown-properties=true

     입력값 이외에 값이 들어 왔을때
     Bad Request 응답 vs 무시 그냥 올바른 값만 취하기 이건 선택!

     */
    @Test
    @TestDescription("받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception{

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


        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print()) // 요청과 응답을 콘솔에서 확인 가능
                .andExpect(status().isBadRequest())

        ;

    }
    /*
    @Test
    @TestDescription("입력값이 비어 있는 경우에 에러가 발생 하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .build()
                ;

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andExpect(status().isBadRequest());
    }
*/
    /*
    테스트 함수를 바로 뭔 테스트인지 알 수가 없다
    jUnit 5 에는 디스크립션이 있지만
    여기는 없기때문에 애노테이션 하나를 만늘어서 해주자
    TestDescription 생성
     */
    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API Development with Spring")
                // 시작하는 일자가 끝나는 일자보다 늦다.
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,26,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,25,14,21))
                // 시작하는 일자가 끝나는 일자보다 늦다.
                .beginEventDateTime(LocalDateTime.of(2018,11,24,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,23,14,21))
                // max 값이 base 보다 작다.
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build()
                ;

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())
                ;
    }


    @Test
    @TestDescription("30개의 이벤틀르 10개씩 두번째 페이지 조회하기 ")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        // When
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", String.valueOf(10))
                .param("sort", "name,Desc")

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))

        ;

    }


    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        // Given
        Event event = this.generateEvent(100);

        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").exists())
                    .andExpect(jsonPath("id").exists())
                    .andExpect(jsonPath("_links.self").exists())
                    .andExpect(jsonPath("_links.profile").exists())
                    .andDo(document("get-an-event"))
                ;
    }


    @Test
    @TestDescription("없는 거 조회 시 404 확인 ")
    public void notFoundEvent() throws Exception{

        this.mockMvc.perform(get("/api/events/3577"))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception{
        // Given
        Event event = this.generateEvent(100);
        String eventName = "Update Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value(eventName))
                    .andExpect(jsonPath("_links.self").exists())
                    .andDo(document("update-event"))
        ;

    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 이벤트 수정 싪패")
    public void updateEven400_Empty() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }


    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 싪패")
    public void updateEven400_wrong() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(100);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }


    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEven404() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/123123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;

    }

    private Event generateEvent(int index){
        Event event = Event.builder()
                .name("event "+index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        return this.eventRepository.save(event);
    }

}
