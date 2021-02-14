package me.kalpha.jdbctemplate.catalog;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@Entity(name = "account")
public class Account {
    @Id
    @Column(name = "user_id")
    private String userId;
    private String email;
    private String password;
    private String role;
}
