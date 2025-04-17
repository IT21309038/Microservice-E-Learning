package com.olp.stripeservice.service;

import com.olp.stripeservice.dto.EmailRequestDto;
import com.olp.stripeservice.dto.PaymentChargeDto;
import com.olp.stripeservice.model.Payment;
import com.olp.stripeservice.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

//    @Value("${notification.service.url}")
//    private String notificationServiceUrl;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentChargeDto charge(PaymentChargeDto chargeRequest) {

            chargeRequest.setSuccess(false);

        if (!isValidCardNumber(chargeRequest.getCardNumber())) {
            chargeRequest.setMessage("Invalid card number provided.");
            return chargeRequest; // Stop further processing
        }

        if (!isValidExpiryDate(chargeRequest.getCardExpiryMonth(), chargeRequest.getCardExpiryYear())) {
            chargeRequest.setMessage("Expired card expiry date.");
            return chargeRequest;
        }
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
            chargeParams.put("currency", "USD");
            chargeParams.put("description", "Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", "test");
            Map<String, Object> metaData = new HashMap<>();
            metaData.putAll(chargeRequest.getAdditionalInfo());
            chargeParams.put("metadata", metaData);
            chargeRequest.setMessage("Success");
                chargeRequest.setSuccess(true);

                // Save payment details to MongoDB
                Payment payment = new Payment();
                payment.setUserId(chargeRequest.getUserId());
                payment.setCourseId(chargeRequest.getCourseId());
                payment.setAmount(chargeRequest.getAmount());
                payment.setDescription("Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
                payment.setMetaData(chargeRequest.getAdditionalInfo());
                paymentRepository.save(payment);

                // Call notification service
//                EmailRequestDto emailRequestDTO = new EmailRequestDto();
//                emailRequestDTO.setToEmail("designbyhasitha@gmail.com"); // Make this dynamic
//                emailRequestDTO.setSubject("Payment Confirmation");
//                emailRequestDTO.setBody("Your payment was successful.");

//                sendNotification(emailRequestDTO);

            return chargeRequest;

    }

    private void sendNotification(EmailRequestDto emailRequestDto) {

        webClientBuilder.build().post()
                .uri("http://email-service/api/v1/email/send-email")
                .body(Mono.just(emailRequestDto), EmailRequestDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }

    private boolean isValidExpiryDate(String expiryMonth, String expiryYear) {
        if (expiryMonth == null || expiryYear == null) {
            return false;
        }

        try {
            int month = Integer.parseInt(expiryMonth);
            int year = Integer.parseInt(expiryYear);

            // Month must be 1 to 12
            if (month < 1 || month > 12) {
                return false;
            }

            java.time.YearMonth currentYearMonth = java.time.YearMonth.now();
            java.time.YearMonth cardYearMonth = java.time.YearMonth.of(year, month);

            // Card expiry must be after or equal to the current month
            return !cardYearMonth.isBefore(currentYearMonth);

        } catch (NumberFormatException e) {
            return false;
        }
    }


    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }

        // Remove spaces and dashes if any
        cardNumber = cardNumber.replaceAll("\\s+", "").replaceAll("-", "");

        // Must be 13 to 19 digits (Visa/MasterCard/Amex standards)
        if (!cardNumber.matches("\\d{13,19}")) {
            return false;
        }

        // Apply Luhn algorithm
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }


}
