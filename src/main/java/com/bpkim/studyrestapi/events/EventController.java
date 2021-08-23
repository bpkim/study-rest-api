package com.bpkim.studyrestapi.events;

import com.bpkim.studyrestapi.common.ErrorsResources;
import com.bpkim.studyrestapi.common.PagedModelUtil;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
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

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    // Vaild 설
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){

        /* @Valid 옆에 있는 객체에 에러를 담아준다.
         * */
        // Valid 애노테이션에 의해 걸린것
        if(errors.hasErrors()){
            // 에러를 아래 리턴 값 처럼 하면 안됀다
            // 객체를 json 으로 변환할 때 objectmapper로 변환하는데 되는 이유는 자바빈 스펙을 준수한다.
            // 그러나 errors 는 자바빈 객체 스펙을 준수하지 않기 때문에 변환 할 수 없다.
            // >> alizer 를 만들어서 이 문제를 해결 해보자
//            return ResponseEntity.badRequest().body(errors);
            return badRequest(errors);
        }

        // Validator 에서 구현한 것에 의해 걸린것
        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
//            return ResponseEntity.badRequest().body(errors);
            return badRequest(errors);
        }


        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);

        //https://docs.spring.io/spring-hateoas/docs/current/reference/html/
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();

        // linkTo
        // Controller에 적용된 uri 가 들어간다.
        // methodOn(EventController.class).createEvent()
        //  해당 메소드에 적용된 uri가 들어간다.

//        EventResource eventResource = new EventResource(event);
//        EventResource<Event> eventResource = new EventResource<>(event);
        EventResource eventResource = new EventResource(event);

        eventResource.add(linkTo(EventController.class).withRel("query-events")); // events : 목록으로 가는 링크
//        eventResource.add(selfLinkBuilder.withSelfRel()); // withSelfRel 셀프 추가 self : view
        eventResource.add(selfLinkBuilder.withRel("update-event")); // update : 업데이트
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createUri).body(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResources.modelOf(errors));
    }


/*
 3 dto 적용하여 필요한 값만 입력 받기
    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto){

        *//*Dto > 도메인 객체로 변환
        ModelMapper 의존성 추가
        * *//*

     *//* 원래 대로 하면 dto 값을 다 도메인으로 옮겨야 하는데 ModelMapper 로 해서 쉽게 하자
     *  리플렉션을 이용하기 때문에 조금 느릴 수도 있으나
     *  자바 버전이 올라 갈 수록 좋아지고 있어 큰 문제가 되지 않을 것이다.
     *  그러나 이점이 걱정이 된다면 걍 세팅 해서 쓰자.
     *  mavenRepository 에서 ModelMapper 검색하여 의존성 추가
     *  공용으로 쓸 수 있는 객체이기 때문에 빈으로 등록해서 쓰자
     *  >> Application 자바 에 등록*//*
        Event event = modelMapper.map(eventDto, Event.class);

        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder createUri = linkTo(EventController.class).slash(newEvent.getId());
        // linkTo
        // Controller에 적용된 uri 가 들어간다.
        // methodOn(EventController.class).createEvent()
        //  해당 메소드에 적용된 uri가 들어간다.

        return ResponseEntity.created(createUri.toUri()).body(event);
    }*/
/*
2   입력값 받아서 처리하기
    @PostMapping
    public ResponseEntity createEvent(@RequestBody Event event){

        Event newEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder createUri = linkTo(EventController.class).slash(newEvent.getId());
        // linkTo
        // Controller에 적용된 uri 가 들어간다.
        // methodOn(EventController.class).createEvent()
        //  해당 메소드에 적용된 uri가 들어간다.

        return ResponseEntity.created(createUri.toUri()).body(event);
    }*/


/*  1 메소드 별로 methodOn 이
    @PostMapping("/api/events")
    public ResponseEntity createEvent(@RequestBody Event event){

        WebMvcLinkBuilder createUri = linkTo(methodOn(EventController.class).createEvent(null)).slash("{id}");
        // linkTo
        // Controller에 적용된 uri 가 들어간다.
        // methodOn(EventController.class).createEvent()
        //  해당 메소드에 적용된 uri가 들어간다.

        return ResponseEntity.created(createUri.toUri()).build();
    }
    */

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> entityModels = PagedModelUtil.getEntityModels(assembler, page
                , linkTo(this.getClass()), Event::getId);
                //PagedModelUtil.getEntityModels(assembler, page, linkTo(this.getClass()), Event::getId);
        entityModels.add(Link.of("docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(entityModels);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable("id") Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }


        Event event = optionalEvent.get();

        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(event.getId());
        URI createUri = selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable("id") Integer id
                                        , @RequestBody @Valid EventDto eventDto
                                        , Errors errors){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        this.eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();

        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(Link.of("/docs/index.html#resource-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

}
