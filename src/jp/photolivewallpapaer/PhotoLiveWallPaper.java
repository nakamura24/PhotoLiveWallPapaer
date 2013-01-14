package jp.photolivewallpapaer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import jp.template.LiveWallPaper;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

public class PhotoLiveWallPaper extends LiveWallPaper {
	private static String Tag ="LiveWallPaper";
	private ArrayList<String> mImageList = new ArrayList<String>();

	@Override
	public void onCreate() {
		super.onCreate();
		getImageList();
		Collections.sort(mImageList);
	}

	private void getImageList() {
		Log.i(Tag, "getImageList");
		try{
			// SDカードのFileを取得  
			File file = Environment.getExternalStorageDirectory();  
			getImageSubDir(file.getAbsolutePath());
			Log.d(Tag, "getImageList - " + String.valueOf(mImageList.size()));
		} catch (Exception e) {
			Log.e(Tag, e.getMessage());
		}
	}
	private void getImageSubDir(String subdir) {
		Log.i(Tag, "getImageSubDir");
		try {
			File subDir = new File(subdir);  
            String subFileName[] = subDir.list();  
            int n = 0;  
            while(subFileName.length > n){  
                File subFile = new File(subDir + "/" + subFileName[n]);  
                if(subFile.isDirectory()){  
                	getImageSubDir(subFile.getAbsolutePath());
                }else if(subFile.getName().endsWith("jpg") || subFile.getName().endsWith("JPG")){  
            		Log.d(Tag, subDir.getPath() + "/" + subFileName[n]);
            		if(subDir.getPath().contains("DCIM")) {
            			mImageList.add(subDir.getPath() + "/" + subFileName[n]);
            		}
                }  
                n++;  
            }  
		} catch (Exception e) {
			Log.e(Tag, e.getMessage());
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new LiveEngine();
	}

	// キャンバスに描画を行う 
	@Override
	public void DrawCanvas(Canvas canvas) {
		// スーパークラスの処理は、画像を画面サイズにあわせて描画する
		super.DrawCanvas(canvas);
	}

	// 描画する画像を変更
	@Override
	public void ChangeImage() {
		// スーパークラスの処理は、ダブルタップした回数に合わせて画像を変更する
		//super.ChangeImage();
		Image = BitmapFactory.decodeFile(mImageList.get(Offset));
	}

	// 再度描画が行われる前に呼び出される
	@Override
	public void DrawDelay() {
		// スーパークラスの処理は、何もしない
		super.DrawDelay();
	}
	
	// ダブルタップした時の処理
	@Override
	public boolean DoubleTap(MotionEvent event) {
		return false;
	}
	// フリックした時の処理
	@Override
	public boolean Fling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
		Offset += mImageList.size();
		if(velocityX > 0) {
			Offset++;
			Offset %= mImageList.size();
		} else if(velocityX < 0) {
			Offset--;
			Offset %= mImageList.size();
		}
		return true;
	}
	// 長押しした時の処理
	@Override
	public boolean LongPress(MotionEvent event) {
		// スーパークラスの処理は、何もしない
		return super.LongPress(event);
	}
	// スクロールした時の処理
	@Override
	public boolean Scroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
		// スーパークラスの処理は、何もしない
		return super.Scroll(event1, event2, distanceX, distanceY);
	}
	// シングルタップした時の処理
	@Override
	public boolean SingleTapConfirmed(MotionEvent event) {
		// スーパークラスの処理は、何もしない
		return super.SingleTapConfirmed(event);
	}
}
