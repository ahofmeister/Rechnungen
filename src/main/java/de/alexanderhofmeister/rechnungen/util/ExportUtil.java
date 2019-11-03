package de.alexanderhofmeister.rechnungen.util;

import com.itextpdf.html2pdf.HtmlConverter;
import de.alexanderhofmeister.rechnungen.model.Bill;
import de.alexanderhofmeister.rechnungen.model.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

public class ExportUtil {

    public static String getFileNameBill(Bill bill) {
        return DateUtil.formatToDisplayDate(bill.date) + " - "
                + bill.customer.company + ".pdf";
    }

    private static String buildArchiveDirectory(LocalDate date) {
        return "Archiv/" + date.getYear() + "/"
                + date.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMANY) + "/";
    }

    /**
     * Activates the print mechanismn of the computer and prints the given file.
     */
    public static void printFile(File file) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.PRINT)) {
            try {
                desktop.print(file);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public static File createFileFromTemplate(LocalDate date, File templateFile, String templateName, Map<String, Object> attributes) {
        File currentArchive = new File(buildArchiveDirectory(date));
        currentArchive.mkdirs();
        try {
            File targetFile = new File(currentArchive, templateFile.getName());
            HtmlConverter.convertToPdf(fillTemplateFromVariables(templateName, attributes), new FileOutputStream(targetFile));
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fillTemplateFromVariables(String templateName, Map<String, Object> attributes) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        Velocity.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        Velocity.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        Velocity.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        Velocity.evaluate(new VelocityContext(attributes), stringWriter, "PackageTemplatesVelocity",
                FileUtils.readFileToString(new File("templates/" + templateName + ".html"), StandardCharsets.UTF_8));
        return stringWriter.toString();
    }

    public static void sendViaEmail(File fileToAttach, String subject, String body, String to) {

        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(fileToAttach.getPath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setName(fileToAttach.getName());

        Properties instance = Properties.getInstance();


        MultiPartEmail email = new MultiPartEmail();

        email.setHostName(instance.getString("Mail-Server"));
        email.setSmtpPort(instance.getInt("Mail-Port"));
        String sender = instance.getString("E-Mail");
        email.setAuthenticator(
                new DefaultAuthenticator(sender,
                        instance.getString("Passwort")));
        email.setSSLOnConnect(true);

        email.setSubject(subject);
        try {
            email.addPart(body, "text/html; charset=UTF-8");
            email.setFrom(instance.getString("Benutzername") + " <" + sender + ">");
            email.addTo(to);
            email.attach(attachment);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }

    }
}
