package com.example.service;

import com.example.entity.Comment;
import com.example.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }
    public void add(Comment comment) {
        commentMapper.insert(comment);
    }

    public void update(Comment comment) {
        commentMapper.updateById(comment);
    }

    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }


    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    public PageInfo<Comment> selectPage(Integer pageNum, Integer pageSize, Comment comment) {
        // 开启分页查询
        PageHelper.startPage(pageNum, pageSize);  //  这两行必须写在一起才生效
        List<Comment> list = commentMapper.selectAll(comment); //  这两行必须写在一起才生效
        return PageInfo.of(list);


    }
}
