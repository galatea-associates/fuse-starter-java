package org.galatea.starter.domain.wit;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NonNull;

@Data
public class WitResponse {

  @NonNull
  @SerializedName("_text")
  private String text;

  @NonNull
  private EntityStore entities;

  @SerializedName("msg_id")
  private String msgId;

}
