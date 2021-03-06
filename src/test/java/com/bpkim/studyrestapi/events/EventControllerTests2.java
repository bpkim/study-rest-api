package com.bpkim.studyrestapi.events;

import com.bpkim.studyrestapi.accounts.Account;
import com.bpkim.studyrestapi.accounts.AccountRepository;
import com.bpkim.studyrestapi.accounts.AccountRole;
import com.bpkim.studyrestapi.accounts.AccountService;
import com.bpkim.studyrestapi.common.AppProperties;
import com.bpkim.studyrestapi.common.RestDocsConfiguration;
import com.bpkim.studyrestapi.common.TestDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
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
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc // SpringBootTest ?????? moc ??????
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) // ????????? ??????????????? ????????? ????????? config ??????
@ActiveProfiles("test")
public class EventControllerTests2 {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

//    @MockBean
    // WebMvc??? ?????? ?????? ????????? ????????? repository??? ?????? ??? ????????? ????????? ??????.
//            EventRepository eventRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp(){
        this.accountRepository.deleteAll();
        this.eventRepository.deleteAll();
    }
    @Test
    @TestDescription("????????? ?????? ?????? ?????????")
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
                .location("????????? D2 ????????? ?????????")
                .build();

        // eventRepository.save??? ??????????????? ????????? event??? ????????? ????????? event ?????? ????????????
        // MOC?????? Repository ???????????? ??????

//        Mockito.when(eventRepository.save(event)).thenReturn(event);
        //>> ????????? ????????? null pointer exception ??????
        // ??? ????????? ! ????????? event??? save?????? ????????? event??? ????????? ?????????
        //   dto??? ?????? ????????? ?????????????????? ?????? ????????? event ??? save??? ?????????, ????????? seve??? ????????? ????????????.
        // ????????? null ??? ????????? ?????????.
        // ????????? ?????? ?????????
        // ???????????? ???????????? ????????? Spring boot test ??? ????????????. 

        ResultActions resultActions = mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print()) // ????????? ????????? ???????????? ?????? ??????

                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())// id??? ????????? ??????
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("offline").value(true))
//                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event"    // ????????? ??????

                        , links(linkWithRel("self").description("link to self")     // ????????? ????????? snipet ?????? > ????????? ????????? ????????? ?????????
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
                        , relaxedResponseFields(
//                        , responseFields(
                                //relaxed ~ ???????????? : ????????? ????????? ?????????.
                                //  ?????? ???????????? ????????? ??? ??? ??????.
                                //  ????????? ????????? ???????????? ?????????.
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
     * ????????? ????????? ?????? ?????? ?????????
     *

     ??????????????? ?????? ??????
     #json > object ?????? : deserialization
     #object > json ?????? : serialization

     # ???????????? ?????? ?????? ????????? ?????? > badRequest ?????? 400 ??????
     spring.jackson.deserialization.fail-on-unknown-properties=true

     ????????? ????????? ?????? ?????? ?????????
     Bad Request ?????? vs ?????? ?????? ????????? ?????? ????????? ?????? ??????!

     */
    @Test
    @TestDescription("?????? ??? ?????? ?????? ????????? ????????? ????????? ???????????? ?????????")
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
                .location("????????? D2 ????????? ?????????")
                .free(true) //  ????????? ??? ????????? ?????????
                .offline(false) //  ????????? ??? ????????? ?????????
                .build();


        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print()) // ????????? ????????? ???????????? ?????? ??????
                .andExpect(status().isBadRequest())

        ;

    }
    /*
    @Test
    @TestDescription("???????????? ?????? ?????? ????????? ????????? ?????? ?????? ?????????")
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
    ????????? ????????? ?????? ??? ??????????????? ??? ?????? ??????
    jUnit 5 ?????? ?????????????????? ?????????
    ????????? ??????????????? ??????????????? ????????? ???????????? ?????????
    TestDescription ??????
     */
    @Test
    @TestDescription("?????? ?????? ????????? ????????? ????????? ???????????? ?????????")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API Development with Spring")
                // ???????????? ????????? ????????? ???????????? ??????.
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,26,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,25,14,21))
                // ???????????? ????????? ????????? ???????????? ??????.
                .beginEventDateTime(LocalDateTime.of(2018,11,24,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,23,14,21))
                // max ?????? base ?????? ??????.
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("????????? D2 ????????? ?????????")
                .build()
                ;

        mockMvc.perform(post("/api/events")
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
    @TestDescription("30?????? ???????????? 10?????? ????????? ????????? ???????????? ")
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
    @TestDescription("????????? ???????????? ?????? ????????????")
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
    @TestDescription("?????? ??? ?????? ??? 404 ?????? ")
    public void notFoundEvent() throws Exception{

        this.mockMvc.perform(get("/api/events/3577"))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("???????????? ??????????????? ????????????")
    public void updateEvent() throws Exception{
        // Given
        Event event = this.generateEvent(100);
        String eventName = "Update Event";
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
    @TestDescription("???????????? ???????????? ????????? ????????? ?????? ??????")
    public void updateEven400_Empty() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
            )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }


    @Test
    @TestDescription("???????????? ????????? ????????? ????????? ?????? ??????")
    public void updateEven400_wrong() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(100);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

    }


    @Test
    @TestDescription("???????????? ?????? ????????? ?????? ??????")
    public void updateEven404() throws Exception{
        // Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/123123")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .location("????????? D2 ????????? ?????????")
                .build();

        return this.eventRepository.save(event);
    }

    private String getBearerToken() throws Exception{
        return "Bearer " +  getAccessToken();
    }

    private String getAccessToken() throws Exception{


        String password = appProperties.getUserPassword();
        String username = appProperties.getUserUsername();
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.USER))
                .build();

        this.accountService.saveAccount(account);

        String clientId = appProperties.getClientId();
        String clientSecret = appProperties.getClientSecret();
        ResultActions result = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))  // header ??????
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        );

        var resultString = result.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(resultString).get("access_token").toString();


    }
}
