package utils;

import model.IdUrlModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ded
 * Date: 26.05.13
 * Time: 14:13
 */
public class ConvertUtils {

    public static List<IdUrlModel> convertAdvertsToIdUrlModels(List<Object> adverts) {
        List<IdUrlModel> models = new ArrayList<IdUrlModel>();

        for (Object advert : adverts) {
            //noinspection unchecked
            Map<String, Object> that = (Map<String, Object>) advert;
            models.add(new IdUrlModel((Integer)that.get("id"), (String)that.get("placement_url")));
        }

        return models;
    }

}
