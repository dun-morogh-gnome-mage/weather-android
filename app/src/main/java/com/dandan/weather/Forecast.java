package com.dandan.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class Forecast implements Parcelable {
    public String startTime;
    public double cloudCover;
    public double humidity;
    public int moonPhase;
    public double precipitationProbability;
    public int precipitationType;
    public double pressureSeaLevel;
    public String sunriseTime;
    public String sunsetTime;
    public double temperature;
    public double temperatureApparent;
    public double temperatureMax;
    public double temperatureMin;
    public int uvIndex;
    public double visibility;
    public String weatherCode;
    public double windDirection;
    public double windSpeed;
    public String path;
    public String status;
    public String location;
    public static String curTime;
    public Forecast(String startTime,
                    double cloudCover,
                    double humidity,
                    int moonPhase,
                    double precipitationProbability,
                    int precipitationType,
                    double pressureSeaLevel,
                    String sunriseTime,
                    String sunsetTime,
                    double temperature,
                    double temperatureApparent,
                    double temperatureMax,
                    double temperatureMin,
                    int uvIndex,
                    double visibility,
                    String weatherCode,
                    double windDirection,
                    double windSpeed,
                    String location) {
        this.startTime = startTime;
        this.cloudCover = cloudCover;
        this.humidity = humidity;
        this.moonPhase = moonPhase;
        this.precipitationProbability = precipitationProbability;
        this.precipitationType = precipitationType;
        this.pressureSeaLevel = pressureSeaLevel;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.temperature = temperature;
        this.temperatureApparent = temperatureApparent;
        this.temperatureMax = temperatureMax;
        this.temperatureMin = temperatureMin;
        this.uvIndex = uvIndex;
        this.visibility = visibility;
        this.weatherCode = weatherCode;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        String[] tmp = setImage(weatherCode, curTime, sunriseTime,sunsetTime);
        this.status = tmp[0];
        this.path = tmp[1];
        this.location = location;
    }

    protected Forecast(Parcel in) {
        startTime = in.readString();
        cloudCover = in.readDouble();
        humidity = in.readDouble();
        moonPhase = in.readInt();
        precipitationProbability = in.readDouble();
        precipitationType = in.readInt();
        pressureSeaLevel = in.readDouble();
        sunriseTime = in.readString();
        sunsetTime = in.readString();
        temperature = in.readDouble();
        temperatureApparent = in.readDouble();
        temperatureMax = in.readDouble();
        temperatureMin = in.readDouble();
        uvIndex = in.readInt();
        visibility = in.readDouble();
        weatherCode = in.readString();
        windDirection = in.readDouble();
        windSpeed = in.readDouble();
        path = in.readString();
        status = in.readString();
        location = in.readString();
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public String[] setImage(String weatherCode, String startTime, String riseTime, String setTime) {
        int code = Integer.parseInt(weatherCode);

        LocalDateTime cur = LocalDateTime.parse(startTime.substring(0,19));
        LocalDateTime rise = LocalDateTime.parse(riseTime.substring(0,19));
        LocalDateTime set = LocalDateTime.parse(setTime.substring(0,19));
        int cur_h = cur.getHour();
        int cur_m = cur.getMinute();
        int rise_h = rise.getHour();
        int rise_m = rise.getMinute();
        int set_h = set.getHour();
        int set_m = set.getMinute();

        switch (code) {
            case 4201:
                return new String[]{"Heavy Rain", "ic_rain_heavy"};
            case 4001:
                return new String[]{"Rain", "ic_rain"};
            case 4200:
                return new String[]{"Light Rain", "ic_rain_light"};
            case 6201:
                return new String[]{"Heavy Freezing Rain", "ic_freezing_rain_heavy"};
            case 6001:
                return new String[]{"Freezing Rain", "ic_freezing_rain"};
            case 6200:
                return new String[]{"Light Freezing Rain", "ic_freezing_rain_light"};
            case 6000:
                return new String[]{"Freezing Drizzle", "ic_freezing_drizzle"};
            case 4000:
                return new String[]{"Drizzle", "ic_drizzle"};
            case 7101:
                return new String[]{"Heavy Ice Pellets", "ic_ice_pellets_heavy"};
            case 7000:
                return new String[]{"Ice Pallets", "ic_ice_pellets"};
            case 7102:
                return new String[]{"Light Ice Pallets", "ic_ice_pallets_light"};
            case 5101:
                return new String[]{"Heavy Snow", "ic_snow_heavy"};
            case 5000:
                return new String[]{"Snow", "ic_snow"};
            case 5100:
                return new String[]{"Light Snow", "ic_snow_light"};
            case 5001:
                return new String[]{"Flurries", "ic_flurries"};
            case 8000:
                return new String[]{"Thunderstorm", "ic_tstorm"};
            case 2100:
                return new String[]{"Light Fog", "ic_fog_light"};
            case 2000:
                return new String[]{"Fog", "ic_fog"};
            case 1001:
                return new String[]{"Cloudy", "ic_cloudy"};
            case 1102:
                return new String[]{"Mostly Cloudy", "ic_mostly_cloudy"};
            case 3000:
                return new String[]{
                    "Light Wind",
                    "ic_light_wind_foreground"
                };
            case 3001:
                return new String[]{
                    "Wind",
                    "ic_wind_foreground"
                };
            case 3002:
                return new String[]{
                    "Strong Wind",
                    "ic_strong_wind_foreground"
                };

            case 1101:
                if (determineDayNight(cur_h,cur_m,set_h,set_m,rise_h,rise_m).equals("day")) {
                    return new String[]{"Partly Cloudy", "ic_partly_cloudy_day"};
                }
                return new String[]{"Partly Cloudy", "ic_partly_cloudy_night"};
            case 1100:
                if (determineDayNight(cur_h,cur_m,set_h,set_m,rise_h,rise_m).equals("day")) {
                    return new String[]{"Mostly Clear", "ic_mostly_clear_day"};
                }
                return new String[]{"Mostly Clear", "ic_mostly_clear_night"};
            case 1000:
                if (determineDayNight(cur_h,cur_m,set_h,set_m,rise_h,rise_m).equals("day")) {
                    return new String[]{"Clear", "ic_clear_day"};
                }
                return new String[]{"Clear", "ic_clear_night"};

            default:
                return new String[]{"Unknown","Unknown"};
        }


    }

    public String determineDayNight(int cur_hour,int cur_min,int set_hour,int set_min,int rise_hour,int rise_min) {
        if (cur_hour < set_hour) {
            if (cur_hour > rise_hour) {
                return "day";
            }
            else if (cur_hour == rise_hour) {
                if (cur_min < rise_min) {
                    return "night";
                } else {
                    return "day";
                }
            }
            else {
                return "night";
            }
        }
        else if (cur_hour == set_hour) {
            if (cur_min < set_min) {
                return "day";
            } else {
                return "night";
            }
        }
        else {
            return "night";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(startTime);
        dest.writeDouble(cloudCover);
        dest.writeDouble(humidity);
        dest.writeInt(moonPhase);
        dest.writeDouble(precipitationProbability);
        dest.writeInt(precipitationType);
        dest.writeDouble(pressureSeaLevel);
        dest.writeString(sunriseTime);
        dest.writeString(sunsetTime);
        dest.writeDouble(temperature);
        dest.writeDouble(temperatureApparent);
        dest.writeDouble(temperatureMax);
        dest.writeDouble(temperatureMin);
        dest.writeInt(uvIndex);
        dest.writeDouble(visibility);
        dest.writeString(weatherCode);
        dest.writeDouble(windDirection);
        dest.writeDouble(windSpeed);
        dest.writeString(path);
        dest.writeString(status);
        dest.writeString(location);
    }
}
