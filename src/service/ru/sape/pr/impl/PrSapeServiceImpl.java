package service.ru.sape.pr.impl;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import model.AdvertModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ru.sape.pr.PrSapeService;
import service.ru.sape.pr.utils.FilterUtils;
import utils.ConvertUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.timroes.axmlrpc.XMLRPCClient.FLAGS_ENABLE_COOKIES;
import static de.timroes.axmlrpc.XMLRPCClient.FLAGS_FORWARD;
import static de.timroes.axmlrpc.XMLRPCClient.FLAGS_NIL;

/**
 * Revision Info : $Author$ $Date$
 * Author  : e.korobitsin
 * Created : 24/05/13 02:37
 *
 * @author e.korobitsin
 */

public class PrSapeServiceImpl implements PrSapeService {

    private final static Logger logger = LoggerFactory.getLogger(PrSapeServiceImpl.class);

    private static final String SAPE_PR_LOGIN = "sape_pr.login";
    private static final String SAPE_PR_ADVERTS = "sape_pr.site.adverts";
    private static final String SAPE_PR_SITE_PROPERTIES = "sape_pr.site.ownlist";

    public static final String XMLRPC_URL = "http://api.pr.sape.ru/xmlrpc/";
    private static final int RPC_OPTIONS = FLAGS_ENABLE_COOKIES | FLAGS_FORWARD | FLAGS_NIL;
    private static final String FILTER_STATUS = "nofs_statuses";
    private static final Integer FILTER_APPROVED = 20;

    private XMLRPCClient rpcClient;

    private String username;
    private String password;
    private Integer platformId;

    public PrSapeServiceImpl() throws XMLRPCException, IOException {
        rpcClient = new XMLRPCClient(new URL(XMLRPC_URL), RPC_OPTIONS);
    }

    @Override
    public List<AdvertModel> getUnindexedAdverts() throws MalformedURLException, XMLRPCException {
        authenticate();
        List<Object> adverts = getAllAdverts();

        logger.info("Converting " + adverts.size() + " adverts...");
        List<AdvertModel> advertsModels = ConvertUtils.convertAdvertsToIdUrlModels(adverts);
        logger.info(advertsModels.size() + " adverts were converted.\n");

        logger.info("Filtering " + advertsModels.size() + " adverts...");
        FilterUtils.filterIndexedAdverts(advertsModels);
        logger.info("Adverts were filtered. Unindexed adverts: " + advertsModels.size() + "\n");

        return advertsModels;
    }

    void authenticate() throws XMLRPCException {
        // authentication
        logger.info("Sending authentication to pr.sape...");
        rpcClient.call(SAPE_PR_LOGIN, username, password);
        logger.info("Authentication to pr.sape was send.");
    }

    List<Object> getAllAdverts() throws XMLRPCException {
        logger.info("Start getting all adverts from pr.sape...");

        int page = 1;
        int perPage = 500;
        boolean isGuest = false;
        Integer allAdvertsCount = getAdvertsAmount();

        Map<String, List> filter = new HashMap<>();
        filter.put("site_ids", Arrays.asList(platformId));
        filter.put("status_codes", Arrays.asList(FILTER_APPROVED));

        List<Object> totalAdverts = new ArrayList<>();
        while (true) {
            logger.info("Getting all adverts (page " + page + ", per page " + perPage + ")...");
            Object[] adverts = (Object[]) rpcClient.call(
                    SAPE_PR_ADVERTS, filter, page, perPage, isGuest);
            logger.info(adverts.length + " adverts were loaded.");

            totalAdverts.addAll(Arrays.asList(adverts));

            if (allAdvertsCount.equals(totalAdverts.size())) {
                logger.info("All adverts were loaded from pr.sape.\n");
                return totalAdverts;
            }

            page++;
        }
    }

    Integer getAdvertsAmount() throws XMLRPCException {
        logger.info("Getting adverts amount from ps.sape...");

        Map<String, List> filter = new HashMap<>();
        filter.put(FILTER_STATUS, Arrays.asList(FILTER_APPROVED));
        List<Integer> platformsId = Arrays.asList(platformId);

        Object[] siteProperties = (Object[]) rpcClient.call(
                SAPE_PR_SITE_PROPERTIES, platformsId, filter);

        Object advertsByStatuses = ((Map<?, ?>) siteProperties[0]).get(FILTER_STATUS);
        String countApprovedAdverts = (String) ((Map<?, ?>) advertsByStatuses)
                .get(FILTER_APPROVED.toString());

        logger.info("Adverts amount was gotten: " + countApprovedAdverts);
        return Integer.parseInt(countApprovedAdverts);
    }

    // Getters and Setters

    XMLRPCClient getRpcClient() {
        return rpcClient;
    }

    void setRpcClient(XMLRPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }
}
