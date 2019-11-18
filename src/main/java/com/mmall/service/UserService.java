package com.mmall.service;

import com.mmall.common.ReturnResponse;
import com.mmall.pojo.User;

public interface UserService {
    ReturnResponse<User> login(String username, String password);

    ReturnResponse<String> register(User user);

    ReturnResponse<String> forgetQuestion(String username);

    ReturnResponse<String> checkAnswer(String username, String question, String answer);

    ReturnResponse<String> updatePassword(String username, String passwordNew, String token);

    ReturnResponse<String> updatePwdWhileLogin(User user, String passwordOld, String passwordNew);

    ReturnResponse<User> updateInformation(User user);

    ReturnResponse<User> getInformation(Integer userId);

    ReturnResponse<String> checkSuperUserRole(User user);
}
