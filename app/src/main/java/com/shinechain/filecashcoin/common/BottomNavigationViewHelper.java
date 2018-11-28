package com.shinechain.filecashcoin.common;

import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by zjs on 2018/11/23 10:28
 * Describe: 取消底部的动画效果
 */
public class BottomNavigationViewHelper {

	@SuppressLint("RestrictedApi")
	public static void disableShiftMode(BottomNavigationView view) {
		BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
		try {
			Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
			shiftingMode.setAccessible(true);
			shiftingMode.setBoolean(menuView, false);
			shiftingMode.setAccessible(false);
			for (int i = 0; i < menuView.getChildCount(); i++) {
				BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
				//noinspection RestrictedApi
				item.setShiftingMode(false);
				// set once again checked value, so view will be updated
				//noinspection RestrictedApi
				item.setChecked(item.getItemData().isChecked());
			}
		} catch (NoSuchFieldException e) {
			Log.e("BNVHelper", "Unable to get shift mode field", e);
		} catch (IllegalAccessException e) {
			Log.e("BNVHelper", "Unable to change value of shift mode", e);
		}
	}

	@SuppressLint("RestrictedApi")
	public static void disableItemScale(BottomNavigationView view) {
		try {
			BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);

			Field mLargeLabelField = BottomNavigationItemView.class.getDeclaredField("mLargeLabel");
			Field mSmallLabelField = BottomNavigationItemView.class.getDeclaredField("mSmallLabel");
			Field mShiftAmountField = BottomNavigationItemView.class.getDeclaredField("mShiftAmount");
			Field mScaleUpFactorField = BottomNavigationItemView.class.getDeclaredField("mScaleUpFactor");
			Field mScaleDownFactorField = BottomNavigationItemView.class.getDeclaredField("mScaleDownFactor");

			mSmallLabelField.setAccessible(true);
			mLargeLabelField.setAccessible(true);
			mShiftAmountField.setAccessible(true);
			mScaleUpFactorField.setAccessible(true);
			mScaleDownFactorField.setAccessible(true);


			final float fontScale = view.getResources().getDisplayMetrics().scaledDensity;

			for (int i = 0; i < menuView.getChildCount(); i++) {
				BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);

				TextView lagerObj = (TextView) mLargeLabelField.get(itemView);
				TextView smallObj = (TextView) mSmallLabelField.get(itemView);
				lagerObj.setTextSize(smallObj.getTextSize() / fontScale + 0.5f);


				mShiftAmountField.set(itemView, 0);
				mScaleUpFactorField.set(itemView, 1f);
				mScaleDownFactorField.set(itemView, 1f);

				itemView.setChecked(itemView.getItemData().isChecked());
			}
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
