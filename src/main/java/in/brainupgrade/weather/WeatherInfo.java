package in.brainupgrade.weather;

public class WeatherInfo {
    private String city;
    private Long temperature;
    private String weatherStateName;
    private Long airPressure;
    private String windDirectionCompass;
    private String sunRise;
    private String sunSet;
    private Long windSpeed;
    private Long windDirection;
    private String IconLink;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getTemperature() {
        return temperature;
    }

    public void setTemperature(Long temperature) {
        this.temperature = temperature;
    }

    public String getWeatherStateName() {
        return weatherStateName;
    }

    public void setWeatherStateName(String weatherStateName) {
        this.weatherStateName = weatherStateName;
    }

    public Long getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(Long airPressure) {
        this.airPressure = airPressure;
    }

    public String getWindDirectionCompass() {
        return windDirectionCompass;
    }

    public void setWindDirectionCompass(String windDirectionCompass) {
        this.windDirectionCompass = windDirectionCompass;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public Long getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Long windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Long getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Long windDirection) {
        this.windDirection = windDirection;
    }

    public String getIconLink() {
        return IconLink;
    }

    public void setIconLink(String iconLink) {
        IconLink = iconLink;
    }

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "city='" + city + '\'' +
                ", temperature=" + temperature +
                ", weatherStateName='" + weatherStateName + '\'' +
                ", airPressure=" + airPressure +
                ", windDirectionCompass='" + windDirectionCompass + '\'' +
                ", sunRise='" + sunRise + '\'' +
                ", sunSet='" + sunSet + '\'' +
                ", windSpeed=" + windSpeed +
                ", IconLink='" + IconLink + '\'' +
                '}';
    }
}
