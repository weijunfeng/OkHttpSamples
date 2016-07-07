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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.HttpDate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 典型的HTTP头 像是一个 Map<String, String> :每个字段都有一个或没有值。但是一些头允许多个值，像Guava的Multimap。例如：HTTP响应里面提供的Vary响应头，就是多值的。OkHttp的api试图让这些情况都适用。
 * 当写请求头的时候，使用header(name, value)可以设置唯一的name、value。如果已经有值，旧的将被移除，然后添加新的。使用addHeader(name, value)可以添加多值（添加，不移除已有的）。
 * 当读取响应头时，使用header(name)返回最后出现的name、value。通常情况这也是唯一的name、value。如果没有值，那么header(name)将返回null。如果想读取字段对应的所有值，使用headers(name)会返回一个list。
 * 为了获取所有的Header，Headers类支持按index访问
 */
public final class AccessHeaders {
  private final OkHttpClient client = new OkHttpClient();

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("https://api.github.com/repos/square/okhttp/issues")
        .header("User-Agent", "OkHttp Headers.java")
        .addHeader("Accept", "application/json; q=0.5")
        .addHeader("Accept", "application/vnd.github.v3+json")
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

      System.out.println("Server: " + response.header("Server"));
//      String data = response.header("Date");
//      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
//      Date parse = simpleDateFormat.parse(data);
//      System.out.println("date" + parse.toString());

      String stringDate = "Tue, 05 Jul 2016 13:27:18 GMT";
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
      Date date = sdf.parse(stringDate);
      System.out.println("date " + date.toString());

      System.out.println("Date5: " + HttpDate.parse(response.header("Date")));
      System.out.println("Date4: " + HttpDate.format(new Date(1467869347000L)));
      System.out.println("Date3: " + response.headers().getDate("Date").getTime());

      System.out.println("Date: " + response.header("Date"));
      System.out.println("Date1: " + response.header("Expires"));
      System.out.println("Date2: " + response.header("Last-Modified"));
      System.out.println("Vary: " + response.headers("Vary"));
    }
  }

  public static void main(String... args) throws Exception {
    new AccessHeaders().run();
  }
}
