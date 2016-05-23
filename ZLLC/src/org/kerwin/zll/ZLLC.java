package org.kerwin.zll;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
  
class StreamToStringC{  
    public static String ConvertToString(InputStream inputStream){  
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);  
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
        StringBuilder result = new StringBuilder();  
        String line = null;  
        try {  
            while((line = bufferedReader.readLine()) != null){  
                result.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try{  
                inputStreamReader.close();  
                inputStream.close();  
                bufferedReader.close();  
            }catch(IOException e){  
                e.printStackTrace();  
            }  
        }  
        return result.toString();  
    }  
  
  
    public static String ConvertToString(FileInputStream inputStream){  
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);  
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
        StringBuilder result = new StringBuilder();  
        String line = null;  
        try {  
            while((line = bufferedReader.readLine()) != null){  
                result.append(line + "\n");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            try{  
                inputStreamReader.close();  
                inputStream.close();  
                bufferedReader.close();  
            }catch(IOException e){  
                e.printStackTrace();  
            }  
        }  
        return result.toString();  
    }  
}  
  
public class ZLLC {  
	
//    public static void main(String[] args) throws Exception{  
//    	System.out.println(ZLLC.getIP("url","id"));
//    }
    
    public static String getIP(String url,String id){
		try{
			if(url == null || "".equals(url) || "null".equalsIgnoreCase(url)){
	    		return "url is null";
	    	}
	    	if(id == null || "".equals(id) || "null".equalsIgnoreCase(id)){
	    		return "id is null";
	    	}
			URL URL = new URL(url + (url.contains("?") ? "&" : "?") + "id="+id+"&random=" + Math.random());  
			HttpURLConnection urlConnection = (HttpURLConnection)URL.openConnection();  
            //GET Request Define:   
            urlConnection.setRequestMethod("GET");  
            urlConnection.connect();  
            InputStream inputStream = urlConnection.getInputStream();  
            //Convert Stream to String  
            return StreamToStringC.ConvertToString(inputStream);
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
    }

}  