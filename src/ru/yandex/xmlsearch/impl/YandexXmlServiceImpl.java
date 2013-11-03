package ru.yandex.xmlsearch.impl;

import model.IdUrlModel;
import model.IndexModel;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import ru.yandex.xmlsearch.ParseYandexException;
import ru.yandex.xmlsearch.YandexXmlService;
import utils.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 25.05.13
 * Time: 16:38
 */
public class YandexXmlServiceImpl implements YandexXmlService {

    public static final int MAX_REQUEST_LENGTH = 6;

    private List<IndexModel> indexedAdverts;
    private List<String> unindexedUrls;

    private Properties appConfig;

    public YandexXmlServiceImpl() throws IOException {
        indexedAdverts = new ArrayList<IndexModel>();
        unindexedUrls = new ArrayList<String>();

        appConfig = ApplicationContext.getAppConfig();
    }

    @Override
    public void search(List<IdUrlModel> models) {
        StringBuilder request = new StringBuilder(appConfig.getProperty("search.string"));
        List<IdUrlModel> tempAdverts = new ArrayList<IdUrlModel>();

        int i = 0;
        for (Iterator<IdUrlModel> iterator = models.iterator(); iterator.hasNext(); ) {
            IdUrlModel nextModel = iterator.next();
            tempAdverts.add(nextModel);

            String concatenator = i != 0 ? "%20|%20" : "";
            addUrlToRequest(request, nextModel.getUrl(), concatenator);

            i++;

            // sends bunch of urls to yandex
            if (i > MAX_REQUEST_LENGTH) {
                try {
                    List<String> foundUrls = sendRequest(request);
                    getIndexedAndUnindexedAdverts(tempAdverts, foundUrls);
                } catch (ParseYandexException parseYandexException) {
                    parseYandexException.printStackTrace();
                }
                tempAdverts.clear();
                request = new StringBuilder(appConfig.getProperty("search.string"));
                i = 0;
                continue;
            }

            // handles last url in list
            if (!iterator.hasNext()) {
                try {
                    List<String> foundUrls = sendRequest(request);
                    getIndexedAndUnindexedAdverts(tempAdverts, foundUrls);
                } catch (ParseYandexException parseYandexException) {
                    parseYandexException.printStackTrace();
                }
            }

        }
    }

    void addUrlToRequest(StringBuilder request, String url, String concatenator) {
        String regex = "([\\d]{1,5})(/[\\d]{0,3})([/\\d]*)";
        String convertedUrl = url.replace("http://", "site:").replaceAll(regex, "$1") + "/";
        request.append(concatenator).append(convertedUrl);
    }

    List<String> sendRequest(StringBuilder request) throws ParseYandexException {
        List<String> foundUrls = new ArrayList<String>();
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(request.toString());
            try {
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
        return foundUrls;
    }

    //todo select better name for this method
    void getIndexedAndUnindexedAdverts(List<IdUrlModel> tempAdverts, List<String> foundUrls) {
        for (IdUrlModel advert : tempAdverts) {
            List<String> urls = new ArrayList<String>();

            for (String url : foundUrls) {
                String tempRegex = "([\\d]{1,5})(/[\\d]{0,3})([/\\d]*)";
                String advertUrl = advert.getUrl().replaceAll(tempRegex, "$1");
                String foundUrl = url.replaceAll(tempRegex, "$1");
                if (foundUrl.equals(advertUrl)) {
                    urls.add(url);
                }
            }

            if (!urls.isEmpty()) {
                indexedAdverts.add(new IndexModel(advert.getId(), urls));
            } else {
                unindexedUrls.add(advert.getUrl());
            }
        }
    }

    @Override
    public List<IndexModel> getIndexedAdverts() {
        return indexedAdverts;
    }

    @Override
    public List<String> getUnindexedUrls() {
        return unindexedUrls;
    }
}
