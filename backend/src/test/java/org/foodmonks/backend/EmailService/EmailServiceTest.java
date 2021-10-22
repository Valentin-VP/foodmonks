package org.foodmonks.backend.EmailService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender javaMailSender;

    //@Mock
    //private TemplateEngine templateEngine;

    @Mock
    private MimeMessage message;

    @BeforeEach
    public void setUp() {
       emailService = new EmailService(javaMailSender);
        ReflectionTestUtils.setField(emailService, "mailReclamos",
                System.getenv("mail.username"));
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void enviarMail() throws EmailNoEnviadoException {

        ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        //when(templateEngine.process(anyString(), any(Context.class))).thenReturn("");
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        //doNothing().when(javaMailSender).send(message);

        emailService.enviarMail("a","g","d", null);

        verify(javaMailSender).send((mimeMessageArgumentCaptor.capture()));
        assertThat(mimeMessageArgumentCaptor.getValue()).isEqualTo(message);

    }

    @Test
    void enviarMailDatosIncompletos() throws MessagingException {
        //when(templateEngine.process(anyString(), any(Context.class))).thenReturn("");
        when(javaMailSender.createMimeMessage()).thenReturn(message);

        assertThatThrownBy(()->emailService.enviarMail("","","",null))
                .isInstanceOf(EmailNoEnviadoException.class).hasMessageContaining("No se pudo enviar el mail");

        verify(javaMailSender, never()).send(any(MimeMessage.class));

    }

}