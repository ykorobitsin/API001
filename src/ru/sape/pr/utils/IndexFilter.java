package ru.sape.pr.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 16:10
 */
public class IndexFilter {

    static public void filterIndexedAdverts(List<Object> adverts) {
        for (Iterator<Object> iterator =  adverts.iterator(); iterator.hasNext();) {
            //noinspection unchecked
            Map<String, Object> advert = (Map<String, Object>) iterator.next();

            //todo refactor
            Object is_indexed = advert.get("is_indexed");
            if (is_indexed != null && (Boolean) is_indexed) {
                iterator.remove();
            }
        }
    }

}
