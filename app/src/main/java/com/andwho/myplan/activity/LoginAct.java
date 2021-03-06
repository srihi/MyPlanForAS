package com.andwho.myplan.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.andwho.myplan.R;
import com.andwho.myplan.model.UserInfo;
import com.andwho.myplan.preference.MyPlanPreference;
import com.andwho.myplan.upgrade.UpDataUtils;
import com.andwho.myplan.utils.AndroidUtil;
import com.andwho.myplan.utils.BmobAgent;
import com.andwho.myplan.utils.ToastUtil;
import com.andwho.myplan.view.CustomEditView;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by zhouf on 2016/1/21.
 */
public class LoginAct extends BaseAct implements View.OnClickListener {

    private CustomEditView mAccountView;

    private CustomEditView mPswView;

    private Button mLoginBtn;
    private Activity myselfContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);

        myselfContext = this;
        findViews();
    }
    private void findViews(){
        mAccountView = (CustomEditView) findViewById(R.id.login_account_et);
        mAccountView.setSingleLine(true);
        mAccountView.setEditViewHint("请输入邮箱");
        mAccountView.setFocus();
        mAccountView.setDeleteBtnVisibility(true);
        mAccountView.setCustomEditViewLister(new CustomEditViewListener());
        mPswView = (CustomEditView) findViewById(R.id.login_password_et);
        mPswView.setSingleLine(true);
        mPswView.setPassWord();
        mPswView.setEditViewHint("请输入密码");
        mPswView.setDeleteBtnVisibility(true);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(this);
        findViewById(R.id.tv_forget).setOnClickListener(this);
        findViewById(R.id.tv_sign).setOnClickListener(this);
        findViewById(R.id.tv_tips).setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_login:
                LoginIn();
                break;
            case R.id.tv_sign:
                IntentHelper.showSign(myselfContext);
                break;
            case R.id.tv_forget:
                IntentHelper.showFindPsw(myselfContext);
                break;
            default:
                break;
        }
    }

    /**
     * 输入框监听
     */
    private class CustomEditViewListener implements CustomEditView.CustomEditViewListner {
        @Override
        public void onDownBtnClick() {
            showAndHideAccoutsPopup();
        }

        @Override
        public void onFuzzyMatch(Editable s, boolean autoCompleteFlag) {

        }

        @Override
        public void onOtherBtnClick() {
        }
    }
    /**
     * 显示或隐藏弹框
     */
    private void showAndHideAccoutsPopup() {
        AndroidUtil.hideKeyboard(this);
//        new ShowOrHidePopwindow(getAutoCancelController()).executeOnExecutor(AppApplication.app.getLocalDataExcutor());
    }


    public void LoginIn(){
        if(TextUtils.isEmpty(mAccountView.getEditViewContent())){
            ToastUtil.showShortToast(this, "邮箱不能为空！");
            return;
        }
        if(TextUtils.isEmpty(mPswView.getEditViewContent())){
            ToastUtil.showShortToast(this, "密码不能为空！");
            return;
        }
        if(!emailValidation(mAccountView.getEditViewContent())){
            ToastUtil.showShortToast(this, getResources().getString(R.string.str_Register_Tips3));
            return;
        }

//        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                cancel();
//            }
//        });
        showProgressDialog(null,true,false);
        BmobAgent.checkUser(this, mAccountView.getEditViewContent(), new FindListener<BmobUser>() {
            @Override
            public void onSuccess(List<BmobUser> list) {
                if (null != list && list.size() > 0&& list.size()==1) {//因为，如果查询字段传null空值时，查出来是整张表
                    final String userId=list.get(0).getObjectId().toString();
                    //是否验证了邮箱
                    if (list.get(0).getEmailVerified()) {
                        BmobAgent.loginIn(LoginAct.this, mAccountView.getEditViewContent(), mPswView.getEditViewContent(), new SaveListener() {
                            @Override
                            public void onSuccess() {
//                                MyPlanPreference.getInstance(myselfContext).setUsername(mAccountView.getEditViewContent());
//                                MyPlanPreference.getInstance(myselfContext).setUserId(userId);

                                UserInfo userInfo=new UserInfo();
                                userInfo.objectId=userId;
                                userInfo.userName=mAccountView.getEditViewContent();
                                MyPlanPreference.getInstance(myselfContext).saveObject(myselfContext,userId,userInfo);

                                new UpDataUtils().upAllPlanDate(myselfContext);
                                dismissProgressDialog();
                                finish();
//                                this.onFinish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                ToastUtil.showLongToast(LoginAct.this, s);
                                dismissProgressDialog();
                            }
                        });
                    }
                } else {
                    ToastUtil.showLongToast(LoginAct.this, getResources().getString(R.string.str_Login_Tips4));
                    dismissProgressDialog();
                }
            }

            @Override
            public void onError(int i, String s) {
                ToastUtil.showLongToast(LoginAct.this, getResources().getString(R.string.str_Register_Tips5));
                dismissProgressDialog();
            }
        });
    }

    /**
     * 验证邮箱格式是否正确
     */
    public boolean emailValidation(String email) {
        String regex = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        return email.matches(regex);
    }

}
