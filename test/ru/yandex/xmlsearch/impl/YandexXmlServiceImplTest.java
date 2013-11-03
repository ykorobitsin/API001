package ru.yandex.xmlsearch.impl;

import junit.framework.Assert;
import model.IdUrlModel;
import model.IndexModel;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.xmlsearch.ParseYandexException;

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
            void getIndexedAndUnindexedAdverts(List<IdUrlModel> tempAdverts, List<String> foundUrls) {
                addAction("getIndexedAndUnindexedAdverts", tempAdverts, foundUrls);
            }
        };

        // When
        List<IdUrlModel> models = Arrays.asList(
                new IdUrlModel(1, "url1"),
                new IdUrlModel(2, "url2"),
                new IdUrlModel(3, "url3"));

        yandexXmlServiceImplSearcher.search(models);

        // Then
        //todo: set correct request (mock appConfig)
        String expectedRequest = "request" + "url1" + "url2" + "url3";
        checkActions(
                "addUrlToRequest", "url1", "",
                "addUrlToRequest", "url2", "%20|%20",
                "addUrlToRequest", "url3", "%20|%20",
                "sendRequest", expectedRequest,
                "getIndexedAndUnindexedAdverts", models, returnedUrls);
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
            void getIndexedAndUnindexedAdverts(List<IdUrlModel> tempAdverts, List<String> foundUrls) {
                addAction("getIndexedAndUnindexedAdverts", new ArrayList<IdUrlModel>(tempAdverts), foundUrls);
            }
        };

        // When
        List<IdUrlModel> models = Arrays.asList(
                new IdUrlModel(1, "url1"),
                new IdUrlModel(2, "url2"),
                new IdUrlModel(3, "url3"),
                new IdUrlModel(3, "url4"),
                new IdUrlModel(3, "url5"),
                new IdUrlModel(3, "url6"),
                new IdUrlModel(3, "url7"));

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
                "getIndexedAndUnindexedAdverts", models, returnedUrls);
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
            void getIndexedAndUnindexedAdverts(List<IdUrlModel> tempAdverts, List<String> foundUrls) {
                addAction("getIndexedAndUnindexedAdverts", new ArrayList<IdUrlModel>(tempAdverts), foundUrls);
            }
        };

        // When
        List<IdUrlModel> models = Arrays.asList(
                new IdUrlModel(1, "url1"),
                new IdUrlModel(2, "url2"),
                new IdUrlModel(3, "url3"),
                new IdUrlModel(3, "url4"),
                new IdUrlModel(3, "url5"),
                new IdUrlModel(3, "url6"),
                new IdUrlModel(3, "url7"),
                new IdUrlModel(3, "url8"),
                new IdUrlModel(3, "url9"));

        yandexXmlServiceImplSearcher.search(models);

        // Then
        //todo: set correct request (mock appConfig)
        String expectedRequest =
                "request" + "url1" + "url2" + "url3" + "url4" + "url5" + "url6" + "url7";
        //todo: set correct request (mock appConfig)
        String expectedRequest2 = "request" + "url8" + "url9";
        List<IdUrlModel> tempModel1 = models.subList(0, 7);
        List<IdUrlModel> tempModel2 = models.subList(7, 9);

        checkActions(
                "addUrlToRequest", "url1", "",
                "addUrlToRequest", "url2", "%20|%20",
                "addUrlToRequest", "url3", "%20|%20",
                "addUrlToRequest", "url4", "%20|%20",
                "addUrlToRequest", "url5", "%20|%20",
                "addUrlToRequest", "url6", "%20|%20",
                "addUrlToRequest", "url7", "%20|%20",
                "sendRequest", expectedRequest,
                "getIndexedAndUnindexedAdverts", tempModel1, returnedUrls,
                "addUrlToRequest", "url8", "",
                "addUrlToRequest", "url9", "%20|%20",
                "sendRequest", expectedRequest2,
                "getIndexedAndUnindexedAdverts", tempModel2, returnedUrls);
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
        List<String> actualUrls = new ArrayList<String>();
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
    public void testAddIndexedAndUnindexedAdverts() {
        // Given
        IdUrlModel advert1 = new IdUrlModel(1, "http://site.com/view/3011/1");//not in index
        IdUrlModel advert2 = new IdUrlModel(2, "http://site.com/view/1111/1");//not in index
        IdUrlModel advert3 = new IdUrlModel(3, "http://site.com/view/301");//in index
        IdUrlModel advert4 = new IdUrlModel(3, "http://site.com/view/9999");//not in index
        IdUrlModel advert5 = new IdUrlModel(5, "http://site.com/view/12345/123/");//in index
        List<IdUrlModel> tempAdverts = Arrays.asList(advert1, advert2, advert3, advert4, advert5);

        List<String> foundUrls = Arrays.asList(
                "http://site.com/view/301/134",
                "http://site.com/view/301/",
                "http://site.com/view/12345/1",
                "http://site.com/view/12345/123/",
                "http://site.com/view/30",
                "http://site.com/view/30155/123/");

        // When
        yandexXmlServiceImplSearcher.getIndexedAndUnindexedAdverts(tempAdverts, foundUrls);

        // Then
        IndexModel indexModel3 = new IndexModel(3, Arrays.asList(
                "http://site.com/view/301/134",
                "http://site.com/view/301/"));
        IndexModel indexModel5 = new IndexModel(5, Arrays.asList("" +
                "http://site.com/view/12345/1",
                "http://site.com/view/12345/123/"));
        List<IndexModel> expectedIndexModels = Arrays.asList(indexModel3, indexModel5);

        Assert.assertEquals(expectedIndexModels, yandexXmlServiceImplSearcher.getIndexedAdverts());

        List<String> unindexedAdvertsList = Arrays.asList(
                "http://site.com/view/3011/1",
                "http://site.com/view/1111/1",
                "http://site.com/view/9999");
        Assert.assertEquals(unindexedAdvertsList, yandexXmlServiceImplSearcher.getUnindexedUrls());
    }

}