package com.ds.userenrolmentservice.controller;

import com.ds.userenrolmentservice.ResponseHandler;
import com.ds.userenrolmentservice.exception.EnrolmentCollectionException;
import com.ds.userenrolmentservice.model.Enrolment;
import com.ds.userenrolmentservice.repo.EnrolmentRepo;
import com.ds.userenrolmentservice.service.EnrolmentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/enrolment")
public class EnrolmentController {

    @Autowired
    private EnrolmentService enrolmentService;

    @Autowired
    private EnrolmentRepo enrolmentRepo;

    @PostMapping("/addEnrolment")
    @CircuitBreaker(name = "enrolmentService", fallbackMethod = "enrolmentServiceFallback")
    public ResponseEntity<?> createEnrolment(@RequestBody Enrolment enrolment) {
        try {
            enrolmentService.createEnrolment(enrolment);
            return ResponseHandler.responseBuilder("Enrolment created successfully", HttpStatus.CREATED, enrolment);
        } catch (ConstraintViolationException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, null);
        }catch (EnrolmentCollectionException e){
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.CONFLICT, null);
        }
    }

    public ResponseEntity<?> enrolmentServiceFallback(Throwable throwable) {
        return ResponseHandler.responseBuilder("Service Unavailable Try again later", HttpStatus.SERVICE_UNAVAILABLE, null);
    }

    @GetMapping("/getAllEnrolments")
    public ResponseEntity<?> getAllEnrolments() {
        List<Enrolment> enrolments = enrolmentService.getAllEnrolments();
        if (!enrolments.isEmpty()) {
            return ResponseHandler.responseBuilder("Enrolments retrieved successfully", HttpStatus.OK, enrolments);
        } else {
            return ResponseHandler.responseBuilder("No enrolments found", HttpStatus.NOT_FOUND, null);
        }
    }

    @GetMapping("/getEnrolmentById/{id}")
    public ResponseEntity<?> getEnrolmentById(@PathVariable("id") String id) {
        try {
            return ResponseHandler.responseBuilder("Enrolment retrieved successfully", HttpStatus.OK, enrolmentService.getSingleEnrolmentById(id));
        } catch (EnrolmentCollectionException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @GetMapping("/getEnrolmentByUserId/{userId}")
    public ResponseEntity<?> getEnrolmentByUserId(@PathVariable("userId") String userId) {
        try {
            List<Enrolment> enrolments = enrolmentService.getEnrolmentByUserId(userId);
            return ResponseHandler.responseBuilder("Enrolments retrieved successfully", HttpStatus.OK, enrolments);
        } catch (EnrolmentCollectionException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @GetMapping("/getEnrolmentByCourseId/{courseId}")
    public ResponseEntity<?> getEnrolmentByCourseId(@PathVariable("courseId") String courseId) {
        try {
            List<Enrolment> enrolments = enrolmentService.getEnrolmentByCourseId(courseId);
            return ResponseHandler.responseBuilder("Enrolments retrieved successfully", HttpStatus.OK, enrolments);
        } catch (EnrolmentCollectionException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @GetMapping("/getEnrolmentByUserIdAndCourseId/{userId}/{courseId}")
    public ResponseEntity<?> getEnrolmentByUserIdAndCourseId(@PathVariable("userId") String userId, @PathVariable("courseId") String courseId) {
        try {
            return ResponseHandler.responseBuilder("Enrolment retrieved successfully", HttpStatus.OK, enrolmentService.getEnrolmentByUserIdAndCourseId(userId, courseId));
        } catch (EnrolmentCollectionException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @DeleteMapping("/deleteEnrolment/{id}")
    public ResponseEntity<?> deleteEnrolment(@PathVariable("id") String id) {
        try {
            enrolmentService.deleteEnrolmentById(id);
            return ResponseHandler.responseBuilder("Enrolment deleted successfully", HttpStatus.OK, null);
        } catch (EnrolmentCollectionException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    @PutMapping("/updateEnrolmentStatus/{userId}/{courseId}")
    public ResponseEntity<?> updateEnrolmentStatus(
            @PathVariable("userId") String userId,
            @PathVariable("courseId") String courseId,
            @RequestBody Map<String, Boolean> requestBody) {
        try {
            Boolean status = requestBody.get("status");
            Enrolment updatedEnrolment = enrolmentService.updateEnrolmentStatus(userId, courseId, status);
            return ResponseHandler.responseBuilder("Enrolment status updated successfully", HttpStatus.OK, updatedEnrolment);
        } catch (EnrolmentCollectionException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.NOT_FOUND, null);
        }
    }

    //Hello World endpoint for testing
    @GetMapping("/helloWorld")
    public ResponseEntity<?> helloWorld() throws EnrolmentCollectionException {
        String response = enrolmentService.HelloWorld();
        if (response != null) {
            return ResponseHandler.responseBuilder("Success", HttpStatus.OK, response);
        } else {
            return ResponseHandler.responseBuilder("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE, null);
        }
    }
}
