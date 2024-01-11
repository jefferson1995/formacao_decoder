package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDTO;
import com.ead.authuser.dtos.ResponsePageDTO;
import com.ead.authuser.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class UserClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;
    @Value("${ead.api.url.course}")
    String REQUEST_URI_COURSE;

    public Page<CourseDTO> getAllCoursesByUser(UUID userId, Pageable pageable) {
        List<CourseDTO> searchResult = null;
        ResponseEntity<ResponsePageDTO<CourseDTO>> result = null;
        String url = REQUEST_URI_COURSE + utilsService.createUrlGetAllCourseByUser(userId, pageable);
        log.debug("Request URL: {}", url);
        log.info("Request URL: {} ", url);
        try {
            ParameterizedTypeReference<ResponsePageDTO<CourseDTO>> responseType = new ParameterizedTypeReference<ResponsePageDTO<CourseDTO>>() {
            };
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Número de elementos retornados: {} ", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error Request /courses  ", e);
        }
        log.info("Request finalizada /courses userId {} ", userId);
        return result.getBody();
    }

}
