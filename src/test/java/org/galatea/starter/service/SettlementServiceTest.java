package org.galatea.starter.service;


import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.rpsy.ISettlementMissionRpsy;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.BDDMockito.given;

public class SettlementServiceTest extends ASpringTest {
    @MockBean
    private ISettlementMissionRpsy mockSettlementMissionRpsy;

    @MockBean
    private IAgreementTransformer mockAgreementTransformer;

    @Test
    public void testFindMissionFound() {
        Long id = 1L;

        SettlementMission testSettlementMission = SettlementMission.builder()
                .id(id)
                .depot("DTC")
                .externalParty("EXT-1")
                .instrument("IBM")
                .direction("REC")
                .qty(100d)
                .build();

        given(this.mockSettlementMissionRpsy.findOne(id))
                .willReturn(testSettlementMission);

        SettlementService service = new SettlementService(this.mockSettlementMissionRpsy,  this.mockAgreementTransformer);

        //service

    }

    @Test
    public void testFindMissionNotFound() {

    }
}
