# EasyMenu

## 仿[ActionSheetForAndroid](https://github.com/baoyongzhang/ActionSheetForAndroid)菜单的制作方法

![示例效果](https://github.com/anxiaoyi/EasyMenu/blob/master/my-demo.gif)

## how to use:
```java
EasyMenu.createBuilder(this, getSupportFragmentManager())
	.setMenuItem(new String[] { "menu 1", "menu 2", "menu 3" })
	.setMargin(30, 0, 30, 0)
	.setMenuOnItemClickListener(new EasyMenu.OnMenuItemClickListener() {
		@Override
		public void onMenuItemClick(int which, View v) {
			Toast.makeText(MainActivity.this, "position: " + which + ", " + ((Button)v).getText().toString(), Toast.LENGTH_SHORT).show();
		}
	})
	.setTextSize(18)
	.show();
```

## 下载jar包[easymenulib.jar](https://github.com/anxiaoyi/EasyMenu/blob/master/easymenulib.jar)