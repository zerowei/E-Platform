package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int CheckUsername(String username);

    User CheckUser(@Param("username") String username, @Param("password") String password);

    int CheckEmail(String email);

    String selectQuestion(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordByForget(@Param("username") String username, @Param("passwordNew")String passwordNew);

    int checkPasswordOld(@Param("id")Integer id, @Param("passwordOld") String passwordOld);

    int checkEmail(@Param("email") String email, @Param("id") Integer id);
}