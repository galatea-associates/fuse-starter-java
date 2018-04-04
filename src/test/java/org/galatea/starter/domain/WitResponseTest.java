package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import org.galatea.starter.ASpringTest;
import org.galatea.starter.domain.Wit.Entity;
import org.galatea.starter.domain.Wit.EntityStore;
import org.galatea.starter.domain.Wit.WitResponse;
import org.galatea.starter.restClient.WitGetter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

public class WitResponseTest extends ASpringTest{

  @SpyBean
  private WitGetter defaultWitGetter;

  @Test
  public void firstTest(){
    String test = "TEST";

    /*
    Entity ent = new Entity(1, test);
    Entity[] eArr = new Entity[1];
    eArr[0] = ent;
    EntityStore eStore = new EntityStore();
    eStore.setIntent(eArr);
    WitResponse witRe = new WitResponse(test, eStore);
    given(this.mockWitGetter.getWitResponse(test)).willReturn(witRe);
    */

    WitResponse testWitRe = defaultWitGetter.getWitResponse(test);
    System.out.println(testWitRe);
    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\nFUCK\n\n\n\n\n\n\n\n\n\n\n");
    //assertEquals("TEST",witRe.getEntities().getIntent()[0].getValue());

  }

}
