package com.itgate.ProShift.controller;

import com.itgate.ProShift.entity.Role;
import com.itgate.ProShift.entity.User;
import com.itgate.ProShift.payload.request.ChangePasswordRequest;
import com.itgate.ProShift.payload.response.MessageResponse;
import com.itgate.ProShift.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService userService;

    ////////////////GET
    @GetMapping("/getAll")
    public  ResponseEntity<?> findAll(){
        try {
            List<User> users =  userService.findAllUser();
            return ResponseEntity.ok().body(users);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("ERROR: Failed to load the users list!"));
        }
    }

    @GetMapping("/getuser/{userId}")
    public ResponseEntity<?> findById(@PathVariable Long userId){
        if(userService.findUserbyId(userId)==null){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("ERROR: user does not exist!"));
        }
        User user =  userService.findUserbyId(userId);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/getbyrole")
    public  ResponseEntity<?> findByRole(@RequestBody Role role){
        List<User> users =  userService.findUserByRole(role.getName());
        return ResponseEntity.ok().body(users);
    }

    ////////////////PUT
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        userService.updateUser(user);
        return ResponseEntity.ok().body(user);
    }


}
