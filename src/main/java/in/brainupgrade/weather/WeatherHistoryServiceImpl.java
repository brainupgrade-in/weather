package in.brainupgrade.weather;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.brainupgrade.weather.model.WeatherHistoryRepo;
import in.brainupgrade.weather.model.currentWeather.City;
import in.brainupgrade.weather.model.currentWeather.ConsolidatedWeather;
import in.brainupgrade.weather.model.currentWeather.IconLink;
import in.brainupgrade.weather.model.currentWeather.WeatherForecast;
import in.brainupgrade.weather.model.historyWeather.WeatherHistory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherHistoryServiceImpl implements WeatherHistoryService {
	private String followedCity;
	private final WeatherHistoryRepo weatherHistoryRepo;
	private WeatherForecast weatherForecast;
	private ConsolidatedWeather forecastFirstDay;
	RestTemplate restTemplateWeather = new RestTemplate();
	@Value(value = "${weatherServiceURL}")
	private String weatherServiceURL;

	@Autowired
	public WeatherHistoryServiceImpl(WeatherHistoryRepo weatherHistoryRepo) {
		this.weatherHistoryRepo = weatherHistoryRepo;
		this.weatherForecast = new WeatherForecast();
		this.forecastFirstDay = new ConsolidatedWeather();
		this.followedCity = "None";
	}

	@Override
	public List<WeatherHistory> findAll() {
		return weatherHistoryRepo.findAll();
	}

	@Override
	public List<WeatherHistory> findByFollowedCity() {
		return weatherHistoryRepo.findByCity(followedCity);
	}

	@Override
	public boolean setCityToFollow(String cityToFollow) {
		Optional<City[]> citiesOpt = getCitiesFromRemoteApi(cityToFollow);
		if (citiesOpt.isEmpty() || (citiesOpt.get().length == 0)) {
			return false;
		}
		this.followedCity = citiesOpt.get()[0].getTitle();
		return true;
	}

	@Override
	public String getFollowedCity() {
		return followedCity;
	}

	@Scheduled(fixedDelay = 60000)
	public void saveDataToDataBase() {
		if (!followedCity.isBlank() || !followedCity.equals("None")) {
			Optional<City[]> citiesOpt = Optional.ofNullable(
					restTemplateWeather.postForObject(weatherServiceURL + "/get-cities", followedCity, City[].class));
			if (citiesOpt.isPresent() && !(citiesOpt.get().length == 0)) {
				boolean isWeatherSet = setWeatherForecast(citiesOpt.get()[0]);
				if (isWeatherSet) {
					saveWeather();
				}
			}
		}
	}

	private boolean setWeatherForecast(City city) {
		Optional<WeatherForecast> weatherForecastOpt = getWeatherForecastFromRemoteApi(city);
		if (weatherForecastOpt.isEmpty()) {
			return false;
		}
		this.weatherForecast = weatherForecastOpt.get();
		this.forecastFirstDay = weatherForecast.getConsolidatedWeather().get(0);
		return true;
	}

	private Optional<City[]> getCitiesFromRemoteApi(String cityToSearch) {
		return Optional.ofNullable(
				restTemplateWeather.postForObject(weatherServiceURL + "/get-cities", cityToSearch, City[].class))
				.filter(cities -> cities.length != 0);
	}

	private Optional<WeatherForecast> getWeatherForecastFromRemoteApi(City cityToSearch) {
		return Optional.ofNullable(restTemplateWeather.postForObject(weatherServiceURL + "/get-weather", cityToSearch,
				WeatherForecast.class));
	}

	private void saveWeather() {
		WeatherHistory weatherHistory = new WeatherHistory();
		weatherHistory.setCity(weatherForecast.getTitle());
		weatherHistory.setTemperature(Math.round(forecastFirstDay.getTheTemp()));
		weatherHistory.setWeatherStateName(forecastFirstDay.getWeatherStateName());
		weatherHistory.setAirPressure(Math.round(forecastFirstDay.getAirPressure()));
		weatherHistory.setWindSpeed(Math.round(forecastFirstDay.getWindSpeed()));
		weatherHistory.setWindDirectionCompass(forecastFirstDay.getWindDirectionCompass());
		weatherHistory.setSunRise(weatherForecast.getSunRise().substring(11, 16));
		weatherHistory.setSunSet(weatherForecast.getSunSet().substring(11, 16));
		weatherHistory.setIconLink(getIconLink());

		String dateTime = forecastFirstDay.getCreated().substring(0, 10) + " "
				+ forecastFirstDay.getCreated().substring(11, 19);
		weatherHistory.setDateTime(dateTime);

		weatherHistoryRepo.save(weatherHistory);
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
