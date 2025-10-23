package com.todolist.auth;

import com.todolist.Models.userDetailsModel;
import com.todolist.adaptors.persistence.jpa.userEntity;
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
    public userDetailsModel findUser(String username){
        try {
            userEntity entityReturned = entityManager.createQuery("SELECT u FROM userEntity u WHERE u.userName = :username", userEntity.class)
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
            entityManager.remove(entityManager.find(userDetailsModel.class, username));
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
        }
    }


    //update user
    @Transactional
    public void updateUser(userDetailsModel userDetailsModel){
        try {
            userEntity entity = userMapper.toEntity(userDetailsModel);
            entityManager.merge(entity);
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
        }
    }


    //create user
    @Transactional
    public void createUser(userDetailsModel userDetailsModel){
        try {
            userEntity entity = userMapper.toEntity(userDetailsModel);
            entityManager.persist(entity);
        }catch (Exception e){
            log.error(String.valueOf(e.getCause()));
            throw new IllegalArgumentException("User with username " + userDetailsModel.getUserName() + " already exists");
        }
    }

}
