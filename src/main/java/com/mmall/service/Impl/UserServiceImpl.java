package com.mmall.service.Impl;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("UserService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    public ReturnResponse<User> login(String username, String password) {
        int count = userMapper.CheckUsername(username);
        if (count == 0) return ReturnResponse.ReturnErrorByMessage("用户名不存在，请检查是否输入错误");

        String md5password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.CheckUser(username, md5password);
        if (user == null) return ReturnResponse.ReturnErrorByMessage("密码错误，请重新输入");
        user.setPassword("");
        return ReturnResponse.ReturnSuccess("登录成功", user);
    }

    public ReturnResponse<String> register(User user) {
        ReturnResponse<String> returnResponse = this.checkData(user.getUsername(), Const.USERNAME);
        if (!returnResponse.isSuccess()) return returnResponse;
        returnResponse = this.checkData(user.getEmail(), Const.EMAIL);
        if (!returnResponse.isSuccess()) return returnResponse;

        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int count = userMapper.insert(user);
        if (count == 0) return ReturnResponse.ReturnErrorByMessage("注册失败");
        return ReturnResponse.ReturnSuccessByMessage("注册成功");
    }

    private ReturnResponse<String> checkData(String data, String kind) {
        if (kind.equals("username")) {
            int count = userMapper.CheckUsername(data);
            if (count > 0) return ReturnResponse.ReturnErrorByMessage("用户名已存在");
            return ReturnResponse.ReturnSuccess();
        }
        else if (kind.equals("email")) {
            int count = userMapper.CheckEmail(data);
            if (count > 0) return ReturnResponse.ReturnErrorByMessage("邮箱已存在");
            return ReturnResponse.ReturnSuccess();
        }
        return ReturnResponse.ReturnError();
    }

    public ReturnResponse<String> forgetQuestion(String username) {
        int count = userMapper.CheckUsername(username);
        if (count == 0) return ReturnResponse.ReturnErrorByMessage("用户名不存在");
        String question = userMapper.selectQuestion(username);
        if (question == null || question.isEmpty()) return ReturnResponse.ReturnErrorByMessage("问题不存在");
        return ReturnResponse.ReturnSuccessByData(question);
    }

    public ReturnResponse<String> checkAnswer(String username, String question, String answer) {
        int userCount = userMapper.checkAnswer(username, question, answer);
        if (userCount == 0) return ReturnResponse.ReturnErrorByMessage("答案错误");
        String token = UUID.randomUUID().toString();
        TokenCache.setCache(Const.PREFIX + username, token);
        return ReturnResponse.ReturnSuccess("答案正确", token);
    }

    public ReturnResponse<String> updatePassword(String username, String passwordNew, String token) {
        if (StringUtils.isBlank(token)) return ReturnResponse.ReturnErrorByMessage("token不能为空");
        String tokenUse = TokenCache.getCache(Const.PREFIX + username);
        if (tokenUse == null) return ReturnResponse.ReturnErrorByMessage("token失效或者获取token时出错");
        if (tokenUse.equals(token)) {
            String MD5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int count = userMapper.updatePasswordByForget(username, MD5Password);
            if (count == 0) return ReturnResponse.ReturnErrorByMessage("更新密码失败");
            else return ReturnResponse.ReturnSuccessByMessage("修改密码成功");
        }
        return ReturnResponse.ReturnErrorByMessage("token错误，请检查");
    }

    public ReturnResponse<String> updatePwdWhileLogin(User user, String passwordOld, String passwordNew) {
        if (StringUtils.isBlank(passwordOld)) return ReturnResponse.ReturnErrorByMessage("请输入旧密码");
        int count = userMapper.checkPasswordOld(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (count == 0) return ReturnResponse.ReturnErrorByMessage("密码错误，请检查");
        else {
            User newUser = new User();
            newUser.setId(user.getId());
            newUser.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            int updateCount = userMapper.updateByPrimaryKeySelective(newUser);
            if (updateCount == 0) return ReturnResponse.ReturnErrorByMessage("更新密码失败");
            return ReturnResponse.ReturnSuccessByMessage("更新密码成功");
        }
    }

    public ReturnResponse<User> updateInformation(User user) {
        int count = userMapper.checkEmail(user.getEmail(), user.getId());
        if (count > 0) return ReturnResponse.ReturnErrorByMessage("该邮箱已被注册，请重新更换");
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount == 0) return ReturnResponse.ReturnErrorByMessage("更新个人信息失败");
        return ReturnResponse.ReturnSuccess("个人信息更新成功", user);
    }

    public ReturnResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) return ReturnResponse.ReturnErrorByMessage("查询用户信息出错");
        user.setPassword("");
        return ReturnResponse.ReturnSuccess("查询用户信息成功", user);
    }
}
