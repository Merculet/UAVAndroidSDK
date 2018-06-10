/*
 * @author Aaron.Liu
 * @Copyright (c) , Magic Window Limited Liability Company
 * @Since 2016.
 * <p/>
 * *****************************************************
 * This project is available under the following license
 * *****************************************************
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.merculet.uav.sdk.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by Aaron.Liu on 16/8/10.
 */
public class ProtoRequest extends Request {

    private final byte[] mRequestBody;

    /**
     * @param method
     * @param url
     * @param listener
     */

    public ProtoRequest(HttpMethod method, String url, byte[] requestBody, ResponseListener<InputStream> listener) {
        super(method, url, listener);

        mRequestBody = requestBody;
    }

    @Override
    public byte[] getBody() {
        return mRequestBody;
    }

    /**
     * 处理Response,该方法运行在UI线程.
     *
     * @param content
     */
    @Override
    public void deliveryResponse(byte[] content) {
        super.deliveryResponse(content);
    }

    private InputStream parseByte(byte[] data) {
        if (data == null) {
            return null;
        }
        return new BufferedInputStream(new ByteArrayInputStream(data));
    }
}
