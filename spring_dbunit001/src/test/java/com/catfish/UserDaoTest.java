package com.catfish;

import com.catfish.dao.UserDao;
import com.catfish.entity.UserEntity;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by A on 2017/4/26.
 */
public class UserDaoTest {


    @Test
    public void queryByAgeTest() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        UserEntity userEntity = userMapper.queryByAge(2);
        System.out.println(userEntity);
    }

    @Test
    public void queryMapByAge() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        Map map = userMapper.queryMapByAge(2);
        System.out.println(map);
    }

    @Test
    public void queryListByAge() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        List<UserEntity> list = userMapper.queryListByAge(3);
        System.out.println(list);
    }

    @Test
    public void queryByAge3Test() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        UserEntity userEntity = userMapper.queryByAge3(2);
        System.out.println(userEntity);
    }

    @Test
    public void queryByAge4Test() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        UserEntity userEntity = userMapper.queryByAge4(2);
        System.out.println(userEntity);
    }

    @Test
    public void queryByAge5Test() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        UserEntity userEntity = userMapper.queryByAge5(2);
        System.out.println(userEntity);
    }


    //Mybatis 通过SqlSessionFactory获取SqlSession, 然后才能通过SqlSession与数据库进行交互
    private static SqlSessionFactory getSessionFactory() {
        SqlSessionFactory sessionFactory = null;
        String resource = "mybatis-config.xml";
        try {
            sessionFactory = new SqlSessionFactoryBuilder().build(Resources
                    .getResourceAsReader(resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }

}
