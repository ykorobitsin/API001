package ru.sape.pr.impl;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import model.IdUrlModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sape.pr.Constants;
import ru.sape.pr.PrSapeService;
import ru.sape.pr.utils.IndexFilter;
import utils.ApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static de.timroes.axmlrpc.XMLRPCClient.FLAGS_ENABLE_COOKIES;
import static de.timroes.axmlrpc.XMLRPCClient.FLAGS_FORWARD;
import static de.timroes.axmlrpc.XMLRPCClient.FLAGS_NIL;
import static utils.ConvertUtils.convertAdvertsToIdUrlModels;

/**
 * Revision Info : $Author$ $Date$
 * Author  : e.korobitsin
 * Created : 24/05/13 02:37
 *
 * @author e.korobitsin
 *
 * For proper use of this class after instanciating call authenticate method
 * and then call other merhods.
 */

public class PrSapeServiceImpl implements PrSapeService {

    private final static Logger logger = LoggerFactory.getLogger(PrSapeServiceImpl.class);

    private static final int OPTIONS = FLAGS_ENABLE_COOKIES | FLAGS_FORWARD | FLAGS_NIL;

    static String XMLRPC_URL = "http://api.pr.sape.ru/xmlrpc/";

    private XMLRPCClient rpcClient;

    private Properties appConfig;

    public PrSapeServiceImpl() throws XMLRPCException, IOException {
        appConfig = ApplicationContext.getAppConfig();
        rpcClient = new XMLRPCClient(new URL(XMLRPC_URL), OPTIONS);
    }

    @Override
    public void authenticate() throws XMLRPCException {
        // authentication
        logger.info("Sending authentication to pr.sape...");

        String username = appConfig.getProperty("pr.sape.username");
        String password = appConfig.getProperty("pr.sape.password");
        rpcClient.call(Constants.SAPE_PR_LOGIN, username, password);

        logger.info("Authentication to pr.sape was send.");
    }

    @Override
    public List<IdUrlModel> getUnindexedAdverts() throws MalformedURLException, XMLRPCException {
        logger.info("Start getting all adverts from pr.sape...");
        List<Object> adverts = getAllAdverts();
        logger.info("All adverts were loaded from pr.sape.");

        logger.info("Filtering " + adverts.size() + " adverts...");
        IndexFilter.filterIndexedAdverts(adverts);
        logger.info("Adverts were filtered. Unindexed adverts: " + adverts.size());

        return convertAdvertsToIdUrlModels(adverts);
    }

    List<Object> getAllAdverts() throws XMLRPCException {
        int page = 1;
        int perPage = 500;
        boolean isGuest = false;
        Integer allAdvertsCount = getCountAdverts();

        Map<String, List> filter = new HashMap<String, List>();
        filter.put("site_ids", Arrays.asList(appConfig.getProperty("platform.id")));
        filter.put("status_codes", Arrays.asList(Constants.APPROVED));

        List<Object> totalAdverts = new ArrayList<Object>();
        while (true) {
            logger.info("Getting all adverts (page " + page + ", per page " + perPage + ")...");

            Object[] adverts = (Object[]) rpcClient.call(
                    Constants.GET_ALL_ADVERTS, filter, page, perPage, isGuest);

            logger.info(adverts.length + " adverts were loaded.");

            totalAdverts.addAll(Arrays.asList(adverts));

            if (totalAdverts.size() == allAdvertsCount) {
                return totalAdverts;
            }

            page++;
        }
    }

    Integer getCountAdverts() throws XMLRPCException {
        Map<String, List> filter = new HashMap<String, List>();
        filter.put("nofs_statuses", Arrays.asList(Constants.APPROVED));

        List<Integer> platformsId = Arrays.asList(Integer.parseInt(appConfig.getProperty("platform.id")));
        Object[] siteProperties = (Object[]) rpcClient.call(
                Constants.GET_SITE_PROPERTIES, platformsId, filter);

        //noinspection unchecked
        Object advertsByStatuses = ((Map<String, Object>) siteProperties[0]).get("nofs_statuses");
        //noinspection unchecked
        String countApprovedAdverts = (String) ((Map<String, Object>) advertsByStatuses).get("20");

        return Integer.parseInt(countApprovedAdverts);
    }

    XMLRPCClient getRpcClient() {
        return rpcClient;
    }

    void setRpcClient(XMLRPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    void setAppConfig(Properties appConfig) {
        this.appConfig = appConfig;
    }
}
