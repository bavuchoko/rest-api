package com.pjs.studyrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        if (eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0) {
            errors.reject("wrongPrice", "value of prices are Wrong");
        }
        LocalDateTime endEvenDateTime = eventDto.getEndEventDateTime();
        if(endEvenDateTime.isBefore(eventDto.getBeginEventDateTime())||
        endEvenDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())||
        endEvenDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime", "WrongValue", "Date is Wrong");
        }
    }
}
