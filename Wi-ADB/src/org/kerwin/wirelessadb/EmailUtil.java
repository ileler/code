package org.kerwin.wirelessadb;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

	private static Properties properties;
	private static PassAuthenticator authenticator;
	private static InternetAddress fromAddress;
	
	static {
		properties = new Properties();
		properties.put("mail.smtp.protocol", "smtp");  
		properties.put("mail.smtp.auth", "true");			//设置要验证  
		properties.put("mail.smtp.host", "smtp.qq.com");	//设置host  
		properties.put("mail.smtp.port", "25");  			//设置端口 
		authenticator = new PassAuthenticator();   			//获取帐号密码 
		try {
			fromAddress = new InternetAddress("coderr@qq.com");
		} catch (AddressException e1) {
			e1.printStackTrace();
		}
	}
	

	/**
     * 发送邮件的方法
     * @return
     */
	public static void dynaSendEmail(final String email,final  String content){  
		new Thread(new Runnable() {
			@Override
			public void run() {
				sendEmail(email,content);
			}
		}).start();
	}
	
	/**
     * 发送邮件的方法
     * @return
     */
	public static boolean sendEmail(String email, String content){  
		if(email == null || email.isEmpty())	return false;
        Session session = Session.getInstance(properties, authenticator); //获取验证会话  
        try {  
        	InternetAddress toAddress = new InternetAddress(email);
        	//配置发送信息  
        	MimeMessage message = new MimeMessage(session);  
            message.setText(content);					//设置消息
            message.setFrom(fromAddress);				//设置发送者
            message.setSubject("Wi-ADB.log");  			//设置主题
            message.addRecipient(javax.mail.Message.RecipientType.TO, toAddress);  	//设置接收者
            message.saveChanges();  
            //发送  
            Transport.send(message);  
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException();
        }
    }
       
	static class PassAuthenticator extends Authenticator {  
		
       public PasswordAuthentication getPasswordAuthentication() {  
           return new PasswordAuthentication("coderr@qq.com", "O0:1603013767");  
       } 

   }  
	
}
