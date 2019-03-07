package com.suntoon.tiles.util;

import com.vividsolutions.jts.geom.Envelope;
import org.apache.pdfbox.cos.COSBase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.image.BufferedImage;

/**
 * @ProjectionName gdal2tilesjava
 * @ClassName PDFReader
 * @Description TODO
 * @Author YueLifeng
 * @Date 2019/3/7 0007下午 3:44
 * @Version 1.0
 */
public class PDFReader {
    private CoordinateReferenceSystem referenceSystem;
    private Envelope envelope;

    private int width;
    private int height;
    private BufferedImage bufferedImage;

    private void printEnvelop(COSBase cosBase) {

    }




}
