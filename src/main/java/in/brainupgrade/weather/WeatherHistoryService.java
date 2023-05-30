package in.brainupgrade.weather;

import java.util.List;

import in.brainupgrade.weather.model.historyWeather.WeatherHistory;

public interface WeatherHistoryService {
    List<WeatherHistory> findAll();
    List<WeatherHistory> findByFollowedCity();
    boolean setCityToFollow(String cityToFollow);
    String getFollowedCity();
}
