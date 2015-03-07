package com.yangjiao.androidtaskmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

public class ExecuteCommand {
	public static final String TAG = "AndroidTaskManager";
    private String mCommand;
    private String mStdout;
    private String mStderr;
    private int mRetvalue;

    public ExecuteCommand(String command) {
        this.mCommand = command;
    }

    public static String runIt(String command) {
        return new ExecuteCommand(command).run();
    }

    public String run() {
        String sRet = "";
        try {
            
            final Process m_process = Runtime.getRuntime().exec(this.mCommand);
            final StringBuilder sbread = new StringBuilder();
            Thread tout = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_process
                            .getInputStream()), 8192);
                    String ls_1 = null;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sbread.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            tout.start();
            final StringBuilder sberr = new StringBuilder();
            Thread terr = new Thread(new Runnable() {
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(m_process
                            .getErrorStream()), 8192);
                    String ls_1 = null;
                    try {
                        while ((ls_1 = bufferedReader.readLine()) != null) {
                            sberr.append(ls_1).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                           e.printStackTrace();
                        }
                    }
                }
            });
            terr.start();
           
            this.mRetvalue = m_process.waitFor();
            while (tout.isAlive()) {
                Thread.sleep(50);
            }
           
            if (terr.isAlive()) terr.interrupt();
            
            this.mStdout = sbread.toString();
            this.mStderr = sberr.toString();
            sRet = this.mStdout + this.mStderr;
        } catch (java.io.IOException ee) {
        	Log.e(TAG, "RunScript have a IO error :" + ee.getMessage());
            return null;
        } catch (InterruptedException ie) {
        	Log.e(TAG, "RunScript have a interrupte error:" + ie.getMessage());
            return null;
        } catch (Exception ex) {
        	Log.e(TAG, "RunScript have a error :" + ex.getMessage());
            return null;
        }
        return sRet;
    }

    public String getCommand() {
        return mCommand;
    }

    public String getStdout() {
        return mStdout;
    }

    public String getStderr() {
        return mStderr;
    }

    public int getRetvalue() {
        return mRetvalue;
    }

}
