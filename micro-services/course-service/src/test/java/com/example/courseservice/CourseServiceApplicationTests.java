package com.example.courseservice;

import com.example.courseservice.exception.CourseCollectionException;
import com.example.courseservice.model.Course;
import com.example.courseservice.repo.CourseRepo;
import com.example.courseservice.service.CourseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseImplTest {

	@InjectMocks
	private CourseImpl courseService;

	@Mock
	private CourseRepo courseRepo;

	private Course sampleCourse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		sampleCourse = new Course();
		sampleCourse.setId("1");
		sampleCourse.setCourseId("C101");
		sampleCourse.setName("Spring Boot Basics");
		sampleCourse.setConductorId("T100");
	}

	@Test
	void testGetAllCoursesReturnsList() {
		when(courseRepo.findAll()).thenReturn(List.of(sampleCourse));

		List<Course> result = courseService.getAllCourses();

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		verify(courseRepo).findAll();
	}

	@Test
	void testGetAllCoursesReturnsEmptyList() {
		when(courseRepo.findAll()).thenReturn(new ArrayList<>());

		List<Course> result = courseService.getAllCourses();

		assertTrue(result.isEmpty());
	}

	@Test
	void testGetUnApprovedCoursesReturnsList() {
		when(courseRepo.findUnapprovedCourses()).thenReturn(List.of(sampleCourse));

		List<Course> result = courseService.getUnApprovedCourses();

		assertFalse(result.isEmpty());
	}

	@Test
	void testCreateCourseSuccess() throws CourseCollectionException {
		when(courseRepo.findByCourseId("C101")).thenReturn(Optional.empty());
		when(courseRepo.findByName("Spring Boot Basics")).thenReturn(Optional.empty());

		courseService.CreateCourse(sampleCourse, "http://course.url");

		assertEquals("http://course.url", sampleCourse.getUrl());
		assertFalse(sampleCourse.isApproved());
		verify(courseRepo).save(sampleCourse);
	}

	@Test
	void testCreateCourseThrowsCourseCodeException() {
		when(courseRepo.findByCourseId("C101")).thenReturn(Optional.of(sampleCourse));

		CourseCollectionException exception = assertThrows(CourseCollectionException.class, () -> {
			courseService.CreateCourse(sampleCourse, "http://course.url");
		});

		assertTrue(exception.getMessage().contains("already exists"));
	}

	@Test
	void testCreateCourseThrowsCourseNameException() {
		when(courseRepo.findByCourseId("C101")).thenReturn(Optional.empty());
		when(courseRepo.findByName("Spring Boot Basics")).thenReturn(Optional.of(sampleCourse));

		CourseCollectionException exception = assertThrows(CourseCollectionException.class, () -> {
			courseService.CreateCourse(sampleCourse, "http://course.url");
		});

		assertTrue(exception.getMessage().contains("already exists"));
	}

	@Test
	void testGetCourseByCodeSuccess() throws CourseCollectionException {
		when(courseRepo.findByCourseId("C101")).thenReturn(Optional.of(sampleCourse));

		Course result = courseService.getCourseByCode("C101");

		assertEquals("C101", result.getCourseId());
	}

	@Test
	void testGetCourseByCodeThrowsException() {
		when(courseRepo.findByCourseId("C101")).thenReturn(Optional.empty());

		assertThrows(CourseCollectionException.class, () -> {
			courseService.getCourseByCode("C101");
		});
	}

	@Test
	void testGetAllCoursesByConductorIdReturnsList() {
		when(courseRepo.findCoursesByConductorId("T100")).thenReturn(List.of(sampleCourse));

		List<Course> result = courseService.getAllCoursesByConductorId("T100");

		assertEquals(1, result.size());
	}

	@Test
	void testGetApprovedCoursesReturnsList() {
		when(courseRepo.findApprovedCourses()).thenReturn(List.of(sampleCourse));

		List<Course> result = courseService.getApprovedCourses();

		assertFalse(result.isEmpty());
	}
}

