package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionDTO;
import com.ead.course.dtos.UserDTO;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    AuthUserClient authUserClient;

    @Autowired
    CourseService courseService;

    @Autowired
    CourseUserService courseUserService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(@PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable,
                                                             @PathVariable(value = "courseId") UUID courseId) {

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não existente. ");
        }
        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersByCourse(courseId, pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                               @RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        ResponseEntity<UserDTO> responseUser;

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (!courseModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não existente. ");
        }
        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDTO.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Usuário já inscrito no curso.");
        }
        try {
            responseUser = authUserClient.getOneUserById(subscriptionDTO.getUserId());
            if(responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário está bloqueado!");
            }
        }
        catch (HttpStatusCodeException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não cadastrado!");
            }
        }

        CourseUserModel courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDTO.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);

    }

    @DeleteMapping("courses/users/{userId}")
    public ResponseEntity<Object> deleteCourseUserByUser(@PathVariable(value = "userId") UUID userId){
        if (!courseUserService.existsByUserId(userId)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseUser não encontrado.");
        }
        courseUserService.deleteCourseUserByUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body("CourseUser deletado com sucesso.");
    }
}
