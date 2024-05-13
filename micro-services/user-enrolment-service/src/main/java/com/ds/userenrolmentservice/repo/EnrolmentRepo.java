package com.ds.userenrolmentservice.repo;

import com.ds.userenrolmentservice.model.Enrolment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EnrolmentRepo extends MongoRepository<Enrolment, String>{

    List<Enrolment> findByUserId(String userId);

    List<Enrolment> findByCourseId(String courseId);

    Optional<Enrolment> findByUserIdAndCourseId(String userId, String courseId);

    Optional<Enrolment> findById(String id);

    void deleteById(String id);
}
