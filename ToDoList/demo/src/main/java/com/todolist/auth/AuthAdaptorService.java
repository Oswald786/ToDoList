package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import com.todolist.adaptors.web.UserMapper;
import com.todolist.exceptions.PermissionDeniedException;
import com.todolist.exceptions.UserAlreadyExistsException;
import com.todolist.exceptions.UserNotFoundException;
import io.micronaut.security.authentication.Authentication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Singleton
public class AuthAdaptorService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    UserMapper userMapper;

    private static final Logger log = LoggerFactory.getLogger(AuthAdaptorService.class);


    //find user
    @Transactional
    public UserDetailsModel findUser(String username){
        try {
            UserEntity entityReturned = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username = :username", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult();
            log.info("Found user");
            log.info("User found Username: ${entityReturned.getUsername()} \n Password:${entityReturned.getPassword()}");
            return userMapper.toModel(entityReturned);
        }catch (NoResultException e){
            throw new UserNotFoundException("User with username " + username + " does not exist");
        }
    }


    //delete user
    @Transactional
    public void deleteUser(String username, Authentication authentication){
        log.info("Deleting user with username: " + username);
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null");
        } else if (!authentication.getRoles().contains("ADMIN")) {
            throw new PermissionDeniedException("User does not have permission to delete this user");
        }
        UserEntity returnedEntity = entityManager.find(UserEntity.class, username);
        if (returnedEntity == null) {
            throw new UserNotFoundException("User with username " + username + " does not exist");
        }
        entityManager.remove(entityManager.find(UserEntity.class, username));
    }


    //update user
    @Transactional
    public void updateUser(UserDetailsModel userDetailsModel,Authentication authentication){
        log.info("Updating user with username: " + userDetailsModel.getUsername());
        if (authentication == null) {
            throw new IllegalStateException("Authentication is null");
        }else if(!authentication.getRoles().contains("ADMIN") && !authentication.getRoles().contains("USER")){
            throw new PermissionDeniedException("User does not have permission to update this user");
        }
        UserEntity returnedEntity = entityManager.find(UserEntity.class, userDetailsModel.getUsername());
        if (returnedEntity == null) {
            throw new UserNotFoundException("User with username " + userDetailsModel.getUsername() + " does not exist");
        }
        validateUser(userDetailsModel);
        UserEntity entity = userMapper.toEntity(userDetailsModel);
        entityManager.merge(entity);
    }


    //create user
    @Transactional
    public void createUser(UserDetailsModel userDetailsModel){
        validateUser(userDetailsModel);
        UserEntity entity = userMapper.toEntity(userDetailsModel);
        try {
            entityManager.persist(entity);
        }catch (ConstraintViolationException e){
            throw new UserAlreadyExistsException("User with username " + userDetailsModel.getUsername() + " already exists");
        }
    }

    private void validateUser(UserDetailsModel userDetailsModel){
        if(userDetailsModel.getUsername() == null || userDetailsModel.getUsername().isBlank()){
            throw new IllegalArgumentException("Username cannot be null or blank");
        }else if(userDetailsModel.getPassword() == null || userDetailsModel.getPassword().isBlank()){
            throw new IllegalArgumentException("Password cannot be null or blank");
        }else if(userDetailsModel.getPassword().length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }

}
