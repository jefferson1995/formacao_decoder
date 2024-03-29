package com.ead.notificationhex.adapters.inbound.controllers;

import com.ead.notificationhex.adapters.dtos.NotificationDTO;
import com.ead.notificationhex.core.domain.NotificationDomain;
import com.ead.notificationhex.core.domain.PageInfo;
import com.ead.notificationhex.core.ports.NotificationServicePort;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationUserController {

    final
    NotificationServicePort notificationServicePort;

    public NotificationUserController(NotificationServicePort notificationServicePort) {
        this.notificationServicePort = notificationServicePort;
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/users/{userId}/notifications")
    public ResponseEntity<Page<NotificationDomain>> getAllNotificationsByUser(@PathVariable(value = "userId") UUID userId,
                                                                              @PageableDefault(page = 0, size = 10, sort = "notificationId", direction = Sort.Direction.ASC) Pageable pageable,
                                                                              Authentication authentication) {
        PageInfo pageInfo = new PageInfo();
        BeanUtils.copyProperties(pageable, pageInfo);
        List<NotificationDomain> notificationDomainList = notificationServicePort.findAllNotificationByUser(userId, pageInfo);
        return ResponseEntity.status(HttpStatus.OK).body(new PageImpl<NotificationDomain>(notificationDomainList, pageable, notificationDomainList.size()));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PutMapping("/users/{userId}/notifications/{notificationId}")
    public ResponseEntity<Object> updateNotification(@PathVariable(value = "userId") UUID userId,
                                                     @PathVariable(value = "notificationId") UUID notificationId,
                                                     @RequestBody @Valid NotificationDTO notificationDTO) {
        Optional<NotificationDomain> notificationModelOptional = notificationServicePort.findByNotificationIdAndUserId(notificationId, userId);
        if (notificationModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notificação não encontrada");
        }

        notificationModelOptional.get().setNotificationStatus(notificationDTO.getNotificationStatus());
        notificationServicePort.saveNotification(notificationModelOptional.get());

        return ResponseEntity.status(HttpStatus.OK).body(notificationModelOptional.get());
    }

}
