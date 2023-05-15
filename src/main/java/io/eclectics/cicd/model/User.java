package io.eclectics.cicd.model;


import io.eclectics.cicd.model.audit.Auditable;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User extends Auditable {
    private String firstName;
    private String nationalId;
    @Column(unique = true)
    private String phoneNumber;
    private String lastName;
    private int age;
    private boolean active = true;
}
