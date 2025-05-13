package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import com.example.entity.Account;
import com.example.entity.Category;
import com.example.entity.Introduction;
import com.example.entity.User;
import com.example.mapper.CategoryMapper;
import com.example.mapper.IntroductionMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class IntroductionService {


    @Resource
    CategoryMapper categoryMapper;
    @Resource
    UserMapper userMapper;

    private final IntroductionMapper introductionMapper;

    public IntroductionService(IntroductionMapper introductionMapper) {
        this.introductionMapper = introductionMapper;
    }

    public void add(Introduction introduction) {
        Account currentUser = TokenUtils.getCurrentUser();
        introduction.setUserId(currentUser.getId());
        introduction.setTime(DateUtil.now());
        introduction.setCollectionCount(0);
        introductionMapper.insert(introduction);
    }

    public void update(Introduction introduction) {
        introductionMapper.updateById(introduction);
    }

    public void deleteById(Integer id) {
        introductionMapper.deleteById(id);
    }


    public List<Introduction> selectAll(Introduction introduction) {
        List<Introduction> introductions = introductionMapper.selectAll(introduction);
        for (Introduction dbIntroduction : introductions) {
            dbIntroduction.setDescription(HtmlUtil.cleanHtmlTag(dbIntroduction.getContent()));
        }
        return introductions;
    }

    public PageInfo<Introduction> selectPage(Integer pageNum, Integer pageSize, Introduction introduction) {
        // 查之前给个条件，看当前用户是管理员还是普通用户
        Account currentUser = TokenUtils.getCurrentUser();
        if ("USER".equals(currentUser.getRole())) {
            introduction.setUserId(currentUser.getId());
        }
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);  //  这两行必须写在一起才生效
        List<Introduction> list = introductionMapper.selectAll(introduction); //  这两行必须写在一起才生效
//        for (Introduction dbIntroduction : list) {
//            Integer categoryId = dbIntroduction.getCategoryId();
//            Integer userId = dbIntroduction.getUserId();
//            Category category = categoryMapper.selectById(categoryId);
//            User user = userMapper.selectById(userId.toString());
//            if (ObjectUtil.isNotEmpty(category)) {
//                dbIntroduction.setCategoryId(category.getId());
//            }
//            if (ObjectUtil.isNotEmpty(user)) {
//                dbIntroduction.setUserName(user.getUsername());
//            }
//        }
        return PageInfo.of(list);


    }

    public Introduction selectById(Integer id) {
        Introduction dbIntroduction = introductionMapper.selectById(id);
        Integer categoryId = dbIntroduction.getCategoryId();
        Integer userId = dbIntroduction.getUserId();
        Category category = categoryMapper.selectById(categoryId);
        User user = userMapper.selectById(userId.toString());
        if (ObjectUtil.isNotEmpty(category)) {
            dbIntroduction.setCategoryTitle(category.getTitle());
        }
        if (ObjectUtil.isNotEmpty(user)) {
            dbIntroduction.setUserName(user.getName());
            dbIntroduction.setUserAvatar(user.getAvatar());
        }
        return dbIntroduction;
    }
}
