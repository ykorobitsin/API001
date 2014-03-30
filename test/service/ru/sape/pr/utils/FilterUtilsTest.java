package service.ru.sape.pr.utils;

import model.AdvertModel;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: web
 * Date: 11/17/13
 * Time: 4:24 PM
 */
public class FilterUtilsTest {

    @Test
    public void testFilterIndexedAdverts() {
        // Given
        List<AdvertModel> models = new ArrayList<>(Arrays.asList(
                new AdvertModel(1, "1", true),
                new AdvertModel(2, "2", true),
                new AdvertModel(3, "3", false),
                new AdvertModel(4, "4", false),
                new AdvertModel(5, "5", true)));

        // When
        FilterUtils.filterIndexedAdverts(models);

        // Then
        List<AdvertModel> expectedModels = Arrays.asList(
                new AdvertModel(3, "3", false),
                new AdvertModel(4, "4", false));
        Assert.assertEquals(expectedModels, models);
    }

}
