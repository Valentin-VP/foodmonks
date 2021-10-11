package org.foodmonks.backend.EmailService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service 
public class EmailService {
	private Log logger = LogFactory.getLog(getClass());
	@Autowired
    private JavaMailSender emailSender;
	@Autowired
	private TemplateEngine templateEngine;
	
	//@Value("${mail.username}")
    private String mailReclamos = System.getenv("mail.username");
	
	
	public Boolean enviarMail (String destinatario, String asunto, String contenido) {
		
		Context context = new Context();
		context.setVariable("contenido", contenido);
		String htmlContent = templateEngine.process("index", context);
		
		logger.info(mailReclamos);
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper message;
		try {
		message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setTo(destinatario);
		message.setFrom(mailReclamos);
		message.setSubject(asunto);
		message.setText(htmlContent, true);
		emailSender.send(mimeMessage);
		return true; 
		//logger.info("El correo se envio de manera exitosa!!");
		} catch (MessagingException e) {
			return false;
			//logger.info(e.getMessage());
		}
		
		
	}

}
