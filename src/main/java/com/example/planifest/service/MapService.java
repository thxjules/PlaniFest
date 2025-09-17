package com.example.planifest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MapService {

    private static final String GOOGLE_MAPS_EMBED_BASE = "https://www.google.com/maps/embed/v1/place";

    @Value("${google.maps.api.key}")
    private String apiKey;


    public String getMapEmbed(String address) {
        if (address == null || address.isBlank()) {
            return "";
        }

        try {
            String encoded = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
            return GOOGLE_MAPS_EMBED_BASE + "?key=" + apiKey + "&q=" + encoded;
        } catch (UnsupportedEncodingException e) {
            return "";
        } 
    }
}
