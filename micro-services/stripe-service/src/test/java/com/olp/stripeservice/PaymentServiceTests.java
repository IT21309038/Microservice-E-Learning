package com.olp.stripeservice;

import com.olp.stripeservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.olp.stripeservice.dto.PaymentChargeDto;
import com.olp.stripeservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class PaymentServiceTests {

	private PaymentService paymentService;

	@BeforeEach
	void setUp() {
		paymentService = new PaymentService(null); // Passing null because MongoDB is not needed for validation tests
	}

	@Test
	void testValidCardNumber() {
		String validCard = "4242424242424242";
		assertTrue(paymentService.isValidCardNumber(validCard), "Card number should be valid");
	}

	@Test
	void testInvalidCardNumber_WrongLength() {
		String invalidCard = "424242424242"; // Only 12 digits
		assertFalse(paymentService.isValidCardNumber(invalidCard), "Card number should be invalid due to wrong length");
	}

	@Test
	void testInvalidCardNumber_FailedLuhn() {
		String invalidCard = "1234567890123456"; // Fails Luhn check
		assertFalse(paymentService.isValidCardNumber(invalidCard), "Card number should be invalid (fails Luhn)");
	}

	@Test
	void testValidExpiryDate_FutureDate() {
		String futureMonth = "12";
		String futureYear = String.valueOf(java.time.Year.now().plusYears(1).getValue()); // Next year
		assertTrue(paymentService.isValidExpiryDate(futureMonth, futureYear), "Expiry date should be valid (future)");
	}

	@Test
	void testInvalidExpiryDate_PastDate() {
		String pastMonth = "01";
		String pastYear = String.valueOf(java.time.Year.now().minusYears(1).getValue()); // Previous year
		assertFalse(paymentService.isValidExpiryDate(pastMonth, pastYear), "Expiry date should be invalid (past)");
	}

	@Test
	void testInvalidExpiryDate_InvalidMonth() {
		String invalidMonth = "13"; // Month should be 1-12
		String currentYear = String.valueOf(java.time.Year.now().getValue());
		assertFalse(paymentService.isValidExpiryDate(invalidMonth, currentYear), "Expiry date should be invalid (invalid month)");
	}

	@Test
	void testCharge_InvalidCard_ShouldReturnFailure() {
		PaymentChargeDto request = new PaymentChargeDto();
		request.setCardNumber("1234567890123456"); // Invalid card
		request.setCardExpiryMonth("12");
		request.setCardExpiryYear("2027");

		PaymentChargeDto response = paymentService.charge(request);

		assertFalse(response.getSuccess());
		assertEquals("Invalid card number provided.", response.getMessage());
	}

	@Test
	void testCharge_ExpiredCard_ShouldReturnFailure() {
		PaymentChargeDto request = new PaymentChargeDto();
		request.setCardNumber("4242424242424242"); // Valid card
		request.setCardExpiryMonth("01");
		request.setCardExpiryYear("2022"); // Expired year

		PaymentChargeDto response = paymentService.charge(request);

		assertFalse(response.getSuccess());
		assertEquals("Expired card expiry date.", response.getMessage());
	}

	@Test
	void contextLoads() {
	}

}
