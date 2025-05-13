package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Notice;
import com.example.excption.CustomerException;
import com.example.mapper.NoticeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }
    public void add(Notice notice) {
        Account currentUser = TokenUtils.getCurrentUser();
        if( "USER".equals(currentUser.getRole())){
            throw new CustomerException("500", "您的角色暂无权限该操作");
        }
        notice.setTime(DateUtil.now());
        noticeMapper.insert(notice);
    }

    public void update(Notice notice) {
        Account currentUser = TokenUtils.getCurrentUser();
        if( "USER".equals(currentUser.getRole())){
            throw new CustomerException("500", "您的角色暂无权限该操作");
        }
        noticeMapper.updateById(notice);
    }

    public void deleteById(Integer id) {
        Account currentUser = TokenUtils.getCurrentUser();
        if( "USER".equals(currentUser.getRole())){
            throw new CustomerException("500", "您的角色暂无权限该操作");
        }
        noticeMapper.deleteById(id);
    }


    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice);
    }

    public PageInfo<Notice> selectPage(Integer pageNum, Integer pageSize, Notice notice) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);  //  这两行必须写在一起才生效
        List<Notice> list = noticeMapper.selectAll(notice); //  这两行必须写在一起才生效
        return PageInfo.of(list);


    }
}
