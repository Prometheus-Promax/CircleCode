package org.bteam.circlecode.controller;

import org.bteam.circlecode.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/CodeCircle/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/disable")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> deleteRequest){
        try{
            Map<String, String> data =  userService.userDisableService(deleteRequest);
            return ResponseEntity.ok(data);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> info(@RequestBody Map<String, String> infoRequest) {
        try {
            Map<String, String> data = userService.userInfoService(infoRequest);
            return ResponseEntity.ok(data);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }

    }

}
