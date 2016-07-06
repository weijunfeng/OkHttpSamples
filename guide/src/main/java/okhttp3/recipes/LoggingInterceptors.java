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

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.logging.Logger;

public final class LoggingInterceptors {
  private static final Logger logger = Logger.getLogger(LoggingInterceptors.class.getName());
  private final OkHttpClient client = new OkHttpClient.Builder()
          //应用拦截器
//          * 不必要担心响应和重定向之间的中间响应。
//            *通常只调用一次，即使HTTP响应是通过缓存提供的。
//            *遵从应用层的最初目的。与OkHttp的注入头部无关，如If-None-Match。
//            *允许短路而且不调用Chain.proceed()。
//            *允许重试和多次调用Chain.proceed()。

            .addInterceptor(new LoggingInterceptor())
          //    网络拦截器
//        *允许像重定向和重试一样操作中间响应。
//            *网络发生短路时不调用缓存响应。
//            *在数据被传递到网络时观察数据。
//            *有权获得装载请求的连接。

//      .addNetworkInterceptor(new LoggingInterceptor())
      .build();

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("http://publicobject.com/helloworld.txt")
        .build();

    Response response = client.newCall(request).execute();
    response.body().close();
  }

  private static class LoggingInterceptor implements Interceptor {
    @Override public Response intercept(Chain chain) throws IOException {
      long t1 = System.nanoTime();
      Request request = chain.request();
      logger.info(String.format("Sending request %s on %s%n%s",
          request.url(), chain.connection(), request.headers()));
      Response response = chain.proceed(request);

      long t2 = System.nanoTime();
//      TimeUnit.NANOSECONDS.toMillis(t2 - t1)
      logger.info(String.format("Received response for %s in %.1fms%n%s",
          response.request().url(), (t2 - t1) / 1.0e6d, response.headers()));
      return response;
    }
  }

  public static void main(String... args) throws Exception {
    new LoggingInterceptors().run();
  }
}
