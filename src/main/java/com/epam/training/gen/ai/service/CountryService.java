package com.epam.training.gen.ai.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Service that retrieves the information from the Countries API at https://restcountries.com/.
 */
@Service
public class CountryService {

    private final WebClient webClient;

    public CountryService(@Qualifier("countryWebClient") final WebClient webClient) {
        this.webClient = webClient;
    }

    private static final String ALL_ENDPOINT = "/all";
    private static final String COUNTRY_ENDPOINT = "/name/";
    private static final String LANGUAGE_ENDPOINT = "/lang/";


    /**
     * Retrieve all existing countries' information.
     *
     * @return a list of all countries
     */
    public List getAllCountries() {

        return webClient.get().uri(ALL_ENDPOINT)
                .retrieve()
                .bodyToMono(List.class).block();
    }

    /**
     * Get a specific country information.
     *
     * @param countryName to search
     * @return the country information
     */
    public List getCountry(String countryName) {

        return webClient.get().uri(COUNTRY_ENDPOINT + countryName)
                .retrieve()
                .bodyToMono(List.class).block();
    }

    /**
     * Retrieve all countries by language.
     *
     * @param language spoken by countries
     * @return a list of countries that speaks the input language
     */
    public List getCountriesByLanguage(String language) {

        return webClient.get().uri(LANGUAGE_ENDPOINT + language)
                .retrieve()
                .bodyToMono(List.class).block();
    }

}
