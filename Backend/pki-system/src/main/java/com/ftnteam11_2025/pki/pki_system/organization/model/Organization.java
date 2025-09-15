package com.ftnteam11_2025.pki.pki_system.organization.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String alias; // alias private key

    private String encryptedKeyStorePassword;
    private String encryptedPrivateKeyPassword;
    private String ksFilePath;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

}
