package jp.ac.meijou.android.powerful_alarm;

import java.util.List;
import java.util.Map;

public class WeatherForecast {
    public String publicTime;
    public String publicTimeFormatted;
    public String publishingOffice;
    public String title;
    public String link;
    public Description description;
    public List<Forecast> forecasts;
    public Location location;
    public Copyright copyright;

    public static class Description {
        public String publicTime;
        public String publicTimeFormatted;
        public String headlineText;
        public String bodyText;
        public String text;
    }

    public static class Forecast {
        public String date;
        public String dateLabel;
        public String telop;
        public Detail detail;
        public Temperature temperature;
        public Map<String, String> chanceOfRain;
        public Image image;

        public static class Detail {
            public String weather;
            public String wind;
            public String wave;
        }

        public static class Temperature {
            public Value min;
            public Value max;

            public static class Value {
                public String celsius;
                public String fahrenheit;
            }
        }

        public static class Image {
            public String title;
            public String url;
            public int width;
            public int height;
        }
    }

    public static class Location {
        public String area;
        public String prefecture;
        public String district;
        public String city;
    }

    public static class Copyright {
        public String title;
        public String link;
        public Image image;
        public List<Provider> provider;

        public static class Image {
            public String title;
            public String link;
            public String url;
            public int width;
            public int height;
        }

        public static class Provider {
            public String link;
            public String name;
            public String note;
        }
    }
}
