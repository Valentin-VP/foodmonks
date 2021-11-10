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
	//private final TemplateEngine templateEngine;
	
	@Value("${mail.username}")
    private String mailReclamos;

	@Autowired
	public EmailService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
		//this.templateEngine = templateEngine;
	}


	public void enviarMail (String destinatario, String asunto, String htmlContent, String[] Cc) throws EmailNoEnviadoException {
		
		//ARMAR EL CONTENIDO DEL MAIL SE DEBE ENCARGAR EL CASO DE USO QUE LO REQUIERA. ESTO VA AHI.
				//Context context = new Context();
				//context.setVariable("contenido", contenido);
				//String htmlContent = templateEngine.process("index", context);
		
		MimeMessage mimeMessage = emailSender.createMimeMessage();
		MimeMessageHelper message;
		try {
		message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setTo(destinatario);
		message.setFrom(mailReclamos);
		message.setSubject(asunto);
		if (Cc != null) message.setCc(Cc);
		message.setText(htmlContent, true);
		emailSender.send(mimeMessage);
		 
		//logger.info("El correo se envio de manera exitosa!!");
		} catch (MessagingException e) {
			throw new EmailNoEnviadoException("No se pudo enviar el mail");
		}
		
		
	}

}
