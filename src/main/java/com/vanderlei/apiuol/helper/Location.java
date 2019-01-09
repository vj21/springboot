package com.vanderlei.apiuol.helper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Location {
	private String apiTogetLocationFroIp = "https://ipvigilante.com/";
	private String apiTogetTemperature = "https://www.metaweather.com/api/location/";
	private Double minTemp;
	private Double maxTemp;
	
	public void getLocationByIp(String ipAddress) throws IOException, UnirestException{
		 JsonNode nodeLocation = execRequest(apiTogetLocationFroIp + ipAddress);

		 ObjectMapper mapper = new ObjectMapper();
		 
		 if(nodeLocation != null) {
			 com.fasterxml.jackson.databind.JsonNode dadosLocation = mapper.readTree(nodeLocation.toString());
			 
			 //set latitude and longitude
			 String latitude = dadosLocation.findPath("data").get("latitude").asText();
			 String longitude = dadosLocation.findPath("data").get("longitude").asText();
			 
			 JsonNode nodeWoeid = execRequest(apiTogetTemperature + "search/?lattlong=" + latitude + "," + longitude);
			 com.fasterxml.jackson.databind.JsonNode dadosWoeid = mapper.readTree(nodeWoeid.toString());			 
			 String woeid = dadosWoeid.get(0).findPath("woeid").asText();
			 
			 DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			 Date date = new Date();
			 
			 //JsonNode nodeMinMaxTemp = execRequest(apiTogetTemperature + woeid + "/" + dateFormat.format(date));
			 JsonNode nodeMinMaxTemp  = execRequest(apiTogetTemperature + woeid + "/" + dateFormat.format(date));
			 com.fasterxml.jackson.databind.JsonNode dadosMinMaxTemp = mapper.readTree(nodeMinMaxTemp.toString());
			 this.setMinTemp(dadosMinMaxTemp.get(0).findPath("min_temp").asDouble());
			 this.setMaxTemp(dadosMinMaxTemp.get(0).findPath("max_temp").asDouble());
		 }
	}
	
	/**
	 * Exec a get-like request from a url
	 * @param url
	 * @return JsonNode result
	 * @throws UnirestException
	 */
	public JsonNode execRequest(String url) throws UnirestException {
		HttpResponse<JsonNode> response = Unirest.get(url)
				  .header("cache-control", "max-age=2592000")//set cache for 30 days
				  .asJson();
		
		if(response.getStatus() == 200) 
			return response.getBody();
		
		return null;
	}

	public Double getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(Double minTemp) {
		this.minTemp = minTemp;
	}

	public Double getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(Double maxTemp) {
		this.maxTemp = maxTemp;
	}	
}
