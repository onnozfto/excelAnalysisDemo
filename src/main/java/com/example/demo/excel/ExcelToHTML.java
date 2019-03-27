package com.example.demo.excel;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.excel.model.CellInfo;
import com.example.demo.excel.model.StyleInfo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author
 * @date 2018/6/27 11:47
 * @desc
 */
public class ExcelToHTML {

  private final static String REGEX = "\\#[^\\#]*\\#";

  private final static String SEARCH_REG = "\\$[^\\$]*\\$";

  //缓存结果集数据
  private static ThreadLocal<JSONObject> dataMap = new ThreadLocal<>();

  /**
   * excel 展示成html表格
   */
  public static String readExcelToHtml(HttpServletResponse response, byte[] fileContent,
      boolean isWithStyle, List<JSONObject> datas, JSONObject paramObject) {

    InputStream is;
    String msg = null;

    Workbook wb = null;
    try {

      is = new ByteArrayInputStream(fileContent);
      //is = new FileInputStream(destFile);
      wb = WorkbookFactory.create(is);
      if (wb instanceof XSSFWorkbook) {   //07及10版以后的excel处理方法
        XSSFWorkbook xWb = (XSSFWorkbook) wb;
        msg = ExcelToHTML.getExcelInfo(xWb, isWithStyle, datas, paramObject);
      } else if (wb instanceof HSSFWorkbook) { //03excel处理方法
        HSSFWorkbook hWb = (HSSFWorkbook) wb;
        msg = ExcelToHTML.getExcelInfo(hWb, isWithStyle, datas, paramObject);
      }

      // 直接页面上显示
      showPage(msg, response);

      /**
       out = new FileOutputStream(destFile);
       //再用wb写到输出流
       wb.write(out);
       out.close();
       **/

    } catch (Exception e) {
      e.printStackTrace();
      return e.getMessage();
    } finally {

      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return "";
  }


  /*
   * @param content 生成的excel表格标签
   * @param htmlPath 生成的html文件地址
   */
  private static void showPage(String content, HttpServletResponse response) {

    StringBuilder sb = new StringBuilder();
    try {

      sb.append(
          "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><title>Html Test</title></head><body>");
      sb.append("<div>");
      sb.append(content);
      sb.append("</div>");
      sb.append("</body></html>");

      ServletOutputStream sos = response.getOutputStream();
      sos.write(sb.toString().getBytes());//将字符串写入文件
      sos.flush();
      sos.close();

    } catch (IOException e) {

      e.printStackTrace();
    }

  }

  /**
   * excel 展示成div中的table
   * @param fileContent
   * @param isWithStyle
   * @param datas
   * @param paramObject
   * @return
   */
  public static String readExcelToDiv(byte[] fileContent,
      boolean isWithStyle, List<JSONObject> datas, JSONObject paramObject) {

    InputStream is;
    String msg = null;

    Workbook wb = null;
    try {

      is = new ByteArrayInputStream(fileContent);
      //is = new FileInputStream(destFile);
      wb = WorkbookFactory.create(is);
      msg = ExcelToHTML.getExcelInfo(wb, isWithStyle, datas, paramObject);

    } catch (Exception e) {
      e.printStackTrace();
      return e.getMessage();
    } finally {
      IOUtils.closeQuietly(wb);
    }
    return msg;
  }


  /**
   * 根据excel模板生成excel
   */
  public static String readExcelToExcel(HttpServletResponse response, String fileName,
      byte[] fileContent, boolean isWithStyle, List<JSONObject> datas, JSONObject paramObject) {

    InputStream is = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Workbook wb = null;
    try {

      is = new ByteArrayInputStream(fileContent);
      wb = WorkbookFactory.create(is);
      ExcelToHTML.getExcelInfo(wb, isWithStyle, datas, paramObject);

      //再用wb写到输出流
      wb.write(baos);

      if (wb != null) {
        try {
          wb.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());

      // 下载流到文件.
      downloadFile(response, fileName, swapStream);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(is);
      IOUtils.closeQuietly(baos);
    }
    return "success";
  }


  /**
   * 下载文件
   */
  private static void downloadFile(HttpServletResponse response, String fileName, InputStream fis)
      throws Exception {
    if (fis != null) {
      String filename = URLEncoder.encode(fileName, "utf-8"); //解决中文文件名下载后乱码的问题
      byte[] b = new byte[fis.available()];
      fis.read(b);
      response.setCharacterEncoding("utf-8");
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename=" + filename + "");
      //获取响应报文输出流对象
      ServletOutputStream out = response.getOutputStream();
      //输出
      out.write(b);
      out.flush();
      out.close();

      try {
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * excel转换成jsonarray
   * @param response
   * @param fileContent
   * @param isWithStyle
   * @param datas
   * @param paramObject
   * @return
   */
  public static JSONArray getExcelJsonDatas(HttpServletResponse response, byte[] fileContent,
      boolean isWithStyle, List<JSONObject> datas, JSONObject paramObject) {

    InputStream is;

    Workbook wb = null;
    JSONArray array = null;
    try {

      is = new ByteArrayInputStream(fileContent);
      wb = WorkbookFactory.create(is);
      array = getCellInfos(wb, isWithStyle, datas, paramObject);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(wb);
    }
    return array;
  }

  private static JSONArray getCellInfos(Workbook wb, boolean isWithStyle, List<JSONObject> datas,
      JSONObject paramObject) {

    Sheet sheet = wb.getSheetAt(0);//获取第一个Sheet的内容
    int lastRowNum = sheet.getLastRowNum();//返回行下标
    Map<String, String> map[] = ExcelUtils.getMergeRegionMap(sheet);
    Row row ;        //兼容
    Cell cell;    //兼容
    JSONArray sheetJson = new JSONArray(lastRowNum + 1);
    for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
      row = sheet.getRow(rowNum);
      if (row == null) {
        sheetJson.add(null);
        continue;
      }
      JSONArray rowJson = new JSONArray();
      int lastColNum = row.getLastCellNum();//行下标加1
      for (int colNum = 0; colNum < lastColNum; colNum++) {
        cell = row.getCell(colNum, MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell == null) {    //特殊情况 空白的单元格会返回null
          rowJson.add(null);
          continue;
        }
        String stringValue = ExcelUtils.getCellValue(cell);
        //当前单元格是合并的
        int rowSpan = 1, colSpan = 1;
        if (map[0].containsKey(rowNum + "," + colNum)) {
          String pointString = map[0].get(rowNum + "," + colNum);
          map[0].remove(rowNum + "," + colNum);
          int bottomeRow = Integer.valueOf(pointString.split(",")[0]);
          int bottomeCol = Integer.valueOf(pointString.split(",")[1]);
          rowSpan = bottomeRow - rowNum + 1;
          colSpan = bottomeCol - colNum + 1;

        } else if (map[1].containsKey(rowNum + "," + colNum)) {
          map[1].remove(rowNum + "," + colNum);
          continue;
        }
        CellInfo cellInfo = dealCellData(stringValue, datas, paramObject);
        //判断是否需要样式
        if (isWithStyle) {
          StyleInfo styleInfo = ExcelUtils.getCellStyleInfo(wb, cell.getSheet(), cell);
          styleInfo.setRowspan(rowSpan);
          styleInfo.setColspan(colSpan);
          cellInfo.setStyleInfo(styleInfo);
        }
        rowJson.add(cellInfo);

      }
      sheetJson.add(rowJson);
    }
    return sheetJson;
  }

  private static String getExcelInfo(Workbook wb, boolean isWithStyle, List<JSONObject> datas,
      JSONObject paramObject) {

    StringBuffer sb = new StringBuffer();
    Sheet sheet = wb.getSheetAt(0);//获取第一个Sheet的内容
    int lastRowNum = sheet.getLastRowNum();
    Map<String, String> map[] = ExcelUtils.getMergeRegionMap(sheet);
    sb.append("<div><table style='border-collapse:collapse;' width='100%'>");
    Row row = null;        //兼容
    Cell cell = null;    //兼容

    for (int rowNum = sheet.getFirstRowNum(); rowNum <= lastRowNum; rowNum++) {
      row = sheet.getRow(rowNum);
      if (row == null) {
        sb.append("<tr><td ><nobr>&nbsp;&nbsp;</nobr></td></tr>");
        continue;
      }
      sb.append("<tr>");
      int lastColNum = row.getLastCellNum();
      for (int colNum = 0; colNum < lastColNum; colNum++) {
        cell = row.getCell(colNum);
        if (cell == null) {    //特殊情况 空白的单元格会返回null
          sb.append("<td>&nbsp;&nbsp;</td>");
          continue;
        }

        String stringValue = ExcelUtils.getCellValue(cell);
        if (map[0].containsKey(rowNum + "," + colNum)) {
          String pointString = map[0].get(rowNum + "," + colNum);
          map[0].remove(rowNum + "," + colNum);
          int bottomeRow = Integer.valueOf(pointString.split(",")[0]);
          int bottomeCol = Integer.valueOf(pointString.split(",")[1]);
          int rowSpan = bottomeRow - rowNum + 1;
          int colSpan = bottomeCol - colNum + 1;
          sb.append("<td rowspan= '" + rowSpan + "' colspan= '" + colSpan + "' ");
        } else if (map[1].containsKey(rowNum + "," + colNum)) {
          map[1].remove(rowNum + "," + colNum);
          continue;
        } else {
          sb.append("<td ");
        }
        //判断是否需要样式
        if (isWithStyle) {
          ExcelUtils.dealExcelStyle(wb, sheet, cell, sb);//处理单元格样式
        }

        sb.append("><nobr>");
        if (stringValue == null || "".equals(stringValue.trim())) {
          sb.append("   ");
        } else {
          if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            cell.setCellFormula(cell.getCellFormula());
          } else {
            // 将ascii码为160的空格转换为html下的空格（ ）
            stringValue = stringValue.replace(String.valueOf((char) 160), " ");
            CellInfo cellInfo = dealCellData(stringValue, datas, paramObject);
            sb.append(cellInfo.getValue());
            switch (cellInfo.getType()) {
              case CellValueType.EXCEL_PRIMITIVE:
                break;
              case CellValueType.CUSTOM_PARAM_EXPRESSION:
                cell.setCellValue(stringValue);
                break;
              case CellValueType.CUSTOM_DATA_EXPRESSION:
                try {
                  //值是数值类型，设置保留2位小数
                  cell.setCellValue(Double.valueOf(cellInfo.getValue()));
                  CellStyle style = cell.getCellStyle();
                  style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
                } catch (Exception e) {
                  //不是数值类型，单元格设置成文本
                  cell.setCellValue(cellInfo.getValue());
                }
                break;
              default:
                break;
            }

          }

        }
        sb.append("</nobr></td>");
      }
      sb.append("</tr>");
    }

    sb.append("</table></div>");
    return sb.toString();
  }


  private static CellInfo dealCellData(String stringValue, List<JSONObject> datas,
      JSONObject paramObject) {
    int type = CellValueType.EXCEL_PRIMITIVE;
    if (stringValue == null || stringValue.trim().equals("")) {
      return new CellInfo("", type);
    } else {
      Pattern pattern = Pattern.compile(REGEX);
      Matcher match = pattern.matcher(stringValue);

      while (match.find()) {
        String param = match.group();
        if (param != null && param.length() >= 3) {
          stringValue = getCellValue(datas, param.toUpperCase());
          type = CellValueType.CUSTOM_DATA_EXPRESSION;
        }

      }
      if (!match.matches()) {
        Pattern paramPattern = Pattern.compile(SEARCH_REG);
        Matcher paramMatch = paramPattern.matcher(stringValue);
        while (paramMatch.find()) {
          String group = paramMatch.group();
          if (group != null && group.length() >= 3) {
            stringValue = getCellValueWithParamObject(paramObject, group.toUpperCase());
            type = CellValueType.CUSTOM_PARAM_EXPRESSION;
          }
        }

      }

      return new CellInfo(stringValue, type);

    }
  }

  /***
   * 获取查询参数的值
   * @param paramObject
   * @param param
   * @return
   */
  private static String getCellValueWithParamObject(JSONObject paramObject, String param) {
    String value = "";
    if (paramObject == null) {
      return value;
    }
    if (param != null && !param.trim().equals("")) {
      String pp = param.substring(1, param.length() - 1);
      if (paramObject.containsKey(pp)) {
        value = paramObject.getString(pp);
      }
    }
    return value;
  }


  /**
   * 根据条件找到符合的行数据，再取对应的列数据
   */
  private static String getCellValue(List<JSONObject> datas, String param) {
    String value = "";
    if (datas == null) {
      return value;
    }
    if (param != null && !param.trim().equals("")) {
      String pp = param.substring(1, param.length() - 1);

      if (pp.contains("|")) {
        String[] arr = pp.split("\\|");

        if (arr != null && arr.length == 2) {
          String dataCondition = arr[0];//维度刷选条件
          String columnKey = arr[1];//数据库列字段
          // dataCondition 举例：curr=156&item=01
          Map<String, String> cmap = new HashMap<>();
          String[] cs = dataCondition.split("&");
          if (cs != null && cs.length > 0) {
            for (int n = 0; n < cs.length; n++) {
              String[] bds = cs[n].split("=");
              cmap.put(bds[0], bds[1]);
            }
          }
          //缓存查询的结果集到线程本地中
          if (Objects.isNull(dataMap.get())) {
            JSONObject map = new JSONObject();
            datas.forEach(data -> {
              StringBuilder sb = new StringBuilder();
              cmap.entrySet().forEach(entry -> {
                if (data.containsKey(entry.getKey())) {
                  sb.append(entry.getKey() + "=" + data.getString(entry.getKey()));
                  sb.append("&");
                } else {
                  throw new RuntimeException("excel填写的模板参数" + entry.getKey() + "没有字段对应！");
                }

              });
              map.put(sb.toString().substring(0, sb.length() - 1), data);
            });
            dataMap.set(map);
          }

          JSONObject cache = dataMap.get();
          if (cache.containsKey(dataCondition)) {
            JSONObject valueObject = cache.getJSONObject(dataCondition);
            return valueObject.getString(columnKey);
          }
          /*for (int k = 0; k < datas.size(); k++) {
            JSONObject obj = datas.get(k);

            boolean b = true;

            for (String ky : cmap.keySet()) {
              if (obj.containsKey(ky)) {
                String v1 = obj.getString(ky);
                String v2 = cmap.get(ky);
                if (v1 != null && !v1.equals(v2)) {
                  b = false;
                }
              } else {
                throw new RuntimeException("条件" + ky + "不存在!");
              }
            }

            // 所有条件都满足
            if (b == true) {
              if (obj != null) {
                if (obj.containsKey(key)) {
                  value = obj.getString(key);
                  return value;
                }
              }
            }
          }*/
        } else {
          throw new RuntimeException("excel填写的模板不符合规范,|符号后面必须带查询条件");
        }

      } else {
        // 没有条件的情况
        if (datas.size() > 1) {
          throw new RuntimeException("返回的业务数据只能有一条!");
        }

        value = datas.get(0).getString(pp);
      }
    }

    return value;
  }




  public static void main(String[] args) {
    String stringValue = "ag#eing#sgeg#aaa#gggg#ssni#gn$";

    String regex = "\\#[^\\#]*\\#";
    String string = "你好#abc#我是#def#早上好";
    Pattern pattern = Pattern.compile(regex);
    Matcher match = pattern.matcher(stringValue);
    while (match.find()) {
      System.out.println(match.group());
    }
    System.out.println("|".split("\\|").length);
    if (true) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("1", 1);
      dataMap.set(jsonObject);
    }
    Thread t = Thread.currentThread();
    if (!Objects.isNull(dataMap.get())) {
      System.out.println(dataMap.get().get("1"));
    }
  }
}