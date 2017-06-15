package com.catfish.dao;

import com.catfish.entity.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by A on 2017/4/26.
 */
public interface UserDao {

    UserEntity queryByAge(Integer age);

    Map queryMapByAge(Integer age);

    List<UserEntity> queryListByAge(Integer age);

    UserEntity queryByAge3(Integer age);

    UserEntity queryByAge4(Integer age);

    UserEntity queryByAge5(Integer age);

}
