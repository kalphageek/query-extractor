package me.kalpha.jdbctemplate.catalog;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(of = "userId")
@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "user_id")
    private String userId;
    private String email;
    private String password;
    private String role;
}
