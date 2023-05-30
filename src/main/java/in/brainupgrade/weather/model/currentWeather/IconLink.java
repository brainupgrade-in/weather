package in.brainupgrade.weather.model.currentWeather;

import java.util.HashMap;
import java.util.Map;

/**
 * https://stackoverflow.com/questions/27807232/finding-enum-value-with-java-8-stream-api#27807324
 */
public enum IconLink {
    SN("https://www.metaweather.com//static/img/weather/sn.svg"),
    SL("https://www.metaweather.com//static/img/weather/sl.svg"),
    H("https://www.metaweather.com//static/img/weather/h.svg"),
    T("https://www.metaweather.com//static/img/weather/t.svg"),
    HR("https://www.metaweather.com//static/img/weather/hr.svg"),
    LR("https://www.metaweather.com//static/img/weather/lr.svg"),
    S("https://www.metaweather.com//static/img/weather/s.svg"),
    HC("https://www.metaweather.com//static/img/weather/hc.svg"),
    LC("https://www.metaweather.com//static/img/weather/lc.svg"),
    C("https://www.metaweather.com//static/img/weather/c.svg");

    private static class Holder {
        static Map<IconLink, String> MAP = new HashMap<>();
    }

    IconLink(String url) {
        Holder.MAP.put(this, url);
    }

    public static String find(String key) {
        return Holder.MAP.get(IconLink.valueOf(key.toUpperCase()));
    }
}



