package com.xiaozi.android.exoplayer.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.xiaozi.framework.libs.utils.Logger;

import java.io.File;
import java.io.IOException;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

/**
 * Created by user on 2018-01-29.
 */

public class FTPClientManager {
    private final String LOG_TAG = getClass().getSimpleName();
    private Context mContext = null;
    private static FTPClientManager mInstance = null;
    private final static FTPClient mFTPClient = new FTPClient();

    private int mPort = 0;
    private String mHost = null;
    private String mUser = null;
    private String mPwd = null;

    public FTPClientManager(String host, int port, String user, String pwd) {
        mHost = host;
        mPort = port;
        mUser = user;
        mPwd = pwd;
        mFTPClient.setPassive(true);
    }

    public static void init(String host, String user, String pwd) {
        init(host, 21, user, pwd);
    }

    public static void init(String host, int port, String user, String pwd) {
        if (mInstance == null)
            mInstance = new FTPClientManager(host, port, user, pwd);
    }

    public static FTPClientManager getInstance() throws Exception {
        if (mInstance == null) throw new Exception("You need call method init first.");
        return mInstance;
    }

    public void getFileList(Context context, String absolutePath, GetFileListCallback callback) {
        Logger.i(LOG_TAG, "getFileList");
        mContext = context;
        new GetFileListTask(callback).execute(absolutePath);
    }

    public void downloadFile(Context context, String fileDir, String remoteFile, String downloadPath, DownloadFileCallback callback) {
        Logger.i(LOG_TAG, "downloadFile");
        mContext = context;
        new DownloadFileTask(callback).execute(new String[]{fileDir, remoteFile, downloadPath});
    }

    private class GetFileListTask extends AsyncTask<String, Integer, FTPFile[]> {
        private GetFileListCallback mCallback = null;

        public GetFileListTask(GetFileListCallback callback) {
            mCallback = callback;
        }

        @Override
        protected FTPFile[] doInBackground(String... strings) {
            try {
                mFTPClient.connect(mHost, mPort);
                mFTPClient.login(mUser, mPwd);
                mFTPClient.changeDirectory(strings[0]);
                return mFTPClient.list();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
                Logger.d(LOG_TAG, "FTPException e.getCode : " + e.getCode());
                Logger.d(LOG_TAG, "FTPException e.getMessage : " + e.getMessage());
                mCallback.onError(e.getCode(), e.getMessage());
            } catch (FTPDataTransferException e) {
                e.printStackTrace();
            } catch (FTPListParseException e) {
                e.printStackTrace();
            } catch (FTPAbortedException e) {
                e.printStackTrace();
            } finally {
                try {
                    mFTPClient.disconnect(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(FTPFile[] files) {
            super.onPostExecute(files);
            if (mCallback != null) mCallback.onSuccess(files);
        }
    }

    private class DownloadFileTask extends AsyncTask<String, Integer, Void> {
        private DownloadFileCallback mCallback = null;
        private ProgressDialog mProgressDialog = null;

        private long mTotalSize = 0;

        public DownloadFileTask(DownloadFileCallback callback) {
            mCallback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                mFTPClient.connect(mHost, mPort);
                mFTPClient.login(mUser, mPwd);
                mFTPClient.changeDirectory(strings[0]);
                mProgressDialog.setMax((int) mFTPClient.fileSize(strings[1]));
                mFTPClient.download(strings[1], new File(strings[2]), 0, new FTPDataTransferListener() {
                    @Override
                    public void started() {
                        Logger.i(LOG_TAG, "DownloadFileTask download started");
                        mCallback.onStarted();
                    }

                    @Override
                    public void transferred(int i) {
                        Logger.i(LOG_TAG, "DownloadFileTask download transferred");
                        Logger.d(LOG_TAG, "DownloadFileTask download transferred i : " + i);
                        mTotalSize += i;
                        mProgressDialog.setProgress((int) mTotalSize);
                        mCallback.onTransferred(i);
                    }

                    @Override
                    public void completed() {
                        Logger.i(LOG_TAG, "DownloadFileTask download completed");
                        mCallback.onCompleted();
                    }

                    @Override
                    public void aborted() {
                        Logger.i(LOG_TAG, "DownloadFileTask download aborted");
                        mCallback.onAborted();
                    }

                    @Override
                    public void failed() {
                        Logger.i(LOG_TAG, "DownloadFileTask download failed");
                        mCallback.onFailed();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
            } catch (FTPException e) {
                e.printStackTrace();
                Logger.d(LOG_TAG, "FTPException e.getCode : " + e.getCode());
                Logger.d(LOG_TAG, "FTPException e.getMessage : " + e.getMessage());
                mCallback.onError(e.getCode(), e.getMessage());
            } catch (FTPAbortedException e) {
                e.printStackTrace();
            } catch (FTPDataTransferException e) {
                e.printStackTrace();
            } finally {
                try {
                    mFTPClient.disconnect(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FTPIllegalReplyException e) {
                    e.printStackTrace();
                } catch (FTPException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.dismiss();
        }
    }

    private interface BaseCallback {
        void onError(int code, String message);
    }

    public interface GetFileListCallback extends BaseCallback {
        void onSuccess(FTPFile[] files);
    }

    public interface DownloadFileCallback extends BaseCallback {
        void onStarted();

        void onTransferred(int size);

        void onCompleted();

        void onAborted();

        void onFailed();
    }
}
