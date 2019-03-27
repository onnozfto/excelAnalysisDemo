package com.example.demo.service;

import com.example.demo.model.RptTemplate;

public interface DemoService {
	
	int saveRptTemplate(RptTemplate record);
	
	RptTemplate selectByPrimaryKey(String url);
}
