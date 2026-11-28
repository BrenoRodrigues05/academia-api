package com.academia.academia_api.services;

import com.academia.academia_api.entity.Matricula;
import com.academia.academia_api.entity.Pagamento;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

@Service
public class NotificacaoCobrancaService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoCobrancaService.class);
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final JavaMailSender mailSender;
    private final String remetente;
    private final String nomeAcademia;

    public NotificacaoCobrancaService(JavaMailSender mailSender,
                                      @Value("${app.mail.from}") String remetente,
                                      @Value("${app.mail.nome-academia}") String nomeAcademia) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.nomeAcademia = nomeAcademia;
    }

    public void enviarCobranca(Matricula matricula, Pagamento pagamento) {
        String email = matricula.getAluno().getEmail();
        if (email == null || email.isBlank()) {
            log.warn("Aluno da matrícula {} sem e-mail, cobrança não enviada.", matricula.getMatricula());
            return;
        }

        String assunto = "Sua mensalidade venceu - " + nomeAcademia;
        enviar(email.trim(), assunto, montarHtml(matricula, pagamento), pagamento.getQrCodeBase64());
    }

    private void enviar(String para, String assunto, String html, String qrCodeBase64) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(remetente, nomeAcademia);
            helper.setTo(para);
            helper.setSubject(assunto);
            helper.setText(html, true);

            if (qrCodeBase64 != null && !qrCodeBase64.isBlank()) {
                byte[] png = Base64.getDecoder().decode(qrCodeBase64);
                helper.addInline("qrcode", new ByteArrayResource(png), "image/png");
            }

            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("Falha ao montar e-mail de cobrança", e);
        }
    }

    private String montarHtml(Matricula matricula, Pagamento pagamento) {
        String nome = HtmlUtils.htmlEscape(primeiroNome(matricula.getAluno().getNome()));
        String plano = HtmlUtils.htmlEscape(matricula.getPlano().getNome());
        String valor = MOEDA.format(pagamento.getValor());
        String vencimento = matricula.getDataVencimento().format(DATA);
        String limite = matricula.getDataVencimento()
                .plusDays(CicloMatriculaService.DIAS_CARENCIA).format(DATA);
        String codigoPix = HtmlUtils.htmlEscape(pagamento.getCodigoPix());

        boolean temQr = pagamento.getQrCodeBase64() != null && !pagamento.getQrCodeBase64().isBlank();
        String imgQr = temQr
                ? "<p style=\"text-align:center\"><img src=\"cid:qrcode\" width=\"200\" height=\"200\" alt=\"QR Code Pix\"/></p>"
                : "";

        return """
            <div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;color:#222">
              <h2>Olá, %s!</h2>
              <p>A mensalidade do plano <b>%s</b> venceu em <b>%s</b>.</p>
              <p>Valor: <b>%s</b></p>
              <p>Pague até <b>%s</b> para manter seu acesso sem interrupções.</p>
              %s
              <p><b>Pix copia e cola:</b></p>
              <p style="word-break:break-all;background:#f4f4f4;padding:12px;border-radius:6px;font-size:12px">%s</p>
              <p style="font-size:12px;color:#777">Se você já pagou, desconsidere este e-mail.</p>
            </div>
            """.formatted(nome, plano, vencimento, valor, limite, imgQr, codigoPix);
    }

    private String primeiroNome(String nomeCompleto) {
        return nomeCompleto == null ? "" : nomeCompleto.trim().split("\\s+")[0];
    }
}