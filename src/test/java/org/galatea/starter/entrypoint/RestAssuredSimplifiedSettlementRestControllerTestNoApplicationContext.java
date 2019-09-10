package org.galatea.starter.entrypoint;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.MessageTranslationConfig;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.service.SettlementService;
import org.galatea.starter.utils.rest.FuseHttpTraceFilter;
import org.galatea.starter.utils.translation.ITranslator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@Slf4j
@Import({MessageTranslationConfig.class, FuseHttpTraceFilter.class})
@RunWith(JUnitParamsRunner.class)
//@TestPropertySource(locations="classpath:application.properties")
//@ActiveProfiles("local-test")
public class RestAssuredSimplifiedSettlementRestControllerTestNoApplicationContext extends ASpringTest {
  @Value("${mvc.settleMissionPath}")
  private String settleMissionPath;

  @Value("${mvc.getMissionPath}")
  private String getMissionPath;

  @Value("${mvc.getMissionsPath}")
  private String getMissionsPath;

  @Value("${mvc.deleteMissionPath}")
  private String deleteMissionPath;

  @Value("${mvc.updateMissionPath}")
  private String updateMissionPath;

  @Autowired
  ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementTranslator;

  @Autowired
  ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator;

  @Autowired
  ITranslator<SettlementMissionMessage, SettlementMission> settlementMissionMsgTranslator;

  @MockBean
  private SettlementService mockSettlementService;

  @Autowired
  SettlementRestController settlementRestController;

  private ObjectMapper objectMapper;

  private JacksonTester<TradeAgreementMessages> agreementJsonTester;

  private JacksonTester<List<Long>> missionIdJsonTester;

  @Before
  public void setup() {
    objectMapper = new ObjectMapper();
    JacksonTester.initFields(this, objectMapper);

//    RestAssuredMockMvc.standaloneSetup(new SettlementRestController(mockSettlementService, tradeAgreementTranslator, settlementMissionTranslator, settlementMissionMsgTranslator));

    //The properties are added to settlementRestController if we create it via autowiring with the line below and
    //@Autowired
    //SettlementRestController settlementRestController
    //above, but not if we're creating "new SettlementRestController..."

    //If we just have:
    //RestAssuredMockMvc.standaloneSetup(settlementRestController)
    //here then we have problems as the properties in @PostMapping and @GetMapping in SettlementRestController cannot be found.
    //MockMvcBuilders.standaloneSetup(...) used based on https://stackoverflow.com/a/47221283
    //We also needed to add the same converters as we use in practice (see MvcConfig.java) as the default MockMvc converters were having problems with posts with XML bodies.  More info at https://stackoverflow.com/questions/12514285/registrer-mappingjackson2httpmessageconverter-in-spring-3-1-2-with-jaxb-annotati
    RestAssuredMockMvc.standaloneSetup(
        MockMvcBuilders.standaloneSetup(settlementRestController).
            addPlaceholderValue("mvc.settleMissionPath", settleMissionPath).
            addPlaceholderValue("mvc.deleteMissionPath", deleteMissionPath).
            addPlaceholderValue("mvc.updateMissionPath", updateMissionPath).
            addPlaceholderValue("mvc.getMissionsPath", getMissionsPath).
            addPlaceholderValue("mvc.getMissionPath", getMissionPath).
            setMessageConverters(new MappingJackson2HttpMessageConverter(), new Jaxb2RootElementHttpMessageConverter()));
  }

  @Test
  @FileParameters(value = "src/test/resources/testSettleAgreement.data",
      mapper = JsonTestFileMapper.class)
  public void testSettleAgreement_JSON(final String agreementJson,
      final String expectedMissionIdJson) throws Exception {
    TradeAgreement expectedAgreement = TradeAgreement.builder().instrument("IBM")
        .internalParty("INT-1").externalParty("EXT-1").buySell("B").qty(100d).build();

    log.info("Agreement json to post {}", agreementJson);

    List<Long> expectedMissionIds = missionIdJsonTester.parse(expectedMissionIdJson).getObject();

    List<String> expectedResponseJsonList = expectedMissionIds.stream()
        .map(id -> "/settlementEngine/mission/" + id).collect(Collectors.toList());

    log.info("Expected json response {}", expectedResponseJsonList);

    TradeAgreementMessages agreementMessages = agreementJsonTester.parse(agreementJson).getObject();
    log.info("Agreement objects that the service will expect {}", agreementMessages);

    BDDMockito.given(this.mockSettlementService.spawnMissions(singletonList(expectedAgreement)))
        .willReturn(Sets.newTreeSet(expectedMissionIds));

    given().
        log().ifValidationFails().
        contentType(ContentType.JSON).
        body(agreementJson).
        when().
        post("/settlementEngine?requestId=1234").
        then().
        log().ifValidationFails().
        body("spawnedMissions", equalTo(expectedResponseJsonList)).
        statusCode(200);
  }

  @Test
  public void testSettleAgreement_XML() throws Exception {
    final long expectedMissionId = 1091;

    TradeAgreementMessages messages = TradeAgreementMessages.builder().agreement(
        TradeAgreementMessage.builder().instrument("IBM").internalParty("INT-1")
            .externalParty("EXT-1").buySell("B").qty(100d).build())
        .build();

    JAXBContext context = JAXBContext.newInstance(TradeAgreementMessages.class);
    Marshaller m = context.createMarshaller();
    StringWriter writer = new StringWriter();
    m.marshal(messages, writer);
    String xml = writer.toString();

    log.info("Agreement xml to post {}", xml);

    List<Long> expectedMissionIds = Collections.singletonList(expectedMissionId);

    String expectedXmlEntry = "/settlementEngine/mission/" + expectedMissionId;

    log.info("Expected xml response {}", expectedXmlEntry);
    
    BDDMockito.given(this.mockSettlementService.spawnMissions(toTradeAgreements(messages)))
        .willReturn(Sets.newTreeSet(expectedMissionIds));

    given().
        log().ifValidationFails().
        contentType(ContentType.XML).
        accept(ContentType.XML).
        body(xml).
        when().
        post("/settlementEngine?requestId=1234").
        then().
        log().ifValidationFails().
        body("settlementResponse.spawnedMission", equalTo(expectedXmlEntry)).
        statusCode(200);
  }

  private List<TradeAgreement> toTradeAgreements(TradeAgreementMessages messages) {
    return tradeAgreementTranslator.translate(messages);
  }



  @Configuration
  @Import(SettlementRestController.class)
  static class PropertyConfig {
    @Bean
    PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
      PropertyPlaceholderConfigurer propertyPlaceholderConfigurer =  new PropertyPlaceholderConfigurer();
      propertyPlaceholderConfigurer.setLocation(new ClassPathResource("application.properties"));
      return propertyPlaceholderConfigurer;
    }
  }
}
