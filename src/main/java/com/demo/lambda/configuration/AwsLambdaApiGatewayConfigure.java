package com.demo.lambda.configuration;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.demo.lambda.model.Employee;
import com.demo.lambda.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsLambdaApiGatewayConfigure {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsLambdaApiGatewayConfigure.class);
  private ObjectMapper objectMapper = new ObjectMapper();
  private final EmployeeService employeeService;

  public AwsLambdaApiGatewayConfigure(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @Bean
  public Supplier<List<Employee>> getEmployees() {
    return () -> employeeService.getEmployees();
  }

  @Bean
  public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> findByName() {
    return proxyRequestEvent -> {
      LOGGER.info("employees: {}", employeeService.getEmployees());
      String param = proxyRequestEvent.getQueryStringParameters().get("name");
      LOGGER.info("proxyRequestEvent: {}", param);
      List<Employee> employees = employeeService.getEmployees().stream()
          .filter(student -> student.getName().equals(param)).collect(
              Collectors.toList());
      LOGGER.info("result: {}", employees);
      APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
      parseJson(employees, responseEvent);
      responseEvent.setStatusCode(200);
      responseEvent.setHeaders(Collections.singletonMap("Content-type", "application/json"));
      return responseEvent;
    };
  }

  @Bean
  public Function<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> getResponse() {
    return apiGatewayProxyRequestEvent -> {
      APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
      responseEvent.setBody(apiGatewayProxyRequestEvent.getBody());
      responseEvent.setStatusCode(200);
      responseEvent.setHeaders(Collections.singletonMap("Content-type", "application/json"));
      return responseEvent;
    };
  }

  private void parseJson(List<Employee> employees, APIGatewayProxyResponseEvent responseEvent) {
    try {
      responseEvent.setBody(objectMapper.writeValueAsString(employees));
    } catch (JsonProcessingException e) {
      LOGGER.error("Error JsonProcessingException: {}", e.getMessage());
    }
  }
}
