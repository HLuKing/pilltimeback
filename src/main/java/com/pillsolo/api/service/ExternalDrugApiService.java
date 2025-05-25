package com.pillsolo.api.service;

import com.pillsolo.api.dto.api.DrugApiResponse;
import com.pillsolo.api.dto.api.DrugApiResponse.Item;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExternalDrugApiService {

    @Value("${drug.api.key}")
    private String serviceKey;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.restTemplate = new RestTemplate();
    }

    public DrugApiResponse search(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String baseUrl = "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";
            URI uri = new URI(baseUrl + "?serviceKey=" + serviceKey + "&itemName=" + encodedQuery + "&type=json");

            ResponseEntity<DrugApiResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    DrugApiResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Item findByItemSeq(String itemSeq) {
        try {
            String url = "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList"
                    + "?serviceKey=" + serviceKey
                    + "&itemSeq=" + URLEncoder.encode(itemSeq, StandardCharsets.UTF_8)
                    + "&type=json";

            URI uri = new URI(url);

            ResponseEntity<DrugApiResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    DrugApiResponse.class
            );

            if (response.getBody() != null &&
                    response.getBody().getBody() != null &&
                    response.getBody().getBody().getItems() != null) {

                List<Item> items = response.getBody().getBody().getItems();
                if (!items.isEmpty()) {
                    return items.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public DrugApiResponse searchByItemSeq(Long itemSeq) {
        try {
            String baseUrl = "https://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";
            String url = baseUrl + "?serviceKey=" + serviceKey + "&itemSeq=" + itemSeq + "&type=json";
            URI uri = new URI(url);

            ResponseEntity<DrugApiResponse> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(new HttpHeaders()),
                    DrugApiResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
