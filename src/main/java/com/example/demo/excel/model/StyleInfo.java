package com.example.demo.excel.model;

/**
 * @Description
 * @Author will
 * @Date 2019/3/22 0022 下午 17:31
 */
public class  StyleInfo {

  private String textAlign;//文本对齐

  private String valign;//垂直对齐

  private String fontSize;//大小

  private String fontWeight;//字体加粗

  private String width;//宽度

  private String fontColor;//颜色

  private String bgColor;//背景颜色

  private String borderTop;//上边框

  private String borderRight;//右边框

  private String borderBottom;//下边框

  private String borderLeft;//左边框

  private Integer rowspan;//行合并

  private Integer colspan;//列合并

  public String getTextAlign() {
    return textAlign;
  }

  public StyleInfo setTextAlign(String textAlign) {
    this.textAlign = textAlign;
    return this;
  }

  public String getValign() {
    return valign;
  }

  public StyleInfo setValign(String valign) {
    this.valign = valign;
    return this;
  }

  public String getFontSize() {
    return fontSize;
  }

  public StyleInfo setFontSize(String fontSize) {
    this.fontSize = fontSize;
    return this;
  }

  public String getFontWeight() {
    return fontWeight;
  }

  public StyleInfo setFontWeight(String fontWeight) {
    this.fontWeight = fontWeight;
    return this;
  }

  public String getWidth() {
    return width;
  }

  public StyleInfo setWidth(String width) {
    this.width = width;
    return this;
  }

  public String getFontColor() {
    return fontColor;
  }

  public StyleInfo setFontColor(String fontColor) {
    this.fontColor = fontColor;
    return this;
  }

  public String getBgColor() {
    return bgColor;
  }

  public StyleInfo setBgColor(String bgColor) {
    this.bgColor = bgColor;
    return this;
  }

  public String getBorderTop() {
    return borderTop;
  }

  public StyleInfo setBorderTop(String borderTop) {
    this.borderTop = borderTop;
    return this;
  }

  public String getBorderRight() {
    return borderRight;
  }

  public StyleInfo setBorderRight(String borderRight) {
    this.borderRight = borderRight;
    return this;
  }

  public String getBorderBottom() {
    return borderBottom;
  }

  public StyleInfo setBorderBottom(String borderBottom) {
    this.borderBottom = borderBottom;
    return this;
  }

  public String getBorderLeft() {
    return borderLeft;
  }

  public StyleInfo setBorderLeft(String borderLeft) {
    this.borderLeft = borderLeft;
    return this;
  }

  public Integer getRowspan() {
    return rowspan;
  }

  public void setRowspan(Integer rowspan) {
    this.rowspan = rowspan;
  }

  public Integer getColspan() {
    return colspan;
  }

  public void setColspan(Integer colspan) {
    this.colspan = colspan;
  }
}