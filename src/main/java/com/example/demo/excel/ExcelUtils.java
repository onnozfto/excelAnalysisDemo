package com.example.demo.excel;

import com.example.demo.excel.model.StyleInfo;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Description
 * @Author will
 * @Date 2019/3/22 0022 下午 16:35
 */
public class ExcelUtils {

  public static StyleInfo getCellStyleInfo(Workbook wb, Sheet sheet, Cell cell) {
    StyleInfo styleInfo = new StyleInfo();
    CellStyle cellStyle = cell.getCellStyle();
    if (cellStyle != null) {
      short alignment = cellStyle.getAlignment();
      short verticalAlignment = cellStyle.getVerticalAlignment();
      styleInfo.setValign(convertVerticalAlignToHtml(verticalAlignment));
      if (wb instanceof XSSFWorkbook) {
        XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
        String align = convertAlignToHtml(alignment);
        styleInfo.setFontWeight(String.valueOf(xf.getBoldweight()));
        styleInfo.setFontSize(xf.getFontHeight() / 2 + "%");
        styleInfo.setWidth(String.valueOf(sheet.getColumnWidth(cell.getColumnIndex())) + "px");
        styleInfo.setTextAlign(align);
        XSSFColor xc = xf.getXSSFColor();
        styleInfo.setFontColor("#" + xc.getARGBHex().substring(2));
        XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
        if (!Objects.isNull(bgColor)) {
          styleInfo.setBgColor("#" + bgColor.getARGBHex().substring(2));
        }
        styleInfo.setBorderTop(getBorderStyle(0, cellStyle.getBorderTop(),
            ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
        styleInfo.setBorderRight(getBorderStyle(1, cellStyle.getBorderRight(),
            ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
        styleInfo.setBorderBottom(getBorderStyle(2, cellStyle.getBorderBottom(),
            ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
        styleInfo.setBorderLeft(getBorderStyle(3, cellStyle.getBorderLeft(),
            ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));

      } else if (wb instanceof HSSFWorkbook) {

        HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
        styleInfo.setFontWeight(String.valueOf(hf.getBoldweight()));
        styleInfo.setFontSize(hf.getFontHeight() / 2 + "%");
        HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
        HSSFColor hc = palette.getColor(hf.getColor());
        styleInfo.setFontColor(convertToStardColor(hc));
        styleInfo.setTextAlign(convertAlignToHtml(alignment));
        styleInfo.setWidth(String.valueOf(sheet.getColumnWidth(cell.getColumnIndex())) + "px");
        short bgColor = cellStyle.getFillForegroundColor();
        hc = palette.getColor(bgColor);
        String bgColorStr = convertToStardColor(hc);
        if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
          styleInfo.setBgColor(bgColorStr);
        }
        styleInfo.setBorderTop(getBorderStyle(palette, 0, cellStyle.getBorderTop(), cellStyle.getTopBorderColor()));
        styleInfo.setBorderRight(getBorderStyle(palette, 1, cellStyle.getBorderRight(),
            cellStyle.getRightBorderColor()));
        styleInfo.setBorderBottom(getBorderStyle(palette, 2, cellStyle.getBorderBottom(),
            cellStyle.getBottomBorderColor()));
        styleInfo.setBorderLeft(getBorderStyle(palette, 3, cellStyle.getBorderLeft(), cellStyle.getLeftBorderColor()));

      }
    }
    return styleInfo;
  }



  /**
   * 处理表格样式
   */
  public static void dealExcelStyle(Workbook wb, Sheet sheet, Cell cell, StringBuffer sb) {

    CellStyle cellStyle = cell.getCellStyle();
    if (cellStyle != null) {
      short alignment = cellStyle.getAlignment();
      //    sb.append("align='" + convertAlignToHtml(alignment) + "' ");//单元格内容的水平对齐方式
      short verticalAlignment = cellStyle.getVerticalAlignment();
      sb.append("valign='" + convertVerticalAlignToHtml(verticalAlignment) + "' ");//单元格中内容的垂直排列方式

      if (wb instanceof XSSFWorkbook) {
        XSSFFont xf = ((XSSFCellStyle) cellStyle).getFont();
        short boldWeight = xf.getBoldweight();
        String align = convertAlignToHtml(alignment);
        sb.append("style='");
        sb.append("font-weight:" + boldWeight + ";"); // 字体加粗
        sb.append("font-size: " + xf.getFontHeight() / 2 + "%;"); // 字体大小
        int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
        sb.append("width:" + columnWidth + "px;");
        sb.append("text-align:" + align + ";");//表头排版样式
        XSSFColor xc = xf.getXSSFColor();
        if (xc != null && !"".equals(xc)) {
          sb.append("fontColor:#" + xc.getARGBHex().substring(2) + ";"); // 字体颜色
        }

        XSSFColor bgColor = (XSSFColor) cellStyle.getFillForegroundColorColor();
        if (bgColor != null && !"".equals(bgColor)) {
          sb.append("background-fontColor:#" + bgColor.getARGBHex().substring(2) + ";"); // 背景颜色
        }
        sb.append(getBorderStyle(0, cellStyle.getBorderTop(),
            ((XSSFCellStyle) cellStyle).getTopBorderXSSFColor()));
        sb.append(getBorderStyle(1, cellStyle.getBorderRight(),
            ((XSSFCellStyle) cellStyle).getRightBorderXSSFColor()));
        sb.append(getBorderStyle(2, cellStyle.getBorderBottom(),
            ((XSSFCellStyle) cellStyle).getBottomBorderXSSFColor()));
        sb.append(getBorderStyle(3, cellStyle.getBorderLeft(),
            ((XSSFCellStyle) cellStyle).getLeftBorderXSSFColor()));

      } else if (wb instanceof HSSFWorkbook) {

        HSSFFont hf = ((HSSFCellStyle) cellStyle).getFont(wb);
        short boldWeight = hf.getBoldweight();
        short fontColor = hf.getColor();
        sb.append("style='");
        HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette(); // 类HSSFPalette用于求的颜色的国际标准形式
        HSSFColor hc = palette.getColor(fontColor);
        sb.append("font-weight:" + boldWeight + ";"); // 字体加粗
        sb.append("font-size: " + hf.getFontHeight() / 2 + "%;"); // 字体大小
        String align = convertAlignToHtml(alignment);
        sb.append("text-align:" + align + ";");//表头排版样式
        String fontColorStr = convertToStardColor(hc);
        if (fontColorStr != null && !"".equals(fontColorStr.trim())) {
          sb.append("fontColor:" + fontColorStr + ";"); // 字体颜色
        }
        int columnWidth = sheet.getColumnWidth(cell.getColumnIndex());
        sb.append("width:" + columnWidth + "px;");
        short bgColor = cellStyle.getFillForegroundColor();
        hc = palette.getColor(bgColor);
        String bgColorStr = convertToStardColor(hc);
        if (bgColorStr != null && !"".equals(bgColorStr.trim())) {
          sb.append("background-fontColor:" + bgColorStr + ";"); // 背景颜色
        }

        sb.append(
            getBorderStyle(palette, 0, cellStyle.getBorderTop(), cellStyle.getTopBorderColor()));
        sb.append(getBorderStyle(palette, 1, cellStyle.getBorderRight(),
            cellStyle.getRightBorderColor()));
        sb.append(
            getBorderStyle(palette, 3, cellStyle.getBorderLeft(), cellStyle.getLeftBorderColor()));
        sb.append(getBorderStyle(palette, 2, cellStyle.getBorderBottom(),
            cellStyle.getBottomBorderColor()));

      }

      sb.append("' ");
    }
  }

  private static String convertToStardColor(HSSFColor hc) {

    StringBuffer sb = new StringBuffer("");
    if (hc != null) {
      if (HSSFColor.AUTOMATIC.index == hc.getIndex()) {
        return null;
      }
      sb.append("#");
      for (int i = 0; i < hc.getTriplet().length; i++) {
        sb.append(fillWithZero(Integer.toHexString(hc.getTriplet()[i])));
      }
    }

    return sb.toString();
  }

  private static String fillWithZero(String str) {
    if (str != null && str.length() < 2) {
      return "0" + str;
    }
    return str;
  }

  static String[] bordesr = {"border-top:", "border-right:", "border-bottom:", "border-left:"};
  static String[] borderStyles = {"solid ", "solid ", "solid ", "solid ", "solid ", "solid ",
      "solid ", "solid ", "solid ", "solid", "solid", "solid", "solid", "solid"};


  /**
   * 个别单元格本来要黑色，却为灰色，是因为读取到excle border的宽度为0，重新设置excle模版并上传就好了。
   */
  private static String getBorderStyle(HSSFPalette palette, int b, short s, short t) {

    if (s == 0) {
      return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
    }
    ;
    String borderColorStr = convertToStardColor(palette.getColor(t));
    borderColorStr =
        borderColorStr == null || borderColorStr.length() < 1 ? "#000000" : borderColorStr;
    return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";

  }

  /**
   * 个别单元格本来要黑色，却为灰色，是因为读取到excle border的宽度为0，重新设置excle模版并上传就好了。
   *
   * @param s border的大小
   */
  private static String getBorderStyle(int b, short s, XSSFColor xc) {

    if (s == 0) {
      return bordesr[b] + borderStyles[s] + "#d0d7e5 1px;";
    }
    ;
    if (xc != null && !"".equals(xc)) {
      String borderColorStr = xc.getARGBHex();//t.getARGBHex();
      borderColorStr = borderColorStr == null || borderColorStr.length() < 1 ? "#000000"
          : borderColorStr.substring(2);
      return bordesr[b] + borderStyles[s] + borderColorStr + " 1px;";
    }

    return "";
  }
  /**
   * 获取sheet页单元格合并的映射
   * @param sheet
   * @return
   */
  public static Map<String, String>[] getMergeRegionMap(Sheet sheet) {

    Map<String, String> regionStartMap = new HashMap();//excel合并的单元格起始行列
    Map<String, String> regionRemainMap = new HashMap();//excel合并的单元格不包含起始行列
    int mergedNum = sheet.getNumMergedRegions();
    CellRangeAddress range;
    for (int i = 0; i < mergedNum; i++) {
      range = sheet.getMergedRegion(i);
      int topRow = range.getFirstRow();
      int topCol = range.getFirstColumn();
      int bottomRow = range.getLastRow();
      int bottomCol = range.getLastColumn();
      regionStartMap.put(topRow + "," + topCol, bottomRow + "," + bottomCol);
      int tempRow = topRow;
      while (tempRow <= bottomRow) {
        int tempCol = topCol;
        while (tempCol <= bottomCol) {
          regionRemainMap.put(tempRow + "," + tempCol, "");
          tempCol++;
        }
        tempRow++;
      }
      regionRemainMap.remove(topRow + "," + topCol);
    }
    return new Map[]{regionStartMap, regionRemainMap};
  }
  public  static String getCellValue(Cell cell) {
    String result ;
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_NUMERIC:// 数字类型
        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
          SimpleDateFormat sdf = null;
          if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
            sdf = new SimpleDateFormat("HH:mm");
          } else {// 日期
            sdf = new SimpleDateFormat("yyyy-MM-dd");
          }
          Date date = cell.getDateCellValue();
          result = sdf.format(date);
        } else if (cell.getCellStyle().getDataFormat() == 58) {
          // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          double value = cell.getNumericCellValue();
          Date date = org.apache.poi.ss.usermodel.DateUtil
              .getJavaDate(value);
          result = sdf.format(date);
        } else {
          double value = cell.getNumericCellValue();
          CellStyle style = cell.getCellStyle();
          DecimalFormat format = new DecimalFormat();
          String temp = style.getDataFormatString();
          // 单元格设置成常规
          if (temp.equals("General")) {
            format.applyPattern("#");
          }
          result = format.format(value);
        }
        break;
      case Cell.CELL_TYPE_STRING:// String类型
        result = cell.getRichStringCellValue().toString();
        break;
      case Cell.CELL_TYPE_FORMULA: //excel 公式
        result = cell.getCellFormula();
        break;
      case Cell.CELL_TYPE_BLANK:
        result = "";
        break;
      case  Cell.CELL_TYPE_BOOLEAN:
        result = String.valueOf(cell.getBooleanCellValue());
      default:
        result = "";
        break;
    }
    return result;
  }

  /**
   *  解析单元格水平对齐方式
   * @param alignment
   * @return
   */
  public static String convertAlignToHtml(short alignment) {

    String align = "center";
    switch (alignment) {
      case CellStyle.ALIGN_LEFT:
        align = "left";
        break;
      case CellStyle.ALIGN_CENTER:
        align = "center";
        break;
      case CellStyle.ALIGN_RIGHT:
        align = "right";
        break;
      case  CellStyle.ALIGN_JUSTIFY:
        align = "justify";
      default:
        break;
    }
    return align;
  }

  /**
   * 解析单元格垂直对齐方式
   * @param verticalAlignment
   * @return
   */
  public static String convertVerticalAlignToHtml(short verticalAlignment) {

    String valign = "middle";

    switch (verticalAlignment) {

      case CellStyle.VERTICAL_BOTTOM:
        valign = "bottom";
        break;
      case CellStyle.VERTICAL_CENTER:
        valign = "middle";
        break;
      case CellStyle.VERTICAL_TOP:
        valign = "top";
        break;
      default:
        break;
    }
    return valign;
  }
}
