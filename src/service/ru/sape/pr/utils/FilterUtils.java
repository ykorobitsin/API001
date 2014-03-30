package service.ru.sape.pr.utils;

import model.AdvertModel;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 16:10
 */
public class FilterUtils {
    static public void filterIndexedAdverts(List<AdvertModel> adverts) {
        for (Iterator<AdvertModel> iterator = adverts.iterator(); iterator.hasNext(); ) {
            AdvertModel advert = iterator.next();
            if (advert != null && advert.isIndexed()) {
                iterator.remove();
            }
        }
    }
}
