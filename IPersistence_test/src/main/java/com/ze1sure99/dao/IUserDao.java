package com.ze1sure99.dao;

import com.ze1sure99.pojo.User;

import java.util.List;

public interface IUserDao {
    //1.查询所有用户
     public List<User> findAll();
    //2.根据条件进行查询
    public User findByCondition(User user);
}
