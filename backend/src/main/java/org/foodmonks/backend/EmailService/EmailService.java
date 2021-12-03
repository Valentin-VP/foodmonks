package org.foodmonks.backend.EmailService;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service 
public class EmailService {
	private final JavaMailSender emailSender;
	
	@Value("${mail.username}")
    private String mailReclamos;

	@Autowired
	public EmailService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
		//this.templateEngine = templateEngine;
	}


	public void enviarMail (String destinatario, String asunto, String htmlContent, String[] Cc) throws EmailNoEnviadoException {

		try {
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper message;
		message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setTo(destinatario);
		message.setFrom(mailReclamos);
		message.setSubject(asunto);
		if (Cc != null) message.setCc(Cc);
		message.setText(htmlContent, true);
		emailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new EmailNoEnviadoException("No se pudo enviar el mail");
		}


	}

}
