package in.brainupgrade.weather;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import in.brainupgrade.weather.model.currentWeather.City;
import in.brainupgrade.weather.model.currentWeather.ConsolidatedWeather;
import in.brainupgrade.weather.model.currentWeather.IconLink;
import in.brainupgrade.weather.model.currentWeather.WeatherForecast;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherServiceImpl implements WeatherService {
	private WeatherForecast weatherForecast;
	private ConsolidatedWeather forecastFirstDay;
	@Value(value = "${weatherServiceURL}")
	private String weatherServiceURL;
	RestTemplate restTemplateWeather = new RestTemplate();

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
		weatherInfo.setWindDirection(forecastFirstDay.getWindDirection().longValue());
		weatherInfo.setSunRise(weatherForecast.getSunRise().substring(11, 16));
		weatherInfo.setSunSet(weatherForecast.getSunSet().substring(11, 16));
		// weatherInfo.setIconLink(getIconLink());
		return weatherInfo;
	}

	private Optional<City[]> getCitiesFromRemoteApi(String cityToSearch) {
		try {
			Optional<City[]> result = Optional.ofNullable(
					restTemplateWeather.postForObject(weatherServiceURL + "/get-cities", cityToSearch, City[].class));
			return result;
		} catch (RestClientException ex) {
			log.error("Error during fetching WeatherForecast from remote API.", ex);
		} finally {
			log.debug("Completed fetching WeatherForecast from remote API.");
		}

		return Optional.empty();
	}

	private Optional<WeatherForecast> getWeatherForecastFromRemoteApi(City cityToSearch) {
		try {
			return Optional.ofNullable(restTemplateWeather.postForObject(weatherServiceURL + "/get-weather",
					cityToSearch, WeatherForecast.class));
		} catch (RestClientException ex) {
			log.error("Error during fetching WeatherForecast from remote API.", ex);
		} finally {
			log.debug("Completed fetching WeatherForecast from remote API.");
		}
		return Optional.empty();
//        return remoteApiFetcher.fetchWeatherForecastFromRemoteApi(cityToSearch);
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
