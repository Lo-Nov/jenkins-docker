package io.eclectics.cicd.resource;

import io.eclectics.cicd.others.PaginationWrapper;
import io.eclectics.cicd.others.UserWrapper;
import io.eclectics.cicd.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public Mono<ResponseEntity<?>> createUser(@RequestBody UserWrapper wrapper, @ModelAttribute String createdBy){
        return userService.createUser(wrapper, createdBy)
                .map(res->ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(res));
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<?>> updateUser(@PathVariable Long id, @RequestBody UserWrapper wrapper, @ModelAttribute String modifiedBy){
        return userService.updateUser(id,wrapper,modifiedBy)
                .map(res->ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(res));
    }

    @DeleteMapping("/delete")
    public Mono<ResponseEntity<?>> deleteUser(@RequestParam(value = "id") Long id, @ModelAttribute String username){
        return userService.deleteUser(id,username)
                .map(res->ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(res));
    }

    @GetMapping("/get-by-id")
    public Mono<ResponseEntity<?>> getById(@RequestParam(value = "id") Long id, @ModelAttribute String username){
        return userService.getById(id,username)
                .map(res->ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(res));
    }

    @GetMapping("/get")
    public Mono<ResponseEntity<?>> getAll(@RequestBody PaginationWrapper wrapper, @ModelAttribute String username){
        return userService.getAll(wrapper,username)
                .map(res->ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(res));
    }

}
