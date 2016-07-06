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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 使用OkHttpClient，所有的HTTP Client配置包括代理设置、超时设置、缓存设置。当你需要为单个call改变配置的时候，
 * clone 一个 OkHttpClient。这个api将会返回一个浅拷贝（shallow copy），你可以用来单独自定义。
 * 下面的例子中，我们让一个请求是500ms的超时、另一个是3000ms的超时。
 */
public final class PerCallSettings {
  private final OkHttpClient client = new OkHttpClient();

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("http://httpbin.org/delay/1") // This URL is served with a 1 second delay.
        .build();

    // Copy to customize OkHttp for this request.
    OkHttpClient client1 = client.newBuilder()
        .readTimeout(500, TimeUnit.MILLISECONDS)
        .build();
    try (Response response = client1.newCall(request).execute()) {
      System.out.println("Response 1 succeeded: " + response);
    } catch (IOException e) {
      System.out.println("Response 1 failed: " + e);
    }

    // Copy to customize OkHttp for this request.
    OkHttpClient client2 = client.newBuilder()
        .readTimeout(3000, TimeUnit.MILLISECONDS)
        .build();
    try (Response response = client2.newCall(request).execute()) {
      System.out.println("Response 2 succeeded: " + response);
    } catch (IOException e) {
      System.out.println("Response 2 failed: " + e);
    }
  }

  public static void main(String... args) throws Exception {
    new PerCallSettings().run();
  }
}
