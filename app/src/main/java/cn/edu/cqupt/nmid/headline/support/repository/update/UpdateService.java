package cn.edu.cqupt.nmid.headline.support.repository.update;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by leon on 2/16/15.
 */
@Deprecated
public interface UpdateService {

  String END_POINT = "http://115.29.139.53";
  @GET("/apk/checkforupdate.php") UpdateResult checkforupdate(
      @Query("versionname") String versionname);
}
