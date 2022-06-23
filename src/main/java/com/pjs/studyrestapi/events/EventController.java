package com.pjs.studyrestapi.events;


import com.pjs.studyrestapi.index.IndexController;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvents(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()){

            return badRequest(errors);
        }
        eventValidator.validate(eventDto,errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }
        //modelmapper 사용해서 EventDto -> Event 를 간편하게.
        //아니면 builder 이용해서 필드별로 각각 세팅
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI uri = selfLinkBuilder.toUri();

        EntityModel eventResource = EntityModel.of(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-events"));
        eventResource.add(Link.of("/docs/index.html").withRel("profile"));
        return ResponseEntity.created(uri).body(eventResource);
    }

    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(IndexController.class).withRel("index")));
    }

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = this.eventRepository.findAll(pageable);
//        var pageResources = assembler.toModel(page, entity -> new EventResource(entity));
        var pageResources = assembler.toModel(page, entity ->EntityModel.of(entity).add(linkTo(EventController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index.html").withRel("profile"));
        return ResponseEntity.ok().body(pageResources);

    }

}
