package com.huixiangshenghuo.app.comm.ftp;

import android.support.annotation.NonNull;


import com.huixiangshenghuo.app.CustomConfig;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *FTP 上传文件
 */
public class FtpUploadUtils {
    public final static String defaultServerPath = "pharmacy/member/";
    public final static String MultifilePath = "pharmacy/multifile/";
    private OnUploadListener listener;

    public void upLoadListFiles(List<File> fileList, final String dir) {
        Observable.from(fileList)
                .filter(new Func1<File, Boolean>() {
                    @Override
                    public Boolean call(File file) {
                        return !(file == null || !file.exists());//file为空或不存在
                    }
                })
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        return upload(file, dir);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (listener != null) {
                            listener.onNext(s, "");
                        }
                    }
                });
    }

    public void upLoadListFtpUploadBean(List<FtpUploadBean> fileList, final String dir) {
        Observable.from(fileList)
                .filter(new Func1<FtpUploadBean, Boolean>() {
                    @Override
                    public Boolean call(FtpUploadBean file) {
                        return !(file.file == null || !file.file.exists());//file为空或不存在
                    }
                })
                .map(new Func1<FtpUploadBean, FtpUploadBean>() {
                    @Override
                    public FtpUploadBean call(FtpUploadBean file) {
                        return upload(file, dir);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FtpUploadBean>() {
                    @Override
                    public void onCompleted() {

                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onNext(FtpUploadBean s) {
                        if (listener != null) {
                            listener.onNext(s.serverPath, s.type);
                        }
                    }
                });
    }

    /**
     * @param file 要上传的文件 {@link File}对象
     * @param dir  {@link String}要上传服务器的路径，工具类自动加时间目录级
     */
    public void upLoadFile(final File file, final String dir) {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String upload = null;
                try {
                    upload = upload(file, dir);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onNext(upload);
                subscriber.onCompleted();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onError(e);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (listener != null) {
                            listener.onNext(s, "");
                        }
                    }
                });
    }


    private String upload(File file, String dir) {
        return upLoadFtp(file, dir);
    }

    private FtpUploadBean upload(FtpUploadBean file, String dir) {
        FtpUploadBean ftpUploadBean = new FtpUploadBean();
        file.serverPath = upLoadFtp(file.file, dir);
        return file;
    }

    @NonNull
    private String upLoadFtp(File file, String dir) {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;
        try {
            ftpClient.connect(CustomConfig.FTP_URL, CustomConfig.FTP_PORT);
            boolean loginResult = ftpClient.login(CustomConfig.LOGIN_NAME,
                    CustomConfig.LOGIN_PWD);
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.setControlEncoding("GBK");
                ftpClient.enterLocalPassiveMode();
                String timeDir = getmDateYYYYMMDD4();

                ftpClient.makeDirectory(dir + timeDir);
                ftpClient.changeWorkingDirectory(dir + timeDir);
                fis = new FileInputStream(file);
                final String imageUUID = UUID.randomUUID().toString()+".jpg";
                boolean b = ftpClient.storeFile(imageUUID, fis);
                return timeDir + "/" + imageUUID;

            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            // IOUtils.closeQuietly(fis);
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return "";
    }

    private String getmDateYYYYMMDD4(){
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(System.currentTimeMillis());
    }

    public interface OnUploadListener {
        void onCompleted();
        void onError(Throwable e);
        void onNext(String s, String type);
    }
    public void setListener(OnUploadListener listener) {
        this.listener = listener;
    }
}
