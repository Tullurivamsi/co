package in.ashokit.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {
	

	 private JavaMailSender mailSender;
	

	private EmailUtils emailUtils;
	
	private Logger logger=LoggerFactory.getLogger(EmailUtils.class);
	
	
	public boolean sendEmail(String to, String subject, String body,File file)
	{
		
		boolean isMailSent =false;
		
		try {
		MimeMessage mimeMessage =mailSender.createMimeMessage();
		
		MimeMessageHelper helper =new MimeMessageHelper(mimeMessage);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body , true);
		
		helper.addAttachment("His-Elig-Notice",file);
		
		
		mailSender.send(mimeMessage);
		isMailSent =true;
			
		}catch(Exception e) {
			logger.error("Exception Occured",file);
		}
		
		return isMailSent;
	}
	 

}
