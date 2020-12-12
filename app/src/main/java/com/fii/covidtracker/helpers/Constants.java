package com.fii.covidtracker.helpers;

public class Constants {

    private static long CACHE_TTL_MILIS = 60 * 10 * 1000;
    public static final String COVID_TRACKER_PLACEHOLDER_API_URL =
            "https://iasi-botanical-garden-app.appspot.com/";
    public static final String COVID_TRACKER_API_URL =
            "https://be-dot-fii-covidtracker.ey.r.appspot.com/";
    public static final String COVID_TRACKER_TRACKING_ENDPOINT =
            "https://tracking-dot-fii-covidtracker.ey.r.appspot.com";

    public static long getCacheTTLMilis() {
        return CACHE_TTL_MILIS;
    }

    public static void setCacheTTLMilis(long miliseconds) {
        CACHE_TTL_MILIS = miliseconds;
    }
}
