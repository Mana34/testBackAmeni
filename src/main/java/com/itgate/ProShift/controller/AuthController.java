package com.itgate.ProShift.controller;

import com.itgate.ProShift.entity.*;
import com.itgate.ProShift.payload.request.LoginRequest;
import com.itgate.ProShift.payload.request.NewPasswordRequest;
import com.itgate.ProShift.payload.request.SignupRequest;
import com.itgate.ProShift.payload.response.JwtResponse;
import com.itgate.ProShift.payload.response.MessageResponse;
import com.itgate.ProShift.repository.JwtTokenRepository;
import com.itgate.ProShift.repository.RoleRepository;
import com.itgate.ProShift.repository.UserRepository;
import com.itgate.ProShift.security.jwt.JwtUtils;
import com.itgate.ProShift.security.services.UserDetailsImpl;
import com.itgate.ProShift.service.interfaces.IJwtTokenService;
import com.itgate.ProShift.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  IJwtTokenService jwtTokenService;
  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  JwtTokenRepository jwtTokenRepository;
  @Autowired
  UserService userService;
  @Autowired
  PasswordEncoder encoder;
  @Autowired
  JwtUtils jwtUtils;




  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) throws BadCredentialsException {
    if (userRepository.findByUsername(loginRequest.getUsername()).orElse(null)==null) {
      if (userRepository.existsByUsername(loginRequest.getUsername())) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: This account dose not exist!"));
      }
    }
    User user =userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
    if (user!=null){
      if (user.getVerificationCode()!=null) {
        return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: This account is not verified by email yet. please check you email"));
      }}

    try {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    // Generate JWT token
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    String jwt = jwtUtils.generateToken(userDetails);

    // Save JWT token to database
    JwtToken jwtToken = new JwtToken();
    jwtToken.setToken(jwt);
    jwtToken.setCreatedDate(jwtUtils.getIssuedDateFromToken(jwt));
    jwtToken.setExpirationDate(jwtUtils.getExpirationDateFromToken(jwt));
    jwtTokenRepository.save(jwtToken);

    return ResponseEntity.ok(new JwtResponse(jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles));
  }
    catch (BadCredentialsException e ){
    return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: Wrong password !"));
  }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Email is already in use!"));
    }
    ////////////////// Nid input control
    if (!(signUpRequest.getCin().length()==8)  ) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: National ID must be exactly 8  characters!"));
    }

    if (userRepository.existsByCin(signUpRequest.getCin())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: National ID is already taken!"));
    }
    if(!userService.isStrongPassword(signUpRequest.getPassword())){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Must be strong password containing 8 characters: 1 LowerCse, 1 UpperCase , 1 Special , 1 digit !"));
    }
    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));


    ERole erole;

    Set<Role> roles = new HashSet<>();
    Role clientRole = roleRepository.findByName(ERole.ROLE_USER).get();
    roles.add(clientRole);
    /////////////////// save Employee with given input
    user.setRoles(roles);
    String randomCode = RandomString.make(64);
    user.setVerificationCode(randomCode);
    user.setFreemium(true);
    user.setNom(signUpRequest.getNom());
    user.setPrenom(signUpRequest.getPrenom());
    user.setCin(signUpRequest.getCin());
    user.setAdresse(signUpRequest.getAdresse());
    user.setNumTelephone(signUpRequest.getNumTelephone());
    userRepository.save(user);

    /////////////////// send verification mail
    userService.sendVerificationEmail(user, userService.getSiteURL(request));

    return ResponseEntity.ok(new MessageResponse("User registered successfully, check your email to verify your account!"));
  }
  @PostMapping("/register-admin")
  public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Email is already in use!"));
    }
    ////////////////// Nid input control
    if (!(signUpRequest.getCin().length()==8)  ) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: National ID must be exactly 8  characters!"));
    }

    if (userRepository.existsByCin(signUpRequest.getCin())) {
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: National ID is already taken!"));
    }
    if(!userService.isStrongPassword(signUpRequest.getPassword())){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse("Error: Must be strong password containing 8 characters: 1 LowerCse, 1 UpperCase , 1 Special , 1 digit !"));
    }
    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            encoder.encode(signUpRequest.getPassword()));


    ERole erole;

    Set<Role> roles = new HashSet<>();
    Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN).get();
    roles.add(roleAdmin);
    /////////////////// save Employee with given input
    user.setRoles(roles);
    String randomCode = RandomString.make(64);
    user.setVerificationCode(randomCode);
    user.setFreemium(false);
    user.setNom(signUpRequest.getNom());
    user.setPrenom(signUpRequest.getPrenom());
    user.setCin(signUpRequest.getCin());
    user.setAdresse(signUpRequest.getAdresse());
    user.setNumTelephone(signUpRequest.getNumTelephone());
    userRepository.save(user);

    /////////////////// send verification mail
    userService.sendVerificationEmail(user, userService.getSiteURL(request));

    return ResponseEntity.ok(new MessageResponse("Admin registered successfully, check your email to verify your account!"));
  }

  @GetMapping("/verify")
  public String verifyUser(@Param("code") String code) {
    if (userService.verify(code)) {
      return "verify_success";
    } else {
      return "verify_fail";
    }
  }



  @PostMapping("/signout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    String token = jwtUtils.getTokenFromRequest(request);
    JwtToken jwtToken=jwtTokenRepository.findByToken(token);
    if(jwtToken==null ){
      return ResponseEntity
              .badRequest()
              .body(new MessageResponse(" you already are signed out!"));
    }
    jwtTokenRepository.deleteById(jwtToken.getId());
    return ResponseEntity
            .ok()
            .body(new MessageResponse( "Logout successful"));
  }





}

