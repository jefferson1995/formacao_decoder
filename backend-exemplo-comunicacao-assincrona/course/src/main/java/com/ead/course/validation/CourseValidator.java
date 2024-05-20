package com.ead.course.validation;

import com.ead.course.configs.security.AuthenticationCurrentUserService;
import com.ead.course.dtos.CourseDTO;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationCurrentUserService authenticationCurrentUserService;


    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        CourseDTO courseDTO = (CourseDTO) o;
        validator.validate(courseDTO, errors);
        if (!errors.hasErrors()) {
            validateUserInstructor(courseDTO.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors) {
        UUID currentUserId = authenticationCurrentUserService.getCurrentUser().getUserId();
        if (currentUserId.equals(userInstructor)) {
            Optional<UserModel> userModelOptional = userService.findById(userInstructor);
            if (!userModelOptional.isPresent()) {
                errors.rejectValue("userInstructor", "UserInstructorError", "Instrutor/Admin não encontrado.");
            }
           else if (userModelOptional.get().getUserType().equals(UserType.STUDENT.toString()) ||
                    userModelOptional.get().getUserType().equals(UserType.USER.toString())) {
                errors.rejectValue("userInstructor", "UserInstructorError", "Usuário dever ser Instrutor/Admin.");
            }

        } else {
            log.warn("user instructor: {}", userInstructor);
            log.warn("user autenticado: {}", currentUserId);
            throw new AccessDeniedException("Forbidden");
        }
    }

}
