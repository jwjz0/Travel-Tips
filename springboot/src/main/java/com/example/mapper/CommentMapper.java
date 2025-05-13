package com.example.mapper;

import com.example.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper {

    List<Comment> selectAll(Comment comment);

    void insert(Comment comment);

    void updateById(Comment comment);

    @Delete("delete from `comment` where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from `comment` where id = #{id}")
    Comment selectById(Integer id);
}
