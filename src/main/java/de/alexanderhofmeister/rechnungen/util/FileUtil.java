package de.alexanderhofmeister.rechnungen.util;

import com.itextpdf.html2pdf.HtmlConverter;
import de.alexanderhofmeister.rechnungen.model.Bill;
import org.apache.commons.io.FileUtils;
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

public class FileUtil {

    public static String getFileNameBill(Bill bill) {
        return DateUtil.formatToDisplayDate(bill.getDate()) + " - "
                + bill.getCustomer().getCompany() + ".pdf";
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

    public static File createFileFromTemplate(LocalDate date, File targetFile, String templateName, Map<String, Object> attributes) {
        File currentArchive = new File(buildArchiveDirectory(date));
        currentArchive.mkdirs();
        try {
            final StringWriter stringWriter = new StringWriter();
            Velocity.evaluate(new VelocityContext(attributes), stringWriter, "PackageTemplatesVelocity",
                    FileUtils.readFileToString(new File("./templates/" + templateName + ".html"), StandardCharsets.ISO_8859_1));
            HtmlConverter.convertToPdf(stringWriter.toString(), new FileOutputStream(new File(currentArchive, targetFile.getName())));
            return targetFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
