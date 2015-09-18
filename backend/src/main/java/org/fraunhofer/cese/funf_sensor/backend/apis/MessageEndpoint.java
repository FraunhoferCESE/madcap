package org.fraunhofer.cese.funf_sensor.backend.apis;


import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.User;


import org.fraunhofer.cese.funf_sensor.backend.models.Message;
import org.fraunhofer.cese.funf_sensor.backend.Constants;
import org.fraunhofer.cese.funf_sensor.backend.utils.EndpointUtil;
import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

import java.util.List;
import java.util.logging.Logger;




@Api(
        name="funfSensor",
        version="v1",
        namespace = @ApiNamespace(
                ownerName = Constants.API_OWNER,
                ownerDomain = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )

    )

@ApiClass(
        resource = "message",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.WEB_CLIENT_ID
        },
        audiences = {Constants.AUDIENCE_ID}
)

/**
 *
 */
public class MessageEndpoint {

    private static final Logger LOGGER = Logger.getLogger(MessageEndpoint.class.getName());

    /**
     * Lists all the entities inserted in datastore.
     * @param user the user requesting the entities.
     * @return the list of all entities persisted.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    public final List<Message>  listMessage(final User user) throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);
        return ofy().load().type(Message.class).list();
    }

    /**
     * Gets the entity having primary key id.
     * @param id the primary key of the java bean.
     * @param user the user requesting the entity.
     * @return The entity with primary key id.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "GET")
    public final Message getMessage(@Named("id") final Long id, User user)
        throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        return findMessage(id);
    }

    /**
     * Inserts the entity into App Engine datastore. It uses HTTP POST method.
     * @param message the entity to be inserted.
     * @param user the user trying to insert the entity.
     * @return The inserted entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "POST")
    public final Message insertMessage(final Message message, final User user)
        throws ServiceException{
        EndpointUtil.throwIfNotAuthenticated(user);

        ofy().save().entity(message).now();

        return message; //Why does this method return the Message-object?
    }


    /**
     * Updates a entity. It uses HTTP PUT method.
     * @param message the entity to be updated.
     * @param user the user trying to update the entity.
     * @return The updated entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final Message updateMessage(final Message message, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(message).now();

        return message;
    }


    /**
     * Removes the entity with primary key id. It uses HTTP DELETE method.
     * @param id the primary key of the entity to be deleted.
     * @param user the user trying to delete the entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeMessage(@Named("id") final Long id, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        Message message = findMessage(id);
        if (message == null) {
            LOGGER.info(
                    "Message " + id + " not found, skipping deletion.");
            return;
        }
        ofy().delete().entity(message).now();
    }


    private Message findMessage(final Long id){
        return ofy().load().type(Message.class).id(id).now();
    }
}
