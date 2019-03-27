package com.example.demo.excel;

/**
 * @Description
 * @Author will
 * @Date 2019/3/12 0012 上午 10:13
 */
public interface CellValueType {

  int EXCEL_PRIMITIVE = 0;//excel模板原来的类型

  int CUSTOM_DATA_EXPRESSION = 10;//自定义匹配数据表达式

  int CUSTOM_PARAM_EXPRESSION = 11; //自定义参数表达式
}
