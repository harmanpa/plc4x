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
package org.apache.plc4x.java.api.messages.specific;

import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.messages.items.WriteRequestItem;
import org.apache.plc4x.java.api.messages.items.WriteResponseItem;
import org.apache.plc4x.java.api.types.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class TypeSafePlcWriteResponseTest {
    WriteResponseItem<String> writeResponseItemString;

    @BeforeEach
    void setUp() {
        writeResponseItemString = new WriteResponseItem<>(mock(WriteRequestItem.class), ResponseCode.OK);
    }

    @Test
    void constuctor() {
        new TypeSafePlcWriteResponse<>(mock(TypeSafePlcWriteRequest.class), writeResponseItemString);
        new TypeSafePlcWriteResponse<>(mock(TypeSafePlcWriteRequest.class), Collections.singletonList(writeResponseItemString));
    }

    @Test
    void of() {
        TypeSafePlcWriteResponse.of(mock(PlcWriteResponse.class, RETURNS_DEEP_STUBS));
    }

    @Test
    void getRequest() {
        new TypeSafePlcWriteResponse<>(mock(TypeSafePlcWriteRequest.class), Collections.emptyList()).getRequest();
    }

    @Test
    void getResponseItems() {
        new TypeSafePlcWriteResponse<>(mock(TypeSafePlcWriteRequest.class), Collections.emptyList()).getResponseItems();
    }

    @Test
    void getResponseItem() {
        new TypeSafePlcWriteResponse<>(mock(TypeSafePlcWriteRequest.class), Collections.emptyList()).getResponseItem();
    }

}