package cn.magicwindow.uav.sdk.domain;

import android.text.TextUtils;

import cn.magicwindow.uav.sdk.util.JSONUtils;
import cn.magicwindow.uav.sdk.util.StringUtils;

/**
 * 用户信息
 *
 * @author aaron
 * @date 15/9/1
 */
public class UserProfile {

    public String profileId;//用户唯一标识
    public String phone; //手机号
    public String displayName; //用户名
    public String gender = "0"; //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
    public String birthday; //生日
    public String email; //邮箱
    public String country; //国家
    public String province;//省
    public String city;//城市
    public String remark;//备注
    public String geo;//地理位置
    public String userRank;//用户级别

    public UserProfile(String userId) {
        this.profileId = userId;

        setTelephone(userId);
        setEmail(userId);
    }

    public UserProfile setUserId(String userId) {
        this.profileId = userId.trim();
        return this;
    }

    public UserProfile setTelephone(String telPhone) {
        if (StringUtils.checkPhoneNum(telPhone)) {
            this.phone = StringUtils.formatPhoneNum(telPhone);
        }
        return this;
    }

    public UserProfile setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public UserProfile setGender(String gender) {
        this.gender = gender.trim();
        return this;
    }

    public UserProfile setBirthday(String birthday) {
        if (!TextUtils.isEmpty(birthday) && !TextUtils.isEmpty(birthday.trim()) && birthday.trim().length() > 18) {
            //获取前十八位
            birthday = birthday.trim().substring(0, 18);
        }
        this.birthday = birthday;
        return this;

    }

    public UserProfile setEmail(String email) {
        if (StringUtils.checkEmail(email)) {
            this.email = email.trim();
        }
        return this;

    }

    public UserProfile setCountry(String country) {
        this.country = country.trim();
        return this;
    }

    public UserProfile setProvince(String province) {
        this.province = province.trim();
        return this;
    }

    public UserProfile setCity(String city) {
        this.city = city.trim();
        return this;
    }

    public UserProfile setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public UserProfile setGeo(String geo) {
        this.geo = geo;
        return this;
    }


    public UserProfile setUserRank(String userRank) {
        this.userRank = userRank;
        return this;
    }


    @Override
    public String toString() {
        return JSONUtils.objectToJsonString(this);
    }
}
