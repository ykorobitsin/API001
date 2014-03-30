import model.AdvertModel;
import model.SearchResultModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.ru.sape.pr.PrSapeService;
import service.ru.yandex.xmlsearch.xmlsearch.YandexXmlService;

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
            ApplicationContext context = new ClassPathXmlApplicationContext("services.xml");
            PrSapeService prSapeServiceImpl = (PrSapeService) context.getBean("prSapeService");
            List<AdvertModel> unindexedAdverts = prSapeServiceImpl.getUnindexedAdverts();

            YandexXmlService yandexXmlServiceImpl =
                    (YandexXmlService) context.getBean("yandexXmlService");
            SearchResultModel models = yandexXmlServiceImpl.search(unindexedAdverts);

            //todo implement
            //SenderService mailService = new SenderServiceImpl();
            //mailService.prepareAndSend(models);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
