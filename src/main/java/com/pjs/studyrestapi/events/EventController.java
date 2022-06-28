package com.pjs.studyrestapi.events;


import com.pjs.studyrestapi.accounts.Account;
import com.pjs.studyrestapi.accounts.AccountAdapter;
import com.pjs.studyrestapi.accounts.CurrentUser;
import com.pjs.studyrestapi.index.IndexController;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
    public ResponseEntity createEvents(@RequestBody @Valid EventDto eventDto, Errors errors,
                                       @CurrentUser Account currentUser) {

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
        event.setManager(currentUser);
        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI uri = selfLinkBuilder.toUri();

        EntityModel eventResource = EntityModel.of(newEvent);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-events"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(uri).body(eventResource);
    }



    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler, @CurrentUser Account account) {
        Page<Event> page = this.eventRepository.findAll(pageable);
//        var pageResources = assembler.toModel(page, entity -> new EventResource(entity));
        var pageResources = assembler.toModel(page, entity ->EntityModel.of(entity).add(linkTo(EventController.class).withSelfRel()));
        pageResources.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null) {
            pageResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok().body(pageResources);

    }
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                    @CurrentUser Account currentUser) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EntityModel resources =new EventResource(event);
        resources.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        if(event.getManager().equals(currentUser)){
            resources.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(resources);
    }


    @PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        this.eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return badRequest(errors);
        }
        Event existingEvent = optionalEvent.get();

        // 본인이 작성한 글이 아닐경우
        if(!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        this.modelMapper.map(eventDto, existingEvent);
        Event event = this.eventRepository.save(existingEvent);
        EventResource resources = new EventResource(event);
        resources.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(resources);
    }


    private ResponseEntity<EntityModel<Errors>> badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(EntityModel.of(errors).add(linkTo(IndexController.class).withRel("index")));
    }




}
