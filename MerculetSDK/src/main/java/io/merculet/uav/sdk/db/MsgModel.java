/**
 * ************************************************************
 * An open source analytics android sdk for mobile applications
 * ************************************************************
 *
 * @author Magic Window Liability Company
 * @copyright Copyright 2014-2016, Magic Window Limited Liability Company
 * @since Version 1.0
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
 */
package io.merculet.uav.sdk.db;

import java.util.ArrayList;
import java.util.List;

public class MsgModel {

    //50条以内的数据（1-50）
    private List<String> dataList;
    //所有数据对于的数据库中的ID，用于删除
    private List<String> idList;
    //每条数据存的token
    private List<String> userIdList;

    MsgModel() {
        idList = new ArrayList<>();
        dataList = new ArrayList<>();
        userIdList = new ArrayList<>();
    }

    void addIdList(String id) {
        getIdList().add(id);
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }

    void addDataList(String data) {
        getDataList().add(data);
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    void addUserIdList(String userId) {
        getUserIdList().add(userId);
    }

    public List<String> getUserIdList() {
        return userIdList;
    }
}
