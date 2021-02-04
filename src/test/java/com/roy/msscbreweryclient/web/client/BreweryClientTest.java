package com.roy.msscbreweryclient.web.client;

import com.roy.msscbreweryclient.web.model.BeerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BreweryClientTest {

    @Autowired
    BreweryClient client;

    @Test
    void getBeeryById() {
        BeerDto dto = client.getBeeryById(UUID.randomUUID());

        assertNotNull(dto);
    }
}
