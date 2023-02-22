package com.betathome.strings;

public class SourceURLs {

    /** COM **/
    private static String comStageURL = "https://bahcom-stage.everymatrix.com/";
    private static String comProdURL = "https://www.bah-app.com?appid=2";

    /** DE **/
    private static String deStageURL = "https://bahde-stage.everymatrix.com/";
    private static String deProdURL = "https://www.bah-app.com?appid=1";

    public static String getComStageURL() {
        return comStageURL;
    }
    public static String getComProdURL() {
        return comProdURL;
    }

    public static String getDeStageURL() { return deStageURL; }
    public static String getDeProdURL() {
        return deProdURL;
    }
}
