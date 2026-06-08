package com.firstclub.membership.service;

import com.firstclub.membership.dto.CreateUserRequest;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserOrder;
import com.firstclub.membership.exception.BusinessException;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.UserOrderRepository;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserOrderRepository userOrderRepository;

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .cohortTags(request.getCohortTags() != null ? request.getCohortTags() : new HashSet<>())
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    @Transactional
    public UserOrder placeOrder(Long userId, BigDecimal orderValue) {
        User user = getUser(userId);
        UserOrder order = UserOrder.builder()
                .user(user)
                .orderValue(orderValue)
                .build();
        return userOrderRepository.save(order);
    }
}
