package com.suntoon.tiles;

import com.suntoon.tiles.util.GlobalGeodetic;
import com.suntoon.tiles.util.GlobalMercator;
import com.suntoon.tiles.util.PDFReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.apache.commons.cli.Options;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * @ProjectionName gdal2tilesjava
 * @ClassName GDAL2Tiles
 * @Description TODO
 * @Author YueLifeng
 * @Date 2019/3/7 0007下午 3:06
 * @Version 1.0
 */
public class GDAL2Tiles {
    private static PDFReader reader = new PDFReader();
    private boolean stopped = false;
    private int tilesize = 256;
    private String tileext = "png";
    private boolean scaledquery = true;
    private int querysize = 4 * tilesize;
    private String resampling = "";
    private int tminz = -1, tmaxz = -1;
    private double ominx, ominy, omaxx, omaxy;
    private String profile;
    private double[] swne;
    private List<int[]> tminmax;
    private double[] out_gt;
    private int nativezoom = -1;
    private double[] tsize;

    private Coordinate leftTop;
    private Coordinate rightBottom;
    private double widthResolution;
    private double heightResolution;
    private CoordinateReferenceSystem referenceSystem;
    private Map<String, String> options;
    private String input;
    private String output;
    private BufferedImage in_ds, out_ds, alphaband;
    private CoordinateReferenceSystem out_srs;
    private Object out_drv, men_drv;
    private GlobalMercator mercator;
    private GlobalGeodetic geodetic;
    private boolean dataBandsCount, kml;
    private String[] args;
    private Options parser;
    private int[] tileswne;
    private int[] in_nodata;
    private String tiledriver = "";
    private boolean overviewquery = false;
    private static boolean geopackage = false;

    public void process() {
        //Opening and preprocessing of the input file
        open_input();






    }

    private void open_input() {
        this.out_drv = null;
        this.men_drv = null;

        if(new File(this.input).exists()) {
            reader.init(this.input, this.output);
            this.in_ds = reader.getImage();
        }

        in_nodata = new int[]{};
        CoordinateReferenceSystem in_srs = reader.getReferenceSystem();

        //初始化in_nodata
        this.out_srs = in_srs;

        this.out_ds = null;

        if(this.out_ds == null) {
            this.out_ds = this.in_ds;
        }

        //Read the georeference
        this.out_gt = getGeoTransform();

        this.ominx = out_gt[0];
        this.omaxx = out_gt[0] + this.out_ds.getWidth() * this.out_gt[1];
        this.omaxy = out_gt[3];
        this.ominy = out_gt[3] - this.out_ds.getHeight() * this.out_gt[1];












    }

    private double[] getGeoTransform() {
        Envelope envelope = null;
        if(this.options.get("profile").equals("mercator")) {
            envelope = reader.getEnvelope();
            double[] min = lonLat2Mercator(envelope.getMinX(), envelope.getMinY());
            double[] max = lonLat2Mercator(envelope.getMaxX(), envelope.getMaxY());
            envelope = new Envelope();
            envelope.init(min[0], max[0], min[1], max[1]);
        } else {
            envelope = reader.getEnvelope();
        }

        widthResolution = (envelope.getMaxX() - envelope.getMinX()) / this.in_ds.getWidth();
        heightResolution = (envelope.getMinY() - envelope.getMaxY()) / this.in_ds.getHeight();
        //mercator(11440253.586413905, 0.2985821410518959, 0.0, 2877395.4927671393, 0.0, -0.2985821410519452)
        //84(102.76954650878781, 2.5533771416895517e-06, 0.0, 25.013439812256067, 0.0, -2.553377141690922e-06)

        return new double[]{envelope.getMinX(), widthResolution, 0.0, envelope.getMinY(), 0.0, heightResolution};
    }

    private double[] lonLat2Mercator(double lon, double lat) {
        double x = lon* 20037508.342789 / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360.0)) / (Math.PI / 180);
        y = y * 20037508.34789 / 180;
        return new double[]{x, y};
    }


}
