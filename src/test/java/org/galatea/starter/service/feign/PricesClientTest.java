//package org.galatea.starter.service.feign;
//
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.galatea.starter.Application;
//import org.galatea.starter.domain.ModelResponse.AlphaPrice;
//import org.galatea.starter.domain.ModelResponse.Posts;
//import org.junit.Test;
//import org.junit.experimental.categories.Category;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootContextLoader;
//import org.springframework.test.context.ContextConfiguration;
//
//@RequiredArgsConstructor
//@Slf4j
//@Category(org.galatea.starter.IntegrationTestCategory.class)
//@ContextConfiguration (classes = {Application.class}, loader = SpringBootContextLoader.class)
//public class PricesClientTest {
//
//
//  @Autowired
//  PricesClient pricesClient;
//
//  @Test
//  public void myTest() {
//    List<Posts> posts = pricesClient.getAlphaPrices();
//
//    System.out.println("hello");
//  }
//
//
//
//}
