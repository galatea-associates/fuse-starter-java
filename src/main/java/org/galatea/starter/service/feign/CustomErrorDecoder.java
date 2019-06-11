package org.galatea.starter.service.feign;

import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode (String methodKey, Response response){
    switch (response.status()){
      case 400:
        return new BadRequestException();
      case 404:
        return new NotFoundException();
      case 500:
        return new RedirectException();
      default:
        return new Exception("Generic Error");
    }
  }
}