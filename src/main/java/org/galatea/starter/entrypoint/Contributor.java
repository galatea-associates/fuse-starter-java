package org.galatea.starter.entrypoint;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Contributor {

    private String login;
    private int contributions;

}
