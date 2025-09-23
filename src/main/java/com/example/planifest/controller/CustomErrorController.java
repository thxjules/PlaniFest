package com.example.planifest.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

@GetMapping("/error")
public String handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    String uri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

    if (status != null) {
        int statusCode = Integer.parseInt(status.toString());
        logger.error("Error {} en la URL {}", statusCode, uri);

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return "error/500";
        }
    }

    return "error/error";
}
}