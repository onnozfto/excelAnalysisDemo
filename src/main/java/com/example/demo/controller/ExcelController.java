package com.example.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.excel.ExcelToHTML;
import com.example.demo.model.RptTemplate;
import com.example.demo.service.DemoService;
import com.example.demo.vo.QueryCond;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ExcelController {

  @Autowired
  private DemoService demoService;
  private Cookie cookie;

  @Autowired
  private DataSource dataSource;


  @RequestMapping("/index")
  public String helloHtml(HashMap<String, Object> map) {
    QueryCond obj = new QueryCond();
    obj.setRptUrl("demo6");
    obj.setBusiDate("2019-01-15");
    obj.setAction("view");
    map.put("queryCond", obj);
    return "/index";
  }


  @RequestMapping("/upload")
  public String upload(HashMap<String, Object> map) {
    return "/upload";
  }

  @RequestMapping("/uploadStatus")
  public String uploadStatus(HashMap<String, Object> map) {
    return "/uploadStatus";
  }


  @PostMapping("/fileUpload")
  public String singleFileUpload(@RequestParam("url") String url,
      @RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) {
    if (file.isEmpty()) {
      redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
      return "redirect:uploadStatus";
    }

    try {
      // Get the file and save it somewhere
      byte[] bytes = file.getBytes();

      RptTemplate rt = new RptTemplate();
      rt.setFileName(file.getOriginalFilename());
      rt.setExcelTemplate(bytes);
      rt.setUrl(url);
      // Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
      // Files.write(path, bytes);
      this.demoService.saveRptTemplate(rt);

      redirectAttributes.addFlashAttribute("message",
          "You successfully uploaded '" + file.getOriginalFilename() + "'");

    } catch (IOException e) {
      e.printStackTrace();
    }
    //redirect:
    return "redirect:/uploadStatus";
  }

  @ResponseBody
  @RequestMapping(value = "/view", method = RequestMethod.POST)
  public String view(@ModelAttribute QueryCond queryCond, HttpServletResponse response) {
    System.out.println(queryCond.getAction() + " " + queryCond.getBusiDate());
    String url = queryCond.getRptUrl();
    RptTemplate rpt = demoService.selectByPrimaryKey(url);
    if (rpt == null) {
      return "报表未设置模版不存在";
    }
    String fileName = rpt.getFileName();
    return this.demo(fileName, rpt.getExcelTemplate(), queryCond.getAction(), response);
  }

  @ResponseBody
  @RequestMapping("/excel/{fileName}/{action}")
  public String demo(@PathVariable("fileName") String fileName, byte[] fileContent,
      @PathVariable("action") String action, HttpServletResponse response) {

    int lastIdx = fileName.lastIndexOf(".");
    if (lastIdx < 0) {
      return "fileName error,must end with *.xls,*.xlsx";
    }
  //List<JSONObject> data = this.datas("", fileName);
    List<JSONObject> data  = getDatas();
    JSONObject paramObject = new JSONObject();
    paramObject.put("RPT_DATE", "2017-11-01");

    String msg = null;
    cookie = new Cookie("authorization", "authorization");
    response.addCookie(cookie);
    if (action.equals("view")) {
      msg = ExcelToHTML.readExcelToHtml(response, fileContent, true, data, paramObject);
    } else if (action.equals("download")) {
      msg = ExcelToHTML.readExcelToExcel(response, fileName, fileContent, true, data, paramObject);
    } else if (action.equals("jsondata")) {
      JSONArray jsonData = ExcelToHTML.getExcelJsonDatas(response, fileContent, true, data, paramObject);
      msg = JSONObject.toJSONString(jsonData);
    }

    System.out.println("生成html成功");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    if (msg != null && msg.length() > 0) {
      return msg;
    }
    return "[" + sdf.format(new Date()) + "]:" + fileName + "转html成功！";
  }

  private List<JSONObject> getDatas() {
    JdbcTemplate template = new JdbcTemplate(dataSource);
    List<Map<String, Object>> list = template.queryForList("select * from  temp_rfr_rpt_fact_kb");
    return getJsonObjects(list);
  }
  private List<JSONObject> getJsonObjects(List<Map<String, Object>> list) {
    ArrayList<JSONObject> resultList = new ArrayList<>();
    for (Map<String, Object> map : list) {
      JSONObject jsonObject = new JSONObject();
      for (Entry<String, Object> entry : map.entrySet()) {
        jsonObject.put(entry.getKey().toUpperCase(), entry.getValue());
      }
      resultList.add(jsonObject);
    }
    return resultList;
  }
  /**
   * 模拟返回业务数据
   */
  @ResponseBody
  @RequestMapping("/datas/{rptUrl]/{query}")
  public List<JSONObject> datas(@PathVariable("rptUrl") String rptUrl,
      @PathVariable("query") String query) {

    System.out.println(query);

    List<JSONObject> ret = new ArrayList<JSONObject>();

    String[] curr = new String[]{"156", "001"};
    String[] items = new String[]{"01", "02", "03", "04", "05"};

    String[] types = new String[]{"A", "B"};

    for (int j = 0; j < curr.length; j++) {
      for (int k = 0; k < items.length; k++) {
        for (int y = 0; y < types.length; y++) {
          JSONObject obj = new JSONObject();
          //try {
          // 维度
          obj.put("DATE", "2019-01-16");
          // 币种
          obj.put("CURR", curr[j]);
          // 项目
          obj.put("ITEM", items[k]);
          // 类型
          obj.put("TYPE", types[y]);

          for (int i = 1; i <= 15; i++) {
            obj.put("K" + String.valueOf(i),
                curr[j] + " " + items[k] + "  " + types[y] + " K=" + i);
          }
          obj.put("QUERY", query);

          obj.put("CUST_NAME", "xxx");
          System.out.println(obj);
          ret.add(obj);
        }
      }
    }

    // 只返回一条(对于没有限定条件的。如excel 配置的是 #k8#)
    if (!"demo6.xlsx".equals(query)) {
      for (int k = ret.size() - 1; k > 0; k--) {
        ret.remove(k);
      }
    }
    return ret;
  }
}
