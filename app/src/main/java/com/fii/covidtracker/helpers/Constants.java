package com.fii.covidtracker.helpers;

public class Constants {

    private static long CACHE_TTL_MILIS = 60 * 10 * 1000;
    public static final String COVID_TRACKER_PLACEHOLDER_API_URL =
            "https://iasi-botanical-garden-app.appspot.com/";
    public static final String COVID_TRACKER_API_URL =
            "https://be-dot-fii-covidtracker.ey.r.appspot.com/";
    public static final String COVID_TRACKER_TRACKING_ENDPOINT =
            "https://tracking-dot-fii-covidtracker.ey.r.appspot.com";
    public static final String DP3TKey =
            "TUZrd0V3WUhLb1pJemowQ0FRWUlLb1pJemowREFRY0RRZ0FFa0VBdlc5REtQb25TVXJXMzMvZUQvNUFmZH" +
                    "M1R20xTTY1NDhwTkxmNXZpamVib0VkNUhVOGNBMTBiK0tuZ3c0TGJ1a0hqUXFadW9YUFdQNVAv" +
                    "Q0lQNmc9PQ==";
    public static final String DP3TKeyCheck =
            "sha256/YLh1dUR9y6Kja30RrAn7JKnbQG/uEtLMkBgFF2Fuihg=";
    public static final String DP3TAppName =
            "\"demo.dpppt.org\"";
    public static long getCacheTTLMilis() {
        return CACHE_TTL_MILIS;
    }

    public static void setCacheTTLMilis(long miliseconds) {
        CACHE_TTL_MILIS = miliseconds;
    }
}
