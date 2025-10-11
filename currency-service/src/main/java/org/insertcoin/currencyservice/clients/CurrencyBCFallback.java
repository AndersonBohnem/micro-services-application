package org.insertcoin.currencyservice.clients;

import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CurrencyBCFallback implements CurrencyBCClient{
    @Override
    public CurrencyBCResponse getCurrencyBC(String coin) {
        CurrencyBCResponse currency = new CurrencyBCResponse();
        currency.setValue(Collections.emptyList());
        return currency;
    }
}
