package com.aide.chenpan.myaide.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Xml;

import com.aide.chenpan.myaide.R;
import com.aide.chenpan.myaide.bean.WeatherDaysForecast;
import com.aide.chenpan.myaide.bean.WeatherInfo;
import com.aide.chenpan.myaide.bean.WeatherLifeIndex;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 * 天气工具类
 */
public class WeatherUtils {


    /**
     * 取得对应的天气类型图片id
     *
     * @param type  天气类型
     * @param isDay 是否为白天
     * @return 天气类型图片id
     */
    public static int getWeatherTypeImageID(String type, boolean isDay) {
        if (type == null) {
            return R.drawable.ic_weather_no;
        }
        int weatherId;
        switch (type) {
            case "晴":
                if (isDay) {
                    weatherId = R.drawable.ic_weather_sunny_day;
                } else {
                    weatherId = R.drawable.ic_weather_sunny_night;
                }
                break;
            case "多云":
                if (isDay) {
                    weatherId = R.drawable.ic_weather_cloudy_day;
                } else {
                    weatherId = R.drawable.ic_weather_cloudy_night;
                }
                break;
            case "阴":
                weatherId = R.drawable.ic_weather_overcast;
                break;
            case "雷阵雨":
            case "雷阵雨伴有冰雹":
                weatherId = R.drawable.ic_weather_thunder_shower;
                break;
            case "雨夹雪":
                weatherId = R.drawable.ic_weather_sleet;
                break;
            case "冻雨":
                weatherId = R.drawable.ic_weather_ice_rain;
                break;
            case "小雨":
            case "小到中雨":
            case "阵雨":
                weatherId = R.drawable.ic_weather_light_rain_or_shower;
                break;
            case "中雨":
            case "中到大雨":
                weatherId = R.drawable.ic_weather_moderate_rain;
                break;
            case "大雨":
            case "大到暴雨":
                weatherId = R.drawable.ic_weather_heavy_rain;
                break;
            case "暴雨":
            case "大暴雨":
            case "特大暴雨":
            case "暴雨到大暴雨":
            case "大暴雨到特大暴雨":
                weatherId = R.drawable.ic_weather_storm;
                break;
            case "阵雪":
            case "小雪":
            case "小到中雪":
                weatherId = R.drawable.ic_weather_light_snow;
                break;
            case "中雪":
            case "中到大雪":
                weatherId = R.drawable.ic_weather_moderate_snow;
                break;
            case "大雪":
            case "大到暴雪":
                weatherId = R.drawable.ic_weather_heavy_snow;
                break;
            case "暴雪":
                weatherId = R.drawable.ic_weather_snowstrom;
                break;
            case "雾":
                weatherId = R.drawable.ic_weather_foggy;
                break;
            case "霾":
                weatherId = R.drawable.ic_weather_haze;
                break;
            case "沙尘暴":
                weatherId = R.drawable.ic_weather_duststorm;
                break;
            case "强沙尘暴":
                weatherId = R.drawable.ic_weather_sandstorm;
                break;
            case "浮尘":
            case "扬沙":
                weatherId = R.drawable.ic_weather_sand_or_dust;
                break;
            default:
                if (type.contains("尘") || type.contains("沙")) {
                    weatherId = R.drawable.ic_weather_sand_or_dust;
                } else if (type.contains("雾") || type.contains("霾")) {
                    weatherId = R.drawable.ic_weather_foggy;
                } else if (type.contains("雨")) {
                    weatherId = R.drawable.ic_weather_ice_rain;
                } else if (type.contains("雪") || type.contains("冰雹")) {
                    weatherId = R.drawable.ic_weather_moderate_snow;
                } else {
                    weatherId = R.drawable.ic_weather_no;
                }
                break;
        }

        return weatherId;
    }
    /**
     * 解析天气信息XML
     *
     * @param inputStream 输入流
     * @return 天气信息
     */
    public static WeatherInfo handleWeatherResponse(InputStream inputStream) {
        // 天气信息
        WeatherInfo weatherInfo = new WeatherInfo();
        // 多天预报信息集合
        List<WeatherDaysForecast> weatherDaysForecasts = new ArrayList<>();
        // 生活指数信息集合
        List<WeatherLifeIndex> weatherLifeIndexes = new ArrayList<>();
        // 是否为多天天气
        boolean isDaysForecast = false;
        // 是否为白天
        boolean isDay = false;
        // 多天预报信息
        WeatherDaysForecast weatherDaysForecast = null;
        // 生活指数信息
        WeatherLifeIndex weatherLifeIndex = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        switch (parser.getName()) {
                            // 城市
                            case "city":
                                weatherInfo.setCity(parser.nextText());
                                break;
                            // 更新时间
                            case "updatetime":
                                weatherInfo.setUpdateTime(parser.nextText());
                                break;
                            // 温度
                            case "wendu":
                                weatherInfo.setTemperature(parser.nextText());
                                break;
                            // 风力
                            case "fengli":
                                // 不是多天预报
                                if (!isDaysForecast)
                                    weatherInfo.setWindPower(parser.nextText());
                                else {
                                    if (weatherDaysForecast != null) {
                                        // 白天
                                        if (isDay) {
                                            weatherDaysForecast.setWindPowerDay(parser.nextText());
                                        } else {
                                            weatherDaysForecast.setWindPowerNight(parser.nextText());
                                        }
                                    }
                                }
                                break;
                            // 湿度
                            case "shidu":
                                weatherInfo.setHumidity(parser.nextText());
                                break;
                            // 风向
                            case "fengxiang":
                                if (!isDaysForecast)
                                    weatherInfo.setWindDirection(parser.nextText());
                                else {
                                    if (weatherDaysForecast != null) {
                                        if (isDay) {
                                            weatherDaysForecast.setWindDirectionDay(parser.nextText());
                                        } else {
                                            weatherDaysForecast.setWindDirectionNight(parser.nextText());
                                        }
                                    }
                                }
                                break;
                            // 日出
                            case "sunrise_1":
                                weatherInfo.setSunrise(parser.nextText());
                                break;
                            // 日落
                            case "sunset_1":
                                weatherInfo.setSunset(parser.nextText());
                                break;
                            // 大气环境
                            case "aqi":
                                weatherInfo.setAQI(parser.nextText());
                                break;
                            // 空气质量
                            case "quality":
                                weatherInfo.setQuality(parser.nextText());
                                break;
                            // 警报类型
                            case "alarmType":
                                weatherInfo.setAlarmType(parser.nextText());
                                break;
                            // 警报等级
                            case "alarmDegree":
                                weatherInfo.setAlarmDegree(parser.nextText());
                                break;
                            // 警报正文
                            case "alarmText":
                                weatherInfo.setAlarmText(parser.nextText());
                                break;
                            // 警报详细
                            case "alarm_details":
                                weatherInfo.setAlarmDetail(parser.nextText());
                                break;
                            // 警报时间
                            case "time":
                                weatherInfo.setAlarmTime(parser.nextText());
                                break;
                            // 多天预报
                            case "forecast":
                                isDaysForecast = true;
                                break;
                            // 多天预报，昨天
                            case "yesterday":
                                isDaysForecast = true;
                                weatherDaysForecast = new WeatherDaysForecast();
                                break;
                            // 日期
                            case "date_1":
                                if (weatherDaysForecast != null)
                                    weatherDaysForecast.setDate(parser.nextText());
                                break;
                            // 高温
                            case "high_1":
                                if (weatherDaysForecast != null)
                                    weatherDaysForecast.setHigh(parser.nextText());
                                break;
                            // 低温
                            case "low_1":
                                if (weatherDaysForecast != null)
                                    weatherDaysForecast.setLow(parser.nextText());
                                break;
                            // 白天
                            case "day_1":
                                isDay = true;
                                break;
                            // 夜间
                            case "night_1":
                                isDay = false;
                                break;
                            // 天气类型
                            case "type_1":
                                if (weatherDaysForecast != null) {
                                    if (isDay) {
                                        weatherDaysForecast.setTypeDay(parser.nextText());
                                    } else {
                                        weatherDaysForecast.setTypeNight(parser.nextText());
                                    }
                                }
                                break;
                            // 风向
                            case "fx_1":
                                if (weatherDaysForecast != null) {
                                    if (isDay) {
                                        weatherDaysForecast.setWindDirectionDay(parser.nextText());
                                    } else {
                                        weatherDaysForecast.setWindDirectionNight(parser.nextText());
                                    }
                                }
                                break;
                            // 风力
                            case "fl_1":
                                if (weatherDaysForecast != null) {
                                    if (isDay) {
                                        weatherDaysForecast.setWindPowerDay(parser.nextText());
                                    } else {
                                        weatherDaysForecast.setWindPowerNight(parser.nextText());
                                    }
                                }
                                break;
                            // 多天天气
                            case "weather":
                                weatherDaysForecast = new WeatherDaysForecast();
                                break;
                            // 日期
                            case "date":
                                if (weatherDaysForecast != null)
                                    weatherDaysForecast.setDate(parser.nextText());
                                break;
                            // 高温
                            case "high":
                                if (weatherDaysForecast != null)
                                    weatherDaysForecast.setHigh(parser.nextText());
                                break;
                            // 低温
                            case "low":
                                if (weatherDaysForecast != null)
                                    weatherDaysForecast.setLow(parser.nextText());
                                break;
                            // 白天
                            case "day":
                                isDay = true;
                                break;
                            // 夜间
                            case "night":
                                isDay = false;
                                break;
                            // 天气类型
                            case "type":
                                if (weatherDaysForecast != null) {
                                    if (isDay) {
                                        weatherDaysForecast.setTypeDay(parser.nextText());
                                    } else {
                                        weatherDaysForecast.setTypeNight(parser.nextText());
                                    }
                                }
                                break;
                            // 生活指数
                            case "zhishu":
                                weatherLifeIndex = new WeatherLifeIndex();
                                break;
                            // 指数名
                            case "name":
                                if (weatherLifeIndex != null) {
                                    weatherLifeIndex.setIndexName(parser.nextText());
                                }
                                break;
                            // 指数值
                            case "value":
                                if (weatherLifeIndex != null) {
                                    weatherLifeIndex.setIndexValue(parser.nextText());
                                }
                                break;
                            // 指数详细
                            case "detail":
                                if (weatherLifeIndex != null) {
                                    weatherLifeIndex.setIndexDetail(parser.nextText());
                                }
                                break;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        switch (parser.getName()) {
                            // 多天，昨天
                            case "yesterday":
                                // 多天，天气
                            case "weather":
                                weatherDaysForecasts.add(weatherDaysForecast);
                                weatherDaysForecast = null;
                                break;
                            // 指数
                            case "zhishu":
                                weatherLifeIndexes.add(weatherLifeIndex);
                                weatherLifeIndex = null;
                                break;
                        }

                        break;
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
        }
        weatherInfo.setWeatherDaysForecast(weatherDaysForecasts);
        weatherInfo.setWeatherLifeIndex(weatherLifeIndexes);
        return weatherInfo;
    }
    /**
     * 取得天气类型描述
     *
     * @param type1 白天天气类型
     * @param type2 夜间天气类型
     * @return 天气类型
     */
    public static String getWeatherType(Context context, String type1, String type2) {
        // 白天和夜间类型相同
        if (type1.equals(type2)) {
            return type1;
        } else {
            return String.format(context.getString(R.string.turn), type1, type2);
        }
    }

    /**
     * 将地址信息转换为城市
     *
     * @param address 地址
     * @return 城市名称
     */
    public static String formatCity(String address) {
        String city = null;
        // TODO: 数据测试
        if (address.contains("自治州")) {
            if (address.contains("市")) {
                city = address.substring(address.indexOf("州") + 1, address.indexOf("市"));
            } else if (address.contains("县")) {
                city = address.substring(address.indexOf("州") + 1, address.indexOf("县"));
            } else if (address.contains("地区")) {
                city = address.substring(address.indexOf("州") + 1, address.indexOf("地区"));
            }

        } else if (address.contains("自治区")) {
            if (address.contains("地区") && address.contains("县")) {
                city = address.substring(address.indexOf("地区") + 2, address.indexOf("县"));
            } else if (address.contains("地区")) {
                city = address.substring(address.indexOf("区") + 1, address.indexOf("地区"));
            } else if (address.contains("市")) {
                city = address.substring(address.indexOf("区") + 1, address.indexOf("市"));
            } else if (address.contains("县")) {
                city = address.substring(address.indexOf("区") + 1, address.indexOf("县"));
            }

        } else if (address.contains("地区")) {
            if (address.contains("县")) {
                city = address.substring(address.indexOf("地区") + 2, address.indexOf("县"));
            }

        } else if (address.contains("香港")) {
            if (address.contains("九龙")) {
                city = "九龙";
            } else if (address.contains("新界")) {
                city = "新界";
            } else {
                city = "香港";
            }

        } else if (address.contains("澳门")) {
            if (address.contains("氹仔")) {
                city = "氹仔岛";
            } else if (address.contains("路环")) {
                city = "路环岛";
            } else {
                city = "澳门";
            }

        } else if (address.contains("台湾")) {
            if (address.contains("台北")) {
                city = "台北";
            } else if (address.contains("高雄")) {
                city = "高雄";
            } else if (address.contains("台中")) {
                city = "台中";
            }

        } else if (address.contains("省")) {
            if (address.contains("市") && address.contains("县")) {
                city = address.substring(address.lastIndexOf("市") + 1, address.indexOf("县"));
            } else if (!address.contains("市") && address.contains("县")) {
                city = address.substring(address.indexOf("省") + 1, address.indexOf("县"));
            } else if (!address.contains("市")) {
                int start = address.indexOf("市");
                int end = address.lastIndexOf("市");
                if (start == end) {
                    city = address.substring(address.indexOf("省") + 1, end);
                } else {
                    city = address.substring(start, end);
                }
            }

        } else if (address.contains("市")) {
/*            if (address.contains("区")) {
                city = address.substring(address.indexOf("市") + 1, address.indexOf("区"));
            } else if (address.contains("县")) {
                city = address.substring(address.indexOf("市") + 1, address.indexOf("县"));
            }*/
            if (address.contains("中国")) {
                city = address.substring(address.indexOf("国") + 1, address.indexOf("市"));
            } else {
                city = address.substring(0, address.indexOf("市"));
            }
        }

        return city;
    }

    /**
     * 将天气信息类转换成Base64编码，并保存转换后的字符串
     *
     * @param weatherInfo 天气信息类
     * @param context     context
     */
    public static void saveWeatherInfo(WeatherInfo weatherInfo, Context context) {
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(weatherInfo);
            // 将字节流编码成base64的字符串
            String weatherInfoBase64 = Base64.encodeToString(baos
                    .toByteArray(), 1);
            SharedPreferencesUtils.saveString(context,weatherInfo.getCity(), weatherInfoBase64);
            // 保存天气更新时间
            SharedPreferencesUtils.saveLong(context,context.getString(R.string.city_weather_update_time,
                    weatherInfo.getCity()), System.currentTimeMillis());
        } catch (IOException e) {
        }
    }

    /**
     * 取得保存的Base64编码天气信息，并解码
     *
     * @param context context
     * @param city    需要取得天气信息的城市名
     * @return 天气信息类
     */
    public static WeatherInfo readWeatherInfo(Context context, String city) {
        WeatherInfo weatherInfo = null;

        String weatherInfoBase64 = SharedPreferencesUtils.getString(context,city, "");
        //读取字节
        byte[] base64 = Base64.decode(weatherInfoBase64.getBytes(), 1);
        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            weatherInfo = (WeatherInfo) bis.readObject();
        } catch (Exception e) {
        }
        return weatherInfo;
    }
}
