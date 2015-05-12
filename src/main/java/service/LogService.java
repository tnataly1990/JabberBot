package service;

import org.json.simple.JSONArray;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;

/**
 * Created by Nataly on 12.05.2015.
 */
@Path("/log")
public class LogService {
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersons() {
        return Response.ok(preparelogs(), MediaType.APPLICATION_JSON).build();
    }

    private JSONArray preparelogs() {
        JSONArray logs = new JSONArray();
        return logs;
    }


}
