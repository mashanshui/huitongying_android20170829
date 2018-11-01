package com.huixiangshenghuo.app.comm.ftp;

import java.io.File;

/**
 * @author zhaoyunhai on 2016/5/13 0013.
 */
public class FtpUploadBean {
    /**
     * need upload file
     */
    public File file;
    /**
     * the file type
     */
    public String type;
    /**
     * after upload file  server return path
     */
    public String serverPath;

    public FtpUploadBean() {
    }

    public FtpUploadBean(File file, String type) {
        this.file = file;
        this.type = type;
    }
}
