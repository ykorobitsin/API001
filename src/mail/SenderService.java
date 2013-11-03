package mail;

import model.IndexModel;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 29.06.13
 * Time: 10:03
 */
public interface SenderService {
    void prepareAndSend(List<IndexModel> models, List<String> unindexedUrls)
            throws EmailException, MessagingException;
}
