package in.brainupgrade.weather;

public interface WeatherService {
    boolean setWeatherForecast(String cityInput);

    WeatherInfo getWeatherInfo();
}
