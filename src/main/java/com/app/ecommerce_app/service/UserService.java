package com.app.ecommerce_app.service;

import com.app.ecommerce_app.dto.UserRequest;
import com.app.ecommerce_app.dto.UserResponse;
import com.app.ecommerce_app.model.User;
import com.app.ecommerce_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    public List<UserResponse> fetchAllUsers(){
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user,UserResponse.class))
                .collect(Collectors.toList());
    }

    public void adduser(UserRequest userRequest){
        User user = modelMapper.map(userRequest, User.class);
        userRepository.save(user);
    }
    public Optional<UserResponse> fetchUser(int id){
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserResponse.class));
    }
    public boolean updateUser(int id,UserRequest userRequest){
        return userRepository.findById(id)
                .map(existingUser -> {
                    modelMapper.map(userRequest, existingUser);
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }
}
