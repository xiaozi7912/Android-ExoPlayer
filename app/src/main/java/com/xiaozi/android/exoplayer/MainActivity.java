package com.xiaozi.android.exoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xiaozi.android.exoplayer.utils.FTPClientManager;
import com.xiaozi.android.exoplayer.utils.HTTPManager;
import com.xiaozi.android.exoplayer.utils.YoutubeModel;
import com.xiaozi.framework.libs.BaseActivity;
import com.xiaozi.framework.libs.utils.Logger;

import java.io.File;

import it.sauronsoftware.ftp4j.FTPFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends BaseActivity {
    private ListView mFTPFileListView;

    private static final String FTP_HOST = "192.168.0.115";
    private static final String FTP_LOGIN_USER = "vod01";
    private static final String FTP_LOGIN_PASSWORD = "vod0102";
    private static final String FILE_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + BuildConfig.APPLICATION_ID;

    private FTPFile[] mFileList = null;

    private String mDownloadVideoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        initView();
        checkDownloadPathExists();
        playVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initialize() {
        super.initialize();
        Logger.init(BuildConfig.DEBUG);
        mFTPFileListView = null;
        FTPClientManager.init(FTP_HOST, FTP_LOGIN_USER, FTP_LOGIN_PASSWORD);
    }

    @Override
    protected void initView() {
        super.initView();
        mFTPFileListView = findViewById(R.id.main_ftp_file_list);

        mFTPFileListView.setOnItemClickListener(onItemClickListener);
    }

    private void checkDownloadPathExists() {
        Logger.i(LOG_TAG, "checkDownloadPathExists");
        Logger.d(LOG_TAG, "checkDownloadPathExists FILE_DOWNLOAD_PATH : " + FILE_DOWNLOAD_PATH);
        File downloadPath = new File(FILE_DOWNLOAD_PATH);
        Logger.d(LOG_TAG, "checkDownloadPathExists downloadPath.exists : " + downloadPath.exists());
        if (!downloadPath.exists()) {
            downloadPath.mkdir();
            Logger.d(LOG_TAG, "checkDownloadPathExists downloadPath.mkdir : " + downloadPath.mkdir());
        }
    }

    private void playVideo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://xiaozi-video-downloader.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        HTTPManager httpManager = retrofit.create(HTTPManager.class);
        Call<YoutubeModel> req = httpManager.getUrl("cui72y5FaNY");

        req.enqueue(new Callback<YoutubeModel>() {
            @Override
            public void onResponse(Call<YoutubeModel> call, Response<YoutubeModel> response) {
                Logger.i(LOG_TAG, "playVideo onResponse");
                YoutubeModel model = response.body();
                Logger.d(LOG_TAG, "playVideo onResponse call.request.url : " + call.request().url());
                Logger.d(LOG_TAG, "playVideo onResponse code : " + response.code());
                Logger.d(LOG_TAG, "playVideo onResponse message : " + response.message());
                Logger.d(LOG_TAG, "playVideo onResponse model.getTitle : " + model.getTitle());
                Logger.d(LOG_TAG, "playVideo onResponse model.getUrl : " + model.getUrl());

                Intent intent = new Intent(mActivity, PlayerActivity.class);
                intent.putExtra("video_path", model.getUrl());
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<YoutubeModel> call, Throwable t) {
                Logger.i(LOG_TAG, "playVideo onFailure");
                Logger.d(LOG_TAG, "playVideo onFailure t.getLocalizedMessage : " + t.getLocalizedMessage());
            }
        });
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Logger.i(LOG_TAG, "onItemClick");
            Logger.d(LOG_TAG, "onItemClick position : " + position);
            Logger.d(LOG_TAG, "onItemClick id : " + id);
            FTPFile selectedItem = mFileList[position];
            mDownloadVideoPath = String.format("%s/%s", FILE_DOWNLOAD_PATH, selectedItem.getName());
            Logger.d(LOG_TAG, "onItemClick selectedItem.getName : " + selectedItem.getName());
            try {
                FTPClientManager.getInstance().downloadFile(mActivity,
                        "/vod", selectedItem.getName(), mDownloadVideoPath,
                        new FTPClientManager.DownloadFileCallback() {
                            @Override
                            public void onStarted() {
                                Logger.i(LOG_TAG, "onItemClick downloadFile onStarted");
                            }

                            @Override
                            public void onTransferred(int size) {
                                Logger.i(LOG_TAG, "onItemClick downloadFile onTransferred");
                                Logger.d(LOG_TAG, "onItemClick downloadFile onTransferred size : " + size);
                            }

                            @Override
                            public void onCompleted() {
                                Logger.i(LOG_TAG, "onItemClick downloadFile onCompleted");
                                Intent intent = new Intent(mActivity, PlayerActivity.class);
                                intent.putExtra("video_path", mDownloadVideoPath);
                                startActivity(intent);
                            }

                            @Override
                            public void onAborted() {
                                Logger.i(LOG_TAG, "onItemClick downloadFile onAborted");
                            }

                            @Override
                            public void onFailed() {
                                Logger.i(LOG_TAG, "onItemClick downloadFile onFailed");
                            }

                            @Override
                            public void onError(int code, String message) {
                                Logger.i(LOG_TAG, "onItemClick downloadFile onError");
                            }
                        });
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    };
}
