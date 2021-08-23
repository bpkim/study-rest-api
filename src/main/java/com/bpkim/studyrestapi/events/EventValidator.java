package com.bpkim.studyrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){

        // 에노테이션으로 검증하기 어려운 것
        if(eventDto.getBasePrice() > eventDto.getMaxPrice()
            && eventDto.getMaxPrice() !=0){
//            errors.rejectValue : filed error
//            errors.reject : global error
//            errors.rejectValue("basePrice", "wrongValue", "BasePrice is wrong.");
//            errors.rejectValue("maxPrice", "wrongValue", "MaxPrice is wrong.");
            errors.reject("wrongPrice", "BasePrice is wrong.");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime beginEnrollmentDateTime = eventDto.getBeginEnrollmentDateTime();
        if(endEventDateTime == null || beginEnrollmentDateTime == null
            || closeEnrollmentDateTime == null || beginEventDateTime == null){
            errors.rejectValue("endEventDateTime","wrongValue", "EndEventDateTime is wrong.");

        }else {
            if (endEventDateTime.isBefore(beginEventDateTime)
                    || endEventDateTime.isBefore(closeEnrollmentDateTime)
                    || endEventDateTime.isBefore(beginEnrollmentDateTime)) {
                errors.rejectValue("endEventDateTime", "wrongValue", "EndEventDateTime is wrong.");
            }

        }
        //TODO 나머지 구현
    }
}
