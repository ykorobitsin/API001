import mail.SenderService;
import mail.impl.SenderServiceImpl;
import model.IdUrlModel;
import model.IndexModel;
import ru.sape.pr.PrSapeService;
import ru.sape.pr.impl.PrSapeServiceImpl;
import ru.yandex.xmlsearch.YandexXmlService;
import ru.yandex.xmlsearch.impl.YandexXmlServiceImpl;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 15:08
 */
public class Main {

    public static void main(String[] args) {
        try {
            PrSapeService prSapeServiceImpl = new PrSapeServiceImpl();
            prSapeServiceImpl.authenticate();
            List<IdUrlModel> unindexedAdverts = prSapeServiceImpl.getUnindexedAdverts();

            YandexXmlService yandexXmlServiceImpl = new YandexXmlServiceImpl();
            yandexXmlServiceImpl.search(unindexedAdverts);

            List<IndexModel> indexedAdverts = yandexXmlServiceImpl.getIndexedAdverts();
            List<String> unindexedUrls = yandexXmlServiceImpl.getUnindexedUrls();
            SenderService mailService = new SenderServiceImpl();
            mailService.prepareAndSend(indexedAdverts, unindexedUrls);

            //todo need to send request to yandex webmaster to index pages
            //System.out.println(adverts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
