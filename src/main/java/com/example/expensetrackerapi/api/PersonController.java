package com.example.expensetrackerapi.api;

import com.example.expensetrackerapi.Constants;
import com.example.expensetrackerapi.domain.User;
import com.example.expensetrackerapi.models.Person;
import com.example.expensetrackerapi.service.PersonService;
import com.example.expensetrackerapi.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1")
@RestController
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public void addPerson(@RequestBody Person person) {
        personService.addPerson(person);
    }

    @GetMapping
    public List<Person> getAllPeople() {
        return personService.getAllPeople();
    }

    //----------------------------------------------------------------------------------------------------


    @Autowired
    UserService userService;

    @PostMapping("user/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");

        User user = userService.validateUser(email, password);

        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    @PostMapping("user/register")
    public ResponseEntity<Map<String, String>> registerUSer(@RequestBody Map<String, Object> userMap) {
        String firstname = (String) userMap.get("first_name");
        String lastname = (String) userMap.get("last_name");
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");

        User user = userService.registerUser(firstname, lastname, email, password);

        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }

    private Map<String, String> generateJWTToken(User user) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", user.getUser_id())
                .claim("firstName", user.getFirst_name())
                .claim("lastName", user.getLast_name())
                .claim("email", user.getEmail())
                .compact();

        Map<String, String> map = new HashMap<>();
        map.put("token", token);

        return map;
    }

}
