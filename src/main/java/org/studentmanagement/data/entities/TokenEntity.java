package org.studentmanagement.data.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity(name = "tokens")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity extends BaseEntity {
    @OneToOne
    private UserEntity user;
    private String token;
    private Date creationDate;
    private Date expirationDate;
}
