package in.brainupgrade.weather;

import java.util.Optional;

import in.brainupgrade.weather.model.currentWeather.City;
import in.brainupgrade.weather.model.currentWeather.WeatherForecast;

public interface RemoteApiFetcher {
    Optional<WeatherForecast> fetchWeatherForecastFromRemoteApi(City city);
    Optional<City[]> fetchCitiesFromRemoteApi(String cityInput);
}
