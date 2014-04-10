package centralperf

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._
import com.excilys.ebi.gatling.http.Headers.Names._
import akka.util.duration._
import bootstrap._

/**
* Sample Gatling Scala based simulation for CentralPerf
*/
class GatlingCPSampleSimulation extends Simulation {

	val httpConf = httpConfig
		.baseURL("http://www.google.fr")
		.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
		.disableFollowRedirect

	val headers_1 = Map(
		"Keep-Alive" -> "115")

	val scn = scenario("Central Perf")
		.exec(
			http("google_home_page")
				.get("/")
				.headers(headers_1)
				.check(status.is(200)))
		.pause(5 seconds)
		.repeat(5) {
			exec(
				http("google_search_cp")
					.get("/search?q=centralperf")
					.headers(headers_1))
			.pause(5 seconds)
		}

	setUp(scn.users(5).ramp(10).protocolConfig(httpConf))
}
