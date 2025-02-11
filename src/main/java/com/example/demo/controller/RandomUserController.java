package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/random-users")
@Validated
public class RandomUserController {

    @Value("${api.key}")
    private String apiKey;

    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public RandomUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<List<UserDTO>> createNewRandomUser() throws BadRequestException {
        List<String> firstNames = getRandomFirstNames(200);
        List<String> lastNames = getRandomLastNames(200);
        List<String> addresses = getAddress(200);
        List<UserDTO> usersDTOToAdd = IntStream.range(0, firstNames.size()).mapToObj(
                index -> new UserDTO(firstNames.get(index), lastNames.get(index), addresses.get(index), getRandomDate())
        ).toList();

        return new ResponseEntity<>(usersDTOToAdd.stream().map(userService::saveUser).toList(), HttpStatus.CREATED);

    }

    private List<String> getAddress(Integer quantity) throws BadRequestException {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity needs to be higher than 0");
        }

        String url = "https://randommer.io/api/Misc/Random-Address?number=" + quantity + "&culture=en";
        HttpEntity<String> entity = getHttpEntity();

        ResponseEntity<List<String>> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    private List<String> getRandomFirstNames(Integer quantity) throws BadRequestException {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity needs to be higher than 0");
        }

        String url = "https://randommer.io/api/Name?nameType=firstName&quantity=" + quantity;
        HttpEntity<String> entity = getHttpEntity();

        ResponseEntity<List<String>> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    private List<String> getRandomLastNames(Integer quantity) throws BadRequestException {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity needs to be higher than 0");
        }

        String url = "https://randommer.io/api/Name?nameType=surname&quantity=" + quantity;
        HttpEntity<String> entity = getHttpEntity();

        ResponseEntity<List<String>> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    private HttpEntity<String> getHttpEntity() {
        // Set headers
        HttpHeaders headers = getHttpHeaders();

        // Create request entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);
        return entity;
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", apiKey);
        return headers;
    }

    private LocalDate getRandomDate() {
        LocalDate startDate = LocalDate.of(1980, 1, 1);
        LocalDate endDate = LocalDate.of(2000, 12, 31);

        // Get the number of days between the two dates
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        // Generate a random number of days to add to the start date
        long randomDays = ThreadLocalRandom.current().nextLong(daysBetween + 1);

        // Return the random date
        return startDate.plusDays(randomDays);
    }
}
