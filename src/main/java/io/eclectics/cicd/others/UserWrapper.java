package io.eclectics.cicd.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserWrapper {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String phoneNumber;
    private int age;
    private boolean active;
}
