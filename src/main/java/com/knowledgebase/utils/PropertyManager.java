package com.knowledgebase.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class PropertyManager {
    private static PropertiesConfiguration configuration;

    public static String getProperty(String propertyName) {
        try {
            if (configuration == null) {
                configuration = new PropertiesConfiguration("knowledgebase.properties");
            }
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return configuration.getString(propertyName);
    }

    public static void main(String[] args) {
        long expL = 1701624499286L;
        LocalDateTime exp = Instant.ofEpochMilli(expL)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        long iatL = 1701622699286L;
        LocalDateTime iat = Instant.ofEpochMilli(iatL)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if (LocalDateTime.now().isBefore(exp)) {
            System.out.println("Token not expired");
        } else {
            System.out.println("Token is expired");
        }

    }
}
