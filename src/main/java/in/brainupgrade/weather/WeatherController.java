package in.brainupgrade.weather;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class WeatherController {
	private final WeatherService weatherService;
	private final WeatherHistoryService weatherHistoryService;

	@Autowired
	public WeatherController(WeatherServiceImpl weatherService, WeatherHistoryServiceImpl weatherHistoryService) {
		this.weatherService = weatherService;
		this.weatherHistoryService = weatherHistoryService;
	}

	@GetMapping("/")
	public String getWeatherHomeBase(Model model) {
		return "redirect:/weather-home";
	}

	@GetMapping("/weather-home")
	public String getWeatherHome(Model model) {
		model.addAttribute("followedCity", weatherHistoryService.getFollowedCity());
		try {
			model.addAttribute("hostname", InetAddress.getLocalHost().getHostName());
			model.addAttribute("hostip", InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
		}
		return "weatherHome";
	}

	@RequestMapping(value = "/set-weather", method = { RequestMethod.PUT, RequestMethod.POST })
	public String setWeather(String city) {
		log.info("Setting weather city: " + city);
		if (city.trim().equals("")) {
			return "weatherError";
		}
		boolean isWeatherSet = weatherService.setWeatherForecast(city);
		if (isWeatherSet) {
			return "redirect:/weather-view";
		}
		return "weatherError";
	}

	@RequestMapping(value = "/set-weather-history-city-home", method = { RequestMethod.PUT, RequestMethod.POST })
	public String setWeatherHistoryCityHome(String city) {
		log.info("Setting weather history city home: " + city);
		boolean isCitySet = weatherHistoryService.setCityToFollow(city);
		if (isCitySet) {
			return "redirect:/weather-home";
		}
		return "weatherError";
	}

	@GetMapping("/get-history")
	public String getHistory(Model model) {
		log.info("Get weather for followed city");
		model.addAttribute("weatherHistory", weatherHistoryService.findByFollowedCity());
		return "weatherHistory";
	}

	@GetMapping("/get-whole-history")
	public String getWholeHistory(Model model) {
		log.info("Get whole history");
		model.addAttribute("weatherHistory", weatherHistoryService.findAll());
		return "weatherHistory";
	}

	@GetMapping("/weather-view")
	public String getWeather(Model model) {
		log.info("View weather");
		WeatherInfo weatherInfo = weatherService.getWeatherInfo();

		model.addAttribute("city", weatherInfo.getCity());
		model.addAttribute("temperature", weatherInfo.getTemperature());
		model.addAttribute("weatherStateName", weatherInfo.getWeatherStateName());
		model.addAttribute("iconLink", weatherInfo.getIconLink());
		model.addAttribute("airPressure", weatherInfo.getAirPressure());
		model.addAttribute("windSpeed", weatherInfo.getWindSpeed());
		model.addAttribute("windDirectionCompass", weatherInfo.getWindDirectionCompass());
		model.addAttribute("sunRise", weatherInfo.getSunRise());
		model.addAttribute("sunSet", weatherInfo.getSunSet());

		return "weatherView";
	}

	@GetMapping("/go-to-home-page")
	public String goToHomePage() {
		return "redirect:/weather-home";
	}
}
