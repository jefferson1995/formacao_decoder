package com.ead.notificationhex.core.services;


import com.ead.notificationhex.core.domain.NotificationDomain;
import com.ead.notificationhex.core.domain.NotificationStatus;
import com.ead.notificationhex.core.domain.PageInfo;
import com.ead.notificationhex.core.ports.NotificationPersistencePort;
import com.ead.notificationhex.core.ports.NotificationServicePort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class NotificationServicePortImpl implements NotificationServicePort {

    private final NotificationPersistencePort notificationPersistencePort;

    public NotificationServicePortImpl(NotificationPersistencePort notificationPersistencePort) {
        this.notificationPersistencePort = notificationPersistencePort;
    }


    @Override
    public NotificationDomain saveNotification(NotificationDomain NotificationDomain) {
        return notificationPersistencePort.save(NotificationDomain);
    }

    @Override
    public List<NotificationDomain> findAllNotificationByUser(UUID userId, PageInfo pageInfo) {
        return notificationPersistencePort.findAllByUserIdAndNotificationStatus(userId, NotificationStatus.CREATED, pageInfo);
    }

    @Override
    public Optional<NotificationDomain> findByNotificationIdAndUserId(UUID notificationId, UUID userId) {
        return notificationPersistencePort.findByNotificationIdAndUserId(notificationId, userId);
    }
}
