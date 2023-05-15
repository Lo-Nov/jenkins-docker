package io.eclectics.cicd.others;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PaginationWrapper {
    private int page = 0;
    private int size = 10;
    private String ordering;
    private ArrayList<String> filters; // Example: "id, created_on"
}
