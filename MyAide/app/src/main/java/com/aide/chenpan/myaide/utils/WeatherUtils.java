package com.aide.chenpan.myaide.utils;

import android.content.Context;

import com.aide.chenpan.myaide.R;

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

}
