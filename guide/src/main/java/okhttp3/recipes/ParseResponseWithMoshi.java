/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package okhttp3.recipes;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * Gson是一个在JSON和Java对象之间转换非常方便的api。这里我们用Gson来解析Github API的JSON响应。
 * 注意：ResponseBody.charStream()使用响应头Content-Type指定的字符集来解析响应体。默认是UTF-8。
 */
public final class ParseResponseWithMoshi {
  private final OkHttpClient client = new OkHttpClient();
  private final Moshi moshi = new Moshi.Builder().build();
  private final JsonAdapter<Gist> gistJsonAdapter = moshi.adapter(Gist.class);

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("https://api.github.com/gists/c2a7c39532239ff261be")
        .build();
    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

      Gist gist = gistJsonAdapter.fromJson(response.body().source());

      for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
        System.out.println(entry.getKey());
        System.out.println(entry.getValue().content);
      }
    }
  }

  static class Gist {
    Map<String, GistFile> files;
  }

  static class GistFile {
    String content;
  }

  public static void main(String... args) throws Exception {
    new ParseResponseWithMoshi().run();
  }
}
