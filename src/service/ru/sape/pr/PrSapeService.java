package service.ru.sape.pr;

import de.timroes.axmlrpc.XMLRPCException;
import model.AdvertModel;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ded
 * Date: 18.06.13
 * Time: 15:49
 */
public interface PrSapeService {

    /**
     * This method returns unindexed adverts from api service.
     */
    List<AdvertModel> getUnindexedAdverts() throws MalformedURLException, XMLRPCException;

}
