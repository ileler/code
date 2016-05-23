package org.kerwin.zll;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
  
class StreamToStringS{  
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
  
public class ZLLS {  
	
	private static String url = null;
	private static Timer selfT = new Timer("kerwin612");
	private static String id = null;
	private static File cf = null;
	private static int time = 300;
	private static Properties hostp = null;
    public static void main(String[] args) throws Exception{  
    	cf = new File("zlls.conf");
    	Properties p = new Properties();
    	if(!cf.exists()){
    		System.out.println("[Error]:[zlls.conf is not exists]");
    		System.exit(0);
    	}
    	p.load(new FileInputStream(cf));
    	id = p.getProperty("id");
    	if(id == null || "".equals(id)|| "null".equalsIgnoreCase(id)){
    		System.out.println("[Error]:[zlls.conf:id is null]");
    		System.exit(0);
    	}
    	url = p.getProperty("url");
    	if(url == null || "".equals(url)|| "null".equalsIgnoreCase(url)){
    		System.out.println("[Error]:[zlls.conf:url is null]");
    		System.exit(0);
    	}
    	String t = p.getProperty("tt");
    	try{
    		time = Integer.parseInt(t);
    	}catch(Exception e){
    		System.out.println("[Info]:[zlls.conf:tt is invalid.default value is 300]");
    	}
    	System.out.println("[Start Success]");
    	selfT.schedule(new ZLLS().new SelfTT(), 1);
    }  
    
    class SelfTT extends TimerTask{

		@Override
		public void run() {
//			BufferedWriter bw = null;
			try{
//				File lf = new File("logs.txt");
//				if(lf.exists()){
//					if(lf.length()>=10240){
//						lf.delete();
//					}
//				}
//				bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lf,true)));
				String ut = url + (url.contains("?") ? "&" : "?") + "id="+id+"&random=" + Math.random();
				sendRequest(ut + (ut.contains("?") ? "&" : "?") + "&localhost=" + InetAddress.getLocalHost().getHostAddress());
				if(hostp == null){
					hostp = System.getProperties();
					Set<Object> ks = hostp.keySet();
					for(Object k : ks){
						ut = url + (url.contains("?") ? "&" : "?") + "id="+id+"&random=" + Math.random();
						ut = ut + "&" + k + "=" + hostp.getProperty(k.toString()).replaceAll(" ", "_");
						sendRequest(ut);
					}
				}else{
					Properties p = new Properties();
					p.load(new FileInputStream(cf));
			    	String pt = p.getProperty("pt");
			    	if(pt != null && !"".equals(pt))
			    		ut = ut + (ut.contains("?") ? "&" : "?") + "&pt=" + pt;
					sendRequest(ut);
				}
			}catch(Exception e){
				e.printStackTrace();
//				if(bw != null){
//					try {
//						bw.write("[Error]:["+new Date()+"]:["+e.getMessage()+"]\n");
//						bw.close();
//					} catch (IOException e1) {
//						e.printStackTrace();
//					}
//				}
			}
			selfT.schedule(new ZLLS().new SelfTT(), time*1000);
		}
		
		public void sendRequest(final String ut){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL(ut);
						HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();  
						//GET Request Define:   
						urlConnection.setRequestMethod("GET");  
						urlConnection.connect();  
						String hms = String.valueOf(new Date().getTime());
						//Connection Response From Test Servlet  
						System.out.println("Connection Response From Servlet["+hms+"]");
						InputStream inputStream = urlConnection.getInputStream();  
						
						//Convert Stream to String  
						String responseStr = StreamToStringS.ConvertToString(inputStream);  
						System.out.println("["+hms+"]"+responseStr);
					} catch (Exception e) {
						e.printStackTrace();
					}  
				}
			}).start();
		}
    }
}  