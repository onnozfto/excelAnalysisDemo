package com.example.demo.excel.model;

/**
 * @Description
 * @Author will
 * @Date 2019/3/22 0022 下午 17:32
 */
public class  CellInfo {

  private String value;

  private int type;

  private StyleInfo styleInfo;

  public CellInfo(String value, int type) {
    this.value = value;
    this.type = type;
  }

  public CellInfo(String value, int type, StyleInfo styleInfo) {
    this.value = value;
    this.type = type;
    this.styleInfo = styleInfo;
  }

  public String getValue() {
    return value;
  }

  public CellInfo setValue(String value) {
    this.value = value;
    return this;
  }

  public int getType() {
    return type;
  }

  public CellInfo setType(int type) {
    this.type = type;
    return this;
  }

  public StyleInfo getStyleInfo() {
    return styleInfo;
  }

  public void setStyleInfo(StyleInfo styleInfo) {
    this.styleInfo = styleInfo;
  }
}