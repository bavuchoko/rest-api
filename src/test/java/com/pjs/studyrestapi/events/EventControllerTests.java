package com.pjs.studyrestapi.events;


import com.pjs.studyrestapi.accounts.Account;
import com.pjs.studyrestapi.accounts.AccountRepository;
import com.pjs.studyrestapi.accounts.AccountRole;
import com.pjs.studyrestapi.accounts.AccountService;
import com.pjs.studyrestapi.common.AppProperties;
import com.pjs.studyrestapi.common.BaseControllerTest;
import com.pjs.studyrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {


    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }


    @Test
    public void createEvnet() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 6, 16, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 6, 17, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2022, 6, 18, 14, 25))
                .endEventDateTime(LocalDateTime.of(2022, 6, 19, 20, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value("DRAFT"))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-events").exists())
                .andDo(document("create-events",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query-event"),
                                linkWithRel("update-events").description("link to update-event"),
                                linkWithRel("profile").description("link to update-event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime   of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime   of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime   of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime   of new event"),
                                fieldWithPath("location").description("location   of new event"),
                                fieldWithPath("basePrice").description("basePrice   of new event"),
                                fieldWithPath("maxPrice").description("maxPrice   of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment   of new event")

                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON TYPE")
                        ),
                        relaxedResponseFields(
//                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("name of new ev ent"),
                                fieldWithPath("description").description("description   of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime   of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime   of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime   of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime   of new event"),
                                fieldWithPath("location").description("location   of new event"),
                                fieldWithPath("basePrice").description("basePrice   of new event"),
                                fieldWithPath("maxPrice").description("maxPrice   of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment  of enrolment "),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline meeting or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to sef"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-events.href").description("link to update event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )

                ))
        ;
    }

    private String getBaererToken() throws Exception {
        return "Bearer " + getAccescToken();
    }

    private String getAccescToken() throws Exception {
        //Given
        Account test = Account.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUsePassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(test);

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUsePassword())
                .param("grant_type", "password"));
        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @TestDescription("없어야 되는 값들이 들어온 경우")
    public void createEvnet_Bad_Request() throws Exception {

        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 6, 16, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 6, 17, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2022, 6, 18, 14, 25))
                .endEventDateTime(LocalDateTime.of(2022, 6, 19, 20, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    public void createEvnet_Bad_Request_Empty() throws Exception {
        EventDto eventDto = EventDto.builder().build();
        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("최소값이 최대값보다 크던지 데이터가 잘못된 경우")
    public void createEvnet_Bad_Request_Wrong_Data() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 6, 16, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 6, 17, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2022, 6, 18, 14, 25))
                .endEventDateTime(LocalDateTime.of(2021, 6, 19, 20, 11))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .build();

        mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].rejectValue").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        //When
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")          //페이지 0 부터 시작 -> 1은 두번째 페이지
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
        ;
    }
    @Test
    @TestDescription("인증정보 가지고 이벤트 리스트 조회시 등록링크 테스트")
    public void queryEventsWithAuthentication() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> {
            this.generateEvent(i);
        });

        //When
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .param("page", "1")          //페이지 0 부터 시작 -> 1은 두번째 페이지
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))
        ;
    }



    private Event generateEvent(int i) {
        Event event = Event.builder()
                .name("event" + i)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2022, 6, 16, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022, 6, 17, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2022, 6, 18, 14, 25))
                .endEventDateTime(LocalDateTime.of(2022, 6, 19, 20, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();
        return this.eventRepository.save(event);
    }


    @Test
    @TestDescription("기존의 이벤트 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
                .andDo(print())
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회 한 경우 404 응답")
    public void getEvent404() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //When & Then
        this.mockMvc.perform(get("/api/events/123412"))
                .andExpect(status().isNotFound())

        ;
    }


    @Test
    @TestDescription("정상적으로 수정하는 경우")
    public void updateEvent() throws Exception {
        //Given
        Event  event =this.generateEvent(200);
        EventDto eventDto= modelMapper.map(event,EventDto.class);
        String eventName = "updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
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
    @TestDescription("입력값이 비어있는 경우 이벤트 수정 실패하기")
    public void updateEvent404_Empty() throws Exception {
        //Given
        Event event  =this.generateEvent(200);
        EventDto eventDto=new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;
    }


    @Test
    @TestDescription("입력값이 잘못된 경우 이벤트 수정 실패하기")
    public void updateEvent400_Wrong() throws Exception {
        //Given
        Event event  =this.generateEvent(200);
        EventDto eventDto= modelMapper.map(event,EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패하기")
    public void updateEvent404_Wrong() throws Exception {
        //Given
        Event event  =this.generateEvent(200);
        EventDto eventDto= modelMapper.map(event,EventDto.class);//dto 자체는 유효한 dto가 존재하지만  수정은 엉뚱한(존재하지 않는id)를 수정하려는경우


        // When & Then
        this.mockMvc.perform(put("/api/events/12312323")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

}
