package org.insertcoin.productservice.clients;

import org.springframework.stereotype.Component;

@Component
public class CurrencyClientFallback implements CurrencyClient {

    @Override
    public CurrencyResponse getCurrency(double value, String source, String target) {
        CurrencyResponse response = new CurrencyResponse();
        response.setConvertedValue(-1.0);
        response.setEnviroment("Fallback: currency-service indisponível");
        return response;
    }
}
