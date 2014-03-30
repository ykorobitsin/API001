package service.ru.sape.pr.impl;

import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import model.AdvertModel;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static utils.Actions.*;

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
        assertEquals("http://api.pr.sape.ru/xmlrpc/",
                service.getRpcClient().getURL().toString());
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

        final List<Object> returnedAdverts = new ArrayList<>(Arrays.asList(
                advert1, advert2, advert3, advert4, advert5));

        service = new PrSapeServiceImpl() {
            @Override
            void authenticate() throws XMLRPCException {
                addAction("PrSapeServiceImpl -> authenticate");
            }

            @Override
            List<Object> getAllAdverts() throws XMLRPCException {
                addAction("PrSapeServiceImpl -> getAllAdverts");
                return returnedAdverts;
            }
        };

        // When
        List<AdvertModel> actualAdverts = service.getUnindexedAdverts();

        // Then
        checkActions("PrSapeServiceImpl -> authenticate", "PrSapeServiceImpl -> getAllAdverts");

        AdvertModel expectedAdvert1 = new AdvertModel(2, "url2", false);
        AdvertModel expectedAdvert2 = new AdvertModel(4, "url4", false);
        List<AdvertModel> expectedAdverts = Arrays.asList(expectedAdvert1, expectedAdvert2);
        assertEquals(expectedAdverts, actualAdverts);
    }

    @Test
    public void testAuthenticate() throws MalformedURLException, XMLRPCException {
        // Given
        service.setUsername("testUsername");
        service.setPassword("testPassword");

        // When
        service.authenticate();

        // Then
        verify(rpcClientMock).call("sape_pr.login", "testUsername", "testPassword");
    }

    @Test
    public void testGetAllAdverts() throws XMLRPCException, IOException {
        // Given
        service = new PrSapeServiceImpl() {
            @Override
            Integer getAdvertsAmount() throws XMLRPCException {
                addAction("PrSapeServiceImpl -> getAdvertsAmount");
                return 5;
            }
        };

        service.setPlatformId(3555);

        rpcClientMock = mock(XMLRPCClient.class);
        service.setRpcClient(rpcClientMock);

        Map<String, List> expectedFilter = new HashMap<String, List>() {{
            put("site_ids", Arrays.asList(3555));
            put("status_codes", Arrays.asList(20));
        }};

        when(rpcClientMock
                .call("sape_pr.site.adverts", expectedFilter, 1, 500, false))
                .thenReturn(new Object[]{1, 2});
        when(rpcClientMock
                .call("sape_pr.site.adverts", expectedFilter, 2, 500, false))
                .thenReturn(new Object[]{3, 4, 5});

        // When
        List<Object> actualAdverts = service.getAllAdverts();

        // Then
        List<Integer> expectedAdverts = Arrays.asList(1, 2, 3, 4, 5);
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(expectedAdverts, actualAdverts);
    }

    @Test
    public void testGetCountAdverts() throws XMLRPCException, IOException {
        // Given
        service.setPlatformId(22335599);

        Map<String, List> filter = new HashMap<>();
        filter.put("nofs_statuses", Arrays.asList(20));

        Object[] returnedObject = new Object[]{new HashMap<String, Object>() {{
            put("nofs_statuses", new HashMap<String, Object>() {{
                put("20", "91919");
            }});
        }}};

        when(rpcClientMock
                .call("sape_pr.site.ownlist", Arrays.asList(22335599), filter))
                .thenReturn(returnedObject);

        // When
        Integer allAdverts = service.getAdvertsAmount();

        // Then
        assertEquals(new Integer(91919), allAdverts);
    }

}
