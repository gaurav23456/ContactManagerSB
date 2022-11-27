package com.example.smartcontactmanager.dao;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smartcontactmanager.entities.Contact;
import com.example.smartcontactmanager.entities.User;

import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer> {

//    @Query("from Contact as c where c.user.id=:userID")
//    public List<Contact> findContactByUser(@Param("userId")int userId);

    @Query("from Contact as c where c.user.id=:ID")
    public Page<Contact> findContactsByUser(@Param("ID") int userId, PageRequest pageable);

    public List<Contact> findByNameContainingAndUser(String name, User user);
}
