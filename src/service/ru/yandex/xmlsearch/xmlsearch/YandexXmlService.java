package service.ru.yandex.xmlsearch.xmlsearch;

import model.AdvertModel;
import model.SearchableModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 18.06.13
 * Time: 4:40
 */
public interface YandexXmlService {

    /**
     * Returns list of indexed urls.
     */
    List<List<SearchableModel>> search(List<AdvertModel> models);
}
