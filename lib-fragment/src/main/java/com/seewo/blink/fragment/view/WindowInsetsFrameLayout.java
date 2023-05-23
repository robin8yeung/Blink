package com.seewo.blink.fragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

public class WindowInsetsFrameLayout extends FrameLayout {

   public WindowInsetsFrameLayout(Context context) {
      this(context, null);
   }

   public WindowInsetsFrameLayout(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public WindowInsetsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context,attrs,defStyleAttr);
      setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
         @Override
         public void onChildViewAdded(View parent, View child) {
            requestApplyInsets();
         }

         @Override
         public void onChildViewRemoved(View parent, View child) {

         }
      });
   }

   @Override
   public WindowInsets onApplyWindowInsets(WindowInsets insets) {
      int childCount = getChildCount();
      for (int index = 0; index < childCount; index++) {
         getChildAt(index).dispatchApplyWindowInsets(insets);
      }
      return insets;
   }

   @Override
   public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
      //重写分发方法，不判断是否消费
      for (int index = 0; index < getChildCount(); index++) {
         try {
            getChildAt(index).dispatchApplyWindowInsets(insets);
         } catch (Throwable e) {
            e.printStackTrace();
         }
      }
      return insets;
   }
}