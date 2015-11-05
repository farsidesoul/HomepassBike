package au.com.bfbapps.homepassbike.service;

import java.util.List;

import au.com.bfbapps.homepassbike.model.BikeLocation;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;

// resource/tdvh-n9dv.json

// X-App-Token: YJwkPWoukPap3ZwErmwkinzyG

public interface MelbourneBikeService {
	@Headers("X-App-Token: YJwkPWoukPap3ZwErmwkinzyG")
	@GET("https://data.melbourne.vic.gov.au/resource/qnjw-wgaj.json")
	Call<List<BikeLocation>> listLocations();
}
