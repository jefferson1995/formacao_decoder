package com.ead.authuser.services.impl;

import com.ead.authuser.clients.CourseClient;
import com.ead.authuser.enums.ActionType;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.publishers.UserEventPublisher;
import com.ead.authuser.repositories.UserRepository;
import com.ead.authuser.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseClient courseClient;

    @Autowired
    UserEventPublisher userEventPublisher;

    @GetMapping
    @Override
    public List<UserModel> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    @Override
    public void delete(UserModel userModel) {
        userRepository.delete(userModel);
    }

    @Override
    public UserModel save(UserModel userModel) {
        userRepository.save(userModel);
        return userModel;
    }

    @Override
    public boolean existByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existByUserEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable) {
        return userRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public UserModel saveUser(UserModel userModel) {
        userModel = save(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDTO(), ActionType.CREATE);
        return userModel;
    }

    @Transactional
    @Override
    public void deleteUser(UserModel userModel) {
        delete(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDTO(), ActionType.DELETE);
    }

    @Transactional
    @Override
    public UserModel updateUser(UserModel userModel) {
        userModel = save(userModel);
        userEventPublisher.publishUserEvent(userModel.convertToUserEventDTO(), ActionType.UPDATE);
        return userModel;
    }

    @Override
    public UserModel updatePassword(UserModel userModel) {
        return save(userModel);
    }


}
