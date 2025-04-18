package com.ds.monitoringservice;

import com.ds.monitoringservice.controller.LearnerProgressController;
import com.ds.monitoringservice.model.LearnerProgressResponse;
import com.ds.monitoringservice.service.LearnerProgressService;
import com.ds.monitoringservice.repo.LearnerProgressRepo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LearnerProgressController.class)
public class LearnerProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearnerProgressService learnerProgressService;

    @MockBean
    private LearnerProgressRepo learnerProgressRepo;

    @Test
    void testGetAllProgressReturnsNotFound() throws Exception {
        LearnerProgressResponse emptyResponse = new LearnerProgressResponse();
        emptyResponse.setLearnerProgressList(Collections.emptyList());

        Mockito.when(learnerProgressService.getAllProgress()).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/v1/learnerProgress/getAllProgress"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No progress found"));
    }

    @Test
    void testGetCourseIdCountsReturnsOk() throws Exception {
        Map<String, Map<String, Integer>> dummyCourseCounts = Map.of(
                "course1", Map.of("completed", 5, "inProgress", 3)
        );

        Mockito.when(learnerProgressService.getCourseIdCounts()).thenReturn(dummyCourseCounts);

        mockMvc.perform(get("/api/v1/learnerProgress/getCourseIdCounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("CourseId counts retrieved successfully"))
                .andExpect(jsonPath("$.data.course1.completed").value(5));
    }
}
