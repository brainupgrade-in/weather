package in.brainupgrade.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import in.brainupgrade.weather.model.currentWeather.City;
import in.brainupgrade.weather.model.currentWeather.WeatherForecast;

import java.util.Optional;

@Service
public class RemoteApiFetcherImpl implements RemoteApiFetcher {
    Logger logger = LoggerFactory.getLogger(RemoteApiFetcherImpl.class);


    public Optional<WeatherForecast> fetchWeatherForecastFromRemoteApi(City city) {
        String weatherUrl = "https://www.metaweather.com/api/location/" +
                city.getWoeid();
        try {
            RestTemplate restTemplateWeather = new RestTemplate();
            return Optional.ofNullable(restTemplateWeather.getForObject(weatherUrl, WeatherForecast.class));
        } catch (
                RestClientException ex) {
            logger.error("Error during fetching WeatherForecast from remote API.", ex);
        } finally {
            logger.debug("Completed fetching WeatherForecast from remote API.");
        }
        return Optional.empty();
    }


    public Optional<City[]> fetchCitiesFromRemoteApi(String cityInput) {
        String cityUrl = "https://www.metaweather.com/api/location/search/?query=" +
                cityInput.toLowerCase();
        try {
            RestTemplate restTemplateCity = new RestTemplate();
            return Optional.ofNullable(restTemplateCity.getForObject(cityUrl, City[].class));

        } catch (
                RestClientException ex) {
            logger.error("Error during fetching City[] from remote API.", ex);
        } finally {
            logger.debug("Completed fetching City[] from remote API.");
        }
        return Optional.empty();
    }
}
