package com.pjs.studyrestapi.events;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pjs.studyrestapi.common.RestDocsConfiguration;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void createEvnet() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,6,16,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,6,17,14,21))
                .beginEventDateTime(LocalDateTime.of(2022,6,18,14,25))
                .endEventDateTime(LocalDateTime.of(2022,6,19,20,11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .build();

        mockMvc.perform(post("/api/events")
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
                        responseFields(
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

    @Test
    public void createEvnet_Bad_Request() throws Exception {

        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,6,16,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2022,6,17,14,21))
                .beginEventDateTime(LocalDateTime.of(2022,6,18,14,25))
                .endEventDateTime(LocalDateTime.of(2022,6,19,20,11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .offline(false)
                .free(true)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void createEvnet_Bad_Request_Wrong_Data() throws Exception {

        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2022,6,16,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,6,17,14,21))
                .beginEventDateTime(LocalDateTime.of(2022,6,18,14,25))
                .endEventDateTime(LocalDateTime.of(2021,6,19,20,11))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("대전")
                .offline(false)
                .free(true)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].rejectValue").exists())
        ;
    }
}
