/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

 package org.whispersystems.textsecuregcm.configuration;

 import static org.mockito.ArgumentMatchers.any;
 import static org.mockito.Mockito.doAnswer;
 import static org.mockito.Mockito.mock;
 import static org.mockito.Mockito.when;

 import com.fasterxml.jackson.annotation.JsonTypeName;
 import io.lettuce.core.api.StatefulRedisConnection;
 import io.lettuce.core.api.async.RedisAsyncCommands;
 import io.lettuce.core.api.sync.RedisCommands;
 import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
 import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
 import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
 import io.lettuce.core.resource.ClientResources;
 import java.util.function.Consumer;
 import java.util.function.Function;
 import org.whispersystems.textsecuregcm.redis.FaultTolerantPubSubConnection;
 import org.whispersystems.textsecuregcm.redis.FaultTolerantRedisClient;

 @JsonTypeName("mock")
 public class MockFaultTolerantRedisClientFactory implements FaultTolerantRedisClientFactory {

   @Override
   public FaultTolerantRedisClient build(final String name, final ClientResources clientResources) {
     return createMockRedisClient();
   }

   @SuppressWarnings("unchecked")
   private static FaultTolerantRedisClient createMockRedisClient() {
     final FaultTolerantRedisClient client = mock(FaultTolerantRedisClient.class);

     // Mock regular connections
     final StatefulRedisConnection<String, String> stringConnection = mock(StatefulRedisConnection.class);
     final StatefulRedisConnection<byte[], byte[]> binaryConnection = mock(StatefulRedisConnection.class);

     final RedisCommands<String, String> stringCommands = mock(RedisCommands.class);
     final RedisAsyncCommands<String, String> stringAsyncCommands = mock(RedisAsyncCommands.class);
     final RedisCommands<byte[], byte[]> binaryCommands = mock(RedisCommands.class);
     final RedisAsyncCommands<byte[], byte[]> binaryAsyncCommands = mock(RedisAsyncCommands.class);

     when(stringConnection.sync()).thenReturn(stringCommands);
     when(stringConnection.async()).thenReturn(stringAsyncCommands);
     when(binaryConnection.sync()).thenReturn(binaryCommands);
     when(binaryConnection.async()).thenReturn(binaryAsyncCommands);

     // Mock PubSub connections
     final StatefulRedisPubSubConnection<String, String> stringPubSubConnection = mock(StatefulRedisPubSubConnection.class);
     final StatefulRedisPubSubConnection<byte[], byte[]> binaryPubSubConnection = mock(StatefulRedisPubSubConnection.class);

     final RedisPubSubCommands<String, String> stringPubSubCommands = mock(RedisPubSubCommands.class);
     final RedisPubSubAsyncCommands<String, String> stringPubSubAsyncCommands = mock(RedisPubSubAsyncCommands.class);
     final RedisPubSubCommands<byte[], byte[]> binaryPubSubCommands = mock(RedisPubSubCommands.class);
     final RedisPubSubAsyncCommands<byte[], byte[]> binaryPubSubAsyncCommands = mock(RedisPubSubAsyncCommands.class);

     when(stringPubSubConnection.sync()).thenReturn(stringPubSubCommands);
     when(stringPubSubConnection.async()).thenReturn(stringPubSubAsyncCommands);
     when(binaryPubSubConnection.sync()).thenReturn(binaryPubSubCommands);
     when(binaryPubSubConnection.async()).thenReturn(binaryPubSubAsyncCommands);

     final FaultTolerantPubSubConnection<String, String> faultTolerantStringPubSubConnection = mock(FaultTolerantPubSubConnection.class);
     final FaultTolerantPubSubConnection<byte[], byte[]> faultTolerantBinaryPubSubConnection = mock(FaultTolerantPubSubConnection.class);

     // Wire up the regular connection methods
     when(client.withConnection(any(Function.class))).thenAnswer(invocation -> {
       return invocation.getArgument(0, Function.class).apply(stringConnection);
     });

     doAnswer(invocation -> {
       invocation.getArgument(0, Consumer.class).accept(stringConnection);
       return null;
     }).when(client).useConnection(any(Consumer.class));

     when(client.withBinaryConnection(any(Function.class))).thenAnswer(invocation -> {
       return invocation.getArgument(0, Function.class).apply(binaryConnection);
     });

     doAnswer(invocation -> {
       invocation.getArgument(0, Consumer.class).accept(binaryConnection);
       return null;
     }).when(client).useBinaryConnection(any(Consumer.class));

     // Wire up the PubSub connection methods
     when(client.createPubSubConnection()).thenReturn(faultTolerantStringPubSubConnection);
     when(client.createBinaryPubSubConnection()).thenReturn(faultTolerantBinaryPubSubConnection);

     when(faultTolerantStringPubSubConnection.withPubSubConnection(any(Function.class))).thenAnswer(invocation -> {
       return invocation.getArgument(0, Function.class).apply(stringPubSubConnection);
     });

     doAnswer(invocation -> {
       invocation.getArgument(0, Consumer.class).accept(stringPubSubConnection);
       return null;
     }).when(faultTolerantStringPubSubConnection).usePubSubConnection(any(Consumer.class));

     when(faultTolerantBinaryPubSubConnection.withPubSubConnection(any(Function.class))).thenAnswer(invocation -> {
       return invocation.getArgument(0, Function.class).apply(binaryPubSubConnection);
     });

     doAnswer(invocation -> {
       invocation.getArgument(0, Consumer.class).accept(binaryPubSubConnection);
       return null;
     }).when(faultTolerantBinaryPubSubConnection).usePubSubConnection(any(Consumer.class));

     return client;
   }
 }
