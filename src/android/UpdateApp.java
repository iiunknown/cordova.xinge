package com.iiunknown.cordova.xinge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
//import org.apache.cordova.api.CallbackContext;
//import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class UpdateApp extends CordovaPlugin {
	
	/*�汾�ż��·��*/
	private String checkPath;
	/* �°汾��*/
	private int newVerCode;
	/* �°汾��� */
	private String newVerName;
	/* APK ����·��*/
	private String  downloadPath;
	/* ������ */
    private static final int DOWNLOAD = 1;
    /* ���ؽ��� */
    private static final int DOWNLOAD_FINISH = 2;
    /* ���ر���·�� */
    private String mSavePath;
    /* ��¼��������� */
    private int progress;
    /* �Ƿ�ȡ����� */
    private boolean cancelUpdate = false;
    /* ������*/
    private Context mContext;
    /* ���½���� */
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;
	
	@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.mContext = cordova.getActivity();
		if (action.equals("checkAndUpdate")) {
			this.checkPath = args.getString(0);
//			callbackContext.error(Environment.getDataDirectory().getPath());;
        	checkAndUpdate(callbackContext);
        	return true;
        }else if(action.equals("getCurrentVersion")){
        	callbackContext.success(this.getCurrentVerCode(callbackContext)+"");
        }else if(action.equals("getCurrentVersionName")){
        	callbackContext.success(this.getCurrentVerName());
        }else if(action.equals("getServerVersion")){
        	this.checkPath = args.getString(0);
        	if(this.getServerVerInfo(callbackContext)){
        		callbackContext.success(newVerCode+"");
        	}else{
        		callbackContext.error("can't connect to the server!please check [checkpath]");
        	}
        	
        }
        return false;
    }
	

    
    /**
     * ������
     */
    private void checkAndUpdate(CallbackContext callbackContext){
    	try
    	{
    	if(getServerVerInfo(callbackContext)){
    		int currentVerCode = getCurrentVerCode(callbackContext);
    		if(newVerCode>currentVerCode){
    			this.showNoticeDialog();
    		}
    	}
    	}catch(Exception e)
    	{
			callbackContext.error(e.getMessage());
    	}
    }
	
	
	/**
     * ��ȡӦ�õ�ǰ�汾����
     * @param context
     * @return
     */
    private int getCurrentVerCode(CallbackContext callbackContext){

    	int currentVer = -1;
    	try {
        	String packageName = this.mContext.getPackageName();
			currentVer = this.mContext.getPackageManager().getPackageInfo(packageName, 0).versionCode;
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			e.printStackTrace();
		}
    	return currentVer;
    }
    
    /**
     * ��ȡӦ�õ�ǰ�汾���
     * @param context
     * @return
     */
    private String getCurrentVerName(){
    	String packageName = this.mContext.getPackageName();
    	String currentVerName = "";
    	try {
    		currentVerName = this.mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	return currentVerName;
    }
        
    /**
     * ��ȡӦ�����
     * @param context
     * @return
     */
    private String getAppName(){
    	return this.mContext.getResources().getText(R.string.app_name).toString();
    }
    
    /**
     * ��ȡ�������ϵİ汾��Ϣ
     * @param path
     * @return
     * @throws Exception
     */
    private boolean getServerVerInfo(CallbackContext callbackContext){
		try {
			StringBuilder verInfoStr = new StringBuilder();
			URL url = new URL(checkPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"),8192);
			String line = null;
			while((line = reader.readLine()) != null){
				verInfoStr.append(line+"\n");
			}
			reader.close();
			
			JSONArray array = new JSONArray(verInfoStr.toString());
			if(array.length()>0){
				JSONObject obj = array.getJSONObject(0);
				newVerCode = obj.getInt("verCode");
				newVerName = obj.getString("verName");
				downloadPath = obj.getString("apkPath");
			}
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			e.printStackTrace();
			return false;
		} 
    	
    	return true;
    	
    }
    
    /**
     * ��ʾ������¶Ի���
     */
    private void showNoticeDialog() {
    	
        // ����Ի���
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        // ����
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
                // ��ʾ���ضԻ���
                showDownloadDialog();
            }
        });
        // �Ժ����
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * ��ʾ������ضԻ���
     */
    private void showDownloadDialog()
    {
        // ����������ضԻ���
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        // �����ضԻ������ӽ����
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        // ȡ�����
        builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // ����ȡ��״̬
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        // �����ļ�
        downloadApk();
    }

    /**
     * ����apk�ļ�
     */
    private void downloadApk()
    {
        // �������߳��������
//    	try
//    	{
		new downloadApkThread().start();
//    	}
//    	catch (Exception e)
//    	{
//            AlertDialog.Builder builder = new Builder(this.mContext);
//            builder.setTitle(e.getMessage());
//            errorDialog = builder.create();
//            errorDialog.show();
//    	}
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            // ��������
            case DOWNLOAD:
                // ���ý����λ��
                mProgress.setProgress(progress);
                break;
            case DOWNLOAD_FINISH:
                // ��װ�ļ�
                installApk();
                break;
            default:
                break;
            }
        };
    };
    
    /**
     * �����ļ��߳�
     */
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {

				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				//Environment.getDownloadCacheDirectory()
				//if (Environment.getDataDirectory().getPath() != "") 
				{
					// ��ô洢����·��

					String sdpath = Environment.getExternalStorageDirectory()+ "/";
					mSavePath = sdpath + "download";
					URL url = new URL(downloadPath);
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();
					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					String fileName = mSavePath + "/" + newVerName;
					File apkFile = new File(fileName);
					if (!apkFile.exists()) {
						apkFile.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ��������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½��
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ���ֹͣ����.
					fos.close();
					is.close();
					progress = 100;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// ���½��
			mHandler.sendEmptyMessage(DOWNLOAD);
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, newVerName);
		if (!apkfile.exists()) {
			return;
		}
		// ͨ��Intent��װAPK�ļ�
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
    

}
