package com.alphaka.userservice.config;

import com.alphaka.userservice.dto.response.ErrorResponse;
import com.alphaka.userservice.exception.ErrorCode;
import com.alphaka.userservice.swagger.annotation.ApiErrorResponseExamples;
import com.alphaka.userservice.swagger.annotation.ApiSuccessResponseExample;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private final ObjectMapper objectMapper;
    @Value("${gateway.url}")
    private String baseUrl;

    @Bean
    public OpenAPI customOpenAPI() {

        // AccessToken Security Scheme 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Security Requirement 정의
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("AccessToken");

        return new OpenAPI()
                .info(new Info().title("유저 서비스"))
                .addSecurityItem(securityRequirement)  // Security Requirement 추가
                .schemaRequirement("AccessToken", securityScheme)  // Security Scheme 추가
                .addTagsItem(new Tag().name("External API").description("프론트엔드에서 사용하는 외부 API"))
                .addTagsItem(new Tag().name("Internal API").description("내부 서비스 간 호출에 사용하는 API"));
    }


    @Bean
    public OpenApiCustomizer customOpenApiCustomiser() {
        return openApi -> {
            // 게이트웨이 경로
            Server gatewayServer = new Server();
            gatewayServer.setUrl(baseUrl + "/user-service");
            gatewayServer.setDescription("게이트웨이 일반 접근");

            // 유저 서비스 직접 경로
            Server gatewayServerWithAuth = new Server();
            gatewayServerWithAuth.setUrl(baseUrl + "/user-service/auth");
            gatewayServerWithAuth.setDescription("게이트웨이 인증 접근");

            // Servers 리스트에 추가
            openApi.setServers(List.of(gatewayServer, gatewayServerWithAuth));
        };
    }

    @Bean
    public OperationCustomizer customizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            ApiSuccessResponseExample apiSuccessResponseExample = handlerMethod.getMethodAnnotation(
                    ApiSuccessResponseExample.class);
            if (apiSuccessResponseExample != null) {
                generateSuccessCodeResponseExample(operation,
                        apiSuccessResponseExample.responseClass(),
                        apiSuccessResponseExample.genericType(),
                        apiSuccessResponseExample.data(),
                        apiSuccessResponseExample.status().value());
            }

            ApiErrorResponseExamples apiErrorResponseExamples =
                    handlerMethod.getMethodAnnotation(ApiErrorResponseExamples.class);
            if (apiErrorResponseExamples != null) {
                generateErrorCodeResponseExamples(operation,
                        apiErrorResponseExamples.value(),
                        apiErrorResponseExamples.name(),
                        apiErrorResponseExamples.description());
            }

            return operation;
        };
    }

    // 정상 응답 예시 추가
    private void generateSuccessCodeResponseExample(Operation operation, Class<?> responseClass, Class<?> genericType,
                                                    boolean data, int status) {
        if (!data) {
            ApiResponse apiResponse = new ApiResponse()
                    .description("Successful response")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().example("{\"status\": " + status + "}")));
            operation.getResponses().addApiResponse("200", apiResponse);
            return;
        }
        try {

            //예시 객체 생성
            Object exampleInstance;
            if (isImmutableClass(responseClass)) {
                exampleInstance = generateImmutableInstance(responseClass);
            }
            else if (Collection.class.isAssignableFrom(responseClass)) {
                Collection<Object> collectionInstance = createCollectionInstance(responseClass);
                Object genericExampleInstace = generateExampleInstance(genericType);
                collectionInstance.add(genericExampleInstace);
                collectionInstance.add(genericExampleInstace);
                exampleInstance = collectionInstance;
            } else {
                exampleInstance = generateExampleInstance(responseClass);
            }

            // ApiResponse 생성
            com.alphaka.userservice.dto.response.ApiResponse<Object> successResponse =
                    com.alphaka.userservice.dto.response.ApiResponse.createSuccessResponseWithData(status,
                            exampleInstance);

            // 직렬화
            String json = objectMapper.writeValueAsString(successResponse);

            // ApiResponse 성공 응답 예시 추가
            ApiResponse apiResponse = new ApiResponse()
                    .description("정상 응답")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().example(json)));

            operation.getResponses().addApiResponse("200", apiResponse);

        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    // 여러 개의 에러 응답 예시 추가
    private void generateErrorCodeResponseExamples(Operation operation, ErrorCode[] errorCodes,
                                                   String[] names, String[] descriptions) {
        ApiResponses responses = operation.getResponses();

        // ExampleHolder(에러 응답값) 객체를 만들고 에러 코드별로 그룹화
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders = new HashMap<>();
        for (int i = 0; i < errorCodes.length; i++) {
            ErrorCode errorCode = errorCodes[i];
            String name = names[i];
            String description = descriptions[i];
            int status = errorCode.getStatus();

            ExampleHolder exampleHolder = ExampleHolder.builder()
                    .holder(getSwaggerExample(errorCode, description))
                    .name(name)
                    .status(status)
                    .build();

            statusWithExampleHolders
                    .computeIfAbsent(status, l -> new ArrayList<>())
                    .add(exampleHolder);

        }

        // ExampleHolders를 ApiResponses에 추가
        addExamplesToResponses(responses, statusWithExampleHolders);
    }


    @Getter
    @Builder
    public static class ExampleHolder {
        private Example holder;
        private String name;
        private int status;
    }

    private Example getSwaggerExample(ErrorCode errorCode, String description) {
        ErrorResponse errorResponse = new ErrorResponse(
                errorCode.getStatus(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
        Example example = new Example();
        example.description(description);
        example.setValue(errorResponse);
        return example;
    }

    private void addExamplesToResponses(ApiResponses responses,
                                        Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    ApiResponse apiResponse = new ApiResponse();

                    v.forEach(
                            exampleHolder -> mediaType.addExamples(
                                    exampleHolder.getName(),
                                    exampleHolder.getHolder()
                            )
                    );
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setContent(content);
                    responses.addApiResponse(String.valueOf(status), apiResponse);
                }
        );
    }


    private Object generateExampleInstance(Class<?> clazz) {
        try {

            Object instance = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                Schema schemaAnnotation = field.getAnnotation(Schema.class);
                if (schemaAnnotation != null) {
                    String exampleValue = schemaAnnotation.example();
                    if (!exampleValue.isEmpty()) {
                        Class<?> fieldType = field.getType();

                        // example 값을 필드 타입에 맞게 변환
                        Object convertedValue = parseValue(fieldType, exampleValue);
                        field.set(instance, convertedValue);
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Object parseValue(Class<?> fieldType, String exampleValue) {
        try {
            if (fieldType == String.class) {
                return exampleValue;
            } else if (fieldType == Integer.class || fieldType == int.class) {
                return Integer.valueOf(exampleValue);
            } else if (fieldType == Long.class || fieldType == long.class) {
                return Long.valueOf(exampleValue);
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                return Boolean.valueOf(exampleValue);
            } else if (fieldType == Double.class || fieldType == double.class) {
                return Double.valueOf(exampleValue);
            } else if (fieldType == Float.class || fieldType == float.class) {
                return Float.valueOf(exampleValue);
            } else if (fieldType.isEnum()) {
                Method method = fieldType.getMethod("valueOf", String.class);
                return method.invoke(null, exampleValue);
            } else if (fieldType == LocalDate.class) {
                // LocalDate 타입 처리
                return LocalDate.parse(exampleValue);
            } else {
                // 다른 객체 타입인 경우 재귀적으로 인스턴스 생성
                return generateExampleInstance(fieldType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Collection<Object> createCollectionInstance(Class<?> clazz) {
        if (List.class.isAssignableFrom(clazz)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(clazz)) {
            return new HashSet<>();
        } else {
            // 기본적으로 ArrayList 사용
            return new ArrayList<>();
        }
    }

    private boolean isImmutableClass(Class<?> clazz) {
        return clazz.equals(Integer.class) || clazz.equals(Boolean.class)
                || clazz.equals(String.class) || clazz.equals(Long.class);
    }

    private Object generateImmutableInstance(Class<?> clazz) {
        if (clazz.equals(Integer.class) || clazz.equals(Long.class)) return 1;
        if (clazz.equals(Boolean.class)) return true;
        if (clazz.equals(String.class)) return "example";
        return null;
    }

}

