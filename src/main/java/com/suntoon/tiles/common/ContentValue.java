package com.suntoon.tiles.common;

/**
 * @ProjectionName gdal2tilesjava
 * @ClassName ContentValue
 * @Description TODO
 * @Author YueLifeng
 * @Date 2019/3/7 0007下午 3:08
 * @Version 1.0
 */
public class ContentValue {
    public static int MAXZOOMLEVEL = 32;
    public static String[] resamplingList = new String[]{"average", "near", "bilinear", "cubic", "cubicspline", "lanczos", "antilalias"};
    public static String[] profileList = new String[]{"mercator", "geodetic", "raster"};
    public static String[] webviewList = new String[]{"all", "google", "openlayers", "leaflet", "none"};
}
