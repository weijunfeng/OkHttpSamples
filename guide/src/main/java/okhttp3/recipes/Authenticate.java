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

import java.io.IOException;

/**
 * 这部分和HTTP AUTH有关。
 * 相关资料：HTTP AUTH 那些事 - 王绍全的博客 - 博客频道 - CSDN.NET
 * <p>
 * OkHttp会自动重试未验证的请求。当响应是401 Not Authorized时，Authenticator会被要求提供证书。Authenticator的实现中需要建立一个新的包含证书的请求。如果没有证书可用，返回null来跳过尝试。
 * public List<Challenge> challenges()
 * Returns the authorization challenges appropriate for this response's code. If the response code is 401 unauthorized,
 * this returns the "WWW-Authenticate" challenges. If the response code is 407 proxy unauthorized, this returns the "Proxy-Authenticate" challenges.
 * Otherwise this returns an empty list of challenges.
 * <p>
 * 当需要实现一个Basic challenge， 使用Credentials.basic(username, password)来编码请求头。
 * <p>
 * OkHttp can automatically retry unauthenticated requests. When a response is  401 Not Authorized , an  Authenticator
 * is asked to supply credentials. Implementations should build a new request that includes the missing credentials.
 * If no credentials are available, return null to skip the retry.
 * <p>
 * Use  Response.challenges()  to get the schemes and realms of any authentication challenges. When fulfilling a  Basic
 * challenge, use  Credentials.basic(username, password)  to encode the request header.
 *
 * <p>
 * To avoid making many retries when authentication isn't working, you can return null to give up. For example,
 * you may want to skip the retry when these exact credentials have already been attempted:
 * <p>
 * if (credential.equals(response.request().header("Authorization"))) {
 * return null; // If we already failed with these credentials, don't retry.
 * }
 * <p>
 * You may also skip the retry when you’ve hit an application-defined attempt limit:
 * <p>
 * if (responseCount(response) >= 3) {
 * return null; // If we've failed 3 times, give up.
 * }
 * <p>
 * This above code relies on this  responseCount()  method:
 * <p>
 * private int responseCount(Response response) {
 * int result = 1;
 * while ((response = response.priorResponse()) != null) {
 * result++;
 * }
 * return result;
 * }
 */
public final class Authenticate {
  private final OkHttpClient client;

  public Authenticate() {
    client = new OkHttpClient.Builder()
        .authenticator(new Authenticator() {
          @Override public Request authenticate(Route route, Response response) throws IOException {
            System.out.println("Authenticating for response: " + response);
            System.out.println("Challenges: " + response.challenges());
            String credential = Credentials.basic("jesse", "password1");
            return response.request().newBuilder()
                .header("Authorization", credential)
                .build();
          }
        })
        .build();
  }

  public void run() throws Exception {
    Request request = new Request.Builder()
        .url("http://publicobject.com/secrets/hellosecret.txt")
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

      System.out.println(response.body().string());
    }
  }

  public static void main(String... args) throws Exception {
    new Authenticate().run();
  }
}
