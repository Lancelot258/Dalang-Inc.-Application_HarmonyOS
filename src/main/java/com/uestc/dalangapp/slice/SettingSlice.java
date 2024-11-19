package com.uestc.dalangapp.slice;



import com.uestc.dalangapp.ResourceTable;
import com.uestc.dalangapp.slice.MangePageAbilitySlice;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;


public class SettingSlice extends AbilitySlice implements Component.ClickedListener {
    Text textTip;
    Button button_log;
    Button button_exit;
    TextField textField_name;
    TextField  textField_password;
    SettingSlice _this =this;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_setting_slice);
        // 当点击登录，改变相应组件的样式
        button_log = (Button) findComponentById(ResourceTable.Id_ensure_button);
        button_exit = (Button) findComponentById(ResourceTable.Id_exit_button);

        button_log.setTouchFocusable(true);

        textTip = (Text) findComponentById(ResourceTable.Id_error_tip_text);
        textTip.setVisibility(Component.INVISIBLE);

        textField_name = (TextField) findComponentById(ResourceTable.Id_name_textField);
        textField_name.setText("");
        textField_name.setHint("输入管理账号");
        textField_password = (TextField) findComponentById(ResourceTable.Id_password_text_field);
        textField_password.setText("");
        textField_password.setHint("输入密码");

        button_log.setClickedListener(this);
        button_exit.setClickedListener(this);

        textField_name.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if(textField_name.getText().length()>0){
                    textTip.setVisibility(Component.INVISIBLE);
                }
            }
        });
    }
    @Override
    public void onClick(Component component) {

        textField_name = (TextField) findComponentById(ResourceTable.Id_name_textField);
        String user_name = textField_name.getText();
        textField_password = (TextField) findComponentById(ResourceTable.Id_password_text_field);
        String  user_password= textField_password.getText();

        if(component==button_log)
        {
            if(user_name.compareTo("dalang")==0 && user_password.compareTo("123")==0 )
            {
                Intent i =new Intent();
                //present(new PageCategoryManageAbilitySlice(),i);
                MangePageAbilitySlice managePages=new MangePageAbilitySlice();
                present(managePages,i);
            }
            else
            {
                // 显示错误提示的Text
                textTip = (Text) findComponentById(ResourceTable.Id_error_tip_text);
                textTip.setVisibility(Component.VISIBLE);
                // 显示TextField错误状态下的样式
                ShapeElement errorElement = new ShapeElement(this, ResourceTable.Graphic_background_text_field_error);
                textField_name.setBackground(errorElement);
                // TextField失去焦点
                textField_name.clearFocus();
            }
        }
        else if(component==button_exit)
        {

            Intent i = new Intent();
            present(new MainAbilitySlice(),i);
        }else  if(component==textField_name)
        {
            textField_name.setHint("");
        }

    }

    @Override
    public void onActive() {

        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}