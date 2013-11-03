package mail.impl;

import mail.SenderService;
import model.IndexModel;
import org.apache.commons.mail.EmailException;
import utils.ApplicationContext;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: ded
 * Date: 26.05.13
 * Time: 23:00
 */
public class SenderServiceImpl implements SenderService {

    private Properties props;
    private Session session;
    private Message message;

    public SenderServiceImpl() throws EmailException, IOException, MessagingException {
        initialize();
    }

    void initialize() throws EmailException, IOException, MessagingException {
        Properties appConfig =  ApplicationContext.getAppConfig();

        props = new Properties();
        session = Session.getDefaultInstance(props, null);
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(appConfig.getProperty("email.from")));
        message.setRecipient(
                Message.RecipientType.TO, new InternetAddress(appConfig.getProperty("email.to")));
        message.setSubject("Message from google app engine");
    }

    @Override
    public void prepareAndSend(
            List<IndexModel> indexedModels, List<String> unindexedUrls)
            throws EmailException, MessagingException {

        StringBuilder indexedAdverts = appendIndexedAdvert(indexedModels);
        StringBuilder unindexedAdverts = appendUnindexedAdvert(unindexedUrls);

        String htmlMsg = String.valueOf(indexedAdverts) + "<br><br><br>" + unindexedAdverts;

        // prepare message body
        Multipart htmlBody = new MimeMultipart();

        MimeBodyPart htmlMimePart = new MimeBodyPart();
        htmlMimePart.setContent(htmlMsg, "text/html");
        htmlBody.addBodyPart(htmlMimePart);

        message.setContent(htmlBody);
        Transport.send(message);
    }

    //todo refactor
    StringBuilder appendIndexedAdvert(List<IndexModel> models) {
        StringBuilder stringBuilder = new StringBuilder("<table>");

        for (IndexModel model : models) {
            List<String> urls = model.getUrls();
            stringBuilder.append("<tr>");

            int idRowSpan = urls.size();
            stringBuilder.append("<td rowspan=\"" + idRowSpan + "\">" + model.getId() + "</td>");

            // add second <td>-tag to first row
            stringBuilder
                    .append("<td>" + urls.get(0) + "</td>")
                    .append("</tr>");

            for (int i = 1; i < urls.size(); i++) {
                stringBuilder
                        .append("<tr>")
                        .append("<td>" + urls.get(i) + "</td>")
                        .append("</tr>");
            }
        }

        stringBuilder.append("</table>");
        return stringBuilder;
    }

    StringBuilder appendUnindexedAdvert(List<String> urls) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String url : urls) {
            stringBuilder.append(url + "<br>");
        }
        return stringBuilder;
    }
}
