package com.voterverification.application.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    private LocalDateTime expirationTime;

    public PasswordResetToken(Users user, String token) {
        this.user = user;
        this.token = token;
        // Get current time
        LocalDateTime now = LocalDateTime.now();

        // Create a LocalDateTime representing today's 3:58 AM
        LocalDateTime todayAt358am = now.toLocalDate().atTime(3, 58);

        // If current time is already after 3:58 AM, set expiration to 3:58 AM tomorrow
        if (now.isAfter(todayAt358am)) {
            todayAt358am = todayAt358am.plusDays(1);
        }

        // Set expiration time to 3:58 AM
        this.expirationTime = todayAt358am;
    }
}
