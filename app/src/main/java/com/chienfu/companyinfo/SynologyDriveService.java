package com.chienfu.companyinfo;

/**
 * Created by isenw on 2023/2/24.
 *在這個示例中，我們定義了一個名為SynologyDriveService的interface，
 並且定義了一個名為uploadFile的API請求，
 該請求會使用@Multipart和@POST注解。在這個API請求中，
 我們定義了五個參數：accessToken、action、path、fileName和fileContent，
 這些參數將會被提交到遠端伺服器。
 */

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SynologyDriveService {
    @Multipart
    @POST("file_upload")
    Call<UploadResponse> uploadFile(@Part("access_token") RequestBody accessToken,
                                    @Part("action") RequestBody action,
                                    @Part("path") RequestBody path,
                                    @Part("file_name") RequestBody fileName,
                                    @Part("file_content") RequestBody fileContent);
}

