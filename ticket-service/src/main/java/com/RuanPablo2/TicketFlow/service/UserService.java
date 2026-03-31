package com.RuanPablo2.TicketFlow.service;

import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.exceptions.ErrorCode;
import com.RuanPablo2.TicketFlow.exceptions.ResourceNotFoundException;
import com.RuanPablo2.TicketFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with ID: " + id,
                        ErrorCode.RESOURCE_NOT_FOUND));
    }
}