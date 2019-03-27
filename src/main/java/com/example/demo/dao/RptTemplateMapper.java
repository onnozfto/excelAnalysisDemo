package com.example.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.RptTemplate;

@Mapper
public interface RptTemplateMapper {
    int deleteByPrimaryKey(String url);

    int insert(RptTemplate record);

    int insertSelective(RptTemplate record);

    RptTemplate selectByPrimaryKey(String url);

    int updateByPrimaryKeySelective(RptTemplate record);

    int updateByPrimaryKeyWithBLOBs(RptTemplate record);

    int updateByPrimaryKey(RptTemplate record);
}