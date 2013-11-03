package ru.sape.pr.impl;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import junit.framework.Assert;
import model.IdUrlModel;
import org.junit.Before;
import org.junit.Test;
import ru.sape.pr.Constants;
import utils.Actions;
import utils.ApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: ded
 * Date: 18.06.13
 * Time: 16:24
 */
public class PrSapeServiceImplTest {

    private PrSapeServiceImpl service;
    private XMLRPCClient rpcClientMock;

    @Before
    public void setUp() throws IOException, XMLRPCException {
        rpcClientMock = mock(XMLRPCClient.class);

        service = new PrSapeServiceImpl();
        service.setRpcClient(rpcClientMock);
    }

    @Test
    public void testCreate() throws IOException, XMLRPCException {
        // When
        service = new PrSapeServiceImpl();

        // Then
        Assert.assertEquals("http://api.pr.sape.ru/xmlrpc/",
                service.getRpcClient().getURL().toString());
    }

    @Test
    public void testAuthenticate() throws MalformedURLException, XMLRPCException {
        // Given
        Properties appConfigMock = mock(Properties.class);
        when(appConfigMock.getProperty("pr.sape.username")).thenReturn("username");
        when(appConfigMock.getProperty("pr.sape.password")).thenReturn("password");
        service.setAppConfig(appConfigMock);

        // When
        service.authenticate();

        // Then
        verify(rpcClientMock).call("sape_pr.login", "username", "password");
    }

    @Test
    public void testGetUnindexedAdverts() throws IOException, XMLRPCException {
        // Given
        Object advert1 = new HashMap<String, Object>() {{
            put("is_indexed", true);
            put("id", 1);
            put("placement_url", "url1");
        }};
        Object advert2 = new HashMap<String, Object>() {{
            put("is_indexed", false);
            put("id", 2);
            put("placement_url", "url2");
        }};
        Object advert3 = new HashMap<String, Object>() {{
            put("is_indexed", true);
            put("id", 3);
            put("placement_url", "url3");
        }};
        Object advert4 = new HashMap<String, Object>() {{
            put("is_indexed", false);
            put("id", 4);
            put("placement_url", "url4");
        }};
        Object advert5 = new HashMap<String, Object>() {{
            put("is_indexed", true);
            put("id", 5);
            put("placement_url", "url5");
        }};

        final List<Object> returnedAdverts = new ArrayList<Object>(Arrays.asList(
                advert1, advert2, advert3, advert4, advert5));

        service = new PrSapeServiceImpl() {
            @Override
            List<Object> getAllAdverts() throws XMLRPCException {
                Actions.addAction("PrSapeServiceImpl -> getAllAdverts");
                return returnedAdverts;
            }
        };

        // When
        List<IdUrlModel> actualAdverts = service.getUnindexedAdverts();

        // Then
        IdUrlModel expectedAdvert1 = new IdUrlModel(2, "url2");
        IdUrlModel ecpectedAdvert2 = new IdUrlModel(4, "url4");
        Assert.assertEquals(2, actualAdverts.size());
        Assert.assertEquals(expectedAdvert1, actualAdverts.get(0));
        Assert.assertEquals(ecpectedAdvert2, actualAdverts.get(1));

        Actions.checkActions("PrSapeServiceImpl -> getAllAdverts");
    }

    @Test
    public void testGetAllAdverts() throws XMLRPCException, IOException {
        // Given
        service = new PrSapeServiceImpl() {
            @Override
            Integer getCountAdverts() throws XMLRPCException {
                return 5;
            }
        };

        rpcClientMock = mock(XMLRPCClient.class);
        service.setRpcClient(rpcClientMock);

        Map<String, List> expectedFilter = new HashMap<String, List>();
        //todo: mock application properties
        expectedFilter.put("site_ids", Arrays.asList(ApplicationContext.getAppConfig().getProperty("platform.id")));
        expectedFilter.put("status_codes", Arrays.asList(20));

        when(rpcClientMock.call("sape_pr.site.adverts", expectedFilter, 1, 500, false))
                .thenReturn(new Object[]{1, 2});
        when(rpcClientMock.call("sape_pr.site.adverts", expectedFilter, 2, 500, false))
                .thenReturn(new Object[]{3, 4, 5});

        // When
        List<Object> returnedAdverts = service.getAllAdverts();

        // Then
        //noinspection AssertEqualsBetweenInconvertibleTypes
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), returnedAdverts);
    }

    @Test
    public void testGetCountAdverts() throws XMLRPCException, IOException {
        // Given
        Object[] returnedObject = new Object[]{new HashMap<String, Object>() {{
            put("nofs_statuses", new HashMap<String, Object>() {{
                put("20", "999");
            }});
        }}};

        Map<String, List> filter = new HashMap<String, List>();
        filter.put("nofs_statuses", Arrays.asList(Constants.APPROVED));
        //todo: mock application properties
        Integer[] platformsId = {Integer.parseInt(ApplicationContext.getAppConfig().getProperty("platform.id"))};
        when(rpcClientMock.call(
                "sape_pr.site.ownlist", platformsId, filter)).thenReturn(returnedObject);

        // When
        Integer allAdverts = service.getCountAdverts();

        // Then
        Assert.assertEquals(new Integer(999), allAdverts);
    }

}
