package com.example.smartcontactmanager.Config;

import com.example.smartcontactmanager.dao.UserRepository;
import com.example.smartcontactmanager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.getUserByName(username);

        if(user==null){
            throw new UsernameNotFoundException("could not find user");
        }
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }
}
