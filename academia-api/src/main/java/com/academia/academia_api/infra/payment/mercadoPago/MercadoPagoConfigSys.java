package com.academia.academia_api.infra.payment.mercadoPago;

import com.mercadopago.MercadoPagoConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfigSys {

    @Value("${mercadopago.access-token}")
    private String accessToken;
    @PostConstruct
    public void init(){
        MercadoPagoConfig.setAccessToken(accessToken);
    }
}
