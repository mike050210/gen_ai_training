package com.epam.training.gen.ai.plugin;

import com.epam.training.gen.ai.service.CountryService;
import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CountryPlugin {
    private final CountryService countryService;


    @DefineKernelFunction(name = "get_all_countries", description = "Get all countries information")
    public List getAllCountries() {
        log.info("Called the all countries endpoint");
        return countryService.getAllCountries();
    }

    @DefineKernelFunction(name = "get_country_information", description = "Get a specific country information")
    public List getCountryInformation(
            @KernelFunctionParameter(name = "country_name", description = "Country name") String countryName
    ) {
        log.info("Getting {} country information .", countryName);
        return countryService.getCountry(countryName);
    }

    @DefineKernelFunction(name = "get_country_information_by_language", description = "Get all countries information where a language is spoken.")
    public List getCountryInformationByLanguage(
            @KernelFunctionParameter(name = "language", description = "language") String language
    ) {
        log.info("Getting countries information where {} is spoken.", language);
        return countryService.getCountriesByLanguage(language);
    }


}
