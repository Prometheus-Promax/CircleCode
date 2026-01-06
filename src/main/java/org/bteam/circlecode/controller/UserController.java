package org.bteam.circlecode.controller;

import org.bteam.circlecode.common.Response;
import org.bteam.circlecode.service.UserService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> disable(@RequestBody Map<String, String> deleteRequest){
        Map<String, String> data =  userService.userDisableService(deleteRequest);
        Response response = Response.builder()
                .code(200)
                .message("User disabled successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/enable")
    public ResponseEntity<?> enable(@RequestBody Map<String, String> enableRequest){
        Map<String, String> data =  userService.userEnableService(enableRequest);
        Response response = Response.builder()
                .code(200)
                .message("User enabled successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> deleteRequest){
        Map<String, String> data =  userService.userDeleteService(deleteRequest);
        Response response = Response.builder()
                .code(200)
                .message("User deleted successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/info")
    public ResponseEntity<?> info(@RequestBody Map<String, String> infoRequest) {
        Map<String, String> data = userService.userInfoService(infoRequest);
        Response response = Response.builder()
                .code(200)
                .message("User info retrieved successfully")
                .data(data)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
