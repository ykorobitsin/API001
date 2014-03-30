package service.ru.yandex.xmlsearch.xmlsearch.impl;

import model.AdvertModel;
import model.SearchResultModel;
import model.SearchableModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ru.yandex.xmlsearch.xmlsearch.ParseYandexException;
import service.ru.yandex.xmlsearch.xmlsearch.YandexXmlService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 16:38
 */
public class YandexXmlServiceImpl implements YandexXmlService {

    private final static Logger logger = LoggerFactory.getLogger(YandexXmlServiceImpl.class);

    private static final int MAX_REQUEST_LENGTH = 6;
    private static final String END_URL_REGEXP = "([\\d]{1,5})(/[\\d]{0,3})([/\\d]*)";

    private String searchString;

    @Override
    public SearchResultModel search(List<AdvertModel> models) {
        logger.info("Start searching unindexed advert in Yandex...");

        StringBuilder request = new StringBuilder(searchString);
        List<SearchableModel> indexedAdverts = new ArrayList<>();
        List<SearchableModel> unindexedAdverts = new ArrayList<>();
        List<AdvertModel> tempAdverts = new ArrayList<>();

        logger.info("Composing queries...");

        int i = 0;
        for (Iterator<AdvertModel> iterator = models.iterator(); iterator.hasNext(); ) {
            AdvertModel nextModel = iterator.next();
            tempAdverts.add(nextModel);

            String concatenator = i != 0 ? "%20|%20" : "";
            addUrlToRequest(request, nextModel.getUrl(), concatenator);
            i++;

            if (i > MAX_REQUEST_LENGTH) {
                searchAdverts(request, tempAdverts, indexedAdverts, unindexedAdverts);
                tempAdverts.clear();
                request = new StringBuilder(searchString);
                i = 0;
                continue;
            }

            if (!iterator.hasNext()) {
                searchAdverts(request, tempAdverts, indexedAdverts, unindexedAdverts);
            }
        }

        logger.info("End searching unindexed advert in Yandex.");
        return new SearchResultModel(indexedAdverts, unindexedAdverts);
    }

    void searchAdverts(
            StringBuilder request, List<AdvertModel> tempAdverts,
            List<SearchableModel> indexedAdverts, List<SearchableModel> unindexedAdverts) {

        try {
            List<String> foundUrls = sendRequest(request);
            if (foundUrls.isEmpty()) {
                return;
            }
            parseSearchResults(indexedAdverts, unindexedAdverts, tempAdverts, foundUrls);
        } catch (ParseYandexException parseYandexException) {
            parseYandexException.printStackTrace();
        }
    }

    void addUrlToRequest(StringBuilder request, String url, String concatenator) {
        String regex = "([\\d]{1,5})(/[\\d]{0,3})([/\\d]*)";
        String convertedUrl = url.replace("http://", "site:").replaceAll(regex, "$1") + "/";
        request.append(concatenator).append(convertedUrl);
    }

    List<String> sendRequest(StringBuilder request) throws ParseYandexException {
        logger.info("Sending request to Yandex...");

        List<String> foundUrls = new ArrayList<>();
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(request.toString());
            try {
                logger.info("Parsing response...");

                List<Element> urlGroups = document.getRootElement().getChild("response")
                        .getChild("results").getChild("grouping").getChildren("group");
                for (Element group : urlGroups) {
                    String url = group.getChild("doc").getChild("url").getText();
                    foundUrls.add(url);
                }
            } catch (NullPointerException e) {
                System.out.println("Error: " + document.getRootElement().getChild("response")
                        .getChild("error").getText() + ", request: " + request);
            }
        } catch (JDOMException e) {
            throw new ParseYandexException("Cannot parse response, request: " + request, e);
        } catch (IOException e) {
            throw new ParseYandexException("Cannot get xml from url, request " + request, e);
        }

        logger.info("End parsing response. Found urls: " + foundUrls.size());
        return foundUrls;
    }

    void parseSearchResults(
            List<SearchableModel> indexedAdverts, List<SearchableModel> unindexedAdverts,
            List<AdvertModel> tempAdverts, List<String> foundUrls) {

        for (AdvertModel advert : tempAdverts) {
            List<String> indexedUrls = new ArrayList<>();
            String advertUrl = advert.getUrl().replaceAll(END_URL_REGEXP, "$1");
            for (String url : foundUrls) {
                String foundUrl = url.replaceAll(END_URL_REGEXP, "$1");
                if (foundUrl.equals(advertUrl)) {
                    indexedUrls.add(url);
                }
            }

            if (!indexedUrls.isEmpty()) {
                indexedAdverts.add(new SearchableModel(advert.getId(), indexedUrls));
            } else {
                unindexedAdverts.add(new SearchableModel(null, Arrays.asList(advert.getUrl())));
            }
        }
        logger.info("End parsing index and unindexed urls.");
    }

    // Getters and Setters

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
}
