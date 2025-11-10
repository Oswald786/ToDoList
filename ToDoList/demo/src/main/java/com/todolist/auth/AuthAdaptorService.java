package com.todolist.auth;

import com.todolist.Models.UserDetailsModel;
import com.todolist.adaptors.persistence.Jpa.UserEntity;
import com.todolist.adaptors.web.UserMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
            UserEntity entityReturned = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.userName = :username", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult();
            System.out.println("Found user");
            System.out.println(entityReturned.getUserName()+ "  " +entityReturned.getPassword());
            return userMapper.toModel(entityReturned);
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
            return null;
        }
    }


    //delete user
    @Transactional
    public void deleteUser(String username){
        try {
            entityManager.remove(entityManager.find(UserDetailsModel.class, username));
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
        }
    }


    //update user
    @Transactional
    public void updateUser(UserDetailsModel userDetailsModel){
        try {
            UserEntity entity = userMapper.toEntity(userDetailsModel);
            entityManager.merge(entity);
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
        }
    }


    //create user
    @Transactional
    public void createUser(UserDetailsModel userDetailsModel){
        try {
            UserEntity entity = userMapper.toEntity(userDetailsModel);
            entityManager.persist(entity);
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("User with username " + userDetailsModel.getUserName() + " already exists");
        }
    }

}
