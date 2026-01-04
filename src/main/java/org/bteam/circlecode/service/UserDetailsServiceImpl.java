//package org.bteam.circlecode.service;
//
//import org.bteam.circlecode.entity.User;
//import org.bteam.circlecode.mapper.UserMapper;
//import org.jspecify.annotations.NonNull;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final UserMapper userMapper;
//
//    public UserDetailsServiceImpl(UserMapper userMapper){
//        this.userMapper = userMapper;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
//
//        User user = userMapper.selectById(username);
//        if (user == null)
//        {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                new ArrayList<>()
//        );
//    }
//}
