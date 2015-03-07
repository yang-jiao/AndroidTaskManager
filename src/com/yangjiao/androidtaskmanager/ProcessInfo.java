package com.yangjiao.androidtaskmanager;

import java.util.ArrayList;

public class ProcessInfo {
    private ArrayList<PsRow> mPsList;
    private static String mRootPid = null;

    public ProcessInfo() {
        ps();
    }

    private void ps() {
        String ps = ExecuteCommand.runIt("ps");
        String[] lines = ps.split("\n");
        mPsList = new ArrayList<PsRow>();
        for (String line : lines) {
            PsRow row = new PsRow(line);
            if (row.pid != null) mPsList.add(row);
        }
    }

    public PsRow getPsRow(String cmd) {
        for (PsRow row : mPsList) {
            if (cmd.equals(row.cmd)) {
                return row;
            }
        }
        return null;
    }

    public static class PsRow {
        String pid = null;
        String cmd;
        String ppid;
        String user;
        int mem;

        public PsRow(String line) {
            if (line == null) return;
            String[] p = line.split("[\\s]+");
            if (p.length != 9) return;
            user = p[0];
            pid = p[1];
            ppid = p[2];
            cmd = p[8];
            mem = Utilities.parseInt(p[4]);
            if (isRoot()) {
                mRootPid = pid;
            }
        }

        public boolean isRoot() {
            return "zygote".equals(cmd);
        }

        public boolean isMain() {
            return ppid.equals(mRootPid) && user.startsWith("app_");
        }

        public String toString() {
            final String TAB = ";";

            String retValue = "";

            retValue = "PsRow ( " + super.toString() + TAB + "pid = " + this.pid + TAB + "cmd = " + this.cmd
                    + TAB + "ppid = " + this.ppid + TAB + "user = " + this.user + TAB + "mem = " + this.mem
                    + " )";

            return retValue;
        }

    }
    
}
