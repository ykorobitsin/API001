package mail.impl;

import junit.framework.Assert;
import model.IndexModel;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 29.06.13
 * Time: 10:04
 */
public class SenderServiceImplTest {

    private SenderServiceImpl service;

    private HtmlEmail emailClientMock;

    @Before
    public void setUp() throws EmailException, IOException, MessagingException {
        service = new SenderServiceImpl() {
            @Override
            void initialize() throws EmailException {
            }
        };
    }

    @Test
    public void testPrepareAndSend() throws EmailException, IOException, MessagingException {
        // Given
        service = new SenderServiceImpl() {
            @Override
            StringBuilder appendIndexedAdvert(List<IndexModel> models) {
                return new StringBuilder("indexedAdverts");
            }

            @Override
            StringBuilder appendUnindexedAdvert(List<String> urls) {
                return new StringBuilder("unindexedAdverts");
            }

            @Override
            void initialize() throws EmailException {
            }
        };

        emailClientMock = mock(HtmlEmail.class);
        //service.setEmailClient(emailClientMock);

        // When
        service.prepareAndSend(null, null);

        // Then
        verify(emailClientMock).setHtmlMsg("indexedAdverts<br><br><br>unindexedAdverts");
        verify(emailClientMock).send();
    }

    @Test
    public void testAppendIndexedAdvert() {
        // When
        IndexModel indexModel1 = new IndexModel(1, Arrays.asList("url1"));
        IndexModel indexModel2 = new IndexModel(2, Arrays.asList("url2", "url3", "url4"));
        StringBuilder actualStringBuilder = service.appendIndexedAdvert(
                Arrays.asList(indexModel1, indexModel2));

        // Then
        StringBuilder expectedStringBuilder = new StringBuilder(
                "<table>"
                        + "<tr><td rowspan=\"1\">1</td><td>url1</td></tr>"
                        + "<tr><td rowspan=\"3\">2</td><td>url2</td></tr>"
                        + "<tr><td>url3</td></tr>"
                        + "<tr><td>url4</td></tr>"
                        + "</table>"
        );
        Assert.assertEquals(expectedStringBuilder.toString(), actualStringBuilder.toString());
    }

    @Test
    public void testAddUnindexedAdverts() {
        // When
        StringBuilder actualStringBuilder = service.appendUnindexedAdvert(
                Arrays.asList("url1", "url2", "url3"));

        // Then
        StringBuilder expectedStringBuilder = new StringBuilder("url1<br>url2<br>url3<br>");
        Assert.assertEquals(expectedStringBuilder.toString(), actualStringBuilder.toString());
    }

}
