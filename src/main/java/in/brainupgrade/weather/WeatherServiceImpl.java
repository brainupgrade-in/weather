package in.brainupgrade.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.brainupgrade.weather.model.currentWeather.City;
import in.brainupgrade.weather.model.currentWeather.ConsolidatedWeather;
import in.brainupgrade.weather.model.currentWeather.IconLink;
import in.brainupgrade.weather.model.currentWeather.WeatherForecast;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


@Service
public class WeatherServiceImpl implements WeatherService {
    private WeatherForecast weatherForecast;
    private ConsolidatedWeather forecastFirstDay;
    private final RemoteApiFetcher remoteApiFetcher;

    @Autowired
    public WeatherServiceImpl(RemoteApiFetcher remoteApiFetcher) {
        this.remoteApiFetcher = remoteApiFetcher;
    }

    @Override
    public boolean setWeatherForecast(String cityInput) {
        Optional<City[]> citiesFromRemoteApi = getCitiesFromRemoteApi(cityInput);
        if (citiesFromRemoteApi.isEmpty()) {
            return false;
        }
        City city = citiesFromRemoteApi.get()[0];
        Optional<WeatherForecast> weatherForecastOpt = getWeatherForecastFromRemoteApi(city);
        if (weatherForecastOpt.isEmpty()) {
            return false;
        }
        this.weatherForecast = weatherForecastOpt.get();
        this.forecastFirstDay = weatherForecast.getConsolidatedWeather().get(0);
        return true;
    }

    @Override
    public WeatherInfo getWeatherInfo() {
        WeatherInfo weatherInfo = new WeatherInfo();
        weatherInfo.setCity(weatherForecast.getTitle());
        weatherInfo.setTemperature(Math.round(forecastFirstDay.getTheTemp()));
        weatherInfo.setWeatherStateName(forecastFirstDay.getWeatherStateName());
        weatherInfo.setAirPressure(Math.round(forecastFirstDay.getAirPressure()));
        weatherInfo.setWindSpeed(Math.round(forecastFirstDay.getWindSpeed()));
        weatherInfo.setWindDirectionCompass(forecastFirstDay.getWindDirectionCompass());
        weatherInfo.setSunRise(weatherForecast.getSunRise().substring(11, 16));
        weatherInfo.setSunSet(weatherForecast.getSunSet().substring(11, 16));
        weatherInfo.setIconLink(getIconLink());
        return weatherInfo;
    }

    private Optional<City[]> getCitiesFromRemoteApi(String cityToSearch) {
        return remoteApiFetcher.fetchCitiesFromRemoteApi(cityToSearch)
                .filter(cities -> cities.length != 0);
    }

    private Optional<WeatherForecast> getWeatherForecastFromRemoteApi(City cityToSearch) {
        return remoteApiFetcher.fetchWeatherForecastFromRemoteApi(cityToSearch);
    }

    private String getIconLink() {
        String weatherState = weatherForecast.getConsolidatedWeather().get(0).getWeatherStateAbbr();
        String link = "";
        try {
            link = IconLink.find(weatherState);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        return link;
    }
}
