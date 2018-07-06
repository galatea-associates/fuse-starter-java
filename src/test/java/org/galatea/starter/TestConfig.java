package org.galatea.starter;

import feign.codec.Decoder;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.utils.ObjectSupplier;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Provides beans used in unit tests.
 */
@Configuration
@EnableFeignClients
public class TestConfig {


  /**
   * @return a bean to create dummy trade agreements
   */
  @Bean
  public ObjectSupplier<TradeAgreement> tradeAgreementSupplier() {
    return () -> TradeAgreement.builder().id(0L).instrument("IBM").internalParty("INT-1")
        .externalParty("EXT-1").buySell("B").qty(100d).build();
  }

  /**
   * @return a bean to create dummy trade agreement messages
   */
  @Bean
  public ObjectSupplier<TradeAgreementMessage> tradeAgreementMessageSupplier() {
    return () -> TradeAgreementMessage.builder().instrument("IBM").internalParty("INT-1")
        .externalParty("EXT-1").buySell("B").qty(100d).build();
  }

  @Bean
  public ObjectSupplier<TradeAgreementProtoMessage> tradeAgreementProtoMessageSupplier() {
    return () -> TradeAgreementProtoMessage.newBuilder().setInstrument("IBM")
        .setInternalParty("INT-1").setExternalParty("EXT-1").setBuySell("B").setQty(100).build();
  }

  @Bean
  public ObjectSupplier<SettlementMission> settlementMissionSupplier() {
    return () -> SettlementMission.builder().id(100L).depot("DTC").externalParty("EXT-1")
        .instrument("IBM").direction("REC").qty(100d).build();
  }

  @Bean
  public Decoder feignDecoder() {
    HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
    ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
    return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
  }

}
