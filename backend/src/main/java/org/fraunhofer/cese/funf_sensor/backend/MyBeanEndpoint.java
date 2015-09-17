package org.fraunhofer.cese.funf_sensor.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myBeanApi",
        version = "v1",
        resource = "myBean",
        namespace = @ApiNamespace(
                ownerDomain = "backend.funf_sensor.cese.fraunhofer.org",
                ownerName = "backend.funf_sensor.cese.fraunhofer.org",
                packagePath = ""
        )
)
public class MyBeanEndpoint {

    private static final Logger logger = Logger.getLogger(MyBeanEndpoint.class.getName());

    /**
     * This method gets the <code>MyBean</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>MyBean</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getMyBean")
    public MyBean getMyBean(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getMyBean method");
        return null;
    }

    /**
     * This inserts a new <code>MyBean</code> object.
     *
     * @param myBean The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertMyBean")
    public MyBean insertMyBean(MyBean myBean) {
        // TODO: Implement this function
        logger.info("Calling insertMyBean method");
        return myBean;
    }
}