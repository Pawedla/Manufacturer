package com.example.manufacturer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.text.MessageFormat;

public class SupplierContactUtil {

    private static RestTemplate restTemplate = new RestTemplate();


    public static String[] getBestOffer(String[] configuration) {

        String[] supplierUrl = {"http://localhost:8081/", "http://localhost:8082/"};
        ResponseEntity<String[]> responseEntity;
        String[] bestOffer = new String[3];

        //loops through all suppliers, gets best offer (best price)
        for (int i = 0; i < supplierUrl.length; i++) {
            final String uri = MessageFormat
                    .format(supplierUrl[i] + "getOffer/{0}/{1}/{2}/{3}", configuration[0],
                            configuration[1], configuration[2], configuration[3]);
            responseEntity = restTemplate.getForEntity(URI.create(uri), String[].class);
            if (bestOffer[0] == null || Double.valueOf(bestOffer[1]) > Double.valueOf(responseEntity.getBody()[1])) {
                bestOffer = responseEntity.getBody();
            }
        }
        return bestOffer;
    }
}
