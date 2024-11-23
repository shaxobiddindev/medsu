package uz.medsu.utils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerService {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handler(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(400).body(ResponseMessage.builder().success(false).message(e.getMessage()).build());
    }
}
