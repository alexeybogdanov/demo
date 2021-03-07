package com.example.demo.controller;

import com.example.demo.dto.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ErrorInfo handleNotFound(RuntimeException ex, HttpServletRequest request) {
        log.error("Request: {} raised: {} ", request.getRequestURL(), ex.getLocalizedMessage());
        return buildErrorInfo(ex, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo handleError(Exception ex, HttpServletRequest request) {
        log.error("Request: {} raised: {} ", request.getRequestURL(), ex.getLocalizedMessage());
        return buildErrorInfo(ex, request);
    }

    private ErrorInfo buildErrorInfo(Exception ex, HttpServletRequest request) {
        return ErrorInfo.builder()
                .url(request.getRequestURL().toString())
                .details(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}
