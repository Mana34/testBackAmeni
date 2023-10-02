package com.itgate.ProShift.service;

import com.itgate.ProShift.entity.ERole;
import com.itgate.ProShift.entity.Role;
import com.itgate.ProShift.entity.User;
import com.itgate.ProShift.repository.RoleRepository;
import com.itgate.ProShift.repository.UserRepository;
import com.itgate.ProShift.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class UserService implements IUserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Value("${spring.mail.username}")
    private String email;
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private JavaMailSender mailSender;

    // Define your criteria for a strong password
    private static final int MIN_LENGTH = 8;
    private static final int MIN_UPPERCASE = 1;
    private static final int MIN_LOWERCASE = 1;
    private static final int MIN_DIGITS = 1;
    private static final int MIN_SPECIAL_CHARACTERS = 1;
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+";
    @Override
    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        userRepository.findAll().forEach(userList::add);
        return userList;
    }

    @Override
    public List<User> findUserByRole(ERole role) {
        Role role1 = roleRepository.findByName(role).orElse(null);
        List<User> users= new ArrayList<>();
        userRepository.findUserByRoles(role1).forEach(users::add);
        return users;
    }






    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public User findUserbyId(Long idUser) {
        return userRepository.findById(idUser).orElse(null);
    }


    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "/auth");
    }



    public void sendVerificationEmail(User user, String siteURL) throws MessagingException, UnsupportedEncodingException, MessagingException {
        String toAddress = user.getEmail();
        String fromAddress = email;
        String senderName = "Go4Dev";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Go4Dev.";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);
        mailSender.send(message);
    }
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null) {
            return false;
        } else {
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
    }
    public static boolean isStrongPassword(String password) {
        // Check length
        if (password.length() < MIN_LENGTH) {
            return false;
        }

        // Check uppercase characters
        if (countUppercaseCharacters(password) < MIN_UPPERCASE) {
            return false;
        }

        // Check lowercase characters
        if (countLowercaseCharacters(password) < MIN_LOWERCASE) {
            return false;
        }

        // Check digits
        if (countDigits(password) < MIN_DIGITS) {
            return false;
        }

        // Check special characters
        if (countSpecialCharacters(password) < MIN_SPECIAL_CHARACTERS) {
            return false;
        }

        return true;
    }

    private static int countUppercaseCharacters(String password) {
        return (int) password.chars().filter(Character::isUpperCase).count();
    }

    private static int countLowercaseCharacters(String password) {
        return (int) password.chars().filter(Character::isLowerCase).count();
    }

    private static int countDigits(String password) {
        return (int) password.chars().filter(Character::isDigit).count();
    }

    private static int countSpecialCharacters(String password) {
        return (int) password.chars().filter(c -> SPECIAL_CHARACTERS.indexOf(c) >= 0).count();
    }



}
