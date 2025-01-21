package com.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    // Implement email or other notification methods here
    public void sendBudgetExceededNotification(String message) {
        // Implementation for sending notifications
        System.out.println("Budget exceeded notification: " + message);
    }
}