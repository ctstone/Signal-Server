/*
 * Copyright 2024 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

 package org.whispersystems.textsecuregcm.configuration;

 import com.fasterxml.jackson.annotation.JsonTypeName;
 import io.lettuce.core.resource.ClientResources;
 import org.whispersystems.textsecuregcm.redis.FaultTolerantRedisClusterClient;
 import org.whispersystems.textsecuregcm.tests.util.RedisClusterHelper;

 @JsonTypeName("mock")
 public class MockFaultTolerantRedisClusterFactory implements FaultTolerantRedisClusterFactory {

   @Override
   public FaultTolerantRedisClusterClient build(final String name, final ClientResources.Builder clientResourcesBuilder) {
     return RedisClusterHelper.builder().build();
   }
 }
