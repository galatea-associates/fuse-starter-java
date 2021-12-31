package org.galatea.starter.service;

import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.aspect4log.Log;
import org.galatea.starter.domain.WitAiEntity;
import org.galatea.starter.domain.WitAiResolvedEntity;
import org.galatea.starter.domain.WitAiResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Log
@Service
public class WitAiService {

  @NonNull
  private WitAiClient witAiClient;

  // Both public and private wit.ai apps require Bearer tokens to access, as that is how they
  // determine which app you are querying against.
  private static final String WIT_AI_BEARER_TOKEN = "Bearer SCGU5JVCPT5IUK47SF3QSPQVX252TFRT";

  // API versioning is done by date. See See https://wit.ai/docs/http/20210928/#api_versioning_link
  private static final String WIT_AI_API_VERSION = "20211220";

  /**
   * Query the wit.api with the given user's query.
   *
   * @param query the full text from the GET command. Wit.ai will break this down to intent(s).
   * @return a WitAiQueryResponse indicating the intents, entities, and traits of the sentence.
   */
  public WitAiResponse queryWitAi(final String query) {
    return witAiClient.query(WIT_AI_BEARER_TOKEN, WIT_AI_API_VERSION, query);
  }

  /**
   * Get the top Entity for the given entity type from a WitAiResponse.
   *
   * @param witAiResponse the response from wit.ai to retrieve the top entity from.
   * @param entityType the type of entity to return the top entity of (eg.
   *     wit$location:location)
   * @return a WitAiEntity which our wit.ai app has the highest degree of certainty of.
   */
  public WitAiEntity getTopEntityForEntityType(final WitAiResponse witAiResponse,
      final String entityType) {
    return getEntitiesForEntityType(witAiResponse, entityType).get(0);
  }

  /**
   * Get all of the entities for the given entity type from a WitAiResponse.
   *
   * @param witAiResponse the response from wit.ai to retrieve the top entity from.
   * @param entityType the type of entity to return the top entity of (eg.
   *     wit$location:location)
   * @return a List of WitAiEntity which our wit.ai app has some degree of certainty of.
   */
  public List<WitAiEntity> getEntitiesForEntityType(final WitAiResponse witAiResponse,
      final String entityType) {
    Map<String, List<WitAiEntity>> entities = witAiResponse.getEntities();
    return entities.get(entityType);
  }

  /**
   * Get the top resolved entity from a given WitAiEntity.
   *
   * @param entity the entity to retrieve the top resolved entity from.
   * @return the resolved entity that our wit.ai app has the highest confidence in.
   */
  public WitAiResolvedEntity getTopResolvedEntity(final WitAiEntity entity) {
    return entity.getResolved().getValues().get(0);
  }


}
