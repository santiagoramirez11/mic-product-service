package com.banreservas.product.e2e;

import com.banreservas.openapi.models.AuthenticationRequestDto;
import com.banreservas.openapi.models.AuthenticationResponseDto;
import com.banreservas.openapi.models.LoginCredentialErrorResponseDto;
import com.banreservas.product.MicProductServiceApplicationTests;
import com.banreservas.product.mapper.ProductDtoMapper;
import com.banreservas.product.model.Product;
import com.banreservas.product.repository.ProductRepository;
import com.banreservas.product.repository.UserRepository;
import com.banreservas.openapi.models.ProductRequestDto;
import com.banreservas.openapi.models.ProductResponseDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

@Slf4j
@Testcontainers(disabledWithoutDocker = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductSystemEndToEndTest extends MicProductServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private String tokenHeader;

    @PostConstruct
    public void postConstruct() {
        var authRequest = new AuthenticationRequestDto();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin123");

        String accessToken = Objects.requireNonNull(client.post().uri("/api/v1/auth/login")
                        .bodyValue(authRequest)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponseDto.class)
                        .returnResult().getResponseBody())
                .getAccessToken();
        tokenHeader = "Bearer " + accessToken;
    }

    @Test
    @Order(1)
    void AuthController_whenRegisterUser_thenSuccess() {
        var username = "user1";
        var roles = List.of("USER");
        com.banreservas.openapi.models.UserRegistrationRequestDto userRegistrationRequestDto = new com.banreservas.openapi.models.UserRegistrationRequestDto();
        userRegistrationRequestDto.setUsername(username);
        userRegistrationRequestDto.setPassword("password123");
        userRegistrationRequestDto.setRoles(roles);

        com.banreservas.openapi.models.UserResponseDto userResponseDto = new com.banreservas.openapi.models.UserResponseDto();
        userResponseDto.setUsername(username);
        userResponseDto.setRoles(roles);

        client.post()
                .uri("/api/v1/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .bodyValue(userRegistrationRequestDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(com.banreservas.openapi.models.UserResponseDto.class)
                .isEqualTo(userResponseDto);

        StepVerifier.create(userRepository.findByUsername(username))
                .expectNextMatches(user -> user.getUsername().equals(username) && user.getRoles().contains("USER"))
                .verifyComplete();
    }

    @Test
    @Order(2)
    void AuthController_whenLoginUserWithBadCredential_thenBadRequest() {
        var authRequest = new AuthenticationRequestDto();
        authRequest.setUsername("invalidUser");
        authRequest.setPassword("invalidAdmin");

        LoginCredentialErrorResponseDto invalidCredentials = new LoginCredentialErrorResponseDto("Invalid Credentials");

        client.post().uri("/api/v1/auth/login")
                .bodyValue(authRequest)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(LoginCredentialErrorResponseDto.class)
                .isEqualTo(invalidCredentials);
    }

    @Test
    @Order(3)
    void ProductService_whenCreateProduct_thenAllCreatedCycleSuccess() {
        Product product = Product.builder()
                .name("Monitor")
                .sku("0000340064")
                .category("Tech")
                .description("Monitor Dell")
                .price(6004d)
                .build();
        ProductRequestDto productRequestDto = new ProductRequestDto("Monitor", "Monitor Dell", 6004d, "Tech", "0000340064");
        ProductResponseDto productResponseDtoExpected = ProductDtoMapper.MAPPER.toProductDto(product);

        ProductResponseDto responseBody = client.post()
                .uri("/api/v1/products")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .bodyValue(productRequestDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();
        Assertions.assertThat(responseBody).usingRecursiveComparison().ignoringFields("id").isEqualTo(productResponseDtoExpected);
    }

    @Test
    @Order(4)
    void ProductService_whenGetProduct_thenSuccess() {
        Product productSaved = productRepository.save(Product.builder()
                .name("Smart TV")
                .sku("00003400645")
                .category("TV")
                .description("Samsung Smart TV")
                .price(16004d)
                .build()).block();

        ProductResponseDto productResponseDtoExpected = ProductDtoMapper.MAPPER.toProductDto(productSaved);

        client.get()
                .uri("/api/v1/products/{id}", Objects.requireNonNull(productSaved).getId())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .isEqualTo(productResponseDtoExpected);
    }

    @Test
    @Order(5)
    void ProductService_whenGetProductByCategory_thenSuccess() {
        Product productSaved = productRepository.save(Product.builder()
                .name("LG Smart TV")
                .sku("00003400676")
                .category("TV")
                .description("LG Smart TV")
                .price(15004d)
                .build()).block();

        List<ProductResponseDto> expectedResponse = productRepository.findAllByCategory(Objects.requireNonNull(productSaved).getCategory())
                .map(ProductDtoMapper.MAPPER::toProductDto)
                .collectList()
                .block();


        client.get()
                .uri("/api/v1/products/category/{category}", Objects.requireNonNull(productSaved).getCategory())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductResponseDto.class)
                .isEqualTo(Objects.requireNonNull(expectedResponse));
    }

    @Test
    @Order(6)
    void ProductService_whenUpdateProduct_thenAllUpdatedCycleSuccess() {
        Product productSaved = productRepository.save(Product.builder()
                .name("Smart Watch ")
                .sku("000034006736")
                .category("Watch")
                .description("I Watch")
                .price(10004d)
                .build()).block();
        ProductRequestDto productRequestDto = new ProductRequestDto("Monitor", "Monitor Dell", 6004d, "Tech", "000034006736");
        ProductResponseDto productResponseDtoExpected = new ProductResponseDto();
        productResponseDtoExpected.setId(Objects.requireNonNull(productSaved).getId());
        productResponseDtoExpected.setName(productRequestDto.getName());
        productResponseDtoExpected.setDescription(productRequestDto.getDescription());
        productResponseDtoExpected.setPrice(productRequestDto.getPrice());
        productResponseDtoExpected.setCategory(productRequestDto.getCategory());
        productResponseDtoExpected.setSku(productRequestDto.getSku());

        client.patch()
                .uri("/api/v1/products/{id}", Objects.requireNonNull(productSaved).getId())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .bodyValue(productRequestDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .isEqualTo(productResponseDtoExpected);
    }

    @Test
    @Order(7)
    void ProductService_whenDeleteProduct_thenAllUpdatedCycleSuccess() {
        Product productSaved = productRepository.save(Product.builder()
                .name("Mouse Dell")
                .sku("00003400673642")
                .category("Mouse")
                .description("Mouse bluetooth")
                .price(1004d)
                .build()).block();

        client.delete()
                .uri("/api/v1/products/{id}", Objects.requireNonNull(productSaved).getId())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(8)
    void ProductService_whenGetProductWithOtherCurrency_thenSuccess() {
        Product productSaved = productRepository.save(Product.builder()
                .name("USB Cable")
                .sku("000034004532")
                .category("Cable")
                .description("USB-C to USB-C")
                .price(60d)
                .build()).block();

        ProductResponseDto productResponseDtoExpected = ProductDtoMapper.MAPPER.toProductDto(productSaved);

        ProductResponseDto responseBody = client.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/products/" + Objects.requireNonNull(productSaved).getId())
                        .queryParam("currency", "USD")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).usingRecursiveComparison().ignoringFields("price").isEqualTo(productResponseDtoExpected);
    }

    @Test
    @Order(9)
    void ProductService_whenListProducts_thenSuccess() {
        Product productSaved = productRepository.save(Product.builder()
                .name("Screen protector")
                .sku("00003400574")
                .category("Smartphone")
                .description("Safe screen protector")
                .price(500d)
                .build()).block();

        List<ProductResponseDto> expectedResponse = productRepository.findAll()
                .map(ProductDtoMapper.MAPPER::toProductDto)
                .collectList()
                .block();


        client.get()
                .uri("/api/v1/products", Objects.requireNonNull(productSaved).getCategory())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(ProductResponseDto.class)
                .isEqualTo(Objects.requireNonNull(expectedResponse));
    }

    @Order(10)
    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/products/{productId}"})
    void StockService_whenGetProduct_thenProductNotFound(String endpoint) {

        final var productId = "123-abcd-12";

        com.banreservas.openapi.models.ErrorResponseDto errorResponseDto = new com.banreservas.openapi.models.ErrorResponseDto(HttpStatus.NOT_FOUND.value(), "product-not-found", String.format("Product with id: '%s' not exist", productId));

        client.get()
                .uri(endpoint, productId)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(com.banreservas.openapi.models.ErrorResponseDto.class)
                .isEqualTo(errorResponseDto);
    }

}
