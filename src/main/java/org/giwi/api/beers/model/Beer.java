package org.giwi.api.beers.model;

import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * The type Beer.
 */
public class Beer {

    /**
     * Each beer was defined by
     * <p>
     * { "alcohol": 6.8, "description":
     * "Affligem Blonde, the classic clear blonde abbey ale, with a gentle roundness and 6.8% alcohol. Low on bitterness, it is eminently drinkable."
     * , "id": "AffligemBlond", "img": "img/AffligemBlond.jpg", "name": "Affligem Blond" }
     */
    private static Gson gson = new Gson();

    private String name;
    private String id;
    private String img;
    private String description;
    private double alcohol;


    private String availability;
    private String brewery;
    private String label;
    private String serving;
    private String style;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets img.
     *
     * @return the img
     */
    public String getImg() {
        return img;
    }

    /**
     * Sets img.
     *
     * @param img the img
     */
    public void setImg(String img) {
        this.img = img;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets alcohol.
     *
     * @return the alcohol
     */
    public double getAlcohol() {
        return alcohol;
    }

    /**
     * Sets alcohol.
     *
     * @param alcohol the alcohol
     */
    public void setAlcohol(double alcohol) {
        this.alcohol = alcohol;
    }

    /**
     * Gets availability.
     *
     * @return the availability
     */
    public String getAvailability() {
        return availability;
    }

    /**
     * Sets availability.
     *
     * @param availability the availability
     */
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    /**
     * Gets brewery.
     *
     * @return the brewery
     */
    public String getBrewery() {
        return brewery;
    }

    /**
     * Sets brewery.
     *
     * @param brewery the brewery
     */
    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    /**
     * Gets label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets serving.
     *
     * @return the serving
     */
    public String getServing() {
        return serving;
    }

    /**
     * Sets serving.
     *
     * @param serving the serving
     */
    public void setServing(String serving) {
        this.serving = serving;
    }

    /**
     * Gets style.
     *
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets style.
     *
     * @param style the style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Gets beer.
     *
     * @param beerId the beer id
     * @param conn   the conn
     *
     * @return the beer
     *
     * @throws SQLException the sql exception
     */
    public static Beer getBeer(String beerId, Connection conn) throws SQLException {
        Beer beer = null;
        PreparedStatement prep = conn.prepareStatement("SELECT * FROM beers WHERE id=?;");
        prep.setString(1, beerId);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            beer = new Beer();
            beer.setId(rs.getString(1));
            beer.setName(rs.getString(2));
            beer.setImg(rs.getString(3));
            beer.setDescription(rs.getString(4));
            beer.setAlcohol(rs.getDouble(5));
            beer.setAvailability(rs.getString(6));
            beer.setBrewery(rs.getString(7));
            beer.setLabel(rs.getString(8));
            beer.setServing(rs.getString(9));
            beer.setStyle(rs.getString(10));
        }
        rs.close();
        prep.close();
        return beer;
    }

    /**
     * Gets beers.
     *
     * @param conn the conn
     *
     * @return the beers
     *
     * @throws SQLException the sql exception
     */
    public static List<Beer> getBeers(Connection conn) throws SQLException {
        List<Beer> list = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM beers");
        while (rs.next()) {
            list.add(readBeer(rs));
        }
        rs.close();
        stmt.close();
        return list;
    }

    /**
     * Read beer beer.
     *
     * @param rs the rs
     *
     * @return the beer
     *
     * @throws SQLException the sql exception
     */
    public static Beer readBeer(ResultSet rs) throws SQLException {
        Beer beer = new Beer();
        beer.setId(rs.getString(1));
        beer.setName(rs.getString(2));
        beer.setImg(rs.getString(3));
        beer.setDescription(rs.getString(4));
        beer.setAlcohol(rs.getDouble(5));
        beer.setAvailability(rs.getString(6));
        beer.setBrewery(rs.getString(7));
        beer.setLabel(rs.getString(8));
        beer.setServing(rs.getString(9));
        beer.setStyle(rs.getString(10));
        return beer;
    }

    /**
     * Add beer beer.
     *
     * @param body the body
     * @param conn the conn
     *
     * @return the beer
     *
     * @throws SQLException the sql exception
     */
    public static Beer addBeer(String body, Connection conn) throws SQLException {
        Beer beer = gson.fromJson(body, Beer.class);
        PreparedStatement prep = conn
                .prepareStatement("INSERT INTO beers (id, name, image, description, alcohol, availability, brewery, label, serving, style)"
                        + "VALUES (?,?,?,?,?,?,?,?,?,?)");

        prep.setString(1, beer.getId());
        prep.setString(2, beer.getName());
        prep.setString(3, beer.getImg());
        prep.setString(4, beer.getDescription());
        prep.setDouble(5, beer.getAlcohol());
        prep.setString(6, beer.getAvailability());
        prep.setString(7, beer.getBrewery());
        prep.setString(8, beer.getLabel());
        prep.setString(9, beer.getServing());
        prep.setString(10, beer.getStyle());
        prep.execute();
        conn.commit();
        prep.close();
        return beer;
    }

    /**
     * Update beer beer.
     *
     * @param body the body
     * @param id   the id
     * @param conn the conn
     *
     * @return the beer
     *
     * @throws SQLException the sql exception
     */
    public static Beer updateBeer(String body, String id, Connection conn) throws SQLException {
        Beer beer = gson.fromJson(body, Beer.class);
        PreparedStatement prep = conn.prepareStatement("UPDATE beers " +
                "SET name=?, image=?, description=?, alcohol=?, availability=?, brewery=?, label=?, serving=?, style=?" +
                " WHERE id=?");
        prep.setString(1, beer.getName());
        prep.setString(2, beer.getImg());
        prep.setString(3, beer.getDescription());
        prep.setDouble(4, beer.getAlcohol());
        prep.setString(5, beer.getAvailability());
        prep.setString(6, beer.getBrewery());
        prep.setString(7, beer.getLabel());
        prep.setString(8, beer.getServing());
        prep.setString(9, beer.getStyle());
        prep.setString(10, id);
        // batch insert
        prep.executeUpdate();
        conn.commit();
        prep.close();
        return beer;
    }

    /**
     * Delete beer.
     *
     * @param id   the id
     * @param conn the conn
     *
     * @throws SQLException the sql exception
     */
    public static boolean deleteBeer(String id, Connection conn) throws SQLException {
        PreparedStatement prep = conn.prepareStatement("DELETE FROM beers WHERE id=?");
        prep.setString(1, id);
        // batch insert
        prep.execute();
        conn.commit();
        prep.close();
        return true;
    }
}
