package com.example.smartcontactmanager.dao;

import com.example.smartcontactmanager.entities.User;

import antlr.collections.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("select u from User u where u.email=:email")
    public User getUserByName(@Param("email") String email);

    
}
