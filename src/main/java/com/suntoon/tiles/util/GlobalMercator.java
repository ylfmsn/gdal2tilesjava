package com.suntoon.tiles.util;

import com.suntoon.tiles.common.ContentValue;

/**
 * @ProjectionName gdal2tilesjava
 * @ClassName GlobalMercator
 * @Description TODO
 * @Author YueLifeng
 * @Date 2019/3/7 0007下午 3:20
 * @Version 1.0
 */
public class GlobalMercator {
    private int tileSize;     //瓦片大小
    private double initialResolution;      //初始化分辨率
    private double originShift;

    public GlobalMercator(int tileSize) {
        this.tileSize = tileSize;
        this.initialResolution = 2 * Math.PI * 6378137 / this.tileSize;
        this.originShift = 2 * Math.PI * 6378137 / 2.0;
    }

    public double[] latlon2Meters(double lat, double lon) {
        double mx = lon * this.originShift / 180.0;
        double my = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0);
        my = my * this.originShift / 180.0;
        return new double[]{mx, my};
    }

    public double[] meters2LatLon(double mx, double my) {
        double lon = (mx / this.originShift) * 180.0;
        double lat = (my / this.originShift) * 180.0;

        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
        return new double[]{lat, lon};
    }

    public double[] pixels2Meters(int px, int py, int zoom) {
        double res = this.resolution(zoom);
        double mx = px * res - this.originShift;
        double my = py * res - this.originShift;
        return new double[]{mx, my};
    }

    public double[] meters2Pixels(double mx, double my, int zoom) {
        double res = this.resolution(zoom);
        double px = (mx + this.originShift) / res;
        double py = (my + this.originShift) / res;
        return new double[]{px, py};
    }

    public int[] pixels2Tile(double px, double py) {
        int tx = (int) (Math.ceil(px / (float) (this.tileSize)) - 1);
        int ty = (int) (Math.ceil(py / (float) (this.tileSize)) - 1);
        return new int[]{tx, ty};
    }

    public double[] pixels2Raster(double px, double py, int zoom) {
        double mapSize = this.tileSize << zoom;
        return new double[]{px, mapSize - py};
    }

    public int[] meters2Tile(double mx, double my, int zoom) {
        double[] coordinate = this.meters2Pixels(mx, my, zoom);
        return this.pixels2Tile(coordinate[0], coordinate[1]);
    }

    public double[] tileBounds(int tx, int ty, int zoom) {
        double[] minxy = pixels2Meters(tx * this.tileSize, ty * this.tileSize, zoom);
        double[] maxxy = pixels2Meters((tx + 1) * this.tileSize, (ty + 1) * this.tileSize, zoom);

        return new double[]{minxy[0], minxy[1], maxxy[0], maxxy[1]};
    }

    public double[] tileLatLonBounds(int tx, int ty, int zoom) {
        double[] bounds = this.tileBounds(tx, ty, zoom);
        double[] minLatLon =this. meters2LatLon(bounds[0], bounds[1]);
        double[] maxLatlon = this.meters2LatLon(bounds[2], bounds[3]);
        return new double[]{minLatLon[0], minLatLon[1], maxLatlon[0], maxLatlon[1]};
    }

    private double resolution(int zoom) {
        return this.initialResolution / (Math.pow(2, zoom));
    }

    public int zoomForPixelSize(double pixelSize) {
        for (int i = 0; i < ContentValue.MAXZOOMLEVEL; i++) {
            if (pixelSize > this.resolution(i)) {
                if (i != 0) {
                    return i - 1;
                } else return 0;
            }
        }
        return 0;
    }

    public int[] googleTile(int tx, int ty, int zoom) {
        return new int[]{tx, ((int) (Math.pow(2, zoom)) - 1) - ty};
    }

    public String quadTree(int tx, int ty, int zoom) {
        String quadKey = "";
        ty = (int) (Math.pow(2, zoom) - 1 - ty);
        for (int i = zoom; i > 0; i--) {
            int digit = 0;
            int mask = 1 << (i - 1);
            if ((tx & mask) != 0) {
                digit += 1;
            }
            if ((ty & mask) != 0) {
                digit += 2;
                quadKey += digit;
            }
        }
        return quadKey;
    }

    public String tileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    public int[] quadKeyToTileXY(String quadKey) throws Exception {
        int tileX = 0, tileY = 0;
        int levelOfDetail = quadKey.length();
        for (int i = levelOfDetail; i > 0; i--) {
            int mask = 1 << (i - 1);
            switch (quadKey.charAt(levelOfDetail - i)) {
                case '0':
                    break;
                case '1':
                    tileX |= mask;
                    break;
                case '2':
                    tileY |= mask;
                    break;
                case '3':
                    tileX |= mask;
                    tileY |= mask;
                    break;
                default:
                    throw new Exception("Invalid QuadKey digit sequence.");
            }
        }
        return new int[]{tileX, tileY, levelOfDetail};
    }


}
