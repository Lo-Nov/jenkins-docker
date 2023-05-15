package io.eclectics.cicd.security.user;

import io.eclectics.cicd.model.audit.Auditable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends Auditable {
    private String username;
    private String password;
    private int locked = 0;
    private int trials = 0;
    private boolean active = true;
}
