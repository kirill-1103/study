package ru.krey.crazy_task_tracker_api.api.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice// indicates that this handler can be used by different controllers
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)//indicates that this method handles Exception
    public ResponseEntity<Object> customHandleException(Exception ex, WebRequest request) throws Exception{
        log.error("Exception during execution of application",ex);
        return handleException(ex,request);
    }
}
