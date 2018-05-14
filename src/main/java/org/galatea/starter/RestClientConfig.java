package org.galatea.starter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.codec.Decoder;
import feign.gson.GsonDecoder;
import java.util.List;
import org.galatea.starter.restclient.QuoteGetter;
import org.galatea.starter.restclient.WitGetter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;

@Configuration
public class RestClientConfig {

/*
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new MappingJackson2HttpMessageConverter()); // JSON
  }

/*
  @Bean
  public QuoteGetter defaultQuoteGetter(@Value("${quote-getter.url}") final String quoteUrl){
    return Feign.builder().decoder(new GsonDecoder()).target(QuoteGetter.class, quoteUrl);
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

    return mapper;
  }

  @Bean MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
    ObjectMapper objectMapper = new ObjectMapper();
    jsonConverter.setObjectMapper(objectMapper);
    return jsonConverter;
  }
*/
  @Bean
  public Decoder feignDecoder() {
    HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
    ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(jacksonConverter);
    return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
  }



/*
  @Bean
  public WitGetter defaultWitGetter(@Value("${wit.url}") final String witUrl){
    return Feign.builder().decoder(new GsonDecoder()).target(WitGetter.class, witUrl);
  }
*/
}
