package com.ead.notificationhex.adapters.configs;

import com.ead.notificationhex.EadNotificationHexApplication;
import com.ead.notificationhex.core.ports.NotificationPersistencePort;
import com.ead.notificationhex.core.services.NotificationServicePortImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = EadNotificationHexApplication.class)
public class BeanConfiguration {

    @Bean
    NotificationServicePortImpl notificationServicePort(NotificationPersistencePort persistence){
        return new NotificationServicePortImpl(persistence);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
