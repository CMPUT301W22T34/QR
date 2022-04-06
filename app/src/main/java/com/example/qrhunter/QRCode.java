package com.example.qrhunter;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class QRCode {
    private int score;
    private String  QRId;
    private Boolean sharedLocation;
    private Boolean sharedPicture;
    private String comment;
    private GeoPoint geoPoint;
    ArrayList<String> scanners;
    ArrayList<String> http;
    /**
     * construct code
     */
    public QRCode() {}

    public void setSharedLocation(Boolean sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    public void setSharedPicture(Boolean sharedPicture) {
        this.sharedPicture = sharedPicture;
    }

    public Boolean getSharedLocation() {
        return sharedLocation;
    }

    public Boolean getSharedPicture() {
        return sharedPicture;
    }
    /**
     * construct a code
     * @param QRId
     * @param score
     */
    public QRCode(String QRId, int score) {
        this.score = score;
        this.QRId = QRId;
        this.sharedLocation = false;
        this.sharedPicture = false;
        this.comment = "";
        this.scanners = new ArrayList<String>();
        this.http = new ArrayList<String>();
    }
    /**
     * set score
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }
    /**
     * get score
     * @return int
     */
    public int getScore() {
        return score;
    }

    public void setQRId(String QRId) {
        this.QRId = QRId;
    }

    public String getQRId() {
        return QRId;
    }

    /**
     * set comment
     * @param comment
     */

    public void setComment(String comment) {
        this.comment = comment;
    }
    /**
     * get comment
     * @return
     */
    public String getComment() {
        return comment;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setScanners(ArrayList<String> scanners) {
        this.scanners = scanners;
    }

    public ArrayList<String> getScanners() {
        return scanners;
    }
    /**
     * add scanner
     * @param scanner
     */
    public void addScanner(String scanner) {
        this.scanners.add(scanner);
    }
    /**
     * setHttp
     * @param http
     */
    public void setHttp(ArrayList<String> http) {
        this.http = http;
    }
    /**
     * get http
     * @return
     */
    public ArrayList<String> getHttp() {
        return http;
    }

}

