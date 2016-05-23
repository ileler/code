package coderr.kerwin.osgi.demo.web;

import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import coderr.kerwin.osgi.demo.api.GeneratePRCode;

@Controller  
public class GCodeController {  
 
    @Resource  
    private GeneratePRCode handler = null;  
 
    @RequestMapping("gcode")
    public void doGCode(String key, HttpServletResponse response) {  
    	PrintWriter writer = null;
    	try {
    		writer = response.getWriter();
    		if (key == null || "".equals(key)) {
    			writer.write("Key is null.");
    		} else {
    			String code = handler.generatePRCode(key);
    			writer.write("code:" + (code == null ? "null" : code));
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		if (writer != null) 	writer.close();
    	}
    }  
}
