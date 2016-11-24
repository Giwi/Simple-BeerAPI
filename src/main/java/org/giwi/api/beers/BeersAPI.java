package org.giwi.api.beers;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpHeader;
import org.giwi.api.beers.model.Beer;
import org.giwi.api.beers.tools.BeerInitialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.utils.IOUtils;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;

import static spark.Spark.*;

/**
 * Created by b3605 on 07/11/16.
 *
 * @author Xavier MARIN (b3605)
 */
public class BeersAPI {
    private static Logger logger = LoggerFactory.getLogger(BeersAPI.class);
    private static Connection conn;
    private static Gson gson = new Gson();

    private static void initDB() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:mem:d1");
            BeerInitialize.initBeerDb(conn);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        initDB();

        staticFileLocation("/static");
        get("/api-doc/", (q, a) -> IOUtils.toString(Spark.class.getResourceAsStream("static/index.html")));

        options("/api/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "GET, POST, PUT, DELETE, OPTION");
            response.header("Access-Control-Allow-Headers", "*");
            // Note: this may or may not be necessary in your particular application
            response.type("application/json");
        });


        before("/api/*", (request, response) -> response.header(HttpHeader.CONTENT_TYPE.asString(), "application/json"));
        /**
         * @api {get} /api/beers Get list of beers
         * @apiName getBeers
         * @apiGroup Beers
         * @apiDescription Get list of beers
         * @apiSuccess {Array} list of beers
         */
        get("/api/beers", (request, response) -> gson.toJson(Beer.getBeers(conn)));
        /**
         * @api {post} /api/beers Add a beer
         * @apiName addBeer
         * @apiGroup Beers
         * @apiParam {Object} beer Beer
         * @apiDescription Add a beer
         * @apiSuccess {Object} beer
         */
        post("/api/beers", (request, response) -> gson.toJson(Beer.addBeer(request.body(), conn)));

        /**
         * @api {get} /api/beers/:id Get a beer
         * @apiName getBeer
         * @apiGroup Beers
         * @apiParam {String} id Beer id
         * @apiDescription Get a beer
         * @apiSuccess {Object} beer
         */
        get("/api/beers/:id", (request, response) -> gson.toJson(Beer.getBeer(request.params(":id"), conn)));

        /**
         * @api {put} /api/beers/:id Update a beer
         * @apiName updateBeer
         * @apiGroup Beers
         * @apiParam {Object} beer Beer
         * @apiParam {String} id Beer id
         * @apiDescription Update a beer
         * @apiSuccess {Object} beer
         */
        put("/api/beers/:id", (request, response) -> gson.toJson(Beer.updateBeer(request.body(), request.params(":id"), conn)));

        /**
         * @api {delete} /api/beers/:id Delete a beer
         * @apiName deleteBeer
         * @apiGroup Beers
         * @apiParam {String} id Beer id
         * @apiDescription Delete a beer
         * @apiSuccess {boolean} result
         */
        delete("/api/beers/:id", (request, response) -> gson.toJson(Beer.deleteBeer(request.params(":id"), conn)));

        get("/upload", (req, res) ->
                "<form method='post' enctype='multipart/form-data'>"
                        + "    <input type='file' name='uploaded_file' accept='.png'>"
                        + "    <input type='text' name='id'>"
                        + "    <button>Upload picture</button>"
                        + "</form>"
        );

        post("/upload", (request, response) -> {
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            Part uploadedFile = request.raw().getPart("uploaded_file");
            Path tempFile = Paths.get("static/img/" + getFileName(uploadedFile));
            try (InputStream input = uploadedFile.getInputStream()) {
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
                Beer b = Beer.getBeer(request.raw().getParameter("id"), conn);
                b.setImg("img/" + getFileName(uploadedFile));
                Beer.updateBeer(gson.toJson(b), String.valueOf(request.raw().getPart("id")), conn);
                uploadedFile.delete();
            }
            return "<h1>You uploaded this image:<h1><img src='img/" + tempFile.getFileName() + "'>";
        });

        exception(Exception.class, (e, request, response) -> {
            logger.error(e.getMessage(), e);
            response.status(500);
            response.body(e.getMessage());
        });
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
