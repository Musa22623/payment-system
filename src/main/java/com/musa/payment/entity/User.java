package com.musa.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Getter
@Setter
public class User {

    @jakarta.persistence.Id
    private Long id1;
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private String password;

}
