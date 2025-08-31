package com.ftnteam11_2025.pki.pki_system.security.refresh.model;

import com.ftnteam11_2025.pki.pki_system.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    private String token;
    private Date issuedAt;
    private Date expiryDate;
}
