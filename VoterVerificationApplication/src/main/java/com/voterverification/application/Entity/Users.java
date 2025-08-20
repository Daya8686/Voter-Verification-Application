package com.voterverification.application.Entity;

import java.util.Objects;
import java.util.UUID;

import org.springframework.context.annotation.Fallback;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Users_info")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false, name = "first_name")
    private String firstName;
    
    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_roles", nullable = false)
    private Role role; // MANAGER, SALESMAN, SUPER_ADMIN
    
    @Column(nullable = false)
    private boolean isUserActive;
    
    @Column(nullable = false)
    private boolean isUserValid;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @ManyToOne
    @JoinColumn(name="election_board_id")
    private ElectionBoard electionBoard;
    
    @Column(name= "terms_and_conditions",nullable = false)
    private boolean termsAndConditions;
    
    @Column(nullable = false)
    private String gender;
    
    @Column(name = "verified_voters_count", nullable = false)
    private long verifiedVotersCount;
    
    @Column(name = "fraud_detected_count_by_user",nullable = false)
    private long fraudDetectedCountByUser;
    
    @Column(name = "is_user_logged_in", nullable = false)
    private boolean isUserLoggedIn;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(id, users.id); // Compare only by id or username
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use only id to generate hash code
    }
    
}
