package ivan.rest.services;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class RestService {

    private RestTemplate restTemplate;

    public RestService() {
        this.restTemplate = new RestTemplate();
    }

    public <T> List getList (String url, Class<T[]> responseType) {
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<T[]> list = restTemplate.getForEntity(url, responseType);
        return Arrays.asList(list.getBody());
    }
}