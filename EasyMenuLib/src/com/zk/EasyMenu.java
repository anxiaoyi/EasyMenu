package com.zk;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class EasyMenu extends Fragment {
	
	private static final String ARG_MENU_TITLE = "arg_menu_title";
	private static final String ARG_MENU_LIST = "arg_menu_list";
	private static final String ARG_MENU_MARGIN = "arg_menu_margin";
	private static final String ARG_MENU_TEXTSIZE= "arg_menu_textsize";
	
	private static final int TRANSLATE_DURATION = 200;
	private static final int ALPHA_DURATION = 300;
	
	private boolean mDismissed = true;
	private View mView;
	private View mBg;
	private LinearLayout mPanel;
	private ViewGroup mGroup;
	private OnMenuItemClickListener mOnMenuItemClickListener;
	
	public void show(FragmentManager manager, String tag){
		if(!mDismissed){
			return;
		}
		
		mDismissed = false;
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(this, tag);
		ft.addToBackStack(null);
		ft.commit();
	}
	
	public void dismiss(){
		if(mDismissed){
			return;
		}
		
		mDismissed = true;
		getFragmentManager().popBackStack();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.remove(this);
		ft.commit();
	}
	
	private EasyMenu setOnMenuItemClickListener(OnMenuItemClickListener listener){
		this.mOnMenuItemClickListener = listener;
		return this;
	}
	
	public static Builder createBuilder(Context context,
			FragmentManager fragmentManager) {
		return new Builder(context, fragmentManager);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = createView();
		createMenuItems();
		
		mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
		mGroup.addView(mView);
		
		mBg.startAnimation(createAlphaInAnimation());
		mPanel.startAnimation(createTranslationInAnimation());
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		mBg.startAnimation(createAlphaOutAnimation());
		mPanel.startAnimation(createTranslationOutAnimation());
		mView.postDelayed(new Runnable(){
			@Override
			public void run() {
				mGroup.removeView(mView);
			}
		}, ALPHA_DURATION);
		super.onDestroyView();
	}

	private Animation createAlphaInAnimation(){
		//fromAlpha:0, toAlpha:1
		AlphaAnimation an = new AlphaAnimation(0, 1);
		an.setDuration(ALPHA_DURATION);
		return an;
	}
	
	private Animation createAlphaOutAnimation(){
		//fromAlpha:1, toAlpha:0
		AlphaAnimation an = new AlphaAnimation(1, 0);
		an.setDuration(ALPHA_DURATION);
		an.setFillAfter(true);
		return an;
	}
	
	private Animation createTranslationInAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				1, type, 0);
		an.setDuration(TRANSLATE_DURATION);
		return an;
	}
	
	private Animation createTranslationOutAnimation() {
		int type = TranslateAnimation.RELATIVE_TO_SELF;
		TranslateAnimation an = new TranslateAnimation(type, 0, type, 0, type,
				0, type, 1);
		an.setDuration(TRANSLATE_DURATION);
		an.setFillAfter(true);
		return an;
	}
	
	private View createView(){
		FrameLayout parent = new FrameLayout(getActivity());
		parent.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		//1.create background
		mBg = new View(getActivity());
		mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mBg.setBackgroundColor(Color.argb(136, 0, 0, 0));
		mBg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
		
		//2.create panel
		mPanel = new LinearLayout(getActivity());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		int[] margin = getArguments().getIntArray(ARG_MENU_MARGIN);
		if(margin != null){
			params.leftMargin = margin[0];
			params.topMargin = margin[1];
			params.rightMargin = margin[2];
			params.bottomMargin = margin[3];
		}
		params.gravity = Gravity.BOTTOM;
		mPanel.setLayoutParams(params);
		mPanel.setOrientation(LinearLayout.VERTICAL);
		
		//3.add background and panel to parent
		parent.addView(mBg);
		parent.addView(mPanel);
		
		return parent;
	}
	
	private void createMenuItems(){
		String[] menuItems = getArguments().getStringArray(ARG_MENU_LIST);
		if(menuItems != null){
			int len = menuItems.length;
			final int textSize = getArguments().getInt(ARG_MENU_TEXTSIZE);
			for(int i=0; i<len; ++i){
				final Button btn = new Button(getActivity());
				btn.setText(menuItems[i]);
				if(textSize != 0){
					btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
				}
				final int position = i;
				if(mOnMenuItemClickListener != null){
					btn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							mOnMenuItemClickListener.onMenuItemClick(position, btn);
						}
					});
				}
				if(i == 0){
					mPanel.addView(btn);
				}else{
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
							LinearLayout.LayoutParams.WRAP_CONTENT);
					params.topMargin = 5;
					mPanel.addView(btn, params);
				}
			}
		}
	}
	
	public static class Builder {
		
		private Context mContext;
		private FragmentManager mFragmentManager;
		private String mTag = "EasyMenu";
		private String mTitle;
		private String[] menuItem;
		private int[] margin;
		private OnMenuItemClickListener mListener;
		private int textSize;
		
		public Builder(Context context, FragmentManager fragmentManager){
			mContext = context;
			mFragmentManager = fragmentManager;
		}
		
		public Builder setTag(String tag){
			mTag = tag;
			return this;
		}
		
		public Builder setTitle(String title){
			this.mTitle = title;
			return this;
		}
		
		public Builder setMenuItem(String[] menuItem){
			this.menuItem = menuItem;
			return this;
		}
		
		public Builder setMargin(int leftMargin, int topMargin,
				int rightMargin, int bottomMargin) {
			margin = new int[4];
			margin[0] = leftMargin;
			margin[1] = topMargin;
			margin[2] = rightMargin;
			margin[3] = bottomMargin;
			return this;
		}
		
		public Builder setMenuOnItemClickListener(OnMenuItemClickListener listener){
			this.mListener = listener;
			return this;
		}
		
		public Builder setTextSize(int textSize) {
			this.textSize = textSize;
			return this;
		}
		
		private Bundle prepareArguments(){
			Bundle bundle = new Bundle();
			bundle.putString(ARG_MENU_TITLE, mTitle);
			bundle.putStringArray(ARG_MENU_LIST, menuItem);
			bundle.putIntArray(ARG_MENU_MARGIN, margin);
			bundle.putInt(ARG_MENU_TEXTSIZE, textSize);
			return bundle;
		}
		
		public EasyMenu show(){
			EasyMenu easyMenu = (EasyMenu) Fragment.instantiate(mContext, EasyMenu.class.getName(), prepareArguments());
			easyMenu.setOnMenuItemClickListener(mListener).show(mFragmentManager, mTag);
			return easyMenu;
		}
	}
	
	public interface OnMenuItemClickListener {
		void onMenuItemClick(int which, View v);
	}
}
