package utils;

import model.AdvertModel;

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

    public static List<AdvertModel> convertAdvertsToIdUrlModels(List<Object> adverts) {
        List<AdvertModel> models = new ArrayList<>();

        for (Object advert : adverts) {
            //noinspection unchecked
            Map<String, Object> that = (Map<String, Object>) advert;
            models.add(new AdvertModel(
                    (Integer)that.get("id"),
                    (String)that.get("placement_url"),
                    (Boolean) that.get("is_indexed")));
        }

        return models;
    }

}
