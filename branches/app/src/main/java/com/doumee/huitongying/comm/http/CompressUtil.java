package com.huixiangshenghuo.app.comm.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressUtil {
    /**

     */
    public static byte[] compressByGzip(byte[] str) {
        if (str == null || str.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str);
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * <p/>
     *
     * @param bytesToUncompress
     * @param encoding
     * @return
     * @throws IOException
     * @author jgzhang2, 2014-8-16
     */
    public static byte[] uncompressByGzip(byte[] bytesToUncompress,
                                          String encoding) throws IOException {
        if (bytesToUncompress == null || bytesToUncompress.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytesToUncompress);

        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }

        return out.toByteArray();

    }
}
