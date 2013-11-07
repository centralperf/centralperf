package org.centralperf.model.graphs;

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
		if(codeFirstChar.length() > 0){
			switch (codeFirstChar.charAt(0)) {
				case '1':this.nbHttp1xx=count;break;
				case '2':this.nbHttp2xx=count;break;
				case '3':this.nbHttp3xx=count;break;
				case '4':this.nbHttp4xx=count;break;
				case '5':this.nbHttp5xx=count;break;
				default: this.nbHttpErr=count;break;
			}
		}
		nbSamples=nbHttp1xx+nbHttp2xx+nbHttp3xx+nbHttp4xx+nbHttp5xx+nbHttpErr;
	}
	
	public long getNbHttp1xx() {return this.nbHttp1xx;}
	public long getNbHttp2xx() {return this.nbHttp2xx;}
	public long getNbHttp3xx() {return this.nbHttp3xx;}
	public long getNbHttp4xx() {return this.nbHttp4xx;}
	public long getNbHttp5xx() {return this.nbHttp5xx;}
	public long getNbHttpErr() {return this.nbHttpErr;}
	public long getNbSamples() {return this.nbSamples;}
	
	public float getHttp1xxRatio() {return (this.nbHttp1xx*100/this.nbSamples);}
	public float getHttp2xxRatio() {return (this.nbHttp2xx*100/this.nbSamples);}
	public float getHttp3xxRatio() {return (this.nbHttp3xx*100/this.nbSamples);}
	public float getHttp4xxRatio() {return (this.nbHttp4xx*100/this.nbSamples);}
	public float getHttp5xxRatio() {return (this.nbHttp5xx*100/this.nbSamples);}
	public float getHttpErrRatio() {return (this.nbHttpErr*100/this.nbSamples);}
	
	@Override
	public String toString() {
		return "SAMPLES["+this.nbSamples+"] 1xx["+this.nbHttp1xx+"] 2xx["+this.nbHttp2xx+"] 3xx["+this.nbHttp3xx+"] 4xx["+this.nbHttp4xx+"] 5xx["+this.nbHttp5xx+"] Err["+this.nbHttpErr+"]";
	}
}
