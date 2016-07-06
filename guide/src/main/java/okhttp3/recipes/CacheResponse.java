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

import okhttp3.*;

import java.io.File;
import java.io.IOException;

/**
 * 为了缓存响应，你需要一个你可以读写的缓存目录，和缓存大小的限制。这个缓存目录应该是私有的，不信任的程序应不能读取缓存内容。
 * 一个缓存目录同时拥有多个缓存访问是错误的。大多数程序只需要调用一次new OkHttp()，在第一次调用时配置好缓存，然后其他地方只需要调用这个实例就可以了。
 * 否则两个缓存示例互相干扰，破坏响应缓存，而且有可能会导致程序崩溃。
 * 响应缓存使用HTTP头作为配置。你可以在请求头中添加Cache-Control: max-stale=3600 ,OkHttp缓存会支持。
 * 你的服务通过响应头确定响应缓存多长时间，例如使用Cache-Control: max-age=9600。
 * <p>
 * 在这一节还提到了下面一句：
 * There are cache headers to force a cached response, force a network response, or force the network response to be validated with a conditional GET.
 * <p>
 * 我不是很懂cache，平时用到的也不多，所以把Google在Android Developers一段相关的解析放到这里吧。
 * <p>
 * Force a Network Response
 * <p>
 * In some situations, such as after a user clicks a 'refresh' button, it may be necessary to skip the cache, and fetch data directly from the server. To force a full refresh, add the no-cache directive:
 * <p>
 * connection.addRequestProperty("Cache-Control", "no-cache");
 * <p>
 * If it is only necessary to force a cached response to be validated by the server, use the more efficient max-age=0 instead:
 * <p>
 * connection.addRequestProperty("Cache-Control", "max-age=0");
 * <p>
 * Force a Cache Response
 * <p>
 * Sometimes you'll want to show resources if they are available immediately, but not otherwise. This can be used so your application can show something while waiting for the latest data to be downloaded. To restrict a request to locally-cached resources, add the only-if-cached directive:
 * <p>
 * try {
 * connection.addRequestProperty("Cache-Control", "only-if-cached");
 * InputStream cached = connection.getInputStream();
 * // the resource was cached! show it
 * catch (FileNotFoundException e) {
 * // the resource was not cached
 * }
 * }
 * <p>
 * This technique works even better in situations where a stale response is better than no response. To permit stale cached responses, use the max-stale directive with the maximum staleness in seconds:
 * <p>
 * int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
 * connection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
 */
public final class CacheResponse {
  private final OkHttpClient client;

  public CacheResponse(File cacheDirectory) throws Exception {
    int cacheSize = 10 * 1024 * 1024; // 10 MiB
    Cache cache = new Cache(cacheDirectory, cacheSize);

    client = new OkHttpClient.Builder()
        .cache(cache)
        .build();
  }

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("http://publicobject.com/helloworld.txt")
        .build();

    String response1Body;
    try (Response response1 = client.newCall(request).execute()) {
      if (!response1.isSuccessful()) throw new IOException("Unexpected code " + response1);

      response1Body = response1.body().string();
      System.out.println("Response 1 response:          " + response1);
      System.out.println("Response 1 cache response:    " + response1.cacheResponse());
        System.out.println("Response 1 network response:  " + response1.networkResponse());
    }

      request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();//强制使用网络
      request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();//强制使用缓存
    String response2Body;
    try (Response response2 = client.newCall(request).execute()) {
      if (!response2.isSuccessful()) throw new IOException("Unexpected code " + response2);

      response2Body = response2.body().string();
      System.out.println("Response 2 response:          " + response2);
      System.out.println("Response 2 cache response:    " + response2.cacheResponse());
      System.out.println("Response 2 network response:  " + response2.networkResponse());
    }

    System.out.println("Response 2 equals Response 1? " + response1Body.equals(response2Body));
  }

  public static void main(String... args) throws Exception {
    new CacheResponse(new File("CacheResponse.tmp")).run();
  }
}
