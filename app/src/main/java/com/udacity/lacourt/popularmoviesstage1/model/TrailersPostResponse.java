package com.udacity.lacourt.popularmoviesstage1.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailersPostResponse {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("results")
@Expose
private List<Trailer> Trailers = null;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public List<Trailer> getTrailers() {
return Trailers;
}

public void setTrailers(List<Trailer> Trailers) {
this.Trailers = Trailers;
}

}