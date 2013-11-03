package ru.yandex.xmlsearch;

import model.IdUrlModel;
import model.IndexModel;

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
    void search(List<IdUrlModel> models);

    List<IndexModel> getIndexedAdverts();

    List<String> getUnindexedUrls();
}
