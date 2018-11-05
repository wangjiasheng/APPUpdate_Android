package com.example.wjs.appupdate;

import java.util.List;

public class APKInfo {
	private String apkName;
	private String versionName;
	private int versionCode;
	private String downUrl;
	private boolean simple;
	private boolean incrementalupdate;
	private List<String> message;
	private long downSize;
	private String appIcon;
	private String apkmd5;
	public void setApkmd5(String apkmd5) {
		this.apkmd5 = apkmd5;
	}
	public String getApkmd5() {
		return apkmd5;
	}
	public String getApkName() {
		return apkName;
	}
	public void setApkName(String apkName) {
		this.apkName = apkName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getDownUrl() {
		return downUrl;
	}
	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}
	public void setSimple(boolean simple) {
		this.simple = simple;
	}
	public boolean isSimple() {
		return simple;
	}
	public void setIncrementalupdate(boolean incrementalupdate) {
		this.incrementalupdate = incrementalupdate;
	}
	public boolean isIncrementalupdate() {
		return incrementalupdate;
	}
	public List<String> getMessage() {
		return message;
	}
	public void setMessage(List<String> message) {
		this.message = message;
	}
	public long getDownSize() {
		return downSize;
	}
	public void setDownSize(long downSize) {
		this.downSize = downSize;
	}
	public String getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}
}
