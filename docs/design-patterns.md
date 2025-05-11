The design patterns and principles applied:

### **Design Patterns**
1. **Builder Pattern**:
    - Used in the `Product` and `ProductPriceHistory` models to create immutable objects with a fluent API.

2. **Singleton Pattern**:
    - Applied to Spring-managed beans like `SecurityConfig`, `JwtProviderService`, and other `@Configuration` or `@Service` classes, ensuring a single instance per application context.

3. **Factory Pattern**:
    - Used implicitly by Spring to create and manage beans (e.g., `ReactiveAuthenticationManager`, `ReactiveUserDetailsService`).

4. **Strategy Pattern**:
    - Implemented in the `JwtAuthenticationFilter` to handle authentication logic dynamically based on the token.

5. **Template Method Pattern**:
    - Spring Security's `SecurityWebFilterChain` uses this pattern to define the security flow while allowing customization of specific steps.

6. **Repository Pattern**:
    - Used in `UserRepository` and other repositories to abstract database operations.

7. **Mapper Pattern**:
    - Applied with `PRODUCT_EVENT_MAPPER` (likely implemented using MapStruct) to convert between domain models and event DTOs.

8. **Observer Pattern**:
    - Used in Kafka integration, where the service publishes events (`ProductCreatedEventV1`, `ProductUpdatedEventV1`, etc.) to Kafka topics, and consumers react to these events.

9. **Decorator Pattern**:
    - Applied in the `PasswordEncoder` bean (`BCryptPasswordEncoder`) to add encryption functionality to raw passwords.

10. **Reactive Streams Pattern**:
    - Leveraged throughout the project with WebFlux and Reactor to handle asynchronous, non-blocking data streams.

---

### **Principles**
1. **Single Responsibility Principle (SRP)**:
    - Each class has a single responsibility, e.g., `SecurityConfig` handles security configuration, `ProductPriceServiceImpl` handles product price logic.

2. **Open/Closed Principle (OCP)**:
    - The system is open for extension but closed for modification, e.g., new Kafka events or security rules can be added without modifying existing logic.

3. **Dependency Inversion Principle (DIP)**:
    - High-level modules depend on abstractions (e.g., `ReactiveAuthenticationManager` depends on `ReactiveUserDetailsService`).

4. **Interface Segregation Principle (ISP)**:
    - Interfaces are kept minimal and focused, e.g., `UserRepository` only defines methods for user-related database operations.

5. **Liskov Substitution Principle (LSP)**:
    - Subtypes (e.g., `ReactiveUserDetailsService`) can replace their base types without altering the correctness of the program.

6. **Separation of Concerns**:
    - Clear separation between layers (e.g., configuration, service, repository, and security).

7. **DRY (Don't Repeat Yourself)**:
    - Reusable components like `JwtAuthenticationFilter` and `ReactiveAuthenticationManager` avoid duplication.

8. **KISS (Keep It Simple, Stupid)**:
    - The architecture is straightforward, leveraging Spring Boot's conventions and abstractions.

9. **YAGNI (You Aren't Gonna Need It)**:
    - Only necessary features are implemented, avoiding over-engineering.

10. **Convention Over Configuration**:
    - Spring Boot's default configurations reduce the need for extensive custom setup.

11. **Reactive Manifesto**:
    - The project embraces reactive programming principles for scalability and responsiveness.