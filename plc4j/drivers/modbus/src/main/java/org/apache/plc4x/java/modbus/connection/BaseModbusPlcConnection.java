/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */
package org.apache.plc4x.java.modbus.connection;

import org.apache.commons.lang3.StringUtils;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.spi.connection.ChannelFactory;
import org.apache.plc4x.java.spi.connection.NettyPlcConnection;
import org.apache.plc4x.java.modbus.util.ModbusPlcFieldHandler;
import org.apache.plc4x.java.spi.messages.DefaultPlcReadRequest;
import org.apache.plc4x.java.spi.messages.DefaultPlcWriteRequest;
import org.apache.plc4x.java.spi.messages.InternalPlcReadRequest;
import org.apache.plc4x.java.spi.messages.InternalPlcReadResponse;
import org.apache.plc4x.java.spi.messages.InternalPlcWriteRequest;
import org.apache.plc4x.java.spi.messages.InternalPlcWriteResponse;
import org.apache.plc4x.java.spi.messages.PlcReader;
import org.apache.plc4x.java.spi.messages.PlcRequestContainer;
import org.apache.plc4x.java.spi.messages.PlcWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class BaseModbusPlcConnection extends NettyPlcConnection implements PlcReader, PlcWriter {

    private static final Logger logger = LoggerFactory.getLogger(BaseModbusPlcConnection.class);

    // This slaveId defaults to 0 which is a broadcast.
    private short slaveId = 0;

    BaseModbusPlcConnection(ChannelFactory channelFactory, String params) {
        super(channelFactory);

        if (!StringUtils.isEmpty(params)) {
            for (String param : params.split("&")) {
                String[] paramElements = param.split("=");
                String paramName = paramElements[0];
                if (paramElements.length == 2) {
                    String paramValue = paramElements[1];
                    switch (paramName) {
                        case "slaveId": {
                            slaveId = Short.parseShort(paramValue);
                        }
                        default:
                            logger.debug("Unknown parameter {} with value {}", paramName, paramValue);
                    }
                } else {
                    logger.debug("Unknown no-value parameter {}", paramName);
                }
            }
        }
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public PlcReadRequest.Builder readRequestBuilder() {
        return new DefaultPlcReadRequest.Builder(this, new ModbusPlcFieldHandler());
    }

    @Override
    public PlcWriteRequest.Builder writeRequestBuilder() {
        return new DefaultPlcWriteRequest.Builder(this, new ModbusPlcFieldHandler());
    }

    @Override
    public CompletableFuture<PlcReadResponse> read(PlcReadRequest readRequest) {
        CompletableFuture<InternalPlcReadResponse> future = new CompletableFuture<>();
        PlcRequestContainer<InternalPlcReadRequest, InternalPlcReadResponse> container =
            new PlcRequestContainer<>((InternalPlcReadRequest) readRequest, future);
        channel.writeAndFlush(container).addListener(f -> {
            if (!f.isSuccess()) {
                future.completeExceptionally(f.cause());
            }
        });
        return future
            .thenApply(PlcReadResponse.class::cast);
    }

    @Override
    public CompletableFuture<PlcWriteResponse> write(PlcWriteRequest writeRequest) {
        CompletableFuture<InternalPlcWriteResponse> future = new CompletableFuture<>();
        PlcRequestContainer<InternalPlcWriteRequest, InternalPlcWriteResponse> container =
            new PlcRequestContainer<>((InternalPlcWriteRequest) writeRequest, future);
        channel.writeAndFlush(container).addListener(f -> {
            if (!f.isSuccess()) {
                future.completeExceptionally(f.cause());
            }
        });
        return future
            .thenApply(PlcWriteResponse.class::cast);
    }

    public short getSlaveId() {
        return slaveId;
    }

}
