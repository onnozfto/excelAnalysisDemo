package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.RptTemplateMapper;
import com.example.demo.model.RptTemplate;

@Service
public class DemoServiceImpl implements DemoService{

	@Autowired
	RptTemplateMapper rptTemplateMapper;
	
	public int saveRptTemplate(RptTemplate record)
	{
		
		RptTemplate tmp = rptTemplateMapper.selectByPrimaryKey(record.getUrl());
		if(tmp==null)
			return rptTemplateMapper.insert(record);
		else
			return rptTemplateMapper.updateByPrimaryKeySelective(record);
	}
	
	public RptTemplate selectByPrimaryKey(String url) {
		return rptTemplateMapper.selectByPrimaryKey(url);
	}
}
