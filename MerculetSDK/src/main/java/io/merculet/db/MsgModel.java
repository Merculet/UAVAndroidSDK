/**
 * ************************************************************
 * An open source analytics android sdk for mobile applications
 * ************************************************************
 *
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
package io.merculet.db;

import java.util.ArrayList;
import java.util.List;

public class MsgModel {

    //50条以内的数据（1-50）
    private List<String> dataList;
    private List<String> adDataList;
    private List<String> adIdList;
    //所有数据对于的数据库中的ID，用于删除
    private List<String> idList;

    MsgModel() {
        idList = new ArrayList<String>();
        dataList = new ArrayList<String>();
        adDataList = new ArrayList<String>();
        adIdList = new ArrayList<String>();
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



    void addAdIdList(String id) {
        getAdIdList().add(id);
    }

    public List<String> getAdIdList() {
        return adIdList;
    }

    public List<String> getAdDataList() {
        return adDataList;
    }

    void addAdDataList(String adDataList) {
        getAdDataList().add(adDataList);
    }
}
