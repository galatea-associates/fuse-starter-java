package org.galatea.starter.entrypoint;

import feign.Headers;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementMissionProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.SettlementResponseProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessages;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.SettlementResponseMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "TestFuseServer", url = "${fuse.url}")
interface TestFuseServer {

  @RequestMapping(method = RequestMethod.GET, value = "/hal?text={text}")
  String halEndpoint(@PathVariable("text") final String text);

  @RequestMapping(method = RequestMethod.POST, value = "/settlementEngine")
  @Headers("Content-Type: application/json")
  SettlementResponseMessage sendTradeAgreementJson(TradeAgreementMessages tradeAgreements);

  @RequestMapping(method = RequestMethod.GET, value = "/settlementEngine/mission/{id}")
  SettlementMissionMessage getSettlementMissionJson(@PathVariable("id") Long id);

  @RequestMapping(method = RequestMethod.POST, value = "/settlementEngine", consumes = "application/x-protobuf")
  @Headers({"Content-Type: application/x-protobuf"})
  SettlementResponseProtoMessage sendTradeAgreementProto(
      TradeAgreementProtoMessages tradeAgreements);

  @RequestMapping(method = RequestMethod.GET, value = "/settlementEngine/mission/{id}")
  @Headers({"Accept: application/x-protobuf"})
  SettlementMissionProtoMessage getSettlementMissionProto(@PathVariable("id") Long id);
}