package com.example.controller;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Category;
import com.example.entity.Introduction;
import com.example.entity.User;
import com.example.service.CategoryService;
import com.example.service.IntroductionService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    public CategoryService categoryService;
    @Resource
    public IntroductionService introductionService;

    @RequestMapping("/pie")
    public Result pie() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Category> categories = categoryService.selectAll(new Category());
        List<Introduction> introductions = introductionService.selectAll(new Introduction());
        for (Category category : categories) {
            long count = introductions.stream().filter(x -> category.getId().equals(x.getCategoryId())).count();
            Map<String, Object> map = new HashMap<>();
            map.put("value", count);
            map.put("name", category.getTitle());
            list.add(map);
        }
        return Result.success(list);
    }

    @RequestMapping("/line")
    public Result line() {
        Map<String, Object> map = new HashMap<>();
        //回去最近7天的年月日（含当天）
        Date today = new Date();
        DateTime start = DateUtil.offsetDay(today, -6);
        List<String> xList = DateUtil.rangeToList(start, today, DateField.DAY_OF_YEAR).stream().map(DateUtil::formatDate).toList();
        List<Introduction> introductions = introductionService.selectAll(new Introduction());
        List<Category> categories = categoryService.selectAll(new Category());
        List<Object> nums = new ArrayList<>();
        List<String> categoryTitles = new ArrayList<>();
        for (Category category : categories) {
            categoryTitles.add(category.getTitle());
            for (String day : xList) {
                long categoryCount = introductions.stream().filter(x -> x.getTime().contains(day) && x.getCategoryId().equals(category.getId())).count();
                nums.add(categoryCount);
            }
            map.put(category.getTitle(), nums);
            nums = new ArrayList<>();
        }
        for (String day : xList) {
            long total = introductions.stream().filter(x -> x.getTime().contains(day)).count();
            nums.add(total);
        }
        map.put("total", nums);
        map.put("xAxis", xList);
        map.put("title", categoryTitles);
        return Result.success(map);
    }

    @RequestMapping("/bar")
    public Result bar() {
        // 用户可以看到自己发布的所有攻略数量，并且各个种类的攻略数
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        List<Category> categories = categoryService.selectAll(new Category());
        List<Introduction> introductions = introductionService.selectAll(new Introduction());
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getRole().equals("USER")) {
            long count = introductions.stream().filter(x -> currentUser.getId().equals(x.getUserId())).count();
            map.put("总攻略数量", count);
            for (Category category : categories) {
                long categoryCount = introductions.stream().filter(x -> x.getUserId().equals(currentUser.getId()) && category.getId().equals(x.getCategoryId())).count();
                map.put(category.getTitle(), categoryCount);
            }
        }

        return Result.success(map);
    }
}
