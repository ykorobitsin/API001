package service.ru.yandex.xmlsearch.xmlsearch.impl;

import model.AdvertModel;
import model.SearchableModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import service.ru.yandex.xmlsearch.xmlsearch.ParseYandexException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.Actions.addAction;
import static utils.Actions.checkActions;

/**
 * Revision Info : $Author$ $Date$
 * Author  : e.korobitsin
 * Created : 27/05/13 08:28
 *
 * @author e.korobitsin
 */
public class YandexXmlServiceImplTest {

    private YandexXmlServiceImpl yandexXmlServiceImplSearcher;

    @Before
    public void setUp() throws IOException {
        yandexXmlServiceImplSearcher = new YandexXmlServiceImpl();
    }

    @Test
    public void testSearchForLessModelsThanMaxRequest() throws IOException {
        // Given
        final List<String> returnedUrls = Arrays.asList("url1", "url3");
        yandexXmlServiceImplSearcher = new YandexXmlServiceImpl() {
            @Override
            void addUrlToRequest(StringBuilder request, String url, String concatenator) {
                addAction("addUrlToRequest", url, concatenator);
                request.append(url);
            }

            @Override
            List<String> sendRequest(StringBuilder request) throws ParseYandexException {
                addAction("sendRequest", request.toString());
                return returnedUrls;
            }

            @Override
            void parseSearchResults(List<AdvertModel> tempAdverts, List<String> foundUrls) {
                addAction("parseSearchResults", tempAdverts, foundUrls);
            }
        };

        // When
        List<AdvertModel> models = Arrays.asList(
                new AdvertModel(1, "url1", false),
                new AdvertModel(2, "url2", false),
                new AdvertModel(3, "url3", false));

        yandexXmlServiceImplSearcher.search(models);

        // Then
        //todo: set correct request (mock appConfig)
        String expectedRequest = "request" + "url1" + "url2" + "url3";
        checkActions(
                "addUrlToRequest", "url1", "",
                "addUrlToRequest", "url2", "%20|%20",
                "addUrlToRequest", "url3", "%20|%20",
                "sendRequest", expectedRequest,
                "parseSearchResults", models, returnedUrls);
    }

    @Test
    public void testSearchForEqualModelsToMaxRequest() throws IOException {
        // Given
        final List<String> returnedUrls = Arrays.asList("url1", "url3");
        yandexXmlServiceImplSearcher = new YandexXmlServiceImpl() {
            @Override
            void addUrlToRequest(StringBuilder request, String url, String concatenator) {
                addAction("addUrlToRequest", url, concatenator);
                request.append(url);
            }

            @Override
            List<String> sendRequest(StringBuilder request) throws ParseYandexException {
                addAction("sendRequest", request.toString());
                return returnedUrls;
            }

            @Override
            void parseSearchResults(List<AdvertModel> tempAdverts, List<String> foundUrls) {
                addAction("parseSearchResults", new ArrayList<>(tempAdverts), foundUrls);
            }
        };

        // When
        List<AdvertModel> models = Arrays.asList(
                new AdvertModel(1, "url1", false),
                new AdvertModel(2, "url2", false),
                new AdvertModel(3, "url3", false),
                new AdvertModel(3, "url4", false),
                new AdvertModel(3, "url5", false),
                new AdvertModel(3, "url6", false),
                new AdvertModel(3, "url7", false));

        yandexXmlServiceImplSearcher.search(models);

        // Then
        //todo: set correct request (mock app config)
        String expectedRequest = "request" + "url1" + "url2" + "url3" + "url4" + "url5"
                + "url6" + "url7";
        checkActions(
                "addUrlToRequest", "url1", "",
                "addUrlToRequest", "url2", "%20|%20",
                "addUrlToRequest", "url3", "%20|%20",
                "addUrlToRequest", "url4", "%20|%20",
                "addUrlToRequest", "url5", "%20|%20",
                "addUrlToRequest", "url6", "%20|%20",
                "addUrlToRequest", "url7", "%20|%20",
                "sendRequest", expectedRequest,
                "parseSearchResults", models, returnedUrls);
    }

    @Test
    public void testSearchForMoreModelsThanMaxRequest() throws IOException {
        // Given
        final List<String> returnedUrls = Arrays.asList("url1", "url3");

        yandexXmlServiceImplSearcher = new YandexXmlServiceImpl() {
            @Override
            void addUrlToRequest(StringBuilder request, String url, String concatenator) {
                addAction("addUrlToRequest", url, concatenator);
                request.append(url);
            }

            @Override
            List<String> sendRequest(StringBuilder request) throws ParseYandexException {
                addAction("sendRequest", request.toString());
                return returnedUrls;
            }

            @Override
            void parseSearchResults(List<AdvertModel> tempAdverts, List<String> foundUrls) {
                addAction("parseSearchResults", new ArrayList<>(tempAdverts), foundUrls);
            }
        };

        // When
        List<AdvertModel> models = Arrays.asList(
                new AdvertModel(1, "url1", false),
                new AdvertModel(2, "url2", false),
                new AdvertModel(3, "url3", false),
                new AdvertModel(3, "url4", false),
                new AdvertModel(3, "url5", false),
                new AdvertModel(3, "url6", false),
                new AdvertModel(3, "url7", false),
                new AdvertModel(3, "url8", false),
                new AdvertModel(3, "url9", false));

        yandexXmlServiceImplSearcher.search(models);

        // Then
        //todo: set correct request (mock appConfig)
        String expectedRequest =
                "request" + "url1" + "url2" + "url3" + "url4" + "url5" + "url6" + "url7";
        //todo: set correct request (mock appConfig)
        String expectedRequest2 = "request" + "url8" + "url9";
        List<AdvertModel> tempModel1 = models.subList(0, 7);
        List<AdvertModel> tempModel2 = models.subList(7, 9);

        checkActions(
                "addUrlToRequest", "url1", "",
                "addUrlToRequest", "url2", "%20|%20",
                "addUrlToRequest", "url3", "%20|%20",
                "addUrlToRequest", "url4", "%20|%20",
                "addUrlToRequest", "url5", "%20|%20",
                "addUrlToRequest", "url6", "%20|%20",
                "addUrlToRequest", "url7", "%20|%20",
                "sendRequest", expectedRequest,
                "parseSearchResults", tempModel1, returnedUrls,
                "addUrlToRequest", "url8", "",
                "addUrlToRequest", "url9", "%20|%20",
                "sendRequest", expectedRequest2,
                "parseSearchResults", tempModel2, returnedUrls);
    }

    @Test
    public void testAddUrlToRequest() {
        // Given
        StringBuilder request = new StringBuilder("request");

        // When
        yandexXmlServiceImplSearcher.addUrlToRequest(request, "http://site.com/view/30123/114", "?");
        yandexXmlServiceImplSearcher.addUrlToRequest(request, "http://site.com/view/3012/12/", "&");
        yandexXmlServiceImplSearcher.addUrlToRequest(request, "http://site.com/view/301/", "&");
        yandexXmlServiceImplSearcher.addUrlToRequest(request, "http://site.com/view/30/11/", "&");
        yandexXmlServiceImplSearcher.addUrlToRequest(request, "http://site.com/view/30133/", "&");
        yandexXmlServiceImplSearcher.addUrlToRequest(request, "http://site.com/view/2756", "&");

        // Then
        String expectedRequest = "request"
                + "?site:site.com/view/30123/"
                + "&site:site.com/view/3012/"
                + "&site:site.com/view/301/"
                + "&site:site.com/view/30/"
                + "&site:site.com/view/30133/"
                + "&site:site.com/view/2756/";
        Assert.assertEquals(expectedRequest, request.toString());
    }

    @Test
    public void testSendRequest() {
        // Given
        String testXml = ".\\test\\ru\\yandex\\xmlsearch\\impl\\sources\\responseExample.xml";

        // When
        List<String> actualUrls = new ArrayList<>();
        try {
            actualUrls = yandexXmlServiceImplSearcher.sendRequest(new StringBuilder(testXml));
        } catch (ParseYandexException parseYandexException) {
            parseYandexException.printStackTrace();
        }

        // Then
        List<String> expectedUrls = Arrays.asList(
                "http://site.com/19055/1/",
                "http://site.com/19055/29/",
                "http://site.com/19945/225/");
        Assert.assertEquals(expectedUrls, actualUrls);
    }

    @Test
    public void testParseEmptyResponse() {
        // Given
        String testXml = ".\\test\\ru\\yandex\\xmlsearch\\impl\\sources\\emptyResponseExample.xml";

        // When
        List<String> actualUrls = new ArrayList<String>();
        try {
            actualUrls = yandexXmlServiceImplSearcher.sendRequest(new StringBuilder(testXml));
        } catch (Exception e) {
            Assert.fail();
        }

        // Then
        Assert.assertTrue(actualUrls.isEmpty());
    }

    @Test
    public void testParseSearchResults() {
        // Given
        AdvertModel advert1 = new AdvertModel(1, "http://site.com/view/3011/1", false);//not in index
        AdvertModel advert2 = new AdvertModel(2, "http://site.com/view/1111/1", false);//not in index
        AdvertModel advert3 = new AdvertModel(3, "http://site.com/view/301", false);//in index
        AdvertModel advert4 = new AdvertModel(3, "http://site.com/view/9999", false);//not in index
        AdvertModel advert5 = new AdvertModel(5, "http://site.com/view/12345/123/", false);//in index
        List<AdvertModel> tempAdverts = Arrays.asList(advert1, advert2, advert3, advert4, advert5);

        List<String> foundUrls = Arrays.asList(
                "http://site.com/view/301/134",
                "http://site.com/view/301/",
                "http://site.com/view/12345/1",
                "http://site.com/view/12345/123/",
                "http://site.com/view/30",
                "http://site.com/view/30155/123/");

        // When
        yandexXmlServiceImplSearcher.parseSearchResults(tempAdverts, foundUrls);

        // Then
        SearchableModel searchableModel3 = new SearchableModel(3, Arrays.asList(
                "http://site.com/view/301/134",
                "http://site.com/view/301/"));
        SearchableModel searchableModel5 = new SearchableModel(5, Arrays.asList("" +
                "http://site.com/view/12345/1",
                "http://site.com/view/12345/123/"));
        List<SearchableModel> expectedSearchableModels = Arrays.asList(searchableModel3, searchableModel5);

        //Assert.assertEquals(expectedSearchableModels, yandexXmlServiceImplSearcher.getIndexedAdverts());

        List<String> unindexedAdvertsList = Arrays.asList(
                "http://site.com/view/3011/1",
                "http://site.com/view/1111/1",
                "http://site.com/view/9999");
        //Assert.assertEquals(unindexedAdvertsList, yandexXmlServiceImplSearcher.getUnindexedUrls());
    }

}