package top.dcenter.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import top.dcenter.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zyw
 * @version V1.0  Created by 2020/5/1 19:49
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserDetails userDetails, Authentication authentication){
        Map<String, Object> map = new HashMap<>(16);
        map.put("authenticationHolder", SecurityContextHolder.getContext().getAuthentication());
        map.put("userDetails", userDetails);
        map.put("authentication",authentication);
        return map;
    }


    @GetMapping(value = "")
    @JsonView(User.UserSimpleView.class)
    public List<User> listUsers(
            @RequestParam(value = "username", required = false) String username,
            @PageableDefault(page = 5, size = 20, sort = "username,asc") Pageable pageable) {
        log.debug("listUsers: " + username);
        log.debug("listUsers: size=" + pageable.getPageSize() + "; page="
                          + pageable.getPageNumber() + " sort="
                          + pageable.getSort());
        List<User> users = new ArrayList<>();
        users.add(new User("jack", "1111"));
        users.add(new User("lose", "1111"));
        users.add(new User("Tom", "1111"));
        return users;
    }

    @GetMapping("/{id:\\d+}")
    @JsonView(User.UserDetailView.class)
    public User getInfo(@PathVariable("id") String id) {
//        throw new UserNotExistException("1");
        log.debug("getInfo: " + id);
        User tom = new User("tom", "1111");
        return tom;
    }

    @PostMapping("")
    @JsonView(User.UserSimpleView.class)
    public User createUser(@Valid @RequestBody() User user) {
        log.debug("createUser: " + user);
        user.setId("1");
        return user;
    }

    @PutMapping("/{id:\\d+}")
    @JsonView(User.UserSimpleView.class)
    public User update(
            @PathVariable("id") String id,
            @Valid @RequestBody User user,
            BindingResult errors) {
        if (errors.hasErrors())
        {
            errors.getAllErrors().stream().forEach(
                    (error) ->
                    {
                        FieldError fieldError = (FieldError) error;
                        String eMsg = fieldError.getField() + error.getDefaultMessage();
                        log.error(eMsg);
                    }
            );
        }
        log.debug("listUsers: id=" + id + "; " + user);
        return user;
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") String id){
        log.info("delete id=" + id);
    }
}
