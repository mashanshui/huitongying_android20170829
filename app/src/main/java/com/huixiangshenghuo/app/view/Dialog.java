package com.huixiangshenghuo.app.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.huixiangshenghuo.app.R;


/**
 */
public class Dialog extends android.app.Dialog {

   private final class OnCustomDialogButtonClickListener implements View.OnClickListener {
      @Override
      public void onClick(View v) {

         switch (v.getId()) {
            case R.id.ui8_wg_dialog_btn_single:
               if (null != singleClick) {
                  singleClick.onClick(Dialog.this, Dialog.BUTTON_POSITIVE);
               }
               break;
            case R.id.ui8_wg_dialog_btn1:
               if (null != cancelClick) {
                  cancelClick.onClick(Dialog.this, Dialog.BUTTON_POSITIVE);
               }
               break;
            case R.id.ui8_wg_dialog_btn2:
               if (null != confirmClick) {
                  confirmClick.onClick(Dialog.this, Dialog.BUTTON_NEGATIVE);
               }
               break;
            default:
               break;
         }

         Dialog.this.dismiss();

      }
   }

   /**
    * 按钮模式
    */
   public enum ButtonMode {
      /**
       * 一个按钮
       */
      single,
      /**
       * 确定与取消按钮
       */
      confirmAndCancel,
      /**
       * 三个按钮
       */
      three
   }

   private Resources resources;

   // 高亮
   boolean isBtnRightHighlight = false;
   boolean isBtnLeftHighlight = false;

   /**
    * 按钮模式
    */
   private ButtonMode buttonMode = ButtonMode.single;

   // 资源
   private CharSequence titleText;
   private CharSequence messageText;
   private Drawable titleIcon;
   private CharSequence singleText;
   private CharSequence confirmText;
   private CharSequence cancelText;

   // 事件
   /**
    * 用于转换的点击监听器（把View.OnClickListener转换为DialogInterface.OnClickListener）
    */
   private OnCustomDialogButtonClickListener conversionClickListener = new OnCustomDialogButtonClickListener();
   private OnClickListener singleClick = null;
   private OnClickListener confirmClick = null;
   private OnClickListener cancelClick = null;

   /**
    * 标题
    */
   private TextView v_title;
   /**
    * 标题左边的图标
    */
   private ImageView v_title_icon;

   /**
    * 信息（滚动）
    */
   private ScrollView v_message;

   /**
    * 信息（文案）
    */
   private TextView v_message_text;
   /**
    * 内容（区域）
    */
   private ViewGroup v_content_layout;
   /**
    * 多个按钮的容器
    */
   private ViewGroup v_btn_multiple_layout;
   /**
    * 左边的按钮
    */
   private Button v_btn_left;
   /**
    * 右边的按钮
    */
   private Button v_btn_right;
   /**
    * 单一的按钮
    */
   private ViewGroup v_btn_single;
   /**
    * 单一的按钮（文案）
    */
   private TextView v_btn_single_text;

   /**
    * 内容
    */
   private View v_content = null;

   public Dialog(Context context) {
      this(context, R.layout.ui8_wg_dialog);
   }

   public Dialog(Context context, View v) {
      this(context);
      setMiddleView(v);
   }

   public Dialog(Context context, int layoutId) {
      super(context, R.style.ui8Dialog);
      super.setContentView(layoutId);
      findView();
      resources = context.getResources();
      initView();
   }

   private void initView() {
      setTitle(resources.getString(R.string.mapbar_prompt));
      setConfirmText(resources.getString(R.string.txt_str_determine));
      setCancelText(resources.getString(R.string.cancel));
      setSingleText(resources.getString(R.string.cancel));
      setMessage(null);
      setMiddleView(null);
   }

   private void findView() {
      v_title = (TextView) findViewById(R.id.ui8_wg_dialog_title);
      v_title_icon = (ImageView) findViewById(R.id.ui8_wg_dialog_ic);
      v_message = (ScrollView) findViewById(R.id.ui8_wg_dialog_message);
      v_message_text = (TextView) findViewById(R.id.ui8_wg_dialog_message_text);
      v_content_layout = (ViewGroup) findViewById(R.id.ui8_wg_dialog_context);
      v_btn_left = (Button) findViewById(R.id.ui8_wg_dialog_btn1);
      v_btn_right = (Button) findViewById(R.id.ui8_wg_dialog_btn2);
      v_btn_single = (ViewGroup) findViewById(R.id.ui8_wg_dialog_btn_single);
      v_btn_single_text = (TextView) findViewById(R.id.ui8_wg_dialog_btn_single_text);
      v_btn_multiple_layout = (ViewGroup) findViewById(R.id.ui8_wg_dialog_btn_multiple);
   }

   /**
    * 刷新按钮
    */
   private void invalidateButton() {

      switch (buttonMode) {

         case single:// 单个按钮
            v_btn_single_text.setText(singleText);
            v_btn_single.setOnClickListener(conversionClickListener);
            break;

         case confirmAndCancel:// 两个按钮
            v_btn_left.setText(cancelText);
            v_btn_right.setText(confirmText);
            v_btn_left.setOnClickListener(conversionClickListener);
            v_btn_right.setOnClickListener(conversionClickListener);
            break;

         default:
            break;

      }

      switchButtonStyle();

   }

   /**
    * 切换按钮样式
    */
   private void switchButtonStyle() {
      switch (buttonMode) {

         case single:// 单个按钮
         {
            singleButton(true);
         }
         break;

         case confirmAndCancel:// 两个按钮
         {
            singleButton(false);
            isBtnLeftHighlight = false;
            isBtnRightHighlight = true;
            // 处理高亮
            if (isBtnLeftHighlight) {
               highlight(v_btn_left);
            } else {
               downplay(v_btn_left);
            }
            if (isBtnRightHighlight) {
               highlight(v_btn_right);
            } else {
               downplay(v_btn_right);
            }
         }
         break;

         default:
            break;

      }
   }

   /**
    * @param b 是否单个按钮
    */
   private void singleButton(boolean b) {
      if (b) {
         v_btn_single.setVisibility(View.VISIBLE);
         v_btn_multiple_layout.setVisibility(View.GONE);
      } else {
         v_btn_single.setVisibility(View.GONE);
         v_btn_multiple_layout.setVisibility(View.VISIBLE);
      }
   }

   /**
    * 高亮
    *
    * @param button
    */
   private void highlight(Button button) {
      button.setBackgroundResource(R.drawable.bg_dialogbutton);
      button.setTextColor(Color.parseColor("#007aff"));
   }

   /**
    * 淡化
    *
    * @param button
    */
   private void downplay(Button button) {
      button.setBackgroundResource(R.drawable.bg_dialogbutton);
      button.setTextColor(Color.parseColor("#007aff"));
   }

   @Override
   @Deprecated
   public void setContentView(int layoutResID) {
      super.setContentView(layoutResID);
   }

   /**
    * 标题
    *
    * @param text
    */
   @Override
   public void setTitle(CharSequence text) {
      titleText = text;
      v_title.setText(titleText);
   }

   /**
    * 标题
    *
    * @param textId
    */
   public void setTitle(int textId) {
      setTitle(resources.getString(textId));
   }

   /**
    * 标题图标
    *
    * @param iconId 图标Id
    */
   private void setTitleIcon(Drawable icon) {
      titleIcon = icon;
      v_title_icon.setBackgroundDrawable(titleIcon);
      if (null != titleIcon) {
         v_title_icon.setVisibility(View.VISIBLE);
      } else {
         v_title_icon.setVisibility(View.GONE);
      }
   }

   /**
    * 标题图标
    *
    * @param iconId 图标Id
    */
   public void setTitleIcon(int iconId) {
      setTitleIcon(resources.getDrawable(iconId));
   }

   /**
    * 设置内容文字
    *
    * @param text
    */
   public void setMessage(CharSequence text) {
      messageText = text;
      v_message_text.setText(messageText);
      if (TextUtils.isEmpty(messageText)) {
         v_message.setVisibility(View.GONE);
      } else {
         v_message.setVisibility(View.VISIBLE);
      }
   }

   /**
    * 设置内容文字
    *
    * @param textId
    */
   public void setMessage(int textId) {
      setMessage(resources.getString(textId));
   }

   /**
    * 左右按钮模式（该方式设置的按钮左右位置固定，如果可以建议使用{@link #setButton(int, OnClickListener, int, OnClickListener)}）
    *
    * @param leftTextId         左边按钮的文字ID
    * @param leftHighlight      左边按钮是否高亮
    * @param leftClickListener  左边按钮点击监听
    * @param rightTextId        右边按钮的文字ID
    * @param rightHighlight     右边按钮是否高亮
    * @param rightClickListener 右边按钮点击监听
    */
   private void setButton(int leftTextId, boolean leftHighlight, OnClickListener leftClickListener, int rightTextId, boolean rightHighlight, OnClickListener rightClickListener) {
      setButtonMode(ButtonMode.confirmAndCancel);
      isBtnRightHighlight = rightHighlight;
      isBtnLeftHighlight = leftHighlight;
      confirmText = resources.getString(leftTextId);
      cancelText = resources.getString(rightTextId);
      confirmClick = leftClickListener;
      cancelClick = rightClickListener;
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）
    *
    * @param confirmTextId        确定按钮的文字ID
    * @param confirmClickListener 确定按钮点击监听
    * @param cancelTextId         取消按钮的文字ID
    * @param cancelClickListener  取消按钮点击监听
    */
   private void setButton(int confirmTextId, OnClickListener confirmClickListener, int cancelTextId, OnClickListener cancelClickListener) {
      setButtonMode(ButtonMode.confirmAndCancel);
      confirmText = resources.getString(confirmTextId);
      cancelText = resources.getString(cancelTextId);
      confirmClick = confirmClickListener;
      cancelClick = cancelClickListener;
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）<br>
    * 设置确定按钮文案
    *
    * @param text
    */
   public void setConfirmText(CharSequence text) {
      setButtonMode(ButtonMode.confirmAndCancel);
      confirmText = text;
      invalidateButton();
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）<br>
    * 设置确定按钮文案
    *
    * @param textId
    */
   public void setConfirmText(int textId) {
      setConfirmText(resources.getString(textId));
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）<br>
    * 设置确定按钮点击
    *
    * @param clickListener
    */
   public void setConfirmClick(OnClickListener clickListener) {
      setButtonMode(ButtonMode.confirmAndCancel);
      confirmClick = clickListener;
      invalidateButton();
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）<br>
    * 设置取消按钮文案
    *
    * @param text
    */
   public void setCancelText(CharSequence text) {
      setButtonMode(ButtonMode.confirmAndCancel);
      cancelText = text;
      invalidateButton();
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）<br>
    * 设置取消按钮文案
    *
    * @param textId
    */
   public void setCancelText(int textId) {
      setCancelText(resources.getString(textId));
   }

   /**
    * 确定取消按钮模式（确定和取消按钮的左右位置将根据全局设定进行调整）<br>
    * 设置取消按钮点击
    *
    * @param clickListener
    */
   public void setCancelClick(OnClickListener clickListener) {
      setButtonMode(ButtonMode.confirmAndCancel);
      cancelClick = clickListener;
      invalidateButton();
   }

   /**
    * 单一按钮模式<br>
    * 设置单一按钮文案
    *
    * @param text
    */
   public void setSingleText(CharSequence text) {
      setButtonMode(ButtonMode.single);
      singleText = text;
      invalidateButton();
   }

   /**
    * 单一按钮模式<br>
    * 设置单一按钮文案
    *
    * @param textId
    */
   public void setSingleText(int textId) {
      setSingleText(resources.getString(textId));
   }

   /**
    * 单一按钮模式<br>
    * 设置单一按钮点击
    *
    * @param clickListener
    */
   public void setSingleClick(OnClickListener clickListener) {
      setButtonMode(ButtonMode.single);
      singleClick = clickListener;
      invalidateButton();
   }

   /**
    * 对话框中间自定义view添加
    *
    * @param aMiddleView
    */
   public void setMiddleView(View v) {
      v_content = v;
      if (null == v_content) {
         v_content_layout.setVisibility(View.GONE);
         v_content_layout.removeAllViews();
      } else {
         v_content_layout.addView(//
               v_content, //
               new android.widget.RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)//
         );
         v_content_layout.setVisibility(View.VISIBLE);
      }
   }

   /**
    * @param buttonMode {@link #buttonMode}
    */
   private void setButtonMode(ButtonMode buttonMode) {
      this.buttonMode = buttonMode;
   }

}
