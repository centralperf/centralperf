package org.centralperf.model;

public class Configuration {
	public static final String INITIALIZED = "initialized";
	
	private String keyLabel;
	private String keyToolTip;
	private String keyName;
	private String keyValue;
	private boolean readOnly;
	private boolean fromDb;
	private boolean booleanValue;
	private String trueValue="TRUE";


	//FIXME: Have to found a better way (DB ?). Should be fix with i18n of the application
	public Configuration(String keyName){
		this.keyName=keyName;
		this.fromDb=false;
		this.readOnly=true;
		this.booleanValue=false;
		
		if("server.mode".equals(keyName)){
			this.keyLabel="Server in production mode:";
			this.keyToolTip="There is more log and debug event when server is not in production mode";
			this.booleanValue=true;
			this.trueValue="PROD";
		}
		
		if("db.driver".equals(keyName)){
			this.keyLabel="Database driver name:";
			this.keyToolTip="Name of the JDBC Driver for the database";
		}
		if("db.url".equals(keyName)){
			this.keyLabel="Database connexion url:";
			this.keyToolTip="Connexion URL to centralPerf Database";
		}
		if("db.login".equals(keyName)){
			this.keyLabel="Database connexion login:";
			this.keyToolTip="Database login for centralPerf Database";
		}
		if("db.sqlDialect".equals(keyName)){
			this.keyLabel="Database SQL Dialect:";
			this.keyToolTip="SQL Dialect to use with centralPerf Database";
		}
		
		if("jmeter.launcher.script.path".equals(keyName)){
			this.keyLabel="Jmeter executable path:";
			this.keyToolTip="Path where are JMeter binaries";
		}
		if("jmeter.launcher.output.csv.default_headers".equals(keyName)){
			this.keyLabel="Result output headers:";
			this.keyToolTip="Headers of Jmeter result output file";
		}
		if("jmeter.launcher.output.format".equals(keyName)){
			this.keyLabel="Result output format:";
			this.keyToolTip="Format of Jmeter result output file";
		}
		
		if("gatling.launcher.path".equals(keyName)){
			this.keyLabel="Gatling home path:";
			this.keyToolTip="Path of Gatling installation";
		}
		if("gatling.launcher.script.relativepath".equals(keyName)){
			this.keyLabel="Gatling executable path:";
			this.keyToolTip="Path where are Gatling binaries";
		}
		if("gatling.launcher.output.log.default_headers".equals(keyName)){
			this.keyLabel="Result output headers:";
			this.keyToolTip="Headers of Gatling result output file";
		}
		if("gatling.launcher.output.format".equals(keyName)){
			this.keyLabel="Result output format:";
			this.keyToolTip="Format of Gatling result output file";
		}
		
		if("csv.field_separator".equals(keyName)){
			this.keyLabel="Separator for CSV export/import:";
			this.keyToolTip="Separator character for CSV export/import";
			this.readOnly=false;
		}
		if("report.cache.delay.seconds".equals(keyName)){
			this.keyLabel="Result refresh interval (seconds):";
			this.keyToolTip="Delay (seconds) between run data refresh";
			this.readOnly=false;
		}
	}
	
	
	public String getKeyLabel() {return keyLabel;}
	public void setKeyLabel(String keyLabel) {this.keyLabel = keyLabel;}
	
	public String getKeyToolTip() {return keyToolTip;}
	public void setKeyToolTip(String keyToolTip) {this.keyToolTip = keyToolTip;}
	
	public String getKeyName() {return keyName;}
	public void setKeyName(String keyName) {this.keyName = keyName;}
	
	public String getKeyValue() {return keyValue;}
	public void setKeyValue(String keyValue) {
		if(booleanValue){
			this.keyValue=Boolean.toString(this.trueValue.equals(keyValue.toUpperCase()));
		}
		else{this.keyValue = keyValue;}
	}
	
	public boolean isReadOnly() {return readOnly;}
	public void setReadOnly(boolean readOnly) {this.readOnly = readOnly;}
	
	public boolean isFromDb() {return fromDb;}
	public void setFromDb(boolean fromDb) {this.fromDb = fromDb;}

	public boolean isBooleanValue() {return booleanValue;}
	public void setBooleanValue(boolean booleanValue) {this.booleanValue = booleanValue;}
}
