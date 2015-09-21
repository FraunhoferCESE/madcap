package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import org.fraunhofer.cese.funf_sensor.backend.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "messageApi",
        version = "v1",
        resource = "message",
        namespace = @ApiNamespace(
                ownerDomain = "models.backend.funf_sensor.cese.fraunhofer.org",
                ownerName = "models.backend.funf_sensor.cese.fraunhofer.org",
                packagePath = ""
        )
)
public class MessageEndpoint {

    private static final Logger logger = Logger.getLogger(MessageEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Message.class);
    }

    /**
     * Returns the {@link Message} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Message} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "message/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Message get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting Message with ID: " + id);
        Message message = ofy().load().type(Message.class).id(id).now();
        if (message == null) {
            throw new NotFoundException("Could not find Message with ID: " + id);
        }
        return message;
    }

    /**
     * Inserts a new {@code Message}.
     */
    @ApiMethod(
            name = "insert",
            path = "message",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Message insert(Message message) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that message.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(message).now();
        logger.info("Created Message with ID: " + message.getId());

        return ofy().load().entity(message).now();
    }

    /**
     * Updates an existing {@code Message}.
     *
     * @param id      the ID of the entity to be updated
     * @param message the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Message}
     */
    @ApiMethod(
            name = "update",
            path = "message/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Message update(@Named("id") Long id, Message message) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(message).now();
        logger.info("Updated Message: " + message);
        return ofy().load().entity(message).now();
    }

    /**
     * Deletes the specified {@code Message}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Message}
     */
    @ApiMethod(
            name = "remove",
            path = "message/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Message.class).id(id).now();
        logger.info("Deleted Message with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "message",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Message> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Message> query = ofy().load().type(Message.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Message> queryIterator = query.iterator();
        List<Message> messageList = new ArrayList<Message>(limit);
        while (queryIterator.hasNext()) {
            messageList.add(queryIterator.next());
        }
        return CollectionResponse.<Message>builder().setItems(messageList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(Message.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Message with ID: " + id);
        }
    }
}