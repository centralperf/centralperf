package org.centralperf.graph.model;

import java.io.Serializable;

public class HttpCodeRepartition implements Serializable{
	
	private static final long serialVersionUID = -5234221824650236119L;
	
	private long nbHttp1xx;
	private long nbHttp2xx;
	private long nbHttp3xx;
	private long nbHttp4xx;
	private long nbHttp5xx;
	private long nbHttpErr;
	private long nbSamples;

	public void setNbHttpXXX(String codeFirstChar, long count){
		switch (codeFirstChar.charAt(0)) {
			case '1':this.nbHttp1xx=count;break;
			case '2':this.nbHttp2xx=count;break;
			case '3':this.nbHttp3xx=count;break;
			case '4':this.nbHttp4xx=count;break;
			case '5':this.nbHttp5xx=count;break;
			default: this.nbHttpErr=count;break;
		}
		nbSamples=nbHttp1xx+nbHttp2xx+nbHttp3xx+nbHttp4xx+nbHttp5xx+nbHttpErr;
	}
	
	public long getNbHttp1xx() {return nbHttp1xx;}
	public long getNbHttp2xx() {return nbHttp2xx;}
	public long getNbHttp3xx() {return nbHttp3xx;}
	public long getNbHttp4xx() {return nbHttp4xx;}
	public long getNbHttp5xx() {return nbHttp5xx;}
	public long getNbHttpErr() {return nbHttpErr;}
	
	public float getHttp1xxRatio() {return (nbHttp1xx/nbSamples)*100;}
	public float getHttp2xxRatio() {return (nbHttp2xx/nbSamples)*100;}
	public float getHttp3xxRatio() {return (nbHttp3xx/nbSamples)*100;}
	public float getHttp4xxRatio() {return (nbHttp4xx/nbSamples)*100;}
	public float getHttp5xxRatio() {return (nbHttp5xx/nbSamples)*100;}
	public float getHttpErrRatio() {return (nbHttpErr/nbSamples)*100;}
	
	@Override
	public String toString() {
		return "SAMPLES["+nbSamples+"] 1xx["+nbHttp1xx+"] 2xx["+nbHttp2xx+"] 3xx["+nbHttp3xx+"] 4xx["+nbHttp4xx+"] 5xx["+nbHttp5xx+"] Err["+nbHttpErr+"]";
	}
}
