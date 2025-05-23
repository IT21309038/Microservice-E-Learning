package com.ds.userenrolmentservice.service;


import com.ds.userenrolmentservice.exception.EnrolmentCollectionException;
import com.ds.userenrolmentservice.model.Enrolment;
import com.ds.userenrolmentservice.repo.EnrolmentRepo;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EnrolmentServiceImpl implements EnrolmentService {

    @Autowired
    private EnrolmentRepo enrolmentRepo;

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Override
    public Enrolment createEnrolment(Enrolment enrolment) throws ConstraintViolationException, EnrolmentCollectionException {
        Optional<Enrolment> enrolmentOptional = enrolmentRepo.findByUserIdAndCourseId(enrolment.getUserId(), enrolment.getCourseId());

        if (enrolmentOptional.isPresent()) {
            throw new EnrolmentCollectionException(EnrolmentCollectionException.AlreadyExists());
        } else {
            enrolment.setCreatedAt(new Date(System.currentTimeMillis()));
            // Retrieve course data from the API
            Object course = webClientBuilder.build()
                    .get()
                    .uri("http://course-service/api/v1/courses/get-course-by-code/{code}", enrolment.getCourseId())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();

            if (course == null) {
                throw new EnrolmentCollectionException("Course not found"); // Handle case where course is not found
            }

            enrolment.setCourse(course);
            enrolmentRepo.save(enrolment);
        }
        return enrolment;
    }

    @Override
    public List<Enrolment> getAllEnrolments() {
        List<Enrolment> enrolment = enrolmentRepo.findAll();
        if (!enrolment.isEmpty()){
            return enrolment;
        }else {
            return new ArrayList<Enrolment>();
        }
    }

    @Override
    public Enrolment getSingleEnrolmentById(String id) throws EnrolmentCollectionException {
        Optional<Enrolment> optEnrolment = enrolmentRepo.findById(id);
        if(optEnrolment.isEmpty()){
            throw new EnrolmentCollectionException(EnrolmentCollectionException.NotFoundException(id));
        }else {
            return optEnrolment.get();
        }
    }

    @Override
    public List<Enrolment> getEnrolmentByUserId(String userId) throws EnrolmentCollectionException {
        List<Enrolment> enrolments = enrolmentRepo.findByUserId(userId);
        if(enrolments.isEmpty()){
            throw new EnrolmentCollectionException(EnrolmentCollectionException.NotFoundException(userId));
        }else {
            return enrolments;
        }
    }

    @Override
    public List<Enrolment> getEnrolmentByCourseId(String courseId) throws EnrolmentCollectionException {
        List<Enrolment> enrolments = enrolmentRepo.findByCourseId(courseId);
        if(enrolments.isEmpty()){
            throw new EnrolmentCollectionException(EnrolmentCollectionException.NotFoundException(courseId));
        }else {
            return enrolments;
        }
    }

    @Override
    public List<Enrolment> getEnrolmentByUserIdAndCourseId(String userId, String courseId) throws EnrolmentCollectionException {
        Optional<Enrolment> optEnrolment = enrolmentRepo.findByUserIdAndCourseId(userId, courseId);
        if(optEnrolment.isEmpty()){
            throw new EnrolmentCollectionException(EnrolmentCollectionException.NotFoundException1(userId, courseId));
        }else {
            return List.of(optEnrolment.get());
        }
    }

    @Override
    public void deleteEnrolmentById(String id) throws EnrolmentCollectionException {
        Optional<Enrolment> enrolment = enrolmentRepo.findById(id);
        if(enrolment.isEmpty()){
            throw new EnrolmentCollectionException(EnrolmentCollectionException.NotFoundException(id));
        }else {
            enrolmentRepo.deleteById(id);
        }
    }

    @Override
    public Enrolment updateEnrolmentStatus(String userId, String courseId, Boolean status)
            throws EnrolmentCollectionException {
        Optional<Enrolment> optEnrolment = enrolmentRepo.findByUserIdAndCourseId(userId, courseId);
        if (optEnrolment.isEmpty()) {
            throw new EnrolmentCollectionException(EnrolmentCollectionException.NotFoundException1(userId, courseId));
        } else {
            Enrolment enrolment = optEnrolment.get();
            enrolment.setStatus(status);
            enrolment.setUpdatedAt(new Date());
            enrolmentRepo.save(enrolment);
            return enrolment;
        }
    }

    @Override
    public String HelloWorld() throws EnrolmentCollectionException {

        String hello = "Hello";
        String world = "World";

        return hello + " " + world;
    }
}
