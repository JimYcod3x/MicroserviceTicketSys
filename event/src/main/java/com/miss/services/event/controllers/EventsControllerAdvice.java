package com.miss.services.event.controllers;

import com.miss.services.common.domains.MetrixException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class EventsControllerAdvice {
    @ExceptionHandler(MetrixException.class)
    public ResponseEntity<Map<String, String>> handleMetrixException(MetrixException err){
        Map<String, String> rtn = Map.of("errCode", Integer.toString(err.getCustomErrCode()), "errMsg", err.getCustomErrMsg());
        return ResponseEntity.badRequest().body(rtn);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Set<ConstraintViolation<?>>> handleConstraintViolationException(ConstraintViolationException err){
        return ResponseEntity.badRequest().body(err.getConstraintViolations());
    }
}