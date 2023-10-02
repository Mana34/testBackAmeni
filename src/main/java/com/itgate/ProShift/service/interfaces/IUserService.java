package com.itgate.ProShift.service.interfaces;

import com.itgate.ProShift.entity.ERole;
import com.itgate.ProShift.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAllUser();
    List<User> findUserByRole(ERole role);
    User updateUser (User user);
    User findUserbyId(Long idUser);


}
